package lms;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Unduh Materi (siswa): daftar materi kelas, buka file, dan simpan salinan ke folder pilihan siswa.
 */
public class FrmMateriSiswa extends JFrame {

    private static final String SEMUA_MAPEL = "Semua mata pelajaran";
    private static final DateTimeFormatter FMT_TGL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FMT_LENGKAP = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static class Materi {
        int id;
        String judul, bab, deskripsi, file, mapel, guru;
        LocalDateTime upload;

        boolean baru() {
            return upload != null && upload.isAfter(LocalDateTime.now().minusDays(7));
        }
    }

    private final Map<Integer, Materi> daftar = new LinkedHashMap<>();

    // daftar (kiri)
    private JTextField txtCari;
    private JComboBox<String> cmbMapel;
    private DefaultTableModel model;
    private JTable tabel;
    private JLabel lblRingkasan;

    // detail (kanan)
    private JLabel lblJudul, lblMeta, lblBab, lblUpload, lblFile, lblHint;
    private JTextArea txtDeskripsi;
    private RoundedButton btnBuka, btnUnduh;

    private boolean sedangMuat = false;

    public FrmMateriSiswa() {
        initUI();
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            kosongkanDetail();
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa atau belum masuk ke sebuah kelas.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatPilihanMapel();
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Unduh Materi", "UNDUH MATERI",
            () -> new FrmMenuSiswa().setVisible(true));

        // ---------- DAFTAR MATERI (kiri) ----------
        RoundedPanel panelList = new RoundedPanel(20, Color.WHITE, true);
        panelList.setBounds(40, 80, 540, 520);
        panelList.setLayout(null);
        bg.add(panelList);

        JLabel judulList = new JLabel("Materi Kelas");
        judulList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judulList.setForeground(UI.DARK);
        judulList.setBounds(25, 18, 260, 25);
        panelList.add(judulList);

        model = UI.modelTabel("ID", "No", "Mapel", "Judul Materi", "BAB", "Diunggah");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(32);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(125);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(93);
        UI.tambahTabel(panelList, tabel);
        txtCari = UI.kolomCari(panelList, this::muatTabel);

        cmbMapel = new JComboBox<>(new String[]{SEMUA_MAPEL});
        cmbMapel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMapel.setBackground(Color.WHITE);
        cmbMapel.setBounds(25, 455, 210, 28);
        cmbMapel.addActionListener(e -> { if (!sedangMuat) muatTabel(); });
        panelList.add(cmbMapel);

        lblRingkasan = new JLabel("", SwingConstants.RIGHT);
        lblRingkasan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRingkasan.setForeground(Color.GRAY);
        lblRingkasan.setBounds(245, 455, 270, 28);
        panelList.add(lblRingkasan);

        // ---------- DETAIL MATERI (kanan) ----------
        RoundedPanel panelDetail = new RoundedPanel(20, Color.WHITE, true);
        panelDetail.setBounds(600, 80, 350, 520);
        panelDetail.setLayout(null);
        bg.add(panelDetail);

        UI.judulForm(panelDetail, "Detail Materi");

        lblJudul = new JLabel();
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblJudul.setForeground(UI.NAVY);
        lblJudul.setVerticalAlignment(SwingConstants.TOP);
        lblJudul.setBounds(25, 48, 300, 42);
        panelDetail.add(lblJudul);

        lblMeta = new JLabel();
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta.setForeground(Color.GRAY);
        lblMeta.setBounds(25, 92, 300, 18);
        panelDetail.add(lblMeta);

        lblBab = new JLabel();
        lblBab.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBab.setForeground(UI.DARK);
        lblBab.setBounds(25, 114, 300, 18);
        panelDetail.add(lblBab);

        lblUpload = new JLabel();
        lblUpload.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUpload.setForeground(UI.DARK);
        lblUpload.setBounds(25, 132, 300, 18);
        panelDetail.add(lblUpload);

