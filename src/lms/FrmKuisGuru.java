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
import javax.swing.JProgressBar;

public class FrmKuisGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- GRID KUIS 1 (RoundedPanel) ---
    private RoundedPanel panelKuis1;
    private JLabel lblKuis1Title, lblKuis1Progress, lblKuis1Sub;
    private JProgressBar barKuis1;
    private RoundedButton btnHasil1, btnEdit1;

    // --- GRID KUIS 2 (RoundedPanel) ---
    private RoundedPanel panelKuis2;
    private JLabel lblKuis2Title, lblKuis2Progress, lblKuis2Sub;
    private JProgressBar barKuis2;
    private RoundedButton btnHasil2, btnEdit2;

    // Tombol Buat Kuis Baru
    private RoundedButton btnBuatKuis;

    public FrmKuisGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Manajemen Kuis Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // Background Gradasi
        bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("MANAJEMEN KUIS & EVALUASI");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(840, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuGuru().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // Tombol Buat Kuis Baru
        btnBuatKuis = new RoundedButton("+ BUAT KUIS BARU", 12, new Color(46, 204, 113), new Color(39, 174, 96));
        btnBuatKuis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuatKuis.setForeground(Color.WHITE);
        btnBuatKuis.setBounds(40, 80, 200, 45);
        btnBuatKuis.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Membuka formulir pembuatan kuis akademik baru...");
        });
        bgPanel.add(btnBuatKuis);

        // ==========================================
        // 1. CARD ITEM KUIS 1
        // ==========================================
        panelKuis1 = new RoundedPanel(20, Color.WHITE, true);
        panelKuis1.setBounds(40, 150, 430, 250);
        panelKuis1.setLayout(null);
        bgPanel.add(panelKuis1);

        lblKuis1Title = new JLabel("Kuis 01: Persamaan Kuadrat");
        lblKuis1Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKuis1Title.setForeground(new Color(15, 52, 96));
        lblKuis1Title.setBounds(25, 20, 350, 25);
        panelKuis1.add(lblKuis1Title);

        lblKuis1Sub = new JLabel("Mata Pelajaran: Matematika Kelas VIII-A");
        lblKuis1Sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKuis1Sub.setForeground(Color.GRAY);
        lblKuis1Sub.setBounds(25, 45, 300, 20);
        panelKuis1.add(lblKuis1Sub);

        // Progress Bar Pengumpulan
        lblKuis1Progress = new JLabel("Partisipasi Pengerjaan Siswa (24 / 30)");
        lblKuis1Progress.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblKuis1Progress.setForeground(new Color(26, 26, 46));
        lblKuis1Progress.setBounds(25, 100, 300, 20);
        panelKuis1.add(lblKuis1Progress);

        barKuis1 = new JProgressBar(0, 30);
        barKuis1.setValue(24);
        barKuis1.setForeground(new Color(46, 204, 113));
        barKuis1.setBounds(25, 125, 380, 20);
        panelKuis1.add(barKuis1);

        // Aksi Kuis 1
        btnEdit1 = new RoundedButton("EDIT", 8, new Color(241, 196, 15), new Color(212, 172, 13));
        btnEdit1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEdit1.setForeground(Color.WHITE);
        btnEdit1.setBounds(25, 180, 175, 40);
        panelKuis1.add(btnEdit1);

        btnHasil1 = new RoundedButton("LIHAT HASIL", 8, new Color(52, 152, 219), new Color(41, 128, 185));
        btnHasil1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHasil1.setForeground(Color.WHITE);
        btnHasil1.setBounds(230, 180, 175, 40);
        btnHasil1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Membuka lembar statistik nilai kuis 01."));
        panelKuis1.add(btnHasil1);

        // ==========================================
        // 2. CARD ITEM KUIS 2
        // ==========================================
        panelKuis2 = new RoundedPanel(20, Color.WHITE, true);
        panelKuis2.setBounds(510, 150, 430, 250);
        panelKuis2.setLayout(null);
        bgPanel.add(panelKuis2);

        lblKuis2Title = new JLabel("Kuis 02: Sistem Pertidaksamaan");
        lblKuis2Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKuis2Title.setForeground(new Color(15, 52, 96));
        lblKuis2Title.setBounds(25, 20, 350, 25);
        panelKuis2.add(lblKuis2Title);

        lblKuis2Sub = new JLabel("Mata Pelajaran: Matematika Kelas VIII-B");
        lblKuis2Sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKuis2Sub.setForeground(Color.GRAY);
        lblKuis2Sub.setBounds(25, 45, 300, 20);
        panelKuis2.add(lblKuis2Sub);

        // Progress Bar Pengumpulan
        lblKuis2Progress = new JLabel("Partisipasi Pengerjaan Siswa (10 / 30)");
        lblKuis2Progress.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblKuis2Progress.setForeground(new Color(26, 26, 46));
        lblKuis2Progress.setBounds(25, 100, 300, 20);
        panelKuis2.add(lblKuis2Progress);

        barKuis2 = new JProgressBar(0, 30);
        barKuis2.setValue(10);
        barKuis2.setForeground(new Color(231, 76, 60));
        barKuis2.setBounds(25, 125, 380, 20);
        panelKuis2.add(barKuis2);

        // Aksi Kuis 2
        btnEdit2 = new RoundedButton("EDIT", 8, new Color(241, 196, 15), new Color(212, 172, 13));
        btnEdit2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEdit2.setForeground(Color.WHITE);
        btnEdit2.setBounds(25, 180, 175, 40);
        panelKuis2.add(btnEdit2);

        btnHasil2 = new RoundedButton("LIHAT HASIL", 8, new Color(52, 152, 219), new Color(41, 128, 185));
        btnHasil2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHasil2.setForeground(Color.WHITE);
        btnHasil2.setBounds(230, 180, 175, 40);
        btnHasil2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Membuka lembar statistik nilai kuis 02."));
        panelKuis2.add(btnHasil2);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmKuisGuru().setVisible(true));
    }
}