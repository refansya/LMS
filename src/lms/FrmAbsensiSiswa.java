package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Absensi Saya (siswa): rekap Hadir/Sakit/Izin/Alpha, riwayat kehadiran per tanggal,
 * dan absen mandiri dengan foto bukti (untuk belajar dari rumah).
 * Absen mandiri menunggu verifikasi guru; guru menyetujuinya dengan menyimpan presensi kelas.
 */
public class FrmAbsensiSiswa extends JFrame {

    private static final String SEMUA = "Semua bulan";
    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter FMT_HARI = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy", ID);
    private static final DateTimeFormatter FMT_BULAN = DateTimeFormatter.ofPattern("MMMM yyyy", ID);

    // jendela waktu absen mandiri (ubah sesuai kebijakan sekolah)
    private static final LocalTime JAM_BUKA = LocalTime.of(5, 0);
    private static final LocalTime JAM_TUTUP = LocalTime.of(13, 0);
    private static final long MAKS_FOTO = 5L * 1024 * 1024; // 5 MB

    private static final Color C_HADIR = new Color(39, 174, 96);
    private static final Color C_SAKIT = new Color(41, 128, 185);
    private static final Color C_IZIN = new Color(142, 68, 173);
    private static final Color C_ALPHA = new Color(192, 57, 43);

    private static class Catatan {
        LocalDate tanggal;
        String status, keterangan, foto, sumber;
        boolean diverifikasi;
    }

    private final List<Catatan> semua = new ArrayList<>();
    private final List<Catatan> tampil = new ArrayList<>(); // sejajar dengan baris tabel
    private final List<YearMonth> daftarBulan = new ArrayList<>();

    private JComboBox<String> cmbBulan;
    private JLabel lblHadir, lblSakit, lblIzin, lblAlpha, lblPersen, lblHariIni;
    private JProgressBar bar;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnAbsen, btnFoto;
    private boolean sedangMuat = false;

    public FrmAbsensiSiswa() {
        initUI();
        if (Session.idSiswa == 0) {
            btnAbsen.setEnabled(false);
            btnFoto.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatData();
        tampilkan();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Absensi Saya", "ABSENSI SAYA",
            () -> new FrmMenuSiswa().setVisible(true));

        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 910, 520);
        p.setLayout(null);
        bg.add(p);

        UI.judulForm(p, "Rekap Kehadiran");

        btnAbsen = UI.tombol(p, "ABSEN HARI INI (DARI RUMAH)", UI.HIJAU, UI.HIJAU_H, 330, 12, 300, 32, this::aksiAbsen);

        cmbBulan = new JComboBox<>(new String[]{SEMUA});
        cmbBulan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbBulan.setBackground(Color.WHITE);
        cmbBulan.setBounds(655, 14, 230, 28);
        cmbBulan.addActionListener(e -> { if (!sedangMuat) tampilkan(); });
        p.add(cmbBulan);

        // ---------- 4 KARTU STATUS ----------
        lblHadir = kartu(p, 25, "Hadir", C_HADIR);
        lblSakit = kartu(p, 243, "Sakit", C_SAKIT);
        lblIzin = kartu(p, 461, "Izin", C_IZIN);
        lblAlpha = kartu(p, 679, "Alpha", C_ALPHA);

        // ---------- PERSENTASE + STATUS HARI INI ----------
        lblPersen = new JLabel(" ");
        lblPersen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPersen.setForeground(UI.NAVY);
        lblPersen.setBounds(25, 148, 560, 20);
        p.add(lblPersen);

        lblHariIni = new JLabel(" ", SwingConstants.RIGHT);
        lblHariIni.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHariIni.setBounds(585, 148, 300, 20);
        p.add(lblHariIni);

        bar = new JProgressBar(0, 100);
        bar.setForeground(C_HADIR);
        bar.setBounds(25, 172, 860, 14);
        p.add(bar);

        // ---------- RIWAYAT ----------
        JLabel lblRiwayat = new JLabel("Riwayat Kehadiran");
        lblRiwayat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRiwayat.setForeground(UI.DARK);
        lblRiwayat.setBounds(25, 198, 300, 22);
        p.add(lblRiwayat);

