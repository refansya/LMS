package lms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FrmMenuGuru extends javax.swing.JFrame {

    private static final Color PINK = new Color(233, 69, 96);
    private static final Color HIJAU = new Color(39, 174, 96);
    private static final Color ORANYE = new Color(212, 143, 0);
    private static final Color NAVY = new Color(15, 52, 96);

    // Komponen Layout Utama
    private JPanel sidebarPanel, mainContentPanel;

    // --- SIDEBAR GURU ---
    private AvatarBulat profilAvatar;
    private JLabel lblNamaGuru, lblNipGuru;
    private RoundedButton btnMateri, btnTugas, btnKuis, btnAbsensi, btnNilai, btnForum, btnKeluar;
    private BadgeBulat badgeTugas; // notifikasi pengumpulan tugas yang belum dinilai

    // --- MAIN CONTENT ---
    private JLabel lblWelcome, lblTanggal;
    private JLabel lblKelasDesc;
    private JLabel lblTugasAngka, lblTugasDesc;
    private JLabel lblKuisAngka, lblKuisDesc;
    private JLabel lblPresensiDesc;
    private JLabel lblDiskusiDesc;

    /** id kelas yang berkaitan dengan guru (dipakai untuk menyaring diskusi). */
    private final List<Integer> idKelasSaya = new ArrayList<>();

    public FrmMenuGuru() {
        initComponentsCustom();
        muatDashboard();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Portal Guru");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); getContentPane().setPreferredSize(new java.awt.Dimension(1000, 650)); pack();
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // ==========================================
        // 1. SIDEBAR PANEL (Biru Gelap)
        // ==========================================
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 52, 96), 0, getHeight(), new Color(10, 36, 66));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setBounds(0, 0, 250, 650);
        sidebarPanel.setLayout(null);
        getContentPane().add(sidebarPanel);

        profilAvatar = new AvatarBulat(Session.inisial(), new Color(233, 69, 96));
        profilAvatar.setBounds(75, 40, 100, 100);
        sidebarPanel.add(profilAvatar);

        lblNamaGuru = new JLabel(Session.namaLengkap);
        lblNamaGuru.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaGuru.setForeground(Color.WHITE);
        lblNamaGuru.setHorizontalAlignment(SwingConstants.CENTER);
        lblNamaGuru.setBounds(10, 155, 230, 25);
        sidebarPanel.add(lblNamaGuru);

        lblNipGuru = new JLabel("NIP. " + Session.nip);
        lblNipGuru.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNipGuru.setForeground(new Color(180, 180, 200));
        lblNipGuru.setHorizontalAlignment(SwingConstants.CENTER);
        lblNipGuru.setBounds(10, 180, 230, 20);
        sidebarPanel.add(lblNipGuru);

        // --- TOMBOL NAVIGASI SIDEBAR ---
        btnMateri = menuButton("MANAJEMEN MATERI", 240);
        btnMateri.addActionListener(e -> { new FrmMateriGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnMateri);

        btnTugas = menuButton("TUGAS MASUK", 305);
        btnTugas.addActionListener(e -> { new FrmTugasGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnTugas);

        // Badge: jumlah pengumpulan tugas yang belum dinilai (diisi di muatDashboard)
        badgeTugas = new BadgeBulat("0", new Color(231, 76, 60));
        badgeTugas.setVisible(false);
        badgeTugas.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeTugas.setBounds(190, 295, 24, 24);
        sidebarPanel.add(badgeTugas);
        sidebarPanel.setComponentZOrder(badgeTugas, 0);

        btnKuis = menuButton("KUIS ONLINE", 365);
        btnKuis.addActionListener(e -> { new FrmKuisGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnKuis);

        btnAbsensi = menuButton("ABSENSI SISWA", 420);
        btnAbsensi.addActionListener(e -> { new FrmAbsensiGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnAbsensi);

        btnNilai = menuButton("INPUT NILAI", 475);
        btnNilai.addActionListener(e -> { new FrmNilaiGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnNilai);

        btnForum = menuButton("FORUM DISKUSI", 530);
        btnForum.addActionListener(e -> { new FrmForumGuru().setVisible(true); this.dispose(); });
        sidebarPanel.add(btnForum);

        btnKeluar = new RoundedButton("LOGOUT", 10, new Color(198, 40, 40), new Color(160, 30, 30));
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBounds(20, 590, 210, 45);
        btnKeluar.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnKeluar);

        // ==========================================
        // 2. MAIN CONTENT PANEL
        // ==========================================
        mainContentPanel = new JPanel();
        mainContentPanel.setBackground(new Color(245, 247, 250));
        mainContentPanel.setBounds(250, 0, 750, 650);
        mainContentPanel.setLayout(null);
        getContentPane().add(mainContentPanel);

        lblWelcome = new JLabel(salam() + ", " + Session.namaLengkap + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(NAVY);
        lblWelcome.setBounds(40, 30, 660, 30);
        mainContentPanel.add(lblWelcome);

        lblTanggal = new JLabel(tanggalHariIni());
        lblTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTanggal.setForeground(Color.GRAY);
        lblTanggal.setBounds(40, 62, 660, 20);
        mainContentPanel.add(lblTanggal);

        // Baris 1: Kelas diampu | Tugas perlu dinilai
        RoundedPanel cardKelas = kartu(40, 105, 320, 190, "Kelas Yang Diampu", NAVY);
        lblKelasDesc = deskripsi(cardKelas, 20, 52, 280, 125);

        RoundedPanel cardTugas = kartu(380, 105, 320, 190, "Tugas Perlu Dinilai", PINK);
        lblTugasAngka = angka(cardTugas, 20, 50);
        lblTugasDesc = deskripsi(cardTugas, 20, 112, 280, 65);
        bisaDiklik(cardTugas, () -> { new FrmTugasGuru().setVisible(true); dispose(); });

        // Baris 2: Kuis online | Presensi hari ini
        RoundedPanel cardKuis = kartu(40, 315, 320, 150, "Kuis Online", NAVY);
        lblKuisAngka = angka(cardKuis, 20, 45);
        lblKuisDesc = deskripsi(cardKuis, 20, 105, 280, 35);
        bisaDiklik(cardKuis, () -> { new FrmKuisGuru().setVisible(true); dispose(); });

        RoundedPanel cardPresensi = kartu(380, 315, 320, 150, "Presensi Hari Ini", NAVY);
        lblPresensiDesc = deskripsi(cardPresensi, 20, 52, 280, 85);
        bisaDiklik(cardPresensi, () -> { new FrmAbsensiGuru().setVisible(true); dispose(); });

        // Baris 3: Diskusi terbaru
        RoundedPanel cardDiskusi = kartu(40, 485, 660, 135, "Diskusi Terbaru", NAVY);
        lblDiskusiDesc = deskripsi(cardDiskusi, 20, 50, 620, 75);
        bisaDiklik(cardDiskusi, () -> { new FrmForumGuru().setVisible(true); dispose(); });
    }

    private RoundedButton menuButton(String teks, int y) {
        RoundedButton b = new RoundedButton(teks, 10, new Color(26, 26, 46), new Color(22, 22, 40));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBounds(20, y, 210, 45);
        return b;
    }

    private RoundedPanel kartu(int x, int y, int w, int h, String judul, Color warnaJudul) {
        RoundedPanel p = new RoundedPanel(25, Color.WHITE, true);
        p.setBounds(x, y, w, h);
        p.setLayout(null);
        mainContentPanel.add(p);

        JLabel t = new JLabel(judul);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(warnaJudul);
        t.setBounds(20, 16, w - 40, 25);
        p.add(t);
        return p;
    }

    private JLabel deskripsi(RoundedPanel p, int x, int y, int w, int h) {
        JLabel l = new JLabel("<html>Memuat data...</html>");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.DARK_GRAY);
        l.setVerticalAlignment(SwingConstants.TOP);
        l.setBounds(x, y, w, h);
        p.add(l);
        return l;
    }

    private JLabel angka(RoundedPanel p, int x, int y) {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Segoe UI", Font.BOLD, 40));
        l.setForeground(NAVY);
        l.setBounds(x, y, 200, 52);
        p.add(l);
        return l;
    }

    private void bisaDiklik(RoundedPanel p, Runnable aksi) {
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aksi.run(); }
        });
    }

    private static String salam() {
        int jam = LocalTime.now().getHour();
        if (jam < 11) return "Selamat pagi";
        if (jam < 15) return "Selamat siang";
        if (jam < 18) return "Selamat sore";
        return "Selamat malam";
    }

    private static String tanggalHariIni() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID")));
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String potong(String s, int maks) {
        return s.length() <= maks ? s : s.substring(0, maks - 3) + "...";
    }

    private static String warna(String teks, Color c) {
        return "<font color='" + String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()) + "'>"
             + teks + "</font>";
    }

    // ------------------------------------------------------------------
    //  DATA
    // ------------------------------------------------------------------
    private void muatDashboard() {
        if (Session.idGuru == 0) {
            lblKelasDesc.setText("<html>Akun ini belum terhubung dengan data guru.<br>"
                + "Minta administrator menambahkan Anda di menu Data Guru.</html>");
            lblTugasAngka.setText("0");
            lblTugasDesc.setText("<html>-</html>");
            lblKuisAngka.setText("0");
            lblKuisDesc.setText("<html>-</html>");
            lblPresensiDesc.setText("<html>-</html>");
            lblDiskusiDesc.setText("<html>-</html>");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                String pesan = "<html>Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running.</html>";
                lblKelasDesc.setText(pesan);
                lblTugasDesc.setText("<html>-</html>");
                lblKuisDesc.setText("<html>-</html>");
                lblPresensiDesc.setText("<html>-</html>");
                lblDiskusiDesc.setText("<html>-</html>");
                return;
            }
            muatMapel(con);
            muatKelas(con);
            muatTugas(con);
            muatKuis(con);
            muatPresensi(con);
            muatDiskusi(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void gagal(JLabel l, SQLException ex) {
        ex.printStackTrace();
        l.setText("<html>Data tidak dapat dimuat.</html>");
    }

    private void muatMapel(Connection con) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT m.nama_mapel FROM tb_guru g LEFT JOIN tb_mapel m ON g.id_mapel = m.id_mapel "
              + "WHERE g.id_guru=?")) {
            ps.setInt(1, Session.idGuru);
            try (ResultSet rs = ps.executeQuery()) {
                String mapel = rs.next() ? rs.getString(1) : null;
                lblTanggal.setText(tanggalHariIni() + (mapel == null ? "" : "   |   " + mapel)
                    + "   |   Portal LMS Guru UPTD SMPN 33 Depok");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void muatKelas(Connection con) {
        idKelasSaya.clear();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT k.id_kelas, k.nama_kelas, (k.id_wali_kelas = ?) AS wali, "
              + "(SELECT COUNT(*) FROM tb_siswa s WHERE s.id_kelas = k.id_kelas) AS jml "
              + "FROM tb_kelas k WHERE k.id_wali_kelas = ? "
              + "OR k.id_kelas IN (SELECT id_kelas FROM tb_jadwal WHERE id_guru = ?) "
              + "OR k.id_kelas IN (SELECT id_kelas FROM tb_tugas WHERE id_guru = ?) "
              + "OR k.id_kelas IN (SELECT id_kelas FROM tb_materi WHERE id_guru = ?) "
              + "OR k.id_kelas IN (SELECT id_kelas FROM tb_kuis WHERE id_guru = ?) "
              + "ORDER BY k.tingkat, k.nama_kelas")) {
            for (int i = 1; i <= 6; i++) ps.setInt(i, Session.idGuru);
            StringBuilder sb = new StringBuilder("<html>");
            int n = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    idKelasSaya.add(rs.getInt("id_kelas"));
                    if (n < 5) {
                        sb.append("&bull; <b>").append(esc(rs.getString("nama_kelas"))).append("</b> - ")
                          .append(rs.getBoolean("wali") ? "Wali kelas" : "Pengampu")
                          .append(", ").append(rs.getInt("jml")).append(" siswa<br>");
                    }
                    n++;
                }
            }
            if (n == 0) {
                lblKelasDesc.setText("<html>Belum ada kelas yang terhubung dengan Anda.<br>"
                    + "Kelas akan muncul setelah Anda menjadi wali kelas atau membuat materi, tugas, atau kuis.</html>");
            } else {
                if (n > 5) sb.append("+ ").append(n - 5).append(" kelas lainnya");
                lblKelasDesc.setText(sb.append("</html>").toString());
            }
        } catch (SQLException ex) {
            gagal(lblKelasDesc, ex);
        }
    }

    private void muatTugas(Connection con) {
        try {
            int belum = 0, aktif = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_pengumpulan p JOIN tb_tugas t ON p.id_tugas = t.id_tugas "
                  + "WHERE t.id_guru = ? AND p.status = 'Belum Dinilai'")) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) belum = rs.getInt(1); }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_tugas WHERE id_guru = ? AND deadline >= NOW()")) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) aktif = rs.getInt(1); }
            }
            badgeTugas.setText(String.valueOf(belum));
            badgeTugas.setVisible(belum > 0);

            lblTugasAngka.setText(String.valueOf(belum));
            lblTugasAngka.setForeground(belum > 0 ? PINK : HIJAU);
            lblTugasDesc.setText(belum > 0
                ? "<html>pengumpulan dari siswa perlu diperiksa dan dinilai.<br>Tugas yang masih berjalan: " + aktif + "</html>"
                : "<html>Semua pengumpulan tugas sudah dinilai.<br>Tugas yang masih berjalan: " + aktif + "</html>");
        } catch (SQLException ex) {
            gagal(lblTugasDesc, ex);
        }
    }

    private void muatKuis(Connection con) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COALESCE(SUM(NOW() BETWEEN tanggal_mulai AND tanggal_selesai),0) AS jalan, "
              + "COALESCE(SUM(NOW() < tanggal_mulai),0) AS nanti FROM tb_kuis WHERE id_guru = ?")) {
            ps.setInt(1, Session.idGuru);
            try (ResultSet rs = ps.executeQuery()) {
                int jalan = 0, nanti = 0;
                if (rs.next()) { jalan = rs.getInt("jalan"); nanti = rs.getInt("nanti"); }
                lblKuisAngka.setText(String.valueOf(jalan));
                lblKuisDesc.setText("<html>kuis sedang berlangsung   |   Akan datang: " + nanti + "</html>");
            }
        } catch (SQLException ex) {
            gagal(lblKuisDesc, ex);
        }
    }

    private void muatPresensi(Connection con) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT k.nama_kelas, "
              + "(SELECT COUNT(*) FROM tb_siswa s WHERE s.id_kelas = k.id_kelas) AS jml, "
              + "(SELECT COUNT(*) FROM tb_absensi a WHERE a.id_kelas = k.id_kelas AND a.tanggal = CURDATE()) AS isi "
              + "FROM tb_kelas k WHERE k.id_wali_kelas = ? ORDER BY k.tingkat, k.nama_kelas")) {
            ps.setInt(1, Session.idGuru);
            StringBuilder sb = new StringBuilder("<html>");
            boolean ada = false;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ada = true;
                    String kelas = esc(rs.getString("nama_kelas"));
                    int jml = rs.getInt("jml"), isi = rs.getInt("isi");
                    sb.append("&bull; <b>").append(kelas).append("</b>: ");
                    if (jml == 0) sb.append("belum ada siswa");
                    else if (isi > 0) sb.append(warna("sudah diisi (" + isi + "/" + jml + ")", HIJAU));
                    else sb.append(warna("belum diisi", ORANYE));
                    sb.append("<br>");
                }
            }
            lblPresensiDesc.setText(ada ? sb.append("</html>").toString()
                : "<html>Anda bukan wali kelas. Presensi tetap bisa diisi lewat menu Absensi Siswa.</html>");
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblPresensiDesc.setText("<html>Data presensi belum tersedia. Pastikan tabel tb_absensi sudah dibuat.</html>");
        }
    }

    private void muatDiskusi(Connection con) {
        StringBuilder filter = new StringBuilder("f.id_kelas IS NULL");
        if (!idKelasSaya.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            for (int id : idKelasSaya) ids.append(ids.length() == 0 ? "" : ",").append(id); // angka, aman
            filter.append(" OR f.id_kelas IN (").append(ids).append(")");
        }
        String sql = "SELECT f.judul, COALESCE(k.nama_kelas,'Semua kelas') AS kelas, "
                   + "COALESCE(u.nama_lengkap,'-') AS nama, "
                   + "(SELECT COUNT(*) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum) AS jml "
                   + "FROM tb_forum f LEFT JOIN tb_kelas k ON f.id_kelas = k.id_kelas "
                   + "LEFT JOIN tb_user u ON f.id_user = u.id_user WHERE " + filter + " "
                   + "ORDER BY COALESCE((SELECT MAX(b.tanggal) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum), "
                   + "f.tanggal_post) DESC LIMIT 3";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            StringBuilder sb = new StringBuilder("<html>");
            boolean ada = false;
            while (rs.next()) {
                ada = true;
                sb.append("&bull; <b>").append(esc(potong(rs.getString("judul"), 55))).append("</b> - ")
                  .append(esc(rs.getString("kelas"))).append(", oleh ").append(esc(rs.getString("nama")))
                  .append(", ").append(rs.getInt("jml")).append(" balasan<br>");
            }
            lblDiskusiDesc.setText(ada ? sb.append("</html>").toString()
                : "<html>Belum ada diskusi. Mulai diskusi baru lewat menu Forum Diskusi.</html>");
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblDiskusiDesc.setText("<html>Data diskusi belum tersedia. Pastikan tabel tb_forum_balasan sudah dibuat.</html>");
        }
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMenuGuru().setVisible(true));
    }
}