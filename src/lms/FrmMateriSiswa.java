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

public class FrmMateriSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- CARD MATERI 1 (PDF) ---
    private RoundedPanel cardMateri1;
    private JLabel lblIcon1, lblTitle1, lblDesc1;
    private RoundedButton btnDownload1;

    // --- CARD MATERI 2 (VIDEO) ---
    private RoundedPanel cardMateri2;
    private JLabel lblIcon2, lblTitle2, lblDesc2;
    private RoundedButton btnTonton2;

    public FrmMateriSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Materi Siswa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // Background Gradasi Utama
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
        bgPanel.setBounds(0, 0, 950, 550);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("MATERI PEMBELAJARAN");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(790, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuSiswa().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // 1. CARD MATERI 1 (PDF - Aljabar)
        // ==========================================
        cardMateri1 = new RoundedPanel(15, Color.WHITE, true);
        cardMateri1.setBounds(40, 90, 410, 240);
        cardMateri1.setLayout(null);
        bgPanel.add(cardMateri1);

        lblIcon1 = new JLabel("[PDF]");
        lblIcon1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIcon1.setForeground(new Color(231, 76, 60)); // Merah PDF
        lblIcon1.setBounds(25, 25, 80, 30);
        cardMateri1.add(lblIcon1);

        lblTitle1 = new JLabel("Bab 1: Konsep Aljabar");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle1.setForeground(new Color(26, 26, 46));
        lblTitle1.setBounds(25, 70, 360, 25);
        cardMateri1.add(lblTitle1);

        lblDesc1 = new JLabel("<html>Dokumen modul belajar konsep dasar variabel, konstanta, dan penyederhanaan fungsi aljabar matematika kelas VIII.</html>");
        lblDesc1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc1.setForeground(Color.GRAY);
        lblDesc1.setBounds(25, 100, 360, 50);
        cardMateri1.add(lblDesc1);

        btnDownload1 = new RoundedButton("DOWNLOAD PDF", 8, new Color(15, 52, 96), new Color(10, 36, 66));
        btnDownload1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDownload1.setForeground(Color.WHITE);
        btnDownload1.setBounds(25, 175, 160, 40);
        btnDownload1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mengunduh file: Bab_1_Konsep_Aljabar.pdf"));
        cardMateri1.add(btnDownload1);

        // ==========================================
        // 2. CARD MATERI 2 (VIDEO - Persamaan Linier)
        // ==========================================
        cardMateri2 = new RoundedPanel(15, Color.WHITE, true);
        cardMateri2.setBounds(490, 90, 410, 240);
        cardMateri2.setLayout(null);
        bgPanel.add(cardMateri2);

        lblIcon2 = new JLabel("[PLAY]");
        lblIcon2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIcon2.setForeground(new Color(46, 204, 113)); // Hijau Video
        lblIcon2.setBounds(25, 25, 85, 30);
        cardMateri2.add(lblIcon2);

        lblTitle2 = new JLabel("Video: Persamaan Linier Satu Variabel");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle2.setForeground(new Color(26, 26, 46));
        lblTitle2.setBounds(25, 70, 360, 25);
        cardMateri2.add(lblTitle2);

        lblDesc2 = new JLabel("<html>Video penjelasan interaktif penyelesaian persamaan matematika linear satu variabel disertai dengan visualisasi contoh nyata.</html>");
        lblDesc2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc2.setForeground(Color.GRAY);
        lblDesc2.setBounds(25, 100, 360, 50);
        cardMateri2.add(lblDesc2);

        btnTonton2 = new RoundedButton("TONTON VIDEO", 8, new Color(46, 204, 113), new Color(39, 174, 96));
        btnTonton2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTonton2.setForeground(Color.WHITE);
        btnTonton2.setBounds(25, 175, 160, 40);
        btnTonton2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Membuka video streaming materi kelas VIII..."));
        cardMateri2.add(btnTonton2);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMateriSiswa().setVisible(true));
    }
}