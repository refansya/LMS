package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class FrmNilaiGuru extends JFrame {

    private static final String[] JENIS = {"Tugas", "Kuis", "UTS", "UAS"};
    private static final BigDecimal[] BOBOT = {
        new BigDecimal("30"), new BigDecimal("20"), new BigDecimal("25"), new BigDecimal("25")};
    private static final BigDecimal SERATUS = new BigDecimal("100");
    private static final String POLA_ANGKA = "\\d{1,3}(\\.\\d{1,2})?";

    // kolom MODEL: 0 ID siswa (tersembunyi), 1 No, 2 NIS, 3 Nama, 4 Tugas, 5 Kuis, 6 UTS, 7 UAS, 8 Nilai Akhir, 9 Keterangan
    private static final int KOL_JENIS_AWAL = 4;
    private static final int KOL_AKHIR = 8;
    private static final int KOL_KET = 9;

    private JComboBox<UI.Item> cmbKelas, cmbMapel;
    private JLabel lblInfo, lblRingkasan;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnTampil, btnSimpan;

    // kelas, mapel, dan KKM yang SEDANG ditampilkan di tabel
    private int kelasMuat = 0, mapelMuat = 0, kkmMuat = 75;
    private String namaKelasMuat = "", namaMapelMuat = "";
    private boolean sedangHitung = false;
    private boolean dirty = false;
    // true jika nilai Tugas baris tsb diambil otomatis dari tugas yang sudah dinilai (tidak bisa diedit)
    private final List<Boolean> tugasOtomatis = new ArrayList<>();
    // true jika nilai Kuis baris tsb diambil otomatis dari kuis online yang sudah dikerjakan siswa
    private final List<Boolean> kuisOtomatis = new ArrayList<>();

    public FrmNilaiGuru() {
        initUI();
        muatPilihan();

        if (Session.idGuru == 0) {
            btnTampil.setEnabled(false);
            btnSimpan.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data guru, jadi belum bisa menginput nilai.\n"
              + "Minta administrator menambahkan Anda di menu Data Guru.",
                "Data Guru Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
        } else {
            tampilkan();
        }
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Input Nilai Guru", "PENGINPUTAN NILAI AKADEMIK",
            () -> new FrmMenuGuru().setVisible(true));

        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 910, 520);
        p.setLayout(null);
        bg.add(p);

        UI.judulForm(p, "Form Evaluasi Nilai Harian & Ujian");

        lbl(p, "Kelas", 25, 50);
        cmbKelas = new JComboBox<>();
        cmbKelas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKelas.setBackground(Color.WHITE);
        cmbKelas.setBounds(25, 68, 180, 28);
        p.add(cmbKelas);

        lbl(p, "Mata Pelajaran", 225, 50);
        cmbMapel = new JComboBox<>();
        cmbMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMapel.setBackground(Color.WHITE);
        cmbMapel.setBounds(225, 68, 230, 28);
        p.add(cmbMapel);

        btnTampil = UI.tombol(p, "TAMPILKAN", UI.NAVY, new Color(10, 36, 66), 475, 66, 110, 32, this::tampilkan);

        lblRingkasan = new JLabel(" ");
        lblRingkasan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRingkasan.setForeground(UI.NAVY);
        lblRingkasan.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRingkasan.setBounds(600, 70, 285, 22);
        p.add(lblRingkasan);

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(25, 104, 860, 20);
        p.add(lblInfo);

        model = new DefaultTableModel(new String[]{"ID", "No", "NIS", "Nama Siswa",
            "Tugas (30%)", "Kuis (20%)", "UTS (25%)", "UAS (25%)", "Nilai Akhir", "Keterangan"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                if (column == KOL_JENIS_AWAL) { // Tugas: hanya jika belum ada nilai otomatis
                    return row < tugasOtomatis.size() && !tugasOtomatis.get(row);
                }
                if (column == KOL_JENIS_AWAL + 1) { // Kuis: hanya jika belum ada nilai otomatis
                    return row < kuisOtomatis.size() && !kuisOtomatis.get(row);
                }
                return column > KOL_JENIS_AWAL + 1 && column < KOL_AKHIR; // UTS, UAS
            }
        };
        tabel = UI.tabel(model); // kolom ID dibuang -> indeks tampilan: 0 No, 1 NIS, 2 Nama, 3-6 nilai, 7 Akhir, 8 Ket
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(220);
        for (int c = 3; c <= 6; c++) tabel.getColumnModel().getColumn(c).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(7).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(8).setPreferredWidth(120);

        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 3; c <= 6; c++) tabel.getColumnModel().getColumn(c).setCellRenderer(tengah);

        DefaultTableCellRenderer hasil = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(c == 7 ? SwingConstants.CENTER : SwingConstants.LEADING);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                String ket = teks(model.getValueAt(t.convertRowIndexToModel(r), KOL_KET));
                Color warna = "TUNTAS".equals(ket) ? new Color(39, 174, 96)
                            : "BELUM TUNTAS".equals(ket) ? new Color(192, 41, 43) : Color.GRAY;
                comp.setForeground(sel ? t.getSelectionForeground() : warna);
                return comp;
            }
        };
        tabel.getColumnModel().getColumn(7).setCellRenderer(hasil);
        tabel.getColumnModel().getColumn(8).setCellRenderer(hasil);

        model.addTableModelListener(e -> {
            if (sedangHitung || e.getType() != TableModelEvent.UPDATE || e.getFirstRow() < 0) return;
            if (e.getColumn() >= KOL_JENIS_AWAL && e.getColumn() < KOL_AKHIR) {
                dirty = true;
                hitungBaris(e.getFirstRow());
                hitungRingkasan();
            }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(25, 130, 860, 305);
        p.add(sp);

        btnSimpan = UI.tombol(p, "SIMPAN NILAI", UI.HIJAU, UI.HIJAU_H, 25, 455, 190, 40, this::simpan);

        JLabel hint = new JLabel("<html>Kolom <b>Tugas</b> dan <b>Kuis</b> terisi otomatis (rata-rata) dari tugas/kuis yang sudah dinilai. "
            + "Jika belum ada, bisa diisi manual. Kosongkan sel untuk menghapus nilai.</html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(235, 455, 650, 40);
        p.add(hint);
    }

    private void lbl(JPanel p, String teks, int x, int y) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UI.NAVY);
        l.setBounds(x, y, 250, 16);
        p.add(l);
    }

    private static String teks(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /** "85.00" -> "85", "87.50" -> "87.5". null -> "". */
    private static String fmt(BigDecimal n) {
        if (n == null) return "";
        BigDecimal r = n.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return (r.scale() < 0 ? r.setScale(0) : r).toPlainString();
    }

    /** Teks sel -> angka, atau null jika kosong / "-" / tidak valid. */
    private static BigDecimal angka(Object o) {
        String s = teks(o).replace(',', '.');
        if (s.isEmpty() || s.equals("-") || !s.matches(POLA_ANGKA)) return null;
        BigDecimal b = new BigDecimal(s);
        return b.compareTo(SERATUS) > 0 ? null : b;
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatPilihan() {
        cmbKelas.removeAllItems();
        cmbMapel.removeAllItems();
        cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
        if (Session.idGuru == 0) return;
        int mapelGuru = 0;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            // kelas yang berkaitan dengan guru: wali kelas, jadwal, tugas, atau materi
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT k.id_kelas, k.nama_kelas FROM tb_kelas k "
                  + "WHERE k.id_wali_kelas = ? "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_jadwal WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_tugas WHERE id_guru = ?) "
                  + "OR k.id_kelas IN (SELECT id_kelas FROM tb_materi WHERE id_guru = ?) "
                  + "ORDER BY k.tingkat, k.nama_kelas")) {
                for (int i = 1; i <= 4; i++) ps.setInt(i, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
            if (cmbKelas.getItemCount() == 0) { // belum ada keterkaitan -> tampilkan semua kelas
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                    while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                while (rs.next()) cmbMapel.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT id_mapel FROM tb_guru WHERE id_guru=?")) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) mapelGuru = rs.getInt(1); // 0 jika NULL
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        UI.pilihItem(cmbMapel, mapelGuru);
    }

    private void tampilkan() {
        UI.Item k = (UI.Item) cmbKelas.getSelectedItem();
        UI.Item m = (UI.Item) cmbMapel.getSelectedItem();
        if (k == null || k.id == 0) {
            UI.peringatan(this, "Belum ada kelas yang bisa dipilih. Tambahkan kelas lewat menu admin.");
            return;
        }
        if (m == null || m.id == 0) {
            UI.peringatan(this, "Pilih mata pelajaran terlebih dahulu.");
            return;
        }
        if (dirty && !UI.konfirmasiHapus(this,
                "Ada perubahan nilai yang belum disimpan.\nTampilkan data lain dan buang perubahan tersebut?")) {
            return;
        }
        kelasMuat = k.id;
        mapelMuat = m.id;
        namaKelasMuat = k.label;
        namaMapelMuat = m.label;
        muatSiswa(namaKelasMuat, namaMapelMuat);
    }

    private void muatSiswa(String namaKelas, String namaMapel) {
        model.setRowCount(0);
        tugasOtomatis.clear();
        kuisOtomatis.clear();
        dirty = false;

        String manual = "(SELECT n.nilai FROM tb_nilai n WHERE n.id_siswa = s.id_siswa AND n.id_mapel = ? "
                      + "AND n.jenis_nilai = '%s' AND n.id_tugas IS NULL AND n.id_kuis IS NULL LIMIT 1)";
        String sql = "SELECT s.id_siswa, s.nis, s.nama_siswa, "
                   + "(SELECT AVG(n.nilai) FROM tb_nilai n WHERE n.id_siswa = s.id_siswa AND n.id_mapel = ? "
                   + "AND n.jenis_nilai = 'Tugas' AND n.id_tugas IS NOT NULL) AS tugas_auto, "
                   + String.format(manual, "Tugas") + " AS tugas_man, "
                   + "(SELECT AVG(n.nilai) FROM tb_nilai n WHERE n.id_siswa = s.id_siswa AND n.id_mapel = ? "
                   + "AND n.jenis_nilai = 'Kuis' AND n.id_kuis IS NOT NULL) AS kuis_auto, "
                   + String.format(manual, "Kuis") + " AS kuis_man, "
                   + String.format(manual, "UTS") + " AS uts, "
                   + String.format(manual, "UAS") + " AS uas "
                   + "FROM tb_siswa s WHERE s.id_kelas = ? ORDER BY s.nama_siswa";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            try (PreparedStatement ps = con.prepareStatement("SELECT kkm FROM tb_mapel WHERE id_mapel=?")) {
                ps.setInt(1, mapelMuat);
                try (ResultSet rs = ps.executeQuery()) {
                    kkmMuat = rs.next() ? rs.getInt(1) : 75;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 1; i <= 6; i++) ps.setInt(i, mapelMuat);
                ps.setInt(7, kelasMuat);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        BigDecimal auto = rs.getBigDecimal("tugas_auto");
                        BigDecimal tugas = (auto != null) ? auto : rs.getBigDecimal("tugas_man");
                        tugasOtomatis.add(auto != null);
                        BigDecimal autoKuis = rs.getBigDecimal("kuis_auto");
                        BigDecimal kuis = (autoKuis != null) ? autoKuis : rs.getBigDecimal("kuis_man");
                        kuisOtomatis.add(autoKuis != null);

                        String sTugas = fmt(tugas), sKuis = fmt(kuis);
                        String sUts = fmt(rs.getBigDecimal("uts")), sUas = fmt(rs.getBigDecimal("uas"));
                        BigDecimal akhir = hitungAkhir(sTugas, sKuis, sUts, sUas);
                        model.addRow(new Object[]{rs.getInt("id_siswa"), no++, rs.getString("nis"),
                            rs.getString("nama_siswa"), sTugas, sKuis, sUts, sUas,
                            akhir == null ? "-" : akhir.toPlainString(), keterangan(akhir)});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }

        lblInfo.setText(namaMapel + " - Kelas " + namaKelas + "   |   KKM: " + kkmMuat
            + (model.getRowCount() == 0 ? "   |   Kelas ini belum memiliki siswa." : ""));
        hitungRingkasan();
    }

    // ------------------------------------------------------------------
    //  PERHITUNGAN
    // ------------------------------------------------------------------
    /** Nilai akhir berbobot (1 desimal), atau null jika ada komponen yang belum terisi. */
    private BigDecimal hitungAkhir(Object tugas, Object kuis, Object uts, Object uas) {
        Object[] v = {tugas, kuis, uts, uas};
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < 4; i++) {
            BigDecimal a = angka(v[i]);
            if (a == null) return null;
            total = total.add(a.multiply(BOBOT[i]));
        }
        return total.divide(SERATUS, 1, RoundingMode.HALF_UP);
    }

    private String keterangan(BigDecimal akhir) {
        if (akhir == null) return "Belum lengkap";
        return akhir.compareTo(new BigDecimal(kkmMuat)) >= 0 ? "TUNTAS" : "BELUM TUNTAS";
    }

    private void hitungBaris(int row) {
        BigDecimal akhir = hitungAkhir(model.getValueAt(row, 4), model.getValueAt(row, 5),
                                       model.getValueAt(row, 6), model.getValueAt(row, 7));
        sedangHitung = true;
        try {
            model.setValueAt(akhir == null ? "-" : akhir.toPlainString(), row, KOL_AKHIR);
            model.setValueAt(keterangan(akhir), row, KOL_KET);
        } finally {
            sedangHitung = false;
        }
    }

    private void hitungRingkasan() {
        int lengkap = 0, tuntas = 0;
        BigDecimal jumlah = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            BigDecimal a = angka(model.getValueAt(i, KOL_AKHIR));
            if (a == null) continue;
            lengkap++;
            jumlah = jumlah.add(a);
            if ("TUNTAS".equals(teks(model.getValueAt(i, KOL_KET)))) tuntas++;
        }
        if (model.getRowCount() == 0) { lblRingkasan.setText(" "); return; }
        lblRingkasan.setText(lengkap == 0 ? "Belum ada nilai akhir lengkap"
            : "Rata-rata: " + jumlah.divide(new BigDecimal(lengkap), 1, RoundingMode.HALF_UP).toPlainString()
              + "  |  Tuntas " + tuntas + "/" + lengkap);
    }

    // ------------------------------------------------------------------
    //  SIMPAN
    // ------------------------------------------------------------------
    /** true jika komponen k (0 Tugas, 1 Kuis, 2 UTS, 3 UAS) baris i berasal dari nilai otomatis. */
    private boolean otomatis(int i, int k) {
        return (k == 0 && tugasOtomatis.get(i)) || (k == 1 && kuisOtomatis.get(i));
    }

    private String validasi() {
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int k = 0; k < 4; k++) {
                if (otomatis(i, k)) continue; // nilai otomatis tidak disimpan dari sini
                String s = teks(model.getValueAt(i, KOL_JENIS_AWAL + k)).replace(',', '.');
                if (s.isEmpty()) continue;
                if (!s.matches(POLA_ANGKA) || new BigDecimal(s).compareTo(SERATUS) > 0) {
                    return "Nilai " + JENIS[k] + " untuk \"" + model.getValueAt(i, 3)
                         + "\" harus angka 0 - 100 (maks. 2 desimal).";
                }
            }
        }
        return null;
    }

    private void simpan() {
        if (tabel.isEditing()) tabel.getCellEditor().stopCellEditing();

        if (kelasMuat == 0 || mapelMuat == 0 || model.getRowCount() == 0) {
            UI.peringatan(this, "Tidak ada data siswa untuk disimpan. Klik TAMPILKAN dulu.");
            return;
        }
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        String kondisi = "WHERE id_siswa=? AND id_mapel=? AND jenis_nilai=? AND id_tugas IS NULL AND id_kuis IS NULL";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try (PreparedStatement del = con.prepareStatement("DELETE FROM tb_nilai " + kondisi);
                 PreparedStatement upd = con.prepareStatement(
                     "UPDATE tb_nilai SET nilai=?, id_guru=?, tanggal_input=NOW() " + kondisi);
                 PreparedStatement ins = con.prepareStatement(
                     "INSERT INTO tb_nilai (id_siswa, id_guru, id_mapel, jenis_nilai, nilai, keterangan) "
                   + "VALUES (?,?,?,?,?,'Input manual')")) {

                for (int i = 0; i < model.getRowCount(); i++) {
                    int idSiswa = (Integer) model.getValueAt(i, 0);
                    for (int k = 0; k < 4; k++) {
                        if (otomatis(i, k)) continue;
                        BigDecimal v = angka(model.getValueAt(i, KOL_JENIS_AWAL + k));

                        if (v == null) { // sel dikosongkan -> hapus nilai manualnya
                            del.setInt(1, idSiswa);
                            del.setInt(2, mapelMuat);
                            del.setString(3, JENIS[k]);
                            del.executeUpdate();
                            continue;
                        }
                        upd.setBigDecimal(1, v);
                        upd.setInt(2, Session.idGuru);
                        upd.setInt(3, idSiswa);
                        upd.setInt(4, mapelMuat);
                        upd.setString(5, JENIS[k]);
                        if (upd.executeUpdate() == 0) {
                            ins.setInt(1, idSiswa);
                            ins.setInt(2, Session.idGuru);
                            ins.setInt(3, mapelMuat);
                            ins.setString(4, JENIS[k]);
                            ins.setBigDecimal(5, v);
                            ins.executeUpdate();
                        }
                    }
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        JOptionPane.showMessageDialog(this, "Nilai " + model.getRowCount() + " siswa berhasil disimpan.");
        muatSiswa(namaKelasMuat, namaMapelMuat);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmNilaiGuru().setVisible(true));
    }
}