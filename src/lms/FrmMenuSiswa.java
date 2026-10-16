package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FrmMenuSiswa extends javax.swing.JFrame {

    private static final Color C_HADIR = new Color(39, 174, 96);
    private static final Color C_SAKIT = new Color(41, 128, 185);
    private static final Color C_IZIN = new Color(142, 68, 173);
    private static final Color C_ALPHA = new Color(192, 57, 43);
    private static final Color C_WARN = new Color(212, 143, 0);

    private JPanel bgPanel;

    // --- SIDEBAR MENU ---
    private JPanel sidebarPanel;
    private AvatarBulat profilAvatar;
    private JLabel lblNamaSiswa, lblNisSiswa;
    private RoundedButton btnMenuTugas, btnMenuKuis, btnMenuMateri, btnMenuNilai, btnMenuAbsensi,
                          btnMenuJadwal, btnMenuForum, btnLogout;

    // --- NOTIFIKASI BADGE ---
    private BadgeBulat badgeTugasAktif;
    private BadgeBulat badgeKuisAktif;

    // --- MAIN DASHBOARD CONTENT ---
    private RoundedPanel panelBanner;
    private JLabel lblBannerTitle, lblBannerSub;

    private RoundedPanel panelStatistik;
    private JLabel lblStatTitle, lblNilaiAngka, lblNilaiInfo;
    private JLabel lblHadirAngka, lblHadirRinci, lblHadirHariIni;

    public FrmMenuSiswa() {
        initComponentsCustom();
        muatDashboard();
    }

    private RoundedButton menu(String teks, int y) {
        RoundedButton b = new RoundedButton(teks, 10, new Color(26, 26, 46), new Color(22, 22, 40));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBounds(30, y, 220, 42);
        sidebarPanel.add(b);
        return b;
    }

    private BadgeBulat badge(int y) {
        BadgeBulat b = new BadgeBulat("0", new Color(231, 76, 60));
        b.setBounds(210, y + 9, 25, 25);
        b.setVisible(false);
        sidebarPanel.add(b);
        sidebarPanel.setComponentZOrder(b, 0);
        return b;
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Dashboard Siswa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); getContentPane().setPreferredSize(new java.awt.Dimension(1000, 650)); pack();
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // Background Utama
        bgPanel = new JPanel();
        bgPanel.setBackground(new Color(245, 246, 250));
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // ==========================================
        // 1. SIDEBAR PANEL (Gradasi Biru Gelap)
        // ==========================================
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setBounds(0, 0, 280, 650);
        sidebarPanel.setLayout(null);
        bgPanel.add(sidebarPanel);

        profilAvatar = new AvatarBulat(Session.inisial(), new Color(46, 204, 113));
        profilAvatar.setBounds(90, 40, 100, 100);
        sidebarPanel.add(profilAvatar);

        lblNamaSiswa = new JLabel(Session.namaLengkap, JLabel.CENTER);
        lblNamaSiswa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNamaSiswa.setForeground(Color.WHITE);
        lblNamaSiswa.setBounds(15, 150, 250, 25);
        sidebarPanel.add(lblNamaSiswa);

        lblNisSiswa = new JLabel("NIS. " + Session.nis, JLabel.CENTER);
        lblNisSiswa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNisSiswa.setForeground(new Color(180, 180, 200));
        lblNisSiswa.setBounds(15, 175, 250, 20);
        sidebarPanel.add(lblNisSiswa);

        // --- BUTTON SIDEBAR MENU (jarak 52 px) ---
        btnMenuTugas = menu("TUGAS SAYA", 215);
        btnMenuTugas.addActionListener(e -> { new FrmTugasSiswa().setVisible(true); dispose(); });
        badgeTugasAktif = badge(215);

        btnMenuKuis = menu("KUIS SAYA", 267);
        btnMenuKuis.addActionListener(e -> { new FrmKuisSiswa().setVisible(true); dispose(); });
        badgeKuisAktif = badge(267);

        btnMenuMateri = menu("UNDUH MATERI", 319);
        btnMenuMateri.addActionListener(e -> { new FrmMateriSiswa().setVisible(true); dispose(); });

        btnMenuNilai = menu("NILAI SAYA", 371);
        btnMenuNilai.addActionListener(e -> { new FrmNilaiSiswa().setVisible(true); dispose(); });

        btnMenuAbsensi = menu("ABSENSI SAYA", 423);
        btnMenuAbsensi.addActionListener(e -> { new FrmAbsensiSiswa().setVisible(true); dispose(); });

        btnMenuJadwal = menu("JADWAL PELAJARAN", 475);
        btnMenuJadwal.addActionListener(e -> { new FrmJadwalSiswa().setVisible(true); dispose(); });

        btnMenuForum = menu("FORUM DISKUSI", 527);
        btnMenuForum.addActionListener(e -> { new FrmForumSiswa().setVisible(true); dispose(); });

        btnLogout = new RoundedButton("LOGOUT SYSTEM", 10, new Color(198, 40, 40), new Color(160, 30, 30));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBounds(30, 593, 220, 42);
        btnLogout.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            dispose();
        });
        sidebarPanel.add(btnLogout);

        // ==========================================
        // 2. DASHBOARD CONTENT AREA
        // ==========================================

        // Banner Selamat Datang (Gradasi Biru Tua)
        panelBanner = new RoundedPanel(20, new Color(24, 76, 120), false) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 52, 96), getWidth(), 0, new Color(24, 76, 120));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelBanner.setBounds(310, 40, 640, 180);
        panelBanner.setLayout(null);
        bgPanel.add(panelBanner);

        lblBannerTitle = new JLabel("Selamat Datang Kembali, " + Session.namaLengkap + "!");
        lblBannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBannerTitle.setForeground(Color.WHITE);
        lblBannerTitle.setBounds(30, 45, 580, 30);
        panelBanner.add(lblBannerTitle);

        lblBannerSub = new JLabel("Memuat ringkasan tugas dan kuis...");
        lblBannerSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBannerSub.setForeground(new Color(200, 214, 229));
        lblBannerSub.setBounds(30, 80, 580, 20);
        panelBanner.add(lblBannerSub);

        // Kotak Statistik (RoundedPanel)
        panelStatistik = new RoundedPanel(20, Color.WHITE, true);
        panelStatistik.setBounds(310, 250, 640, 315);
        panelStatistik.setLayout(null);
        bgPanel.add(panelStatistik);

        lblStatTitle = new JLabel("Ringkasan Akademik Anda");
        lblStatTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatTitle.setForeground(new Color(26, 26, 46));
        lblStatTitle.setBounds(35, 25, 300, 25);
        panelStatistik.add(lblStatTitle);

        // --- kiri: rata-rata nilai ---
        label(panelStatistik, "Rata-rata Nilai:", 35, 75, 14, Font.PLAIN, Color.GRAY, 250, 20);
        lblNilaiAngka = label(panelStatistik, "-", 35, 100, 56, Font.BOLD, new Color(46, 204, 113), 250, 70);
        lblNilaiInfo = label(panelStatistik, " ", 35, 175, 12, Font.PLAIN, Color.GRAY, 270, 20);

        // --- kanan: kehadiran ---
        label(panelStatistik, "Persentase Kehadiran:", 345, 75, 14, Font.PLAIN, Color.GRAY, 250, 20);
        lblHadirAngka = label(panelStatistik, "-", 345, 100, 56, Font.BOLD, C_HADIR, 250, 70);
        lblHadirRinci = label(panelStatistik, " ", 345, 175, 12, Font.PLAIN, Color.GRAY, 270, 20);
        lblHadirHariIni = label(panelStatistik, " ", 345, 197, 13, Font.BOLD, Color.GRAY, 270, 20);

        UI.tombol(panelStatistik, "LIHAT NILAI", UI.NAVY, new Color(10, 36, 66), 35, 250, 270, 38,
            () -> { new FrmNilaiSiswa().setVisible(true); dispose(); });
        UI.tombol(panelStatistik, "LIHAT ABSENSI", UI.NAVY, new Color(10, 36, 66), 345, 250, 260, 38,
            () -> { new FrmAbsensiSiswa().setVisible(true); dispose(); });
    }

    private JLabel label(JPanel p, String teks, int x, int y, int ukuran, int gaya, Color warna, int w, int h) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", gaya, ukuran));
        l.setForeground(warna);
        l.setBounds(x, y, w, h);
        p.add(l);
        return l;
    }

    // ------------------------------------------------------------------
    //  DATA
    // ------------------------------------------------------------------
    private void muatDashboard() {
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            lblBannerSub.setText("Akun ini belum terhubung dengan data siswa atau kelas. Hubungi administrator.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                lblBannerSub.setText("Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running.");
                return;
            }
            muatTugasKuis(con);
            muatNilai(con);
            muatKehadiran(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void muatTugasKuis(Connection con) {
        int tugas = 0, kuis = 0;
        try {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_tugas t WHERE t.id_kelas = ? "
                  + "AND (t.deadline IS NULL OR t.deadline >= NOW()) "
                  + "AND NOT EXISTS (SELECT 1 FROM tb_pengumpulan p WHERE p.id_tugas = t.id_tugas AND p.id_siswa = ?)")) {
                ps.setInt(1, Session.idKelas);
                ps.setInt(2, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) tugas = rs.getInt(1); }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_kuis k WHERE k.id_kelas = ? "
                  + "AND (k.tanggal_mulai IS NULL OR k.tanggal_mulai <= NOW()) "
                  + "AND (k.tanggal_selesai IS NULL OR k.tanggal_selesai >= NOW()) "
                  + "AND EXISTS (SELECT 1 FROM tb_soal s WHERE s.id_kuis = k.id_kuis) "
                  + "AND NOT EXISTS (SELECT 1 FROM tb_nilai n WHERE n.id_kuis = k.id_kuis "
                  + "AND n.id_siswa = ? AND n.jenis_nilai = 'Kuis')")) {
                ps.setInt(1, Session.idKelas);
                ps.setInt(2, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) kuis = rs.getInt(1); }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblBannerSub.setText("Ringkasan tugas dan kuis tidak dapat dimuat.");
            return;
        }
        badgeTugasAktif.setText(String.valueOf(tugas));
        badgeTugasAktif.setVisible(tugas > 0);
        badgeKuisAktif.setText(String.valueOf(kuis));
        badgeKuisAktif.setVisible(kuis > 0);

        lblBannerSub.setText(tugas + kuis == 0
            ? "Tidak ada tugas atau kuis yang menunggu. Kerja bagus!"
            : "Kamu memiliki " + tugas + " tugas dan " + kuis + " kuis yang belum diselesaikan.");
    }

    private void muatNilai(Connection con) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT AVG(nilai), COUNT(*) FROM tb_nilai WHERE id_siswa = ?")) {
            ps.setInt(1, Session.idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(2) > 0) {
                    BigDecimal rata = rs.getBigDecimal(1).setScale(1, RoundingMode.HALF_UP);
                    lblNilaiAngka.setText(rata.toPlainString());
                    lblNilaiInfo.setText("dari " + rs.getInt(2) + " nilai tugas, kuis, dan ujian");
                } else {
                    lblNilaiAngka.setText("-");
                    lblNilaiInfo.setText("Belum ada nilai yang masuk");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblNilaiInfo.setText("Nilai tidak dapat dimuat");
        }
    }

    private void muatKehadiran(Connection con) {
        int hadir = 0, sakit = 0, izin = 0, alpha = 0;
        String hariIni = null;
        try {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT status, COUNT(*) FROM tb_absensi WHERE id_siswa = ? GROUP BY status")) {
                ps.setInt(1, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        switch (rs.getString(1)) {
                            case "Hadir": hadir = rs.getInt(2); break;
                            case "Sakit": sakit = rs.getInt(2); break;
                            case "Izin": izin = rs.getInt(2); break;
                            case "Alpha": alpha = rs.getInt(2); break;
                            default: break;
                        }
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT status FROM tb_absensi WHERE id_siswa = ? AND tanggal = CURDATE()")) {
                ps.setInt(1, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) hariIni = rs.getString(1); }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblHadirRinci.setText("Data absensi belum tersedia");
            return;
        }

        int total = hadir + sakit + izin + alpha;
        if (total == 0) {
            lblHadirAngka.setText("-");
            lblHadirAngka.setForeground(Color.GRAY);
            lblHadirRinci.setText("Belum ada absensi yang tercatat");
        } else {
            double persen = hadir * 100.0 / total;
            lblHadirAngka.setText(String.format(java.util.Locale.US, "%.0f%%", persen));
            lblHadirAngka.setForeground(persen >= 90 ? C_HADIR : persen >= 75 ? C_WARN : C_ALPHA);
            lblHadirRinci.setText("Hadir " + hadir + "  |  Sakit " + sakit + "  |  Izin " + izin
                + "  |  Alpha " + alpha);
        }

        if (hariIni == null) {
            lblHadirHariIni.setText("Hari ini: belum dicatat");
            lblHadirHariIni.setForeground(Color.GRAY);
        } else {
            lblHadirHariIni.setText("Hari ini: " + hariIni);
            lblHadirHariIni.setForeground("Hadir".equals(hariIni) ? C_HADIR : "Sakit".equals(hariIni) ? C_SAKIT
                : "Izin".equals(hariIni) ? C_IZIN : C_ALPHA);
        }
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMenuSiswa().setVisible(true));
    }
}