package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tugas Saya (siswa): melihat tugas kelas, membuka file soal,
 * mengumpulkan / mengirim ulang jawaban, dan melihat nilai.
 */
public class FrmTugasSiswa extends JFrame {

    // kunci status (dipakai untuk filter, warna, dan teks tabel)
    private static final String BELUM = "Belum Dikumpulkan";
    private static final String TERKUMPUL = "Terkumpul";
    private static final String DINILAI = "Dinilai";
    private static final String TERLEWAT = "Terlewat";

    private static final String[] FILTER_TEKS = {"Semua status", "Belum dikumpulkan", "Terkumpul", "Dinilai", "Terlewat"};
    private static final String[] FILTER_KUNCI = {null, BELUM, TERKUMPUL, DINILAI, TERLEWAT};

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /** Satu baris tugas + pengumpulan milik siswa yang login. */
    private static class Tugas {
        int id;
        String judul, deskripsi, fileTugas, mapel, guru;
        LocalDateTime deadline;
        int idPengumpulan;            // 0 = belum mengumpulkan
        String fileJawaban, statusDb;
        LocalDateTime tglKumpul;
        double nilai;

        boolean sudahKumpul() { return idPengumpulan != 0; }
        boolean sudahDinilai() { return sudahKumpul() && "Sudah Dinilai".equals(statusDb); }
        boolean lewatDeadline() { return deadline != null && deadline.isBefore(LocalDateTime.now()); }

        String kunci() {
            if (sudahDinilai()) return DINILAI;
            if (sudahKumpul()) return TERKUMPUL;
            if (lewatDeadline()) return TERLEWAT;
            return BELUM;
        }

        int urutan() {
            switch (kunci()) {
                case BELUM: return 0;
                case TERKUMPUL: return 1;
                case DINILAI: return 2;
                default: return 3;
            }
        }
    }

    private final Map<Integer, Tugas> daftar = new LinkedHashMap<>();

    // daftar (kiri)
    private JTextField txtCari;
    private JComboBox<String> cmbFilter;
    private DefaultTableModel model;
    private JTable tabel;
    private JLabel lblRingkasan;

    // detail (kanan)
    private JLabel lblJudul, lblMeta, lblDeadline, lblSisa, lblPengumpulan, lblHint;
    private JTextArea txtDeskripsi;
    private JTextField txtFile;
    private RoundedButton btnBukaTugas, btnPilih, btnKumpul, btnBukaJawaban;

    private int idDetail = 0;        // tugas yang sedang tampil di panel detail
    private File fileBaru = null;    // file jawaban yang dipilih tapi belum dikirim
    private boolean sedangMuat = false;

    public FrmTugasSiswa() {
        initUI();
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            kosongkanDetail();
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa atau belum masuk ke sebuah kelas.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Tugas Saya", "TUGAS SAYA",
            () -> new FrmMenuSiswa().setVisible(true));

        // ---------- DAFTAR TUGAS (kiri) ----------
        RoundedPanel panelList = new RoundedPanel(20, Color.WHITE, true);
        panelList.setBounds(40, 80, 540, 520);
        panelList.setLayout(null);
        bg.add(panelList);

        JLabel judulList = new JLabel("Daftar Tugas Kelas");
        judulList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judulList.setForeground(UI.DARK);
        judulList.setBounds(25, 18, 260, 25);
        panelList.add(judulList);

