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
import javax.swing.JTextArea;

public class FrmForumGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- CARD UTAMA THREAD FORUM (RoundedPanel) ---
    private RoundedPanel panelCardPost;
    private AvatarBulat avatarUser;
    private JLabel lblNamaUser, lblRoleUser, lblPostTime;
    private JTextArea txtAreaKonten;
    private RoundedButton btnBalas, btnBagikan;

    // --- FORM BUAT POSTINGAN BARU ---
    private RoundedPanel panelBuatPost;
    private JLabel lblTulisPost;
    private JTextArea txtInputPost;
    private RoundedButton btnPosting;

    public FrmForumGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Forum Diskusi Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 650);
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
        bgPanel.setBounds(0, 0, 950, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("FORUM DISKUSI AKADEMIK");
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
            new FrmMenuGuru().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // 1. INPUT MEMBUAT POSTINGAN BARU
        // ==========================================
        panelBuatPost = new RoundedPanel(20, Color.WHITE, true);
        panelBuatPost.setBounds(40, 80, 860, 160);
        panelBuatPost.setLayout(null);
        bgPanel.add(panelBuatPost);

        lblTulisPost = new JLabel("Mulai Diskusi Baru");
        lblTulisPost.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTulisPost.setForeground(new Color(26, 26, 46));
        lblTulisPost.setBounds(25, 15, 200, 20);
        panelBuatPost.add(lblTulisPost);

        txtInputPost = new JTextArea("Tulis pertanyaan atau informasi materi kelas di sini...");
        txtInputPost.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtInputPost.setBounds(25, 45, 810, 50);
        txtInputPost.setLineWrap(true);
        panelBuatPost.add(txtInputPost);

        btnPosting = new RoundedButton("POSTING", 8, new Color(46, 204, 113), new Color(39, 174, 96));
        btnPosting.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnPosting.setForeground(Color.WHITE);
        btnPosting.setBounds(715, 105, 120, 35);
        btnPosting.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Thread diskusi baru berhasil diterbitkan!");
            txtInputPost.setText("");
        });
        panelBuatPost.add(btnPosting);

        // ==========================================
        // 2. KARTU POSTINGAN FORUM AKTIF (RoundedPanel + Avatar)
        // ==========================================
        panelCardPost = new RoundedPanel(20, Color.WHITE, true);
        panelCardPost.setBounds(40, 260, 860, 310);
        panelCardPost.setLayout(null);
        bgPanel.add(panelCardPost);

        // Avatar Bulat (Menggunakan inisial nama "SH" - Sari Hartati, M.Pd.)
        avatarUser = new AvatarBulat("SH", new Color(233, 69, 96));
        avatarUser.setBounds(25, 25, 55, 55);
        panelCardPost.add(avatarUser);

        lblNamaUser = new JLabel("Sari Hartati, M.Pd.");
        lblNamaUser.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaUser.setForeground(new Color(26, 26, 46));
        lblNamaUser.setBounds(95, 25, 250, 20);
        panelCardPost.add(lblNamaUser);

        lblRoleUser = new JLabel("Guru Matematika");
        lblRoleUser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRoleUser.setForeground(new Color(46, 204, 113));
        lblRoleUser.setBounds(95, 45, 150, 15);
        panelCardPost.add(lblRoleUser);

        lblPostTime = new JLabel("Diposting pada: Hari ini, 10:15 WIB");
        lblPostTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPostTime.setForeground(Color.GRAY);
        lblPostTime.setBounds(95, 60, 250, 15);
        panelCardPost.add(lblPostTime);

        // Isi Konten Postingan Forum
        txtAreaKonten = new JTextArea("Halo anak-anak kelas VIII-A, silahkan unggah progres pengumpulan "
                + "soal latihan halaman 45 terkait Persamaan Kuadrat di dalam form tugas "
                + "sebelum jam 17:00 WIB sore nanti ya. Jika ada kendala, silakan tanyakan di bawah ini.");
        txtAreaKonten.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAreaKonten.setWrapStyleWord(true);
        txtAreaKonten.setLineWrap(true);
        txtAreaKonten.setEditable(false);
        txtAreaKonten.setBounds(25, 100, 810, 120);
        panelCardPost.add(txtAreaKonten);

        // Tombol Balas
        btnBalas = new RoundedButton("BALAS DISKUSI", 8, new Color(15, 52, 96), new Color(10, 36, 66));
        btnBalas.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBalas.setForeground(Color.WHITE);
        btnBalas.setBounds(25, 245, 160, 40);
        btnBalas.addActionListener(e -> {
            String balasan = JOptionPane.showInputDialog(this, "Tulis balasan Anda:");
            if (balasan != null && !balasan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Balasan berhasil dikirim!");
            }
        });
        panelCardPost.add(btnBalas);

        // Tombol Bagikan
        btnBagikan = new RoundedButton("BAGIKAN LINK", 8, new Color(108, 117, 125), new Color(90, 98, 104));
        btnBagikan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBagikan.setForeground(Color.WHITE);
        btnBagikan.setBounds(200, 245, 150, 40);
        btnBagikan.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tautan forum disalin ke clipboard!"));
        panelCardPost.add(btnBagikan);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmForumGuru().setVisible(true));
    }
}