package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class FrmAbsensiGuru extends JFrame {

    private static final String[] STATUS = {"Hadir", "Sakit", "Izin", "Alpha"};
    private static final DateTimeFormatter TGL_TAMPIL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private JComboBox<UI.Item> cmbKelas;
    private JTextField txtTanggal;
    private JLabel lblInfo, lblRingkasan;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnTampil, btnSemuaHadir, btnSimpan;

    // kelas & tanggal yang SEDANG ditampilkan di tabel (bukan isi field saat ini)
    private int kelasMuat = 0;
    private LocalDate tanggalMuat = null;

    public FrmAbsensiGuru() {
        initUI();
        muatKelas();

        if (Session.idGuru == 0) {
            btnTampil.setEnabled(false);
            btnSemuaHadir.setEnabled(false);
            btnSimpan.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data guru, jadi belum bisa mengisi presensi.\n"
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
        JPanel bg = UI.siapkanFrame(this, "Presensi Siswa", "PRESENSI HARIAN SISWA",
            () -> new FrmMenuGuru().setVisible(true));

        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 910, 520);
        p.setLayout(null);
        bg.add(p);

        UI.judulForm(p, "Lembar Presensi Kelas");

        lbl(p, "Kelas", 25, 50);
        cmbKelas = new JComboBox<>();
        cmbKelas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKelas.setBackground(Color.WHITE);
        cmbKelas.setBounds(25, 68, 200, 28);
        p.add(cmbKelas);

        lbl(p, "Tanggal (format: 2026-10-14)", 245, 50);
        txtTanggal = new JTextField(LocalDate.now().toString());
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTanggal.setBounds(245, 68, 160, 28);
        txtTanggal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        p.add(txtTanggal);

        btnTampil = UI.tombol(p, "TAMPILKAN", UI.NAVY, new Color(10, 36, 66), 420, 66, 110, 32, this::tampilkan);

        lblRingkasan = new JLabel(" ");
        lblRingkasan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRingkasan.setForeground(UI.NAVY);
        lblRingkasan.setBounds(550, 70, 340, 22);
        p.add(lblRingkasan);

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(25, 104, 860, 20);
        p.add(lblInfo);

        // kolom model: 0 ID siswa (disembunyikan), 1 No, 2 NIS, 3 Nama, 4 Status, 5 Keterangan
        model = new DefaultTableModel(
            new String[]{"ID", "No", "NIS", "Nama Siswa", "Status Kehadiran", "Keterangan"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };
        tabel = UI.tabel(model); // kolom ID dibuang dari tampilan -> indeks tampilan: 0 No ... 4 Keterangan
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(290);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(140);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(270);

        tabel.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(STATUS)));
        tabel.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                comp.setForeground(sel ? t.getSelectionForeground() : warnaStatus(String.valueOf(v)));
                return comp;
            }
        });
        model.addTableModelListener(e -> hitungRingkasan());

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(25, 130, 860, 305);
        p.add(sp);

        btnSemuaHadir = UI.tombol(p, "SEMUA HADIR", UI.NAVY, new Color(10, 36, 66), 25, 455, 150, 40, this::semuaHadir);
        btnSimpan = UI.tombol(p, "SIMPAN PRESENSI", UI.HIJAU, UI.HIJAU_H, 190, 455, 190, 40, this::simpan);
    }

    private void lbl(JPanel p, String teks, int x, int y) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UI.NAVY);
        l.setBounds(x, y, 250, 16);
        p.add(l);
    }

    private static Color warnaStatus(String s) {
        switch (s) {
            case "Hadir": return new Color(39, 174, 96);
            case "Sakit": return new Color(41, 128, 185);
            case "Izin":  return new Color(212, 143, 0);
            case "Alpha": return new Color(192, 41, 43);
            default:      return Color.DARK_GRAY;
        }
    }

    private static String teks(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** Kelas milik guru (wali kelas / ada di jadwal). Jika tidak ada, tampilkan semua kelas. */
    private void muatKelas() {
        cmbKelas.removeAllItems();
        if (Session.idGuru == 0) return;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT DISTINCT k.id_kelas, k.nama_kelas, k.tingkat FROM tb_kelas k "
                  + "LEFT JOIN tb_jadwal j ON j.id_kelas = k.id_kelas "
                  + "WHERE k.id_wali_kelas = ? OR j.id_guru = ? ORDER BY k.tingkat, k.nama_kelas")) {
                ps.setInt(1, Session.idGuru);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
            if (cmbKelas.getItemCount() == 0) {
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                    while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private LocalDate parseTanggal() {
        String s = txtTanggal.getText().trim();
        if (!s.matches("\\d{4}-\\d{2}-\\d{2}")) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void tampilkan() {
        UI.Item k = (UI.Item) cmbKelas.getSelectedItem();
        if (k == null || k.id == 0) {
            UI.peringatan(this, "Belum ada kelas yang bisa dipilih. Tambahkan kelas lewat menu admin.");
            return;
        }
        LocalDate t = parseTanggal();
        if (t == null) {
            UI.peringatan(this, "Tanggal harus berformat 2026-10-14 dan tanggalnya valid.");
            return;
        }
        if (t.isAfter(LocalDate.now())) {
            UI.peringatan(this, "Presensi tidak bisa diisi untuk tanggal yang belum terjadi.");
            return;
        }
        kelasMuat = k.id;
        tanggalMuat = t;
        muatSiswa();
    }

    private void muatSiswa() {
        model.setRowCount(0);
        int sudahAda = 0;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT s.id_siswa, s.nis, s.nama_siswa, COALESCE(a.status,'Hadir') AS status, "
                  + "COALESCE(a.keterangan,'') AS ket, (a.id_absensi IS NOT NULL) AS ada "
                  + "FROM tb_siswa s LEFT JOIN tb_absensi a ON a.id_siswa = s.id_siswa AND a.tanggal = ? "
                  + "WHERE s.id_kelas = ? ORDER BY s.nama_siswa")) {
                ps.setDate(1, java.sql.Date.valueOf(tanggalMuat));
                ps.setInt(2, kelasMuat);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        if (rs.getBoolean("ada")) sudahAda++;
                        model.addRow(new Object[]{rs.getInt("id_siswa"), no++, rs.getString("nis"),
                            rs.getString("nama_siswa"), rs.getString("status"), rs.getString("ket")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }

        String tgl = tanggalMuat.format(TGL_TAMPIL);
        if (model.getRowCount() == 0) {
            lblInfo.setText("Kelas ini belum memiliki siswa.");
        } else if (sudahAda > 0) {
            lblInfo.setText("Presensi tanggal " + tgl + " sudah pernah disimpan. Ubah lalu simpan lagi untuk memperbarui.");
        } else {
            lblInfo.setText("Belum ada presensi tanggal " + tgl
                + ". Semua siswa diisi Hadir; ubah yang tidak hadir lalu simpan.");
        }
        hitungRingkasan();
    }

    private void hitungRingkasan() {
        int[] n = new int[STATUS.length];
        int total = model.getRowCount();
        for (int i = 0; i < total; i++) {
            String s = teks(model.getValueAt(i, 4));
            for (int k = 0; k < STATUS.length; k++) if (STATUS[k].equals(s)) n[k]++;
        }
        if (total == 0) { lblRingkasan.setText(" "); return; }
        lblRingkasan.setText("Hadir " + n[0] + "  |  Sakit " + n[1] + "  |  Izin " + n[2]
            + "  |  Alpha " + n[3] + "  (" + (n[0] * 100 / total) + "% hadir)");
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void semuaHadir() {
        if (tabel.isEditing()) tabel.getCellEditor().stopCellEditing();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt("Hadir", i, 4);
            model.setValueAt("", i, 5);
        }
    }

    private void simpan() {
        if (tabel.isEditing()) tabel.getCellEditor().stopCellEditing();

        if (kelasMuat == 0 || tanggalMuat == null || model.getRowCount() == 0) {
            UI.peringatan(this, "Tidak ada data siswa untuk disimpan. Klik TAMPILKAN dulu.");
            return;
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (teks(model.getValueAt(i, 5)).length() > 100) {
                UI.peringatan(this, "Keterangan untuk \"" + model.getValueAt(i, 3)
                    + "\" maksimal 100 karakter.");
                return;
            }
        }
        DayOfWeek hari = tanggalMuat.getDayOfWeek();
        if (hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY) {
            int ok = JOptionPane.showConfirmDialog(this,
                "Tanggal " + tanggalMuat.format(TGL_TAMPIL) + " jatuh pada akhir pekan.\nTetap simpan presensi?",
                "Akhir Pekan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
        }

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tb_absensi (id_siswa, id_kelas, id_guru, tanggal, status, keterangan) "
                  + "VALUES (?,?,?,?,?,?) "
                  + "ON DUPLICATE KEY UPDATE status=VALUES(status), keterangan=VALUES(keterangan), "
                  + "id_guru=VALUES(id_guru)")) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String ket = teks(model.getValueAt(i, 5));
                    ps.setInt(1, (Integer) model.getValueAt(i, 0));
                    ps.setInt(2, kelasMuat);
                    ps.setInt(3, Session.idGuru);
                    ps.setDate(4, java.sql.Date.valueOf(tanggalMuat));
                    ps.setString(5, teks(model.getValueAt(i, 4)));
                    ps.setString(6, ket.isEmpty() ? null : ket);
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        JOptionPane.showMessageDialog(this, "Presensi " + model.getRowCount() + " siswa tanggal "
            + tanggalMuat.format(TGL_TAMPIL) + " berhasil disimpan.");
        muatSiswa();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmAbsensiGuru().setVisible(true));
    }
}