        model = UI.modelTabel("ID", "No", "Hari / Tanggal", "Status", "Keterangan", "Bukti");
        tabel = UI.tabel(model); // indeks tampilan: 0 No, 1 Tanggal, 2 Status, 3 Keterangan, 4 Bukti
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(330);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(160);

        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        tabel.getColumnModel().getColumn(0).setCellRenderer(tengah);
        tabel.getColumnModel().getColumn(4).setCellRenderer(tengah);
        tabel.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                if (!sel) comp.setForeground(warna(String.valueOf(v)));
                return comp;
            }
        });
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) aksiLihatFoto();
            }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(25, 224, 860, 205);
        p.add(sp);

        btnFoto = UI.tombol(p, "LIHAT FOTO BUKTI", UI.NAVY, new Color(10, 36, 66), 25, 438, 190, 34, this::aksiLihatFoto);

        JLabel hint = new JLabel("<html>Persentase kehadiran = hari Hadir dibagi seluruh hari yang tercatat. "
            + "Absen dari rumah memerlukan foto bukti dan <b>menunggu verifikasi guru</b>. "
            + "Hubungi wali kelas jika ada data yang tidak sesuai.</html>");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(25, 478, 860, 32);
        p.add(hint);
    }

    /** Kartu angka kecil; mengembalikan label angkanya. */
    private JLabel kartu(JPanel parent, int x, String judul, Color warna) {
        RoundedPanel k = new RoundedPanel(15, new Color(245, 246, 250), false);
        k.setBounds(x, 55, 205, 80);
        k.setLayout(null);
        parent.add(k);

        JLabel angka = new JLabel("0", SwingConstants.CENTER);
        angka.setFont(new Font("Segoe UI", Font.BOLD, 30));
        angka.setForeground(warna);
        angka.setBounds(0, 10, 205, 40);
        k.add(angka);

        JLabel teks = new JLabel(judul, SwingConstants.CENTER);
        teks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teks.setForeground(Color.GRAY);
        teks.setBounds(0, 52, 205, 18);
        k.add(teks);
        return angka;
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatData() {
        semua.clear();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT tanggal, status, keterangan, foto, sumber, diverifikasi "
                  + "FROM tb_absensi WHERE id_siswa = ? ORDER BY tanggal DESC")) {
                ps.setInt(1, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Catatan c = new Catatan();
                        Date d = rs.getDate("tanggal");
                        c.tanggal = d.toLocalDate();
                        c.status = rs.getString("status");
                        c.keterangan = rs.getString("keterangan");
                        c.foto = rs.getString("foto");
                        c.sumber = rs.getString("sumber");
                        c.diverifikasi = rs.getBoolean("diverifikasi");
                        semua.add(c);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        // pilihan bulan: hanya bulan yang punya catatan, terbaru di atas
        Set<YearMonth> bulan = new LinkedHashSet<>();
        for (Catatan c : semua) bulan.add(YearMonth.from(c.tanggal));
        daftarBulan.clear();
        daftarBulan.addAll(bulan);

        sedangMuat = true;
        cmbBulan.removeAllItems();
        cmbBulan.addItem(SEMUA);
        for (YearMonth ym : daftarBulan) cmbBulan.addItem(kapital(FMT_BULAN.format(ym.atDay(1))));
        sedangMuat = false;

        perbaruiHariIni();
    }

    private void perbaruiHariIni() {
        Catatan hariIni = null;
        for (Catatan c : semua) if (c.tanggal.equals(LocalDate.now())) { hariIni = c; break; }
        if (hariIni == null) {
            lblHariIni.setText("Hari ini: belum absen");
            lblHariIni.setForeground(Color.GRAY);
        } else {
            boolean menunggu = "Mandiri".equals(hariIni.sumber) && !hariIni.diverifikasi;
            lblHariIni.setText("Hari ini: " + hariIni.status + (menunggu ? " (menunggu verifikasi guru)" : ""));
            lblHariIni.setForeground(menunggu ? new Color(212, 143, 0) : warna(hariIni.status));
        }
    }

    // ------------------------------------------------------------------
    //  TAMPILAN (rekap + tabel mengikuti filter bulan)
    // ------------------------------------------------------------------
    private void tampilkan() {
        int idx = cmbBulan.getSelectedIndex();
        YearMonth filter = (idx <= 0 || idx > daftarBulan.size()) ? null : daftarBulan.get(idx - 1);

        model.setRowCount(0);
        tampil.clear();
        int hadir = 0, sakit = 0, izin = 0, alpha = 0, no = 1;
        for (Catatan c : semua) {
            if (filter != null && !YearMonth.from(c.tanggal).equals(filter)) continue;
            switch (c.status) {
                case "Hadir": hadir++; break;
                case "Sakit": sakit++; break;
                case "Izin": izin++; break;
                case "Alpha": alpha++; break;
                default: break;
            }
            tampil.add(c);
            model.addRow(new Object[]{0, no++, kapital(FMT_HARI.format(c.tanggal)), c.status,
                c.keterangan == null || c.keterangan.trim().isEmpty() ? "-" : c.keterangan, teksBukti(c)});
        }

        lblHadir.setText(String.valueOf(hadir));
        lblSakit.setText(String.valueOf(sakit));
        lblIzin.setText(String.valueOf(izin));
        lblAlpha.setText(String.valueOf(alpha));

        int total = hadir + sakit + izin + alpha;
        if (total == 0) {
            bar.setValue(0);
            lblPersen.setText(semua.isEmpty() ? "Belum ada data absensi yang tercatat."
                : "Tidak ada catatan absensi pada bulan ini.");
        } else {
            double persen = hadir * 100.0 / total;
            bar.setValue((int) Math.round(persen));
            lblPersen.setText(String.format(ID, "Kehadiran: %.1f%%  (%d hari hadir dari %d hari tercatat)",
                persen, hadir, total));
        }
    }

    private static String teksBukti(Catatan c) {
        if (c.foto == null || c.foto.isEmpty()) return "-";
        return c.diverifikasi ? "Foto (terverifikasi)" : "Foto (menunggu)";
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void aksiLihatFoto() {
        int r = tabel.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu salah satu baris di riwayat.");
            return;
        }
        Catatan c = tampil.get(tabel.convertRowIndexToModel(r));
        if (c.foto == null || c.foto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Catatan ini tidak memiliki foto bukti.");
            return;
        }
        Storage.buka(this, c.foto);
    }

    /** Pesan kenapa absen mandiri belum bisa dilakukan sekarang, atau null jika boleh. */
    private String cekBolehAbsen() {
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            return "Akun ini belum terhubung dengan data siswa atau kelas.";
        }
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek hari = now.getDayOfWeek();
        if (hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY) {
            return "Absen mandiri hanya tersedia pada hari sekolah (Senin - Jumat).";
        }
        if (now.toLocalTime().isBefore(JAM_BUKA) || now.toLocalTime().isAfter(JAM_TUTUP)) {
            return "Absen mandiri hanya dibuka pukul " + JAM_BUKA + " - " + JAM_TUTUP + ".";
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return "Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running!";
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT status FROM tb_absensi WHERE id_siswa = ? AND tanggal = ?")) {
                ps.setInt(1, Session.idSiswa);
                ps.setDate(2, Date.valueOf(now.toLocalDate()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "Absensi hari ini sudah tercatat (" + rs.getString(1) + ").";
                }
            }
        } catch (SQLException ex) {
            return "Terjadi kesalahan database: " + ex.getMessage();
        }
        return null;
    }

    private void aksiAbsen() {
        String err = cekBolehAbsen();
        if (err != null) { UI.peringatan(this, err); return; }
        DialogAbsen d = new DialogAbsen();
        d.setVisible(true);
        if (d.terkirim) {
            muatData();
            tampilkan();
        }
    }

    // ==================================================================
    //  DIALOG ABSEN MANDIRI
    // ==================================================================
    private class DialogAbsen extends JDialog {

        private boolean terkirim = false;
        private File fileFoto = null;
        private JComboBox<String> cmbStatus;
        private JTextField txtKet, txtFile;
        private JLabel lblPreview;

        DialogAbsen() {
            super(FrmAbsensiSiswa.this, "Absen Mandiri (Belajar dari Rumah)", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new java.awt.Dimension(350, 520));
            setContentPane(p);

            JLabel tgl = new JLabel("Hari ini: " + kapital(FMT_HARI.format(LocalDate.now())));
            tgl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tgl.setForeground(UI.NAVY);
            tgl.setBounds(25, 12, 300, 20);
            p.add(tgl);

            cmbStatus = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin"});
            UI.combo(p, "Status Kehadiran *", 42, cmbStatus);
            txtKet = UI.field(p, "Keterangan (opsional, maks. 100 karakter)", 98);

            UI.label(p, "Foto Bukti * (jpg / png, maks. 5 MB)", 154);
            txtFile = new JTextField();
            txtFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtFile.setEditable(false);
            txtFile.setBounds(25, 171, 190, 28);
            txtFile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
            p.add(txtFile);
            UI.tombol(p, "PILIH FOTO", UI.NAVY, new Color(10, 36, 66), 225, 171, 100, 28, this::pilihFoto);

            lblPreview = new JLabel("Pratinjau foto", SwingConstants.CENTER);
            lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblPreview.setForeground(Color.GRAY);
            lblPreview.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
            lblPreview.setBounds(25, 210, 300, 200);
            p.add(lblPreview);

            JLabel hint = new JLabel("<html>Gunakan foto selfie yang jelas (atau foto surat untuk Sakit/Izin). "
                + "Absen akan diperiksa guru.</html>");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hint.setForeground(Color.GRAY);
            hint.setBounds(25, 414, 300, 30);
            p.add(hint);

            UI.tombol(p, "KIRIM ABSEN", UI.HIJAU, UI.HIJAU_H, 25, 458, 145, 42, this::kirim);
            UI.tombol(p, "BATAL", UI.ABU, UI.ABU_H, 180, 458, 145, 42, this::dispose);

            pack();
            setLocationRelativeTo(FrmAbsensiSiswa.this);
        }

        private void pilihFoto() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pilih foto bukti kehadiran");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter("Foto (jpg, png)", "jpg", "jpeg", "png"));
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            String ext = Storage.ekstensi(f.getName());
            if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))) {
                UI.peringatan(this, "Hanya foto berformat jpg atau png yang diizinkan.");
                return;
            }
            if (f.length() > MAKS_FOTO) {
                UI.peringatan(this, "Ukuran foto melebihi 5 MB.");
                return;
            }
            BufferedImage img;
            try {
                img = ImageIO.read(f);
            } catch (IOException ex) {
                img = null;
            }
            if (img == null) {
                UI.peringatan(this, "File tersebut bukan gambar yang valid.");
                return;
            }
            fileFoto = f;
            txtFile.setText(f.getName());

            double skala = Math.min(296.0 / img.getWidth(), 196.0 / img.getHeight());
            int w = Math.max(1, (int) Math.round(img.getWidth() * Math.min(1.0, skala)));
            int h = Math.max(1, (int) Math.round(img.getHeight() * Math.min(1.0, skala)));
            lblPreview.setText("");
            lblPreview.setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
        }

        private void kirim() {
            String ket = txtKet.getText().trim();
            if (fileFoto == null) { UI.peringatan(this, "Foto bukti wajib dilampirkan."); return; }
            if (!fileFoto.exists()) { UI.peringatan(this, "File foto sudah tidak ada. Pilih ulang."); return; }
            if (ket.length() > 100) { UI.peringatan(this, "Keterangan maksimal 100 karakter."); return; }

            String err = cekBolehAbsen(); // diperiksa ulang: waktu bisa saja sudah lewat atau guru sudah mengisi
            if (err != null) { UI.peringatan(this, err); return; }

            String status = cmbStatus.getSelectedItem().toString();
            if (JOptionPane.showConfirmDialog(this,
                    "Kirim absen hari ini dengan status \"" + status + "\"?\nAbsen tidak bisa diubah setelah dikirim.",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            // 1) salin foto lebih dulu
            String rel;
            try {
                rel = Storage.simpan(fileFoto, "absensi");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan foto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) simpan ke database; jika gagal, salinan foto dibuang lagi
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) {
                    Storage.hapus(rel);
                    UI.koneksiGagal(this);
                    return;
                }
                LocalDateTime now = LocalDateTime.now();
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_absensi (id_siswa, id_kelas, tanggal, status, keterangan, foto, sumber, "
                      + "waktu_absen, diverifikasi) VALUES (?,?,?,?,?,?,'Mandiri',?,0)")) {
                    ps.setInt(1, Session.idSiswa);
                    ps.setInt(2, Session.idKelas);
                    ps.setDate(3, Date.valueOf(now.toLocalDate()));
                    ps.setString(4, status);
                    ps.setString(5, ket.isEmpty() ? null : ket);
                    ps.setString(6, rel);
                    ps.setTimestamp(7, Timestamp.valueOf(now));
                    ps.executeUpdate();
                }
            } catch (SQLIntegrityConstraintViolationException dup) {
                Storage.hapus(rel);
                UI.peringatan(this, "Absensi hari ini sudah tercatat.");
                dispose();
                return;
            } catch (SQLException ex) {
                Storage.hapus(rel);
                UI.errorDb(this, ex);
                return;
            }

            terkirim = true;
            JOptionPane.showMessageDialog(this, "Absen berhasil dikirim dan menunggu verifikasi guru.");
            dispose();
        }
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static Color warna(String status) {
        switch (status) {
            case "Hadir": return C_HADIR;
            case "Sakit": return C_SAKIT;
            case "Izin": return C_IZIN;
            default: return C_ALPHA;
        }
    }

    /** "senin, 05-10-2026" -> "Senin, 05-10-2026". */
    private static String kapital(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmAbsensiSiswa().setVisible(true));
    }
}