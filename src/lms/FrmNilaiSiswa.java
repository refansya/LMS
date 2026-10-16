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
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Nilai Saya (siswa): rekap nilai per mata pelajaran + rincian nilai tugas dan kuis.
 * Rumus nilai akhir sama dengan FrmNilaiGuru (Tugas 30%, Kuis 20%, UTS 25%, UAS 25%).
 */
public class FrmNilaiSiswa extends JFrame {

    private static final BigDecimal[] BOBOT = {
        new BigDecimal("30"), new BigDecimal("20"), new BigDecimal("25"), new BigDecimal("25")};
    private static final BigDecimal SERATUS = new BigDecimal("100");
    private static final DateTimeFormatter FMT_TGL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final String TUNTAS = "TUNTAS";
    private static final String BELUM_TUNTAS = "BELUM TUNTAS";
    private static final String BELUM_LENGKAP = "Belum lengkap";

    private static class Rekap {
        int idMapel, kkm;
        String mapel, ket;
        BigDecimal tugas, kuis, uts, uas, akhir;
    }

    private final Map<Integer, Rekap> daftar = new LinkedHashMap<>();

    private DefaultTableModel modelRekap, modelRincian;
    private JTable tabelRekap, tabelRincian;
    private JLabel lblRingkasan, lblRincian;
    private boolean sedangMuat = false;

    public FrmNilaiSiswa() {
        initUI();
        if (Session.idSiswa == 0) {
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatRekap();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Nilai Saya", "NILAI SAYA",
            () -> new FrmMenuSiswa().setVisible(true));

        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 910, 520);
        p.setLayout(null);
        bg.add(p);

        UI.judulForm(p, "Rekapitulasi Nilai Akademik");

        JLabel sub = new JLabel("Nilai Tugas dan Kuis adalah rata-rata semua tugas/kuis yang sudah dinilai. "
            + "Nilai akhir muncul setelah Tugas, Kuis, UTS, dan UAS terisi.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        sub.setBounds(25, 42, 860, 18);
        p.add(sub);

        // ---------- REKAP PER MAPEL ----------
        modelRekap = UI.modelTabel("ID", "Mata Pelajaran", "Tugas (30%)", "Kuis (20%)", "UTS (25%)",
            "UAS (25%)", "Nilai Akhir", "KKM", "Keterangan");
        tabelRekap = UI.tabel(modelRekap); // indeks tampilan: 0 Mapel, 1-4 komponen, 5 Akhir, 6 KKM, 7 Ket
        tabelRekap.getColumnModel().getColumn(0).setPreferredWidth(230);
        for (int c = 1; c <= 4; c++) tabelRekap.getColumnModel().getColumn(c).setPreferredWidth(80);
        tabelRekap.getColumnModel().getColumn(5).setPreferredWidth(90);
        tabelRekap.getColumnModel().getColumn(6).setPreferredWidth(60);
        tabelRekap.getColumnModel().getColumn(7).setPreferredWidth(140);

        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c <= 4; c++) tabelRekap.getColumnModel().getColumn(c).setCellRenderer(tengah);
        tabelRekap.getColumnModel().getColumn(6).setCellRenderer(tengah);