        UI.label(panelDetail, "Deskripsi", 160);
        txtDeskripsi = new JTextArea();
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDeskripsi.setEditable(false);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        JScrollPane spDesk = new JScrollPane(txtDeskripsi);
        spDesk.setBounds(25, 177, 300, 110);
        spDesk.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spDesk);

        UI.label(panelDetail, "File Materi", 300);
        lblFile = new JLabel();
        lblFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFile.setForeground(Color.DARK_GRAY);
        lblFile.setVerticalAlignment(SwingConstants.TOP);
        lblFile.setBounds(25, 319, 300, 62);
        panelDetail.add(lblFile);

        btnBuka = UI.tombol(panelDetail, "BUKA FILE", new Color(52, 152, 219), new Color(41, 128, 185),
            25, 395, 145, 45, this::aksiBuka);
        btnUnduh = UI.tombol(panelDetail, "UNDUH", UI.HIJAU, UI.HIJAU_H,
            180, 395, 145, 45, this::aksiUnduh);

        lblHint = new JLabel();
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setBounds(25, 448, 300, 34);
        panelDetail.add(lblHint);

        // dipasang paling akhir supaya semua komponen detail sudah ada
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!sedangMuat && !e.getValueIsAdjusting()) tampilDetail();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** Isi combo filter dengan mapel yang benar-benar punya materi di kelas ini. */
    private void muatPilihanMapel() {
        List<String> mapel = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT DISTINCT mp.nama_mapel FROM tb_materi m "
                  + "JOIN tb_mapel mp ON m.id_mapel = mp.id_mapel "
                  + "WHERE m.id_kelas = ? ORDER BY mp.nama_mapel")) {
                ps.setInt(1, Session.idKelas);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) mapel.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        sedangMuat = true;
        for (String m : mapel) cmbMapel.addItem(m);
        sedangMuat = false;
    }

    private void muatTabel() {
        if (Session.idSiswa == 0 || Session.idKelas == 0) return;

        int pilihSebelumnya = UI.idTerpilih(tabel, model);
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT m.id_materi, m.judul, m.bab, m.deskripsi, m.file_materi, m.tanggal_upload, "
                   + "COALESCE(mp.nama_mapel,'-') AS mapel, COALESCE(g.nama_guru,'-') AS guru "
                   + "FROM tb_materi m "
                   + "LEFT JOIN tb_mapel mp ON m.id_mapel = mp.id_mapel "
                   + "LEFT JOIN tb_guru g ON m.id_guru = g.id_guru "
                   + "WHERE m.id_kelas = ? AND (m.judul LIKE ? OR mp.nama_mapel LIKE ? OR m.bab LIKE ?) "
                   + "ORDER BY m.tanggal_upload DESC, m.id_materi DESC";

        List<Materi> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idKelas);
                ps.setString(2, kata);
                ps.setString(3, kata);
                ps.setString(4, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Materi m = new Materi();
                        m.id = rs.getInt("id_materi");
                        m.judul = rs.getString("judul");
                        m.bab = rs.getString("bab");
                        m.deskripsi = rs.getString("deskripsi");
                        m.file = rs.getString("file_materi");
                        Timestamp tu = rs.getTimestamp("tanggal_upload");
                        m.upload = tu == null ? null : tu.toLocalDateTime();
                        m.mapel = rs.getString("mapel");
                        m.guru = rs.getString("guru");
                        hasil.add(m);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        String mapelFilter = cmbMapel.getSelectedItem() == null ? SEMUA_MAPEL : cmbMapel.getSelectedItem().toString();

        daftar.clear();
        sedangMuat = true;
        model.setRowCount(0);
        int no = 1, barisPilih = -1, baru = 0;
        for (Materi m : hasil) {
            if (!SEMUA_MAPEL.equals(mapelFilter) && !mapelFilter.equals(m.mapel)) continue;
            daftar.put(m.id, m);
            if (m.baru()) baru++;
            if (m.id == pilihSebelumnya) barisPilih = model.getRowCount();
            model.addRow(new Object[]{m.id, no++, m.mapel,
                m.judul + (m.baru() ? "  (BARU)" : ""),
                m.bab == null || m.bab.isEmpty() ? "-" : m.bab,
                m.upload == null ? "-" : FMT_TGL.format(m.upload)});
        }
        sedangMuat = false;

        lblRingkasan.setText(daftar.size() + " materi" + (baru > 0 ? "  |  " + baru + " baru (7 hari terakhir)" : ""));

        if (barisPilih == -1 && pilihSebelumnya == 0 && model.getRowCount() > 0) barisPilih = 0;
        if (barisPilih >= 0) {
            tabel.setRowSelectionInterval(barisPilih, barisPilih); // memicu tampilDetail()
        } else {
            kosongkanDetail();
        }
    }

    // ------------------------------------------------------------------
    //  DETAIL
    // ------------------------------------------------------------------
    private void kosongkanDetail() {
        lblJudul.setText("<html><body style='width:300px'><i>Pilih materi di daftar untuk melihat detailnya.</i></body></html>");
        lblMeta.setText("");
        lblBab.setText("");
        lblUpload.setText("");
        txtDeskripsi.setText("");
        lblFile.setText("");
        lblHint.setText("");
        btnBuka.setEnabled(false);
        btnUnduh.setEnabled(false);
    }

    private void tampilDetail() {
        Materi m = daftar.get(UI.idTerpilih(tabel, model));
        if (m == null) { kosongkanDetail(); return; }

        lblJudul.setText("<html><body style='width:300px'>" + esc(m.judul) + "</body></html>");
        lblMeta.setText(m.mapel + "  \u2022  " + m.guru);
        lblBab.setText("BAB: " + (m.bab == null || m.bab.isEmpty() ? "-" : m.bab));
        lblUpload.setText("Diunggah: " + (m.upload == null ? "-" : FMT_LENGKAP.format(m.upload)));

        txtDeskripsi.setText(m.deskripsi == null || m.deskripsi.trim().isEmpty()
            ? "(Tidak ada deskripsi)" : m.deskripsi);
        txtDeskripsi.setCaretPosition(0);

        File f = Storage.file(m.file);
        boolean ada = f != null && f.isFile();
        if (m.file == null || m.file.isEmpty()) {
            lblFile.setText("<html><i>Guru tidak melampirkan file untuk materi ini.</i></html>");
        } else if (!ada) {
            lblFile.setText("<html><b>" + esc(Storage.namaAsli(m.file)) + "</b><br>"
                + "<font color='#c0392b'>File tidak ditemukan di folder penyimpanan.</font></html>");
        } else {
            lblFile.setText("<html><b>" + esc(Storage.namaAsli(m.file)) + "</b><br>"
                + Storage.ekstensi(m.file).toUpperCase() + "  \u2022  " + fmtUkuran(f.length()) + "</html>");
        }
        btnBuka.setEnabled(ada);
        btnUnduh.setEnabled(ada);

        if (m.file == null || m.file.isEmpty()) {
            lblHint.setText("Belum ada file yang bisa dibuka atau diunduh.");
        } else if (!ada) {
            lblHint.setText("Hubungi guru atau administrator untuk memeriksa file ini.");
        } else {
            lblHint.setText("UNDUH menyimpan salinan file ke folder pilihanmu.");
        }
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private Materi materiTerpilih() {
        Materi m = daftar.get(UI.idTerpilih(tabel, model));
        if (m == null) JOptionPane.showMessageDialog(this, "Pilih dulu materi di daftar.");
        return m;
    }

    private void aksiBuka() {
        Materi m = materiTerpilih();
        if (m == null) return;
        Storage.buka(this, m.file);
    }

    private void aksiUnduh() {
        Materi m = materiTerpilih();
        if (m == null) return;

        File sumber = Storage.file(m.file);
        if (sumber == null || !sumber.isFile()) {
            UI.peringatan(this, "File materi tidak ditemukan di folder penyimpanan.");
            tampilDetail();
            return;
        }

        String nama = Storage.namaAsli(m.file);
        File downloads = new File(System.getProperty("user.home"), "Downloads");
        JFileChooser fc = new JFileChooser(downloads.isDirectory() ? downloads : new File(System.getProperty("user.home")));
        fc.setDialogTitle("Simpan materi");
        fc.setSelectedFile(new File(fc.getCurrentDirectory(), nama));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File tujuan = fc.getSelectedFile();
        // jaga ekstensi asli supaya file tetap bisa dibuka dengan aplikasi yang tepat
        String ext = Storage.ekstensi(nama);
        if (!ext.isEmpty() && Storage.ekstensi(tujuan.getName()).isEmpty()) {
            tujuan = new File(tujuan.getParentFile(), tujuan.getName() + "." + ext);
        }

        try {
            if (tujuan.getCanonicalFile().equals(sumber.getCanonicalFile())) {
                UI.peringatan(this, "Pilih lokasi lain, tidak bisa menyimpan di atas file sumber.");
                return;
            }
            if (tujuan.exists() && JOptionPane.showConfirmDialog(this,
                    "File \"" + tujuan.getName() + "\" sudah ada di folder tersebut.\nTimpa?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

            Files.copy(sumber.toPath(), tujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int pilih = JOptionPane.showOptionDialog(this,
            "Materi berhasil diunduh ke:\n" + tujuan.getAbsolutePath(),
            "Unduh Selesai", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
            new String[]{"Buka Folder", "Tutup"}, "Tutup");
        if (pilih == 0) {
            try {
                Desktop.getDesktop().open(tujuan.getParentFile());
            } catch (Exception ex) {
                UI.peringatan(this, "Tidak bisa membuka folder:\n" + tujuan.getParentFile());
            }
        }
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static String fmtUkuran(long byteCount) {
        if (byteCount < 1024) return byteCount + " B";
        if (byteCount < 1024L * 1024) return String.format("%.1f KB", byteCount / 1024.0);
        return String.format("%.1f MB", byteCount / (1024.0 * 1024));
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmMateriSiswa().setVisible(true));
    }
}