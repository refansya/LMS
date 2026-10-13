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
import javax.swing.JScrollPane; // <--- Ini import yang tadi kurang
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FrmPengumuman extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL KIRI: FORM KIRIM PENGUMUMAN ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblSubjek, lblIsi;
    private JTextField txtSubjek;
    private JTextArea txtIsi;
    private RoundedButton btnKirim, btnReset;

    // --- PANEL KANAN: CARD LIST PENGUMUMAN AKTIF ---
    private RoundedPanel panelFeed1, panelFeed2;
    private JLabel lblFeedTitle1, lblFeedTitle2;
    private JLabel lblFeedDate1, lblFeedDate2;
    private JLabel lblFeedBody1, lblFeedBody2;

    public FrmPengumuman() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Pengumuman");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
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
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("PENGUMUMAN SEKOLAH");
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
            new FrmMenuAdmin().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // 1. FORM BUAT PENGUMUMAN (Kiri)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        lblFormTitle = new JLabel("Buat Pengumuman Baru");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 250, 25);
        panelForm.add(lblFormTitle);

        // Subjek
        lblSubjek = new JLabel("Judul / Subjek");
        lblSubjek.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSubjek.setForeground(new Color(15, 52, 96));
        lblSubjek.setBounds(25, 70, 300, 20);
        panelForm.add(lblSubjek);

        txtSubjek = new JTextField();
        txtSubjek.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSubjek.setBounds(25, 95, 300, 35);
        txtSubjek.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtSubjek);

        // Isi Pengumuman
        lblIsi = new JLabel("Isi Informasi");
        lblIsi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblIsi.setForeground(new Color(15, 52, 96));
        lblIsi.setBounds(25, 150, 300, 20);
        panelForm.add(lblIsi);

        txtIsi = new JTextArea();
        txtIsi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        
        JScrollPane scrollIsi = new JScrollPane(txtIsi);
        scrollIsi.setBounds(25, 175, 300, 140);
        scrollIsi.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelForm.add(scrollIsi);

        // Tombol Kirim
        btnKirim = new RoundedButton("KIRIM SEKARANG", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnKirim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setBounds(25, 340, 300, 40);
        btnKirim.addActionListener(e -> {
            if (!txtSubjek.getText().isEmpty() && !txtIsi.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pengumuman berhasil disebarkan ke siswa & guru!");
                txtSubjek.setText("");
                txtIsi.setText("");
            }
        });
        panelForm.add(btnKirim);

        // Tombol Reset
        btnReset = new RoundedButton("BATAL", 10, new Color(149, 165, 166), new Color(127, 140, 141));
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setForeground(Color.WHITE);
        btnReset.setBounds(25, 395, 300, 40);
        btnReset.addActionListener(e -> {
            txtSubjek.setText("");
            txtIsi.setText("");
        });
        panelForm.add(btnReset);

        // ==========================================
        // CARD FEED PENGUMUMAN AKTIF (Kanan - Melengkung Indah)
        // ==========================================
        
        // --- FEED 1 (PENGUMUMAN TERBARU) ---
        panelFeed1 = new RoundedPanel(20, Color.WHITE, true);
        panelFeed1.setBounds(410, 80, 540, 230);
        panelFeed1.setLayout(null);
        bgPanel.add(panelFeed1);

        lblFeedTitle1 = new JLabel("🚨 Pembagian Rapor Semester Genap");
        lblFeedTitle1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFeedTitle1.setForeground(new Color(233, 69, 96));
        lblFeedTitle1.setBounds(25, 20, 350, 25);
        panelFeed1.add(lblFeedTitle1);

        lblFeedDate1 = new JLabel("Diposting oleh: Admin • 12 Juli 2026");
        lblFeedDate1.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFeedDate1.setForeground(Color.GRAY);
        lblFeedDate1.setBounds(25, 45, 300, 15);
        panelFeed1.add(lblFeedDate1);

        lblFeedBody1 = new JLabel("<html>Diberitahukan kepada seluruh siswa UPTD SMPN 33 Depok, bahwa pembagian rapor semester genap akan dilaksanakan pada hari Jumat ini. Harap hadir didampingi orang tua/wali masing-masing tepat pukul 08.00 WIB di kelas masing-masing.</html>");
        lblFeedBody1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFeedBody1.setForeground(Color.DARK_GRAY);
        lblFeedBody1.setBounds(25, 75, 490, 120);
        panelFeed1.add(lblFeedBody1);

        // --- FEED 2 (PENGUMUMAN KEDUA) ---
        panelFeed2 = new RoundedPanel(20, Color.WHITE, true);
        panelFeed2.setBounds(410, 340, 540, 230);
        panelFeed2.setLayout(null);
        bgPanel.add(panelFeed2);

        lblFeedTitle2 = new JLabel("📢 Libur Tahun Baru Hijriah");
        lblFeedTitle2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFeedTitle2.setForeground(new Color(15, 52, 96));
        lblFeedTitle2.setBounds(25, 20, 350, 25);
        panelFeed2.add(lblFeedTitle2);

        lblFeedDate2 = new JLabel("Diposting oleh: Kurikulum • 10 Juli 2026");
        lblFeedDate2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFeedDate2.setForeground(Color.GRAY);
        lblFeedDate2.setBounds(25, 45, 300, 15);
        panelFeed2.add(lblFeedDate2);

        lblFeedBody2 = new JLabel("<html>Sehubungan dengan peringatan Tahun Baru Islam, seluruh kegiatan belajar-mengajar di lingkungan sekolah akan diliburkan. KBM akan aktif kembali seperti biasa pada hari Senin berikutnya. Tetap jaga kesehatan selama berlibur!</html>");
        lblFeedBody2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFeedBody2.setForeground(Color.DARK_GRAY);
        lblFeedBody2.setBounds(25, 75, 490, 120);
        panelFeed2.add(lblFeedBody2);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmPengumuman().setVisible(true));
    }
}