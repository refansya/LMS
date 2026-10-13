package lms;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ultro
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FrmMenuAdmin extends javax.swing.JFrame {

    // 1. Deklarasi Komponen Sidebar (Kiri)
    private JPanel sidebarPanel;
    private AvatarBulat avatarProfile;
    private JLabel lblNamaAdmin, lblRole;
    private RoundedButton btnDashboard, btnDataGuru, btnDataSiswa, btnKelas, btnMapel, btnJadwal, btnPengumuman, btnLaporan, btnKeluar;
    private BadgeBulat badgePengumuman;

    // 2. Deklarasi Komponen Main Content (Kanan)
    private JPanel mainContentPanel;
    private JLabel lblHeaderTitle;
    
    // 4 Kotak Statistik Dashboard
    private RoundedPanel cardSiswa, cardGuru, cardKelas, cardMapel;
    private JLabel lblSiswaVal, lblSiswaTitle;
    private JLabel lblGuruVal, lblGuruTitle;
    private JLabel lblKelasVal, lblKelasTitle;
    private JLabel lblMapelVal, lblMapelTitle;

    /**
     * Creates new form FrmMenuAdmin
     */
    public FrmMenuAdmin() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        // --- WINDOW SETTINGS ---
        setTitle("LMS UPTD SMPN 33 Depok - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null); // Absolute layout

        // ==========================================
        // I. SIDEBAR PANEL (KIRI)
        // ==========================================
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Gradasi Gelap Elegan untuk Sidebar
                GradientPaint gp = new GradientPaint(0, 0, new Color(21, 21, 30), 0, getHeight(), new Color(32, 32, 48));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setBounds(0, 0, 250, 650);
        sidebarPanel.setLayout(null);
        getContentPane().add(sidebarPanel);

        // Avatar Profil Bulat ("A" untuk Admin)
        // (Asumsi AvatarBulat memiliki constructor AvatarBulat(String inisial, int ukuran))
        avatarProfile = new AvatarBulat("A", new Color(26, 26, 46));
        avatarProfile.setBounds(85, 40, 80, 80);
        sidebarPanel.add(avatarProfile);

        // Nama & Role Admin
        lblNamaAdmin = new JLabel("Administrator");
        lblNamaAdmin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaAdmin.setForeground(Color.WHITE);
        lblNamaAdmin.setHorizontalAlignment(SwingConstants.CENTER);
        lblNamaAdmin.setBounds(0, 130, 250, 20);
        sidebarPanel.add(lblNamaAdmin);

        lblRole = new JLabel("UPTD SMPN 33 Depok");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(150, 150, 170));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);
        lblRole.setBounds(0, 150, 250, 15);
        sidebarPanel.add(lblRole);

        // --- TOMBOL NAVIGASI SIDEBAR ---
        // Dashboard
        btnDashboard = new RoundedButton("Dashboard", 10, new Color(40, 42, 58), new Color(233, 69, 96));
        btnDashboard.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDashboard.setForeground(Color.WHITE);
        btnDashboard.setBounds(20, 185, 210, 38);
        sidebarPanel.add(btnDashboard);

        // Data Guru
        btnDataGuru = new RoundedButton("Data Guru", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnDataGuru.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDataGuru.setForeground(Color.WHITE);
        btnDataGuru.setBounds(20, 227, 210, 38);
        sidebarPanel.add(btnDataGuru);
        btnDataGuru.addActionListener(e -> {
            new FrmDataGuru().setVisible(true);
            this.dispose(); // Tutup menu utama agar rapi
        });

        // Data Siswa
        btnDataSiswa = new RoundedButton("Data Siswa", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnDataSiswa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDataSiswa.setForeground(Color.WHITE);
        btnDataSiswa.setBounds(20, 269, 210, 38);
        sidebarPanel.add(btnDataSiswa);
        btnDataSiswa.addActionListener(e -> {
            new FrmDataSiswa().setVisible(true);
            this.dispose(); // Tutup menu utama agar rapi
        });

        // Kelas
        btnKelas = new RoundedButton("Kelas", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnKelas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKelas.setForeground(Color.WHITE);
        btnKelas.setBounds(20, 311, 210, 38);
        sidebarPanel.add(btnKelas);
        btnKelas.addActionListener(e -> {
            new FrmKelas().setVisible(true);
            this.dispose();
        });

        // Mapel
        btnMapel = new RoundedButton("Mata Pelajaran", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnMapel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMapel.setForeground(Color.WHITE);
        btnMapel.setBounds(20, 353, 210, 38);
        sidebarPanel.add(btnMapel);
        btnMapel.addActionListener(e -> {
            new FrmMapel().setVisible(true);
            this.dispose();
        });

        // Jadwal
        btnJadwal = new RoundedButton("Jadwal Pelajaran", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnJadwal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnJadwal.setForeground(Color.WHITE);
        btnJadwal.setBounds(20, 395, 210, 38);
        sidebarPanel.add(btnJadwal);
        btnJadwal.addActionListener(e -> {
            new FrmJadwal().setVisible(true);
            this.dispose();
        });

        // Pengumuman (Dilengkapi dengan BadgeBulat merah)
        btnPengumuman = new RoundedButton("Pengumuman", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnPengumuman.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPengumuman.setForeground(Color.WHITE);
        btnPengumuman.setBounds(20, 437, 210, 38);
        sidebarPanel.add(btnPengumuman);
        btnPengumuman.addActionListener(e -> {
            new FrmPengumuman().setVisible(true);
            this.dispose();
        });

        // Badge Notifikasi Angka Merah di atas tombol Pengumuman
        // (Asumsi BadgeBulat adalah JLabel bulat merah)
        badgePengumuman = new BadgeBulat(new Color(233, 69, 96));
        badgePengumuman.setText("5");
        badgePengumuman.setBounds(190, 447, 20, 20);
        sidebarPanel.add(badgePengumuman);
        sidebarPanel.setComponentZOrder(badgePengumuman, 0); // Menaruh badge di paling depan

        // Laporan
        btnLaporan = new RoundedButton("Laporan", 10, new Color(32, 32, 48), new Color(233, 69, 96));
        btnLaporan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLaporan.setForeground(Color.WHITE);
        btnLaporan.setBounds(20, 479, 210, 38);
        sidebarPanel.add(btnLaporan);
        btnLaporan.addActionListener(e -> {
            new FrmLaporan().setVisible(true);
            this.dispose();
        });

        // Tombol Keluar
        btnKeluar = new RoundedButton("LOGOUT", 10, new Color(198, 40, 40), new Color(255, 92, 119));
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBounds(20, 550, 210, 38);
        btnKeluar.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnKeluar);


        // ==========================================
        // II. MAIN CONTENT PANEL (KANAN)
        // ==========================================
        mainContentPanel = new JPanel();
        mainContentPanel.setBackground(new Color(245, 246, 250)); // Background abu-abu terang yang bersih
        mainContentPanel.setBounds(250, 0, 750, 650);
        mainContentPanel.setLayout(null);
        getContentPane().add(mainContentPanel);

        // Header Title
        lblHeaderTitle = new JLabel("DASHBOARD UTAMA");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeaderTitle.setForeground(new Color(40, 42, 58));
        lblHeaderTitle.setBounds(30, 30, 300, 30);
        mainContentPanel.add(lblHeaderTitle);

        // --- 4 KOTAK STATISTIK (RoundedPanel) ---
        
        cardSiswa = new RoundedPanel(15, Color.WHITE, true);
cardSiswa.setBounds(30, 90, 150, 100);
cardSiswa.setLayout(null);
mainContentPanel.add(cardSiswa);
// Tambahkan aksi klik pada kartu statistik siswa
cardSiswa.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        new FrmDataSiswa().setVisible(true);
        dispose();
    }
    @Override
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        cardSiswa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
});

        lblSiswaVal = new JLabel("320");
        lblSiswaVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblSiswaVal.setForeground(new Color(26, 26, 46));
        lblSiswaVal.setBounds(15, 15, 120, 35);
        cardSiswa.add(lblSiswaVal);

        lblSiswaTitle = new JLabel("Total Siswa");
        lblSiswaTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSiswaTitle.setForeground(Color.GRAY);
        lblSiswaTitle.setBounds(15, 55, 120, 20);
        cardSiswa.add(lblSiswaTitle);

        cardGuru = new RoundedPanel(15, Color.WHITE, true);
cardGuru.setBounds(200, 90, 150, 100);
cardGuru.setLayout(null);
mainContentPanel.add(cardGuru);
// Tambahkan aksi klik pada kartu statistik guru
cardGuru.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        new FrmDataGuru().setVisible(true);
        dispose();
    }
    @Override
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        cardGuru.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
});

        lblGuruVal = new JLabel("45");
        lblGuruVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGuruVal.setForeground(new Color(26, 26, 46));
        lblGuruVal.setBounds(15, 15, 120, 35);
        cardGuru.add(lblGuruVal);

        lblGuruTitle = new JLabel("Total Guru");
        lblGuruTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGuruTitle.setForeground(Color.GRAY);
        lblGuruTitle.setBounds(15, 55, 120, 20);
        cardGuru.add(lblGuruTitle);

        // 3. Kotak Statistik Kelas
        cardKelas = new RoundedPanel(15, Color.WHITE, true);
        cardKelas.setBounds(370, 90, 150, 100);
        cardKelas.setLayout(null);
        mainContentPanel.add(cardKelas);
        // Tambahkan aksi klik pada kartu statistik kelas
        cardKelas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new FrmKelas().setVisible(true);
                dispose(); // Menutup menu utama
            }

            // Efek tambahan: Mengubah kursor menjadi icon tangan (Hand Cursor) saat dilewati mouse
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardKelas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        });

        lblKelasVal = new JLabel("12");
        lblKelasVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblKelasVal.setForeground(new Color(26, 26, 46));
        lblKelasVal.setBounds(15, 15, 120, 35);
        cardKelas.add(lblKelasVal);

        lblKelasTitle = new JLabel("Total Kelas");
        lblKelasTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKelasTitle.setForeground(Color.GRAY);
        lblKelasTitle.setBounds(15, 55, 120, 20);
        cardKelas.add(lblKelasTitle);

        // 4. Kotak Statistik Mata Pelajaran (Mapel)
        cardMapel = new RoundedPanel(15, Color.WHITE, true);
        cardMapel.setBounds(540, 90, 150, 100);
        cardMapel.setLayout(null);
        mainContentPanel.add(cardMapel);
        cardMapel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new FrmMapel().setVisible(true);
                dispose();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardMapel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        });

        lblMapelVal = new JLabel("18");
        lblMapelVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblMapelVal.setForeground(new Color(26, 26, 46));
        lblMapelVal.setBounds(15, 15, 120, 35);
        cardMapel.add(lblMapelVal);

        lblMapelTitle = new JLabel("Mata Pelajaran");
        lblMapelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMapelTitle.setForeground(Color.GRAY);
        lblMapelTitle.setBounds(15, 55, 120, 20);
        cardMapel.add(lblMapelTitle);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Gagal menerapkan tampilan Nimbus: " + ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FrmMenuAdmin().setVisible(true);
        });
    }                   
                    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
