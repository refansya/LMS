package lms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class FrmTugasGuru extends JFrame {

    private JLabel lblInfo;
    private DefaultTableModel modelTugas, modelKumpul;
    private JTable tabelTugas, tabelKumpul;
    private RoundedButton btnBuat, btnUbah, btnHapus, btnBuka, btnNilai;

    public FrmTugasGuru() {
        initUI();
        muatTugas(0);

        if (Session.idGuru == 0) {
            btnBuat.setEnabled(false);
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            btnBuka.setEnabled(false);
            btnNilai.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data guru, jadi belum bisa mengelola tugas.\n"
              + "Minta administrator menambahkan Anda di menu Data Guru.",
                "Data Guru Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Tugas Masuk", "TUGAS MASUK & PENILAIAN",
            () -> new FrmMenuGuru().setVisible(true));

        // ---------- KIRI: daftar tugas ----------
        RoundedPanel kiri = new RoundedPanel(20, Color.WHITE, true);
        kiri.setBounds(40, 80, 440, 520);
        kiri.setLayout(null);
        bg.add(kiri);

        JLabel l1 = new JLabel("Tugas Saya");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l1.setForeground(UI.DARK);
        l1.setBounds(25, 18, 250, 25);
        kiri.add(l1);

        modelTugas = UI.modelTabel("ID", "No", "Judul Tugas", "Kelas", "Deadline", "Terkumpul");
        tabelTugas = UI.tabel(modelTugas);
        tabelTugas.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelTugas.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabelTugas.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabelTugas.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabelTugas.getColumnModel().getColumn(4).setPreferredWidth(65);
        JScrollPane spT = new JScrollPane(tabelTugas);
        spT.setBounds(25, 60, 390, 380);
        kiri.add(spT);

        btnBuat = UI.tombol(kiri, "BUAT TUGAS", UI.HIJAU, UI.HIJAU_H, 25, 458, 125, 38, this::aksiBuat);
        btnUbah = UI.tombol(kiri, "UBAH", UI.KUNING, UI.KUNING_H, 160, 458, 125, 38, this::aksiUbah);
        btnHapus = UI.tombol(kiri, "HAPUS", UI.MERAH, UI.MERAH_H, 295, 458, 120, 38, this::aksiHapus);

        // ---------- KANAN: pengumpulan siswa ----------
        RoundedPanel kanan = new RoundedPanel(20, Color.WHITE, true);
        kanan.setBounds(500, 80, 450, 520);
        kanan.setLayout(null);
        bg.add(kanan);

        JLabel l2 = new JLabel("Pengumpulan Siswa");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l2.setForeground(UI.DARK);
        l2.setBounds(25, 18, 300, 25);
        kanan.add(l2);

        lblInfo = new JLabel("Pilih tugas di sebelah kiri.");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(25, 42, 400, 18);
        kanan.add(lblInfo);

        modelKumpul = UI.modelTabel("ID", "No", "Nama Siswa", "Tanggal Kirim", "Status");
        tabelKumpul = UI.tabel(modelKumpul);
        tabelKumpul.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelKumpul.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabelKumpul.getColumnModel().getColumn(2).setPreferredWidth(125);
        tabelKumpul.getColumnModel().getColumn(3).setPreferredWidth(110);
        JScrollPane spK = new JScrollPane(tabelKumpul);
        spK.setBounds(25, 70, 400, 370);
        kanan.add(spK);

        btnBuka = UI.tombol(kanan, "BUKA FILE", new Color(52, 152, 219), new Color(41, 128, 185),
            25, 458, 125, 38, this::aksiBuka);
        btnNilai = UI.tombol(kanan, "BERI NILAI", UI.HIJAU, UI.HIJAU_H, 160, 458, 125, 38, this::aksiNilai);

        // pasang listener terakhir, setelah semua model siap
        tabelTugas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) muatKumpul();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** @param pilihId id_tugas yang dipilih ulang setelah reload (0 = tidak ada) */
    private void muatTugas(int pilihId) {
        modelTugas.setRowCount(0);
        if (Session.idGuru == 0) return;
        String sql = "SELECT t.id_tugas, t.judul, COALESCE(k.nama_kelas,'-') AS kelas, "
                   + "DATE_FORMAT(t.deadline,'%d-%m-%Y %H:%i') AS dl, "
                   + "(SELECT COUNT(*) FROM tb_pengumpulan p WHERE p.id_tugas = t.id_tugas) AS kumpul, "
                   + "(SELECT COUNT(*) FROM tb_siswa s WHERE s.id_kelas = t.id_kelas) AS total "
                   + "FROM tb_tugas t LEFT JOIN tb_kelas k ON t.id_kelas = k.id_kelas "
                   + "WHERE t.id_guru = ? ORDER BY t.deadline DESC";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        modelTugas.addRow(new Object[]{rs.getInt("id_tugas"), no++, rs.getString("judul"),
                            rs.getString("kelas"), rs.getString("dl"),
                            rs.getInt("kumpul") + "/" + rs.getInt("total")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        if (pilihId != 0) {
            for (int i = 0; i < modelTugas.getRowCount(); i++) {
                if ((Integer) modelTugas.getValueAt(i, 0) == pilihId) {
                    tabelTugas.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private void muatKumpul() {
        modelKumpul.setRowCount(0);
        int idTugas = UI.idTerpilih(tabelTugas, modelTugas);
        if (idTugas == 0) {
            lblInfo.setText("Pilih tugas di sebelah kiri.");
            return;
        }
        lblInfo.setText("Tugas: " + UI.teksTerpilih(tabelTugas, modelTugas, 2));
        String sql = "SELECT p.id_pengumpulan, s.nama_siswa, "
                   + "DATE_FORMAT(p.tanggal_kumpul,'%d-%m-%Y %H:%i') AS tgl, p.nilai, p.status, "
                   + "(p.tanggal_kumpul > t.deadline) AS telat "
                   + "FROM tb_pengumpulan p "
                   + "JOIN tb_siswa s ON p.id_siswa = s.id_siswa "
                   + "JOIN tb_tugas t ON p.id_tugas = t.id_tugas "
                   + "WHERE p.id_tugas = ? AND t.id_guru = ? ORDER BY p.tanggal_kumpul";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idTugas);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        boolean sudah = "Sudah Dinilai".equals(rs.getString("status"));
                        String tgl = rs.getString("tgl") + (rs.getBoolean("telat") ? " (telat)" : "");
                        modelKumpul.addRow(new Object[]{rs.getInt("id_pengumpulan"), no++,
                            rs.getString("nama_siswa"), tgl,
                            sudah ? "Sudah Dinilai (" + rapikan(rs.getBigDecimal("nilai")) + ")" : "Belum Dinilai"});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    // ------------------------------------------------------------------
    //  AKSI - TUGAS
    // ------------------------------------------------------------------
    private void aksiBuat() {
        DialogTugas d = new DialogTugas(0);
        d.setVisible(true);
        if (d.tersimpan) muatTugas(0);
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabelTugas, modelTugas);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu tugas yang ingin diubah di tabel.");
            return;
        }
        DialogTugas d = new DialogTugas(id);
        d.setVisible(true);
        if (d.tersimpan) muatTugas(id);
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabelTugas, modelTugas);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu tugas yang ingin dihapus di tabel.");
            return;
        }
        String judul = UI.teksTerpilih(tabelTugas, modelTugas, 2);
        String jmlKumpul = UI.teksTerpilih(tabelTugas, modelTugas, 5).split("/")[0];
        if (!UI.konfirmasiHapus(this,
                "Hapus tugas \"" + judul + "\"?\n\n"
              + jmlKumpul + " pengumpulan siswa beserta nilainya juga akan dihapus.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        List<String> files = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT file_tugas FROM tb_tugas WHERE id_tugas=? AND id_guru=? "
                  + "UNION ALL "
                  + "SELECT p.file_jawaban FROM tb_pengumpulan p JOIN tb_tugas t ON p.id_tugas=t.id_tugas "
                  + "WHERE p.id_tugas=? AND t.id_guru=?")) {
                ps.setInt(1, id);
                ps.setInt(2, Session.idGuru);
                ps.setInt(3, id);
                ps.setInt(4, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getString(1) != null) files.add(rs.getString(1));
                    }
                }
            }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM tb_nilai WHERE id_tugas=? AND jenis_nilai='Tugas' AND id_guru=?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, Session.idGuru);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM tb_tugas WHERE id_tugas=? AND id_guru=?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, Session.idGuru);
                    ps.executeUpdate();
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
        for (String f : files) Storage.hapus(f);
        muatTugas(0);
        JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus.");
    }

    // ------------------------------------------------------------------
    //  AKSI - PENGUMPULAN
    // ------------------------------------------------------------------
    private void aksiBuka() {
        int idP = UI.idTerpilih(tabelKumpul, modelKumpul);
        if (idP == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu siswa di tabel pengumpulan.");
            return;
        }
        String path = null;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT p.file_jawaban FROM tb_pengumpulan p JOIN tb_tugas t ON p.id_tugas=t.id_tugas "
                  + "WHERE p.id_pengumpulan=? AND t.id_guru=?")) {
                ps.setInt(1, idP);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) path = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        if (path == null || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Siswa ini tidak melampirkan file.");
            return;
        }
        Storage.buka(this, path);
    }

    /** 85.00 -> "85", 87.50 -> "87.5" */
    private static String rapikan(BigDecimal n) {
        if (n == null) return "0";
        BigDecimal r = n.stripTrailingZeros();
        return (r.scale() < 0 ? r.setScale(0) : r).toPlainString();
    }

    private void aksiNilai() {
        int idP = UI.idTerpilih(tabelKumpul, modelKumpul);
        if (idP == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu siswa di tabel pengumpulan.");
            return;
        }
        int idTugas = UI.idTerpilih(tabelTugas, modelTugas);
        String nama = UI.teksTerpilih(tabelKumpul, modelKumpul, 2);
        String judul = UI.teksTerpilih(tabelTugas, modelTugas, 2);

        int idSiswa = 0, idMapel = 0;
        String nilaiLama = "";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT p.id_siswa, p.nilai, p.status, t.id_mapel FROM tb_pengumpulan p "
                  + "JOIN tb_tugas t ON p.id_tugas=t.id_tugas WHERE p.id_pengumpulan=? AND t.id_guru=?")) {
                ps.setInt(1, idP);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    idSiswa = rs.getInt("id_siswa");
                    idMapel = rs.getInt("id_mapel"); // 0 jika NULL
                    if ("Sudah Dinilai".equals(rs.getString("status"))) {
                        nilaiLama = rapikan(rs.getBigDecimal("nilai"));
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        Object in = JOptionPane.showInputDialog(this, "Nilai untuk " + nama + " (0 - 100):",
            "Beri Nilai", JOptionPane.PLAIN_MESSAGE, null, null, nilaiLama);
        if (in == null) return;
        String s = in.toString().trim().replace(',', '.');
        if (!s.matches("\\d{1,3}(\\.\\d{1,2})?") || new BigDecimal(s).compareTo(new BigDecimal("100")) > 0) {
            UI.peringatan(this, "Nilai harus berupa angka 0 - 100 (maks. 2 angka desimal).");
            return;
        }
        BigDecimal nilai = new BigDecimal(s);

        // tb_pengumpulan + tb_nilai diperbarui bersama dalam satu transaksi
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_pengumpulan p JOIN tb_tugas t ON p.id_tugas=t.id_tugas "
                      + "SET p.nilai=?, p.status='Sudah Dinilai' WHERE p.id_pengumpulan=? AND t.id_guru=?")) {
                    ps.setBigDecimal(1, nilai);
                    ps.setInt(2, idP);
                    ps.setInt(3, Session.idGuru);
                    ps.executeUpdate();
                }
                int terupdate;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_nilai SET nilai=?, id_guru=?, id_mapel=?, tanggal_input=NOW() "
                      + "WHERE id_siswa=? AND id_tugas=? AND jenis_nilai='Tugas'")) {
                    ps.setBigDecimal(1, nilai);
                    ps.setInt(2, Session.idGuru);
                    if (idMapel == 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, idMapel);
                    ps.setInt(4, idSiswa);
                    ps.setInt(5, idTugas);
                    terupdate = ps.executeUpdate();
                }
                if (terupdate == 0) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_nilai (id_siswa, id_guru, id_mapel, id_tugas, jenis_nilai, nilai, keterangan) "
                          + "VALUES (?,?,?,?,'Tugas',?,?)")) {
                        ps.setInt(1, idSiswa);
                        ps.setInt(2, Session.idGuru);
                        if (idMapel == 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, idMapel);
                        ps.setInt(4, idTugas);
                        ps.setBigDecimal(5, nilai);
                        ps.setString(6, judul.length() > 100 ? judul.substring(0, 100) : judul);
                        ps.executeUpdate();
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
        muatTugas(idTugas); // refresh dua tabel (memicu muatKumpul)
        JOptionPane.showMessageDialog(this, "Nilai " + nama + " berhasil disimpan.");
    }

    // ==================================================================
    //  DIALOG BUAT / UBAH TUGAS
    // ==================================================================
    private class DialogTugas extends JDialog {

        private final int idEdit;
        private boolean tersimpan = false;
        private JTextField txtJudul, txtDeadline, txtFile;
        private JTextArea txtDesk;
        private JComboBox<UI.Item> cmbMapel, cmbKelas;
        private File fileBaru = null;
        private String fileLama = null;

        DialogTugas(int idEdit) {
            super(FrmTugasGuru.this, idEdit == 0 ? "Buat Tugas Baru" : "Ubah Tugas", true);
            this.idEdit = idEdit;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(350, 500));
            setContentPane(p);

            txtJudul = UI.field(p, "Judul Tugas *", 12);
            txtDeadline = UI.field(p, "Deadline * (format: 2026-10-31 23:59)", 68);
            cmbMapel = new JComboBox<>();
            UI.combo(p, "Mata Pelajaran *", 124, cmbMapel);
            cmbKelas = new JComboBox<>();
            UI.combo(p, "Kelas *", 180, cmbKelas);
            txtDesk = UI.area(p, "Deskripsi / Instruksi", 236, 80);

            UI.label(p, "Lampiran (opsional, maks. 25 MB)", 345);
            txtFile = new JTextField();
            txtFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtFile.setEditable(false);
            txtFile.setBounds(25, 362, 190, 28);
            txtFile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
            p.add(txtFile);
            UI.tombol(p, "PILIH FILE", UI.NAVY, new Color(10, 36, 66), 225, 362, 100, 28, this::pilihFile);

            UI.tombol(p, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 430, 145, 42, this::simpan);
            UI.tombol(p, "BATAL", UI.ABU, UI.ABU_H, 180, 430, 145, 42, this::dispose);

            muatPilihan();
            if (idEdit == 0) {
                txtDeadline.setText(LocalDateTime.now().plusDays(7).withHour(23).withMinute(59)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                muatData();
            }
            pack();
            setLocationRelativeTo(FrmTugasGuru.this);
        }

        private void muatPilihan() {
            cmbMapel.removeAllItems();
            cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
            cmbKelas.removeAllItems();
            cmbKelas.addItem(new UI.Item(0, "- Pilih kelas -"));
            int mapelGuru = 0;
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (Statement st = con.createStatement()) {
                    try (ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                        while (rs.next()) cmbMapel.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                    try (ResultSet rs = st.executeQuery("SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                        while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
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

        private void muatData() {
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT judul, deskripsi, file_tugas, DATE_FORMAT(deadline,'%Y-%m-%d %H:%i') AS dl, "
                      + "id_mapel, id_kelas FROM tb_tugas WHERE id_tugas=? AND id_guru=?")) {
                    ps.setInt(1, idEdit);
                    ps.setInt(2, Session.idGuru);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return;
                        txtJudul.setText(rs.getString("judul"));
                        txtDeadline.setText(rs.getString("dl"));
                        txtDesk.setText(rs.getString("deskripsi") == null ? "" : rs.getString("deskripsi"));
                        fileLama = rs.getString("file_tugas");
                        txtFile.setText(fileLama == null ? "" : Storage.namaAsli(fileLama));
                        UI.pilihItem(cmbMapel, rs.getInt("id_mapel"));
                        UI.pilihItem(cmbKelas, rs.getInt("id_kelas"));
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
            }
        }

        private void pilihFile() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pilih lampiran tugas");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter(
                "Dokumen & media (pdf, doc, docx, ppt, pptx, xls, xlsx, txt, jpg, png, mp4, zip)",
                "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "jpg", "jpeg", "png", "mp4", "zip"));
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            if (!Storage.ekstensiDiizinkan(f.getName())) {
                UI.peringatan(this, "Jenis file tidak diizinkan.");
                return;
            }
            if (f.length() > Storage.MAKS_BYTE) {
                UI.peringatan(this, "Ukuran file melebihi 25 MB.");
                return;
            }
            fileBaru = f;
            txtFile.setText(f.getName());
        }

        private LocalDateTime parseDeadline(String s) {
            if (!s.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) return null;
            try {
                return LocalDateTime.parse(s.replace(' ', 'T'));
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        private String validasi() {
            String judul = txtJudul.getText().trim();
            if (judul.isEmpty()) return "Judul tugas wajib diisi.";
            if (judul.length() > 150) return "Judul maksimal 150 karakter.";
            LocalDateTime dl = parseDeadline(txtDeadline.getText().trim());
            if (dl == null) return "Deadline harus berformat 2026-10-31 23:59 dan tanggalnya valid.";
            if (idEdit == 0 && dl.isBefore(LocalDateTime.now())) return "Deadline tidak boleh di masa lalu.";
            if (((UI.Item) cmbMapel.getSelectedItem()).id == 0) return "Pilih mata pelajaran.";
            if (((UI.Item) cmbKelas.getSelectedItem()).id == 0) return "Pilih kelas.";
            if (fileBaru != null && !fileBaru.exists()) return "File yang dipilih sudah tidak ada. Pilih ulang.";
            return null;
        }

        private void simpan() {
            String err = validasi();
            if (err != null) { UI.peringatan(this, err); return; }

            String judul = txtJudul.getText().trim();
            String desk = txtDesk.getText().trim();
            Timestamp dl = Timestamp.valueOf(parseDeadline(txtDeadline.getText().trim()));
            int idMapel = ((UI.Item) cmbMapel.getSelectedItem()).id;
            int idKelas = ((UI.Item) cmbKelas.getSelectedItem()).id;

            // 1) salin lampiran lebih dulu (jika ada file baru)
            String relBaru = null;
            if (fileBaru != null) {
                try {
                    relBaru = Storage.simpan(fileBaru, "tugas");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan file:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String relSimpan = (relBaru != null) ? relBaru : fileLama;

            // 2) simpan ke database; jika gagal, salinan file dibuang lagi
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) {
                    Storage.hapus(relBaru);
                    UI.koneksiGagal(this);
                    return;
                }
                if (idEdit == 0) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_tugas (judul, deskripsi, file_tugas, deadline, id_guru, id_mapel, id_kelas) "
                          + "VALUES (?,?,?,?,?,?,?)")) {
                        ps.setString(1, judul);
                        ps.setString(2, desk.isEmpty() ? null : desk);
                        ps.setString(3, relSimpan);
                        ps.setTimestamp(4, dl);
                        ps.setInt(5, Session.idGuru);
                        ps.setInt(6, idMapel);
                        ps.setInt(7, idKelas);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_tugas SET judul=?, deskripsi=?, file_tugas=?, deadline=?, id_mapel=?, id_kelas=? "
                          + "WHERE id_tugas=? AND id_guru=?")) {
                        ps.setString(1, judul);
                        ps.setString(2, desk.isEmpty() ? null : desk);
                        ps.setString(3, relSimpan);
                        ps.setTimestamp(4, dl);
                        ps.setInt(5, idMapel);
                        ps.setInt(6, idKelas);
                        ps.setInt(7, idEdit);
                        ps.setInt(8, Session.idGuru);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                Storage.hapus(relBaru);
                UI.errorDb(this, ex);
                return;
            }

            // 3) lampiran lama dibuang jika sudah diganti
            if (relBaru != null && idEdit != 0 && fileLama != null) Storage.hapus(fileLama);

            tersimpan = true;
            JOptionPane.showMessageDialog(this, idEdit == 0
                ? "Tugas berhasil dibuat!" : "Tugas berhasil diperbarui!");
            dispose();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmTugasGuru().setVisible(true));
    }
}