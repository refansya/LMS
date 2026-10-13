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
import javax.swing.SwingConstants;

public class FrmMenuGuru extends javax.swing.JFrame {

    // Komponen Layout Utama
    private JPanel sidebarPanel, mainContentPanel;
    
    // --- SIDEBAR GURU ---
    private AvatarBulat profilAvatar;
    private JLabel lblNamaGuru, lblNipGuru;
    private RoundedButton btnMateri, btnTugas, btnKuis, btnAbsensi, btnNilai, btnForum, btnKeluar;
    private BadgeBulat badgeTugas; // Notifikasi tugas masuk

    // --- MAIN CONTENT ---
    private JLabel lblWelcome, lblTanggal;
    private RoundedPanel cardKelas, cardPenilaian;
    private JLabel lblCardKelasTitle, lblCardKelasDesc, lblCardNilaiTitle, lblCardNilaiDesc;

    public FrmMenuGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Portal Guru");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
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

        // AvatarBulat profil "Bu Sari"
        // Menggunakan inisial huruf "S" (Default jika belum diset gambar internal)
        profilAvatar = new AvatarBulat("S", new Color(233, 69, 96));
        profilAvatar.setBounds(75, 40, 100, 100);
        sidebarPanel.add(profilAvatar);

        // Nama & NIP Guru
        lblNamaGuru = new JLabel("Budi Santoso, M.Pd.");
        lblNamaGuru.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaGuru.setForeground(Color.WHITE);
        lblNamaGuru.setHorizontalAlignment(SwingConstants.CENTER);
        lblNamaGuru.setBounds(10, 155, 230, 25);
        sidebarPanel.add(lblNamaGuru);

        lblNipGuru = new JLabel("NIP. 198204122009122003");
        lblNipGuru.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNipGuru.setForeground(new Color(180, 180, 200));
        lblNipGuru.setHorizontalAlignment(SwingConstants.CENTER);
        lblNipGuru.setBounds(10, 180, 230, 20);
        sidebarPanel.add(lblNipGuru);

        // --- TOMBOL NAVIGASI SIDEBAR ---
        // Tombol Manajemen Materi
        btnMateri = new RoundedButton("MANAJEMEN MATERI", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnMateri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMateri.setForeground(Color.WHITE);
        btnMateri.setBounds(20, 240, 210, 45);
        btnMateri.addActionListener(e -> {
            new FrmMateriGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnMateri);

        // Tombol Tugas (Dilengkapi Badge Bulat Notifikasi)
        btnTugas = new RoundedButton("TUGAS MASUK", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnTugas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTugas.setForeground(Color.WHITE);
        btnTugas.setBounds(20, 305, 210, 45);
        btnTugas.addActionListener(e -> {
            new FrmTugasGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnTugas);

        // Badge Bulat Notifikasi Tugas Masuk (Menunjukkan ada 5 tugas baru belum dinilai)
        badgeTugas = new BadgeBulat("5", new Color(231, 76, 60));
        badgeTugas.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeTugas.setBounds(190, 295, 24, 24);
        sidebarPanel.add(badgeTugas);

        // Tombol Kuis Online
        btnKuis = new RoundedButton("KUIS ONLINE", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnKuis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKuis.setForeground(Color.WHITE);
        btnKuis.setBounds(20, 365, 210, 45);
        btnKuis.addActionListener(e -> {
            new FrmKuisGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnKuis);

        // Tombol Absensi
        btnAbsensi = new RoundedButton("ABSENSI SISWA", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnAbsensi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbsensi.setForeground(Color.WHITE);
        btnAbsensi.setBounds(20, 420, 210, 45);
        btnAbsensi.addActionListener(e -> {
            new FrmAbsensiGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnAbsensi);

        // Tombol Nilai
        btnNilai = new RoundedButton("INPUT NILAI", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnNilai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNilai.setForeground(Color.WHITE);
        btnNilai.setBounds(20, 475, 210, 45);
        btnNilai.addActionListener(e -> {
            new FrmNilaiGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnNilai);

        // Tombol Forum Diskusi
        btnForum = new RoundedButton("FORUM DISKUSI", 10, new Color(26, 26, 46), new Color(22, 22, 40));
        btnForum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnForum.setForeground(Color.WHITE);
        btnForum.setBounds(20, 530, 210, 45);
        btnForum.addActionListener(e -> {
            new FrmForumGuru().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnForum);

        // Tombol Keluar (Logout)
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
        // 2. MAIN CONTENT PANEL (Warna Latar Terang)
        // ==========================================
        mainContentPanel = new JPanel();
        mainContentPanel.setBackground(new Color(245, 247, 250));
        mainContentPanel.setBounds(250, 0, 750, 650);
        mainContentPanel.setLayout(null);
        getContentPane().add(mainContentPanel);

        // Salam Selamat Datang
        lblWelcome = new JLabel("Selamat Datang, Pak Budi!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(15, 52, 96));
        lblWelcome.setBounds(40, 35, 400, 30);
        mainContentPanel.add(lblWelcome);

        lblTanggal = new JLabel("Portal LMS Guru UPTD SMPN 33 Depok");
        lblTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTanggal.setForeground(Color.GRAY);
        lblTanggal.setBounds(40, 65, 400, 20);
        mainContentPanel.add(lblTanggal);

        // --- DASHBOARD CARDS (RoundedPanel) ---
        
        // Card 1: Kelas diampu
        cardKelas = new RoundedPanel(25, Color.WHITE, true);
        cardKelas.setBounds(40, 130, 310, 180);
        cardKelas.setLayout(null);
        mainContentPanel.add(cardKelas);

        lblCardKelasTitle = new JLabel("🏫 Kelas Yang Diampu");
        lblCardKelasTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCardKelasTitle.setForeground(new Color(15, 52, 96));
        lblCardKelasTitle.setBounds(20, 20, 250, 25);
        cardKelas.add(lblCardKelasTitle);

        lblCardKelasDesc = new JLabel("<html><ul><li>VIII-A (Matematika)</li><li>VIII-B (Matematika)</li><li>IX-C (Informatika)</li></ul></html>");
        lblCardKelasDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCardKelasDesc.setForeground(Color.DARK_GRAY);
        lblCardKelasDesc.setBounds(20, 55, 270, 100);
        cardKelas.add(lblCardKelasDesc);

        // Card 2: Tugas perlu dinilai
        cardPenilaian = new RoundedPanel(25, Color.WHITE, true);
        cardPenilaian.setBounds(380, 130, 320, 180);
        cardPenilaian.setLayout(null);
        mainContentPanel.add(cardPenilaian);

        lblCardNilaiTitle = new JLabel("📝 Tugas Perlu Dinilai");
        lblCardNilaiTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCardNilaiTitle.setForeground(new Color(233, 69, 96));
        lblCardNilaiTitle.setBounds(20, 20, 250, 25);
        cardPenilaian.add(lblCardNilaiTitle);

        lblCardNilaiDesc = new JLabel("<html>Ada total <b>5 tugas</b> pengumpulan baru dari siswa kelas VIII-A yang perlu segera Anda periksa dan beri penilaian akademik.</html>");
        lblCardNilaiDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCardNilaiDesc.setForeground(Color.DARK_GRAY);
        lblCardNilaiDesc.setBounds(20, 55, 280, 100);
        cardPenilaian.add(lblCardNilaiDesc);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMenuGuru().setVisible(true));
    }
}