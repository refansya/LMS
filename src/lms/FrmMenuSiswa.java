package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FrmMenuSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;

    // --- SIDEBAR MENU ---
    private JPanel sidebarPanel;
    private AvatarBulat profilAvatar;
    private JLabel lblNamaSiswa, lblNisSiswa;
    private RoundedButton btnMenuTugas, btnMenuKuis, btnMenuMateri, btnMenuNilai, btnMenuJadwal, btnMenuForum, btnLogout;
    
    // --- NOTIFIKASI BADGE ---
    private BadgeBulat badgeTugasAktif;
    private BadgeBulat badgeKuisAktif;

    // --- MAIN DASHBOARD CONTENT ---
    private RoundedPanel panelBanner;
    private JLabel lblBannerTitle, lblBannerSub;
    
    private RoundedPanel panelStatistik;
    private JLabel lblStatTitle, lblRataNilai, lblNilaiAngka, lblKehadiran;

    public FrmMenuSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Dashboard Siswa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
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

        // Avatar Bulat (Foto Profil inisial "AR" untuk Ahmad Rizki)
        profilAvatar = new AvatarBulat("AP", new Color(46, 204, 113));
        profilAvatar.setBounds(90, 45, 100, 100);
        sidebarPanel.add(profilAvatar);

        lblNamaSiswa = new JLabel("Andi Pratama", JLabel.CENTER);
        lblNamaSiswa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNamaSiswa.setForeground(Color.WHITE);
        lblNamaSiswa.setBounds(15, 160, 250, 25);
        sidebarPanel.add(lblNamaSiswa);

        lblNisSiswa = new JLabel("NIS. 10118021", JLabel.CENTER);
        lblNisSiswa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNisSiswa.setForeground(new Color(180, 180, 200));
        lblNisSiswa.setBounds(15, 185, 250, 20);
        sidebarPanel.add(lblNisSiswa);

        // --- BUTTON SIDEBAR MENU ---
        
        // Menu Tugas (Ada Badge Bulat "2")
        btnMenuTugas = new RoundedButton("TUGAS SAYA", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuTugas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuTugas.setForeground(Color.WHITE);
        btnMenuTugas.setBounds(30, 240, 220, 45);
        btnMenuTugas.addActionListener(e -> {
            new FrmTugasSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuTugas);

        badgeTugasAktif = new BadgeBulat("2", new Color(231, 76, 60));
        badgeTugasAktif.setBounds(210, 250, 25, 25);
        sidebarPanel.add(badgeTugasAktif);

        // Menu Kuis (Ada Badge Bulat "1")
        btnMenuKuis = new RoundedButton("KUIS SAYA", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuKuis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuKuis.setForeground(Color.WHITE);
        btnMenuKuis.setBounds(30, 305, 220, 45);
        btnMenuKuis.addActionListener(e -> {
            new FrmKuisSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuKuis);

        badgeKuisAktif = new BadgeBulat("1", new Color(231, 76, 60));
        badgeKuisAktif.setBounds(210, 315, 25, 25);
        sidebarPanel.add(badgeKuisAktif);

        // Menu Materi
        btnMenuMateri = new RoundedButton("UNDUH MATERI", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuMateri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuMateri.setForeground(Color.WHITE);
        btnMenuMateri.setBounds(30, 370, 220, 45);
        btnMenuMateri.addActionListener(e -> {
            new FrmMateriSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuMateri);

        // Menu Nilai
        btnMenuNilai = new RoundedButton("NILAI SAYA", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuNilai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuNilai.setForeground(Color.WHITE);
        btnMenuNilai.setBounds(30, 425, 220, 45);
        btnMenuNilai.addActionListener(e -> {
            new FrmNilaiSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuNilai);

        // Menu Jadwal
        btnMenuJadwal = new RoundedButton("JADWAL PELAJARAN", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuJadwal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuJadwal.setForeground(Color.WHITE);
        btnMenuJadwal.setBounds(30, 480, 220, 45);
        btnMenuJadwal.addActionListener(e -> {
            new FrmJadwalSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuJadwal);

        // Menu Forum
        btnMenuForum = new RoundedButton("FORUM DISKUSI", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMenuForum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMenuForum.setForeground(Color.WHITE);
        btnMenuForum.setBounds(30, 535, 220, 45);
        btnMenuForum.addActionListener(e -> {
            new FrmForumSiswa().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMenuForum);

        // Tombol Logout
        btnLogout = new RoundedButton("LOGOUT SYSTEM", 10, new Color(198, 40, 40), new Color(160, 30, 30));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBounds(30, 590, 220, 45);
        btnLogout.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
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

        lblBannerTitle = new JLabel("Selamat Datang Kembali, Andi!");
        lblBannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBannerTitle.setForeground(Color.WHITE);
        lblBannerTitle.setBounds(30, 45, 500, 30);
        panelBanner.add(lblBannerTitle);

        lblBannerSub = new JLabel("Kamu memiliki total 3 tugas akademik dan kuis yang belum diselesaikan.");
        lblBannerSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBannerSub.setForeground(new Color(200, 214, 229));
        lblBannerSub.setBounds(30, 80, 550, 20);
        panelBanner.add(lblBannerSub);

        // Kotak Statistik Nilai (RoundedPanel)
        panelStatistik = new RoundedPanel(20, Color.WHITE, true);
        panelStatistik.setBounds(310, 250, 640, 315);
        panelStatistik.setLayout(null);
        bgPanel.add(panelStatistik);

        lblStatTitle = new JLabel("Ringkasan Akademik Anda");
        lblStatTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatTitle.setForeground(new Color(26, 26, 46));
        lblStatTitle.setBounds(35, 25, 300, 25);
        panelStatistik.add(lblStatTitle);

        lblRataNilai = new JLabel("Rata-rata Nilai:");
        lblRataNilai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRataNilai.setForeground(Color.GRAY);
        lblRataNilai.setBounds(35, 90, 150, 20);
        panelStatistik.add(lblRataNilai);

        lblNilaiAngka = new JLabel("88.5");
        lblNilaiAngka.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblNilaiAngka.setForeground(new Color(46, 204, 113));
        lblNilaiAngka.setBounds(35, 115, 200, 70);
        panelStatistik.add(lblNilaiAngka);

        lblKehadiran = new JLabel("Persentase Kehadiran:  98% (Hadir)");
        lblKehadiran.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKehadiran.setForeground(new Color(15, 52, 96));
        lblKehadiran.setBounds(35, 210, 350, 25);
        panelStatistik.add(lblKehadiran);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMenuSiswa().setVisible(true));
    }
}