        DefaultTableCellRenderer hasil = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(c == 5 ? SwingConstants.CENTER : SwingConstants.LEADING);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                String ket = String.valueOf(modelRekap.getValueAt(t.convertRowIndexToModel(r), 8));
                Color warna = TUNTAS.equals(ket) ? new Color(39, 174, 96)
                            : BELUM_TUNTAS.equals(ket) ? new Color(192, 41, 43) : Color.GRAY;
                comp.setForeground(sel ? t.getSelectionForeground() : warna);
                return comp;
            }
        };
        tabelRekap.getColumnModel().getColumn(5).setCellRenderer(hasil);
        tabelRekap.getColumnModel().getColumn(7).setCellRenderer(hasil);

        JScrollPane spRekap = new JScrollPane(tabelRekap);
        spRekap.setBounds(25, 70, 860, 200);
        p.add(spRekap);

        lblRingkasan = new JLabel(" ");
        lblRingkasan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRingkasan.setForeground(UI.NAVY);
        lblRingkasan.setBounds(25, 278, 860, 20);
        p.add(lblRingkasan);

        // ---------- RINCIAN ----------
        lblRincian = new JLabel("Rincian Nilai");
        lblRincian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRincian.setForeground(UI.DARK);
        lblRincian.setBounds(25, 308, 860, 22);
        p.add(lblRincian);

        modelRincian = UI.modelTabel("ID", "Jenis", "Keterangan / Judul", "Nilai", "Tanggal");
        tabelRincian = UI.tabel(modelRincian);
        tabelRincian.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabelRincian.getColumnModel().getColumn(1).setPreferredWidth(480);
        tabelRincian.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabelRincian.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabelRincian.getColumnModel().getColumn(2).setCellRenderer(tengah);
        tabelRincian.getColumnModel().getColumn(3).setCellRenderer(tengah);
        JScrollPane spRincian = new JScrollPane(tabelRincian);
        spRincian.setBounds(25, 334, 860, 140);
        p.add(spRincian);

        JLabel hint = new JLabel("Nilai akhir = Tugas 30% + Kuis 20% + UTS 25% + UAS 25%. "
            + "Nilai diperbarui oleh guru mata pelajaran masing-masing.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(25, 484, 860, 18);
        p.add(hint);

        // dipasang paling akhir supaya semua komponen sudah ada
        tabelRekap.getSelectionModel().addListSelectionListener(e -> {
            if (!sedangMuat && !e.getValueIsAdjusting()) muatRincian();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private static String subqueryRata(String jenis, String kolomId) {
        return "(SELECT AVG(n.nilai) FROM tb_nilai n WHERE n.id_siswa = ? AND n.id_mapel = mp.id_mapel "
             + "AND n.jenis_nilai = '" + jenis + "' AND n." + kolomId + " IS NOT NULL)";
    }

    private static String subqueryManual(String jenis) {
        return "(SELECT n.nilai FROM tb_nilai n WHERE n.id_siswa = ? AND n.id_mapel = mp.id_mapel "
             + "AND n.jenis_nilai = '" + jenis + "' AND n.id_tugas IS NULL AND n.id_kuis IS NULL LIMIT 1)";
    }

    private void muatRekap() {
        // mapel yang relevan: sudah punya nilai, atau punya tugas/kuis/materi/jadwal di kelas siswa
        String sql = "SELECT mp.id_mapel, mp.nama_mapel, mp.kkm, "
                   + subqueryRata("Tugas", "id_tugas") + " AS tugas_auto, "
                   + subqueryManual("Tugas") + " AS tugas_man, "
                   + subqueryRata("Kuis", "id_kuis") + " AS kuis_auto, "
                   + subqueryManual("Kuis") + " AS kuis_man, "
                   + subqueryManual("UTS") + " AS uts, "
                   + subqueryManual("UAS") + " AS uas "
                   + "FROM tb_mapel mp "
                   + "WHERE mp.id_mapel IN (SELECT id_mapel FROM tb_nilai WHERE id_siswa = ?) "
                   + "OR mp.id_mapel IN (SELECT id_mapel FROM tb_tugas WHERE id_kelas = ?) "
                   + "OR mp.id_mapel IN (SELECT id_mapel FROM tb_kuis WHERE id_kelas = ?) "
                   + "OR mp.id_mapel IN (SELECT id_mapel FROM tb_materi WHERE id_kelas = ?) "
                   + "OR mp.id_mapel IN (SELECT id_mapel FROM tb_jadwal WHERE id_kelas = ?) "
                   + "ORDER BY mp.nama_mapel";

        int pilihSebelumnya = UI.idTerpilih(tabelRekap, modelRekap);
        daftar.clear();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 1; i <= 7; i++) ps.setInt(i, Session.idSiswa); // 6 subquery nilai + filter mapel bernilai
                for (int i = 8; i <= 11; i++) ps.setInt(i, Session.idKelas); // tugas, kuis, materi, jadwal
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Rekap r = new Rekap();
                        r.idMapel = rs.getInt("id_mapel");
                        r.mapel = rs.getString("nama_mapel");
                        r.kkm = rs.getInt("kkm");
                        BigDecimal ta = rs.getBigDecimal("tugas_auto");
                        r.tugas = ta != null ? ta : rs.getBigDecimal("tugas_man");
                        BigDecimal ka = rs.getBigDecimal("kuis_auto");
                        r.kuis = ka != null ? ka : rs.getBigDecimal("kuis_man");
                        r.uts = rs.getBigDecimal("uts");
                        r.uas = rs.getBigDecimal("uas");
                        r.akhir = hitungAkhir(r);
                        r.ket = r.akhir == null ? BELUM_LENGKAP
                            : (r.akhir.compareTo(new BigDecimal(r.kkm)) >= 0 ? TUNTAS : BELUM_TUNTAS);
                        daftar.put(r.idMapel, r);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        sedangMuat = true;
        modelRekap.setRowCount(0);
        int barisPilih = -1;
        for (Rekap r : daftar.values()) {
            if (r.idMapel == pilihSebelumnya) barisPilih = modelRekap.getRowCount();
            modelRekap.addRow(new Object[]{r.idMapel, r.mapel, fmt(r.tugas), fmt(r.kuis), fmt(r.uts), fmt(r.uas),
                r.akhir == null ? "-" : r.akhir.toPlainString(), r.kkm, r.ket});
        }
        sedangMuat = false;

        hitungRingkasan();

        if (barisPilih == -1 && modelRekap.getRowCount() > 0) barisPilih = 0;
        if (barisPilih >= 0) {
            tabelRekap.setRowSelectionInterval(barisPilih, barisPilih); // memicu muatRincian()
        } else {
            muatRincian();
        }
    }

    private void muatRincian() {
        modelRincian.setRowCount(0);
        Rekap r = daftar.get(UI.idTerpilih(tabelRekap, modelRekap));
        if (r == null) {
            lblRincian.setText(daftar.isEmpty() ? "Belum ada mata pelajaran untuk kelas kamu."
                : "Pilih mata pelajaran untuk melihat rincian nilai.");
            return;
        }
        lblRincian.setText("Rincian Nilai - " + r.mapel);

        String sql = "SELECT n.jenis_nilai, COALESCE(t.judul, k.judul, "
                   + "CASE WHEN n.keterangan = 'Input manual' THEN 'Input guru' ELSE n.keterangan END, '-') AS judul, "
                   + "n.nilai, n.tanggal_input "
                   + "FROM tb_nilai n "
                   + "LEFT JOIN tb_tugas t ON n.id_tugas = t.id_tugas "
                   + "LEFT JOIN tb_kuis k ON n.id_kuis = k.id_kuis "
                   + "WHERE n.id_siswa = ? AND n.id_mapel = ? "
                   + "ORDER BY FIELD(n.jenis_nilai,'Tugas','Kuis','UTS','UAS'), n.tanggal_input";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idSiswa);
                ps.setInt(2, r.idMapel);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Timestamp tg = rs.getTimestamp("tanggal_input");
                        modelRincian.addRow(new Object[]{0, rs.getString("jenis_nilai"), rs.getString("judul"),
                            fmt(rs.getBigDecimal("nilai")), tg == null ? "-" : FMT_TGL.format(tg.toLocalDateTime())});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        if (modelRincian.getRowCount() == 0) {
            lblRincian.setText("Rincian Nilai - " + r.mapel + "  (belum ada nilai yang masuk)");
        }
    }

    // ------------------------------------------------------------------
    //  PERHITUNGAN (sama dengan FrmNilaiGuru)
    // ------------------------------------------------------------------
    /** Nilai akhir berbobot (1 desimal), atau null jika ada komponen yang belum terisi. */
    private static BigDecimal hitungAkhir(Rekap r) {
        BigDecimal[] v = {r.tugas, r.kuis, r.uts, r.uas};
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < 4; i++) {
            if (v[i] == null) return null;
            total = total.add(v[i].multiply(BOBOT[i]));
        }
        return total.divide(SERATUS, 1, RoundingMode.HALF_UP);
    }

    private void hitungRingkasan() {
        int lengkap = 0, tuntas = 0;
        BigDecimal jumlah = BigDecimal.ZERO;
        for (Rekap r : daftar.values()) {
            if (r.akhir == null) continue;
            lengkap++;
            jumlah = jumlah.add(r.akhir);
            if (TUNTAS.equals(r.ket)) tuntas++;
        }
        if (daftar.isEmpty()) {
            lblRingkasan.setText("Belum ada mata pelajaran untuk kelas kamu.");
        } else if (lengkap == 0) {
            lblRingkasan.setText("Belum ada nilai akhir yang lengkap (" + daftar.size() + " mata pelajaran).");
        } else {
            lblRingkasan.setText("Rata-rata nilai akhir: "
                + jumlah.divide(new BigDecimal(lengkap), 1, RoundingMode.HALF_UP).toPlainString()
                + "   |   Tuntas " + tuntas + " dari " + lengkap + " mapel yang sudah lengkap"
                + "   |   " + (daftar.size() - lengkap) + " mapel belum lengkap");
        }
    }

    /** "85.00" -> "85", "87.50" -> "87.5". null -> "-". */
    private static String fmt(BigDecimal n) {
        if (n == null) return "-";
        BigDecimal r = n.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return (r.scale() < 0 ? r.setScale(0) : r).toPlainString();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmNilaiSiswa().setVisible(true));
    }
}