        model = UI.modelTabel("ID", "No", "Mapel", "Judul Tugas", "Deadline", "Status");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(32);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(95);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(145);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(115);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(103);
        tabel.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setForeground(warnaStatus(String.valueOf(v)));
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                return comp;
            }
        });
        UI.tambahTabel(panelList, tabel);
        txtCari = UI.kolomCari(panelList, this::muatTabel);

        cmbFilter = new JComboBox<>(FILTER_TEKS);
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBounds(25, 455, 170, 28);
        cmbFilter.addActionListener(e -> muatTabel());
        panelList.add(cmbFilter);

        lblRingkasan = new JLabel("", SwingConstants.RIGHT);
        lblRingkasan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRingkasan.setForeground(Color.GRAY);
        lblRingkasan.setBounds(205, 455, 310, 28);
        panelList.add(lblRingkasan);

        // ---------- DETAIL TUGAS (kanan) ----------
        RoundedPanel panelDetail = new RoundedPanel(20, Color.WHITE, true);
        panelDetail.setBounds(600, 80, 350, 520);
        panelDetail.setLayout(null);
        bg.add(panelDetail);

        UI.judulForm(panelDetail, "Detail Tugas");

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

        lblDeadline = new JLabel();
        lblDeadline.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDeadline.setForeground(UI.DARK);
        lblDeadline.setBounds(25, 114, 300, 18);
        panelDetail.add(lblDeadline);

        lblSisa = new JLabel();
        lblSisa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSisa.setBounds(25, 132, 300, 18);
        panelDetail.add(lblSisa);

        UI.label(panelDetail, "Deskripsi", 158);
        txtDeskripsi = new JTextArea();
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDeskripsi.setEditable(false);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        JScrollPane spDesk = new JScrollPane(txtDeskripsi);
        spDesk.setBounds(25, 175, 300, 66);
        spDesk.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spDesk);

        btnBukaTugas = UI.tombol(panelDetail, "BUKA FILE TUGAS", UI.NAVY, new Color(10, 36, 66),
            25, 250, 300, 32, this::aksiBukaTugas);

        UI.label(panelDetail, "Pengumpulan Saya", 296);
        lblPengumpulan = new JLabel();
        lblPengumpulan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPengumpulan.setForeground(Color.DARK_GRAY);
        lblPengumpulan.setVerticalAlignment(SwingConstants.TOP);
        lblPengumpulan.setBounds(25, 315, 300, 52);
        panelDetail.add(lblPengumpulan);

        UI.label(panelDetail, "File Jawaban", 374);
        txtFile = new JTextField();
        txtFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFile.setEditable(false);
        txtFile.setBounds(25, 391, 190, 28);
        txtFile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        panelDetail.add(txtFile);
        btnPilih = UI.tombol(panelDetail, "PILIH FILE", UI.NAVY, new Color(10, 36, 66),
            225, 391, 100, 28, this::pilihFile);

        btnKumpul = UI.tombol(panelDetail, "KUMPULKAN", UI.HIJAU, UI.HIJAU_H,
            25, 430, 145, 40, this::aksiKumpul);
        btnBukaJawaban = UI.tombol(panelDetail, "BUKA JAWABAN", new Color(52, 152, 219), new Color(41, 128, 185),
            180, 430, 145, 40, this::aksiBukaJawaban);

        lblHint = new JLabel();
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setBounds(25, 478, 300, 30);
        panelDetail.add(lblHint);

        // dipasang paling akhir supaya semua komponen detail sudah ada
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!sedangMuat && !e.getValueIsAdjusting()) tampilDetail();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatTabel() {
        if (Session.idSiswa == 0 || Session.idKelas == 0) return;

        int pilihSebelumnya = idDetail;
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT t.id_tugas, t.judul, t.deskripsi, t.file_tugas, t.deadline, "
                   + "COALESCE(mp.nama_mapel,'-') AS mapel, COALESCE(g.nama_guru,'-') AS guru, "
                   + "p.id_pengumpulan, p.file_jawaban, p.tanggal_kumpul, p.nilai, p.status "
                   + "FROM tb_tugas t "
                   + "LEFT JOIN tb_mapel mp ON t.id_mapel = mp.id_mapel "
                   + "LEFT JOIN tb_guru g ON t.id_guru = g.id_guru "
                   + "LEFT JOIN tb_pengumpulan p ON p.id_tugas = t.id_tugas AND p.id_siswa = ? "
                   + "WHERE t.id_kelas = ? AND (t.judul LIKE ? OR mp.nama_mapel LIKE ?)";

        List<Tugas> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idSiswa);
                ps.setInt(2, Session.idKelas);
                ps.setString(3, kata);
                ps.setString(4, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Tugas t = new Tugas();
                        t.id = rs.getInt("id_tugas");
                        t.judul = rs.getString("judul");
                        t.deskripsi = rs.getString("deskripsi");
                        t.fileTugas = rs.getString("file_tugas");
                        Timestamp dl = rs.getTimestamp("deadline");
                        t.deadline = dl == null ? null : dl.toLocalDateTime();
                        t.mapel = rs.getString("mapel");
                        t.guru = rs.getString("guru");
                        t.idPengumpulan = rs.getInt("id_pengumpulan"); // 0 jika NULL
                        t.fileJawaban = rs.getString("file_jawaban");
                        Timestamp tk = rs.getTimestamp("tanggal_kumpul");
                        t.tglKumpul = tk == null ? null : tk.toLocalDateTime();
                        t.nilai = rs.getDouble("nilai");
                        t.statusDb = rs.getString("status");
                        hasil.add(t);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        // belum dikumpulkan dulu (deadline terdekat di atas), terlewat paling bawah
        hasil.sort(Comparator.comparingInt(Tugas::urutan)
            .thenComparing(t -> t.deadline, Comparator.nullsLast(Comparator.naturalOrder())));

        daftar.clear();
        int belum = 0, terlewat = 0;
        for (Tugas t : hasil) {
            daftar.put(t.id, t);
            if (BELUM.equals(t.kunci())) belum++;
            if (TERLEWAT.equals(t.kunci())) terlewat++;
        }

        String kunciFilter = FILTER_KUNCI[cmbFilter.getSelectedIndex()];

        sedangMuat = true;
        model.setRowCount(0);
        int no = 1, barisPilih = -1;
        for (Tugas t : hasil) {
            if (kunciFilter != null && !kunciFilter.equals(t.kunci())) continue;
            if (t.id == pilihSebelumnya) barisPilih = model.getRowCount();
            model.addRow(new Object[]{t.id, no++, t.mapel, t.judul,
                t.deadline == null ? "-" : FMT.format(t.deadline), teksStatus(t)});
        }
        sedangMuat = false;

        lblRingkasan.setText(hasil.size() + " tugas  |  " + belum + " belum dikumpulkan  |  " + terlewat + " terlewat");

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
        idDetail = 0;
        fileBaru = null;
        txtFile.setText("");
        lblJudul.setText("<html><body style='width:300px'><i>Pilih tugas di daftar untuk melihat detailnya.</i></body></html>");
        lblMeta.setText("");
        lblDeadline.setText("");
        lblSisa.setText("");
        txtDeskripsi.setText("");
        lblPengumpulan.setText("");
        lblHint.setText("");
        btnBukaTugas.setEnabled(false);
        btnPilih.setEnabled(false);
        btnKumpul.setEnabled(false);
        btnBukaJawaban.setEnabled(false);
        btnKumpul.setText("KUMPULKAN");
    }

    private void tampilDetail() {
        int id = UI.idTerpilih(tabel, model);
        Tugas t = daftar.get(id);
        if (t == null) { kosongkanDetail(); return; }

        if (id != idDetail) { // ganti tugas -> buang file yang belum terkirim
            fileBaru = null;
            txtFile.setText("");
        }
        idDetail = id;

        lblJudul.setText("<html><body style='width:300px'>" + esc(t.judul) + "</body></html>");
        lblMeta.setText(t.mapel + "  \u2022  " + t.guru);
        lblDeadline.setText("Deadline: " + (t.deadline == null ? "Tanpa batas waktu" : FMT.format(t.deadline)));
        lblSisa.setText(sisaWaktu(t));
        boolean mendesak = t.deadline != null && !t.sudahKumpul()
            && Duration.between(LocalDateTime.now(), t.deadline).toHours() < 24;
        lblSisa.setForeground(t.sudahKumpul() ? new Color(39, 174, 96)
            : (mendesak ? new Color(192, 57, 43) : Color.GRAY));
        if (t.sudahKumpul()) lblSisa.setText("Sudah dikumpulkan");

        txtDeskripsi.setText(t.deskripsi == null || t.deskripsi.trim().isEmpty()
            ? "(Tidak ada deskripsi)" : t.deskripsi);
        txtDeskripsi.setCaretPosition(0);

        if (!t.sudahKumpul()) {
            lblPengumpulan.setText("<html><i>Belum ada jawaban yang dikumpulkan.</i></html>");
        } else {
            String nilaiTeks = t.sudahDinilai()
                ? "Nilai: <b>" + fmtNilai(t.nilai) + "</b>"
                : "Status: menunggu penilaian guru";
            lblPengumpulan.setText("<html><b>File:</b> " + esc(Storage.namaAsli(t.fileJawaban)) + "<br>"
                + "<b>Dikirim:</b> " + (t.tglKumpul == null ? "-" : FMT.format(t.tglKumpul)) + "<br>"
                + nilaiTeks + "</html>");
        }

        boolean bisaKirim = !t.sudahDinilai() && !t.lewatDeadline();
        btnBukaTugas.setEnabled(t.fileTugas != null && !t.fileTugas.isEmpty());
        btnBukaJawaban.setEnabled(t.fileJawaban != null && !t.fileJawaban.isEmpty());
        btnPilih.setEnabled(bisaKirim);
        btnKumpul.setEnabled(bisaKirim);
        btnKumpul.setText(t.sudahKumpul() ? "KIRIM ULANG" : "KUMPULKAN");

        if (t.sudahDinilai()) {
            lblHint.setText("Tugas sudah dinilai, jawaban tidak bisa diubah.");
        } else if (t.lewatDeadline()) {
            lblHint.setText("Deadline sudah lewat, pengumpulan ditutup.");
        } else if (t.sudahKumpul()) {
            lblHint.setText("Kamu bisa kirim ulang sebelum deadline. Maks. 25 MB.");
        } else {
            lblHint.setText("Format: pdf, doc, docx, jpg, png, zip, dll. Maks. 25 MB.");
        }
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void pilihFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Pilih file jawaban");
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(
            "Dokumen & gambar (pdf, doc, docx, ppt, pptx, xls, xlsx, txt, jpg, png, zip)",
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

    private void aksiBukaTugas() {
        Tugas t = daftar.get(idDetail);
        if (t == null) return;
        if (t.fileTugas == null || t.fileTugas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guru tidak melampirkan file untuk tugas ini.");
            return;
        }
        Storage.buka(this, t.fileTugas);
    }

    private void aksiBukaJawaban() {
        Tugas t = daftar.get(idDetail);
        if (t == null || t.fileJawaban == null) return;
        Storage.buka(this, t.fileJawaban);
    }

    private void aksiKumpul() {
        Tugas t = daftar.get(idDetail);
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Pilih dulu tugas yang ingin dikumpulkan.");
            return;
        }
        if (t.sudahDinilai()) {
            UI.peringatan(this, "Tugas ini sudah dinilai, jawaban tidak bisa diubah.");
            return;
        }
        if (t.lewatDeadline()) {
            UI.peringatan(this, "Deadline sudah lewat, pengumpulan ditutup.");
            muatTabel();
            return;
        }
        if (fileBaru == null) {
            UI.peringatan(this, "Pilih file jawaban terlebih dahulu.");
            return;
        }
        if (!fileBaru.exists()) {
            UI.peringatan(this, "File yang dipilih sudah tidak ada. Pilih ulang.");
            return;
        }
        String pesan = t.sudahKumpul()
            ? "Kirim ulang jawaban untuk \"" + t.judul + "\"?\nJawaban sebelumnya akan diganti."
            : "Kumpulkan jawaban untuk \"" + t.judul + "\"?";
        if (JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        // 1) salin file lebih dulu
        String rel;
        try {
            rel = Storage.simpan(fileBaru, "jawaban");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2) simpan ke database; jika gagal, salinan file dibuang lagi
        String fileLama = t.fileJawaban;
        boolean update = t.sudahKumpul();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                Storage.hapus(rel);
                UI.koneksiGagal(this);
                return;
            }
            Timestamp sekarang = Timestamp.valueOf(LocalDateTime.now());
            int baris;
            if (update) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tb_pengumpulan SET file_jawaban=?, tanggal_kumpul=?, nilai=0, status='Belum Dinilai' "
                      + "WHERE id_pengumpulan=? AND id_siswa=? AND status='Belum Dinilai'")) {
                    ps.setString(1, rel);
                    ps.setTimestamp(2, sekarang);
                    ps.setInt(3, t.idPengumpulan);
                    ps.setInt(4, Session.idSiswa);
                    baris = ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_pengumpulan (id_tugas, id_siswa, file_jawaban, tanggal_kumpul, nilai, status) "
                      + "VALUES (?,?,?,?,0,'Belum Dinilai')")) {
                    ps.setInt(1, t.id);
                    ps.setInt(2, Session.idSiswa);
                    ps.setString(3, rel);
                    ps.setTimestamp(4, sekarang);
                    baris = ps.executeUpdate();
                }
            }
            if (baris == 0) { // guru menilai tepat sebelum siswa menekan tombol
                Storage.hapus(rel);
                UI.peringatan(this, "Jawaban tidak dapat diubah karena tugas ini baru saja dinilai guru.");
                muatTabel();
                return;
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            Storage.hapus(rel);
            UI.peringatan(this, "Tugas ini sudah pernah dikumpulkan. Daftar akan dimuat ulang.");
            muatTabel();
            return;
        } catch (SQLException ex) {
            Storage.hapus(rel);
            UI.errorDb(this, ex);
            return;
        }

        // 3) file lama dibuang jika sudah diganti
        if (update && fileLama != null) Storage.hapus(fileLama);

        fileBaru = null;
        txtFile.setText("");
        JOptionPane.showMessageDialog(this, update
            ? "Jawaban berhasil dikirim ulang!" : "Jawaban berhasil dikumpulkan!");
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static String teksStatus(Tugas t) {
        String k = t.kunci();
        return DINILAI.equals(k) ? "Dinilai (" + fmtNilai(t.nilai) + ")" : k;
    }

    private static Color warnaStatus(String teks) {
        if (teks.startsWith(DINILAI)) return new Color(39, 174, 96);
        if (teks.startsWith(TERKUMPUL)) return new Color(41, 128, 185);
        if (teks.startsWith(TERLEWAT)) return new Color(192, 57, 43);
        return new Color(211, 84, 0); // belum dikumpulkan
    }

    private static String fmtNilai(double n) {
        return n == Math.rint(n) ? String.valueOf((int) n) : String.format("%.1f", n);
    }

    private static String sisaWaktu(Tugas t) {
        if (t.deadline == null) return "Tanpa batas waktu";
        Duration d = Duration.between(LocalDateTime.now(), t.deadline);
        if (d.isNegative()) return "Deadline sudah lewat";
        long jam = d.toHours();
        if (jam >= 24) return "Sisa waktu: " + (jam / 24) + " hari " + (jam % 24) + " jam";
        if (jam > 0) return "Sisa waktu: " + jam + " jam " + (d.toMinutes() % 60) + " menit";
        return "Sisa waktu: " + d.toMinutes() + " menit";
    }

    /** Teks dari guru bisa mengandung < atau &, jadi di-escape sebelum masuk label HTML. */
    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmTugasSiswa().setVisible(true));
    }
}