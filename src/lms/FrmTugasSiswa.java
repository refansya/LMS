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

public class FrmTugasSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- CARD TUGAS 1 (Kritis - Deadline Besok / Merah) ---
    private RoundedPanel cardTugas1;
    private JLabel lblTag1, lblTitle1, lblDesc1, lblDeadline1;
    private RoundedButton btnKumpul1;

    // --- CARD TUGAS 2 (Sedang - Deadline Minggu Depan / Kuning) ---
    private RoundedPanel cardTugas2;
    private JLabel lblTag2, lblTitle2, lblDesc2, lblDeadline2;
    private RoundedButton btnKumpul2;

    public FrmTugasSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Daftar Tugas Aktif");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 550);
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
        bgPanel.setBounds(0, 0, 950, 550);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("DAFTAR TUGAS MANDIRI");
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
        // 1. CARD TUGAS 1 (MERAH - SANGAT MENDESAK)
        // ==========================================
        cardTugas1 = new RoundedPanel(15, new Color(253, 237, 237), false); // Soft Red BG
        cardTugas1.setBounds(40, 90, 410, 250);
        cardTugas1.setLayout(null);
        bgPanel.add(cardTugas1);

        lblTag1 = new JLabel("SEGERA");
        lblTag1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTag1.setForeground(new Color(198, 40, 40));
        lblTag1.setBounds(25, 20, 80, 20);
        cardTugas1.add(lblTag1);

        lblTitle1 = new JLabel("Tugas 01: Latihan Fungsi Aljabar");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle1.setForeground(new Color(198, 40, 40));
        lblTitle1.setBounds(25, 50, 360, 25);
        cardTugas1.add(lblTitle1);

        lblDesc1 = new JLabel("<html>Selesaikan 5 soal penyederhanaan fungsi aljabar kuadrat pada buku LKS halaman 45 dan kumpulkan dalam format PDF.</html>");
        lblDesc1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc1.setForeground(new Color(110, 50, 50));
        lblDesc1.setBounds(25, 80, 360, 50);
        cardTugas1.add(lblDesc1);

        lblDeadline1 = new JLabel("Deadline: Besok, 17:00 WIB");
        lblDeadline1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDeadline1.setForeground(new Color(198, 40, 40));
        lblDeadline1.setBounds(25, 145, 250, 20);
        cardTugas1.add(lblDeadline1);

        btnKumpul1 = new RoundedButton("KUMPULKAN JAWABAN", 8, new Color(198, 40, 40), new Color(160, 30, 30));
        btnKumpul1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKumpul1.setForeground(Color.WHITE);
        btnKumpul1.setBounds(25, 185, 190, 40);
        btnKumpul1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Silakan pilih berkas PDF jawaban tugas aljabar Anda."));
        cardTugas1.add(btnKumpul1);

        // ==========================================
        // 2. CARD TUGAS 2 (KUNING - URGENSI SEDANG)
        // ==========================================
        cardTugas2 = new RoundedPanel(15, new Color(255, 251, 230), false); // Soft Yellow BG
        cardTugas2.setBounds(490, 90, 410, 250);
        cardTugas2.setLayout(null);
        bgPanel.add(cardTugas2);

        lblTag2 = new JLabel("MENDATANG");
        lblTag2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTag2.setForeground(new Color(212, 143, 0));
        lblTag2.setBounds(25, 20, 100, 20);
        cardTugas2.add(lblTag2);

        lblTitle2 = new JLabel("Tugas 02: Grafik Persamaan Linier");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle2.setForeground(new Color(125, 85, 0));
        lblTitle2.setBounds(25, 50, 360, 25);
        cardTugas2.add(lblTitle2);

        lblDesc2 = new JLabel("<html>Gambarkan grafik himpunan penyelesaian pertidaksamaan linier di kertas millimeter blok, lalu foto dan unggah di sini.</html>");
        lblDesc2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc2.setForeground(new Color(105, 80, 20));
        lblDesc2.setBounds(25, 80, 360, 50);
        cardTugas2.add(lblDesc2);

        lblDeadline2 = new JLabel("Deadline: 21 Juli 2026, 23:59 WIB");
        lblDeadline2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDeadline2.setForeground(new Color(125, 85, 0));
        lblDeadline2.setBounds(25, 145, 250, 20);
        cardTugas2.add(lblDeadline2);

        btnKumpul2 = new RoundedButton("KUMPULKAN JAWABAN", 8, new Color(241, 196, 15), new Color(212, 172, 13));
        btnKumpul2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKumpul2.setForeground(Color.WHITE);
        btnKumpul2.setBounds(25, 185, 190, 40);
        btnKumpul2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Silakan pilih berkas foto jawaban grafik linier Anda."));
        cardTugas2.add(btnKumpul2);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmTugasSiswa().setVisible(true));
    }
}