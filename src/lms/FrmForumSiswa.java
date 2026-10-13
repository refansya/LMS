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

public class FrmForumSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- AREA ATAS: MENU AKSI ---
    private RoundedButton btnBuatDiskusi;

    // --- CARD POSTINGAN FORUM (RoundedPanel) ---
    private RoundedPanel panelForumCard;
    private AvatarBulat avatarPengirim;
    private JLabel lblNamaPengirim, lblRolePengirim, lblPostTime;
    private JTextArea txtKontenDiskusi;
    private RoundedButton btnBalasPost, btnLaporkan;

    public FrmForumSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Forum Diskusi Siswa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 560);
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
        bgPanel.setBounds(0, 0, 900, 560);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("FORUM DISKUSI KELAS");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 350, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(740, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuSiswa().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // Tombol Buat Diskusi Baru (+ Buat Diskusi Baru)
        btnBuatDiskusi = new RoundedButton("+ BUAT DISKUSI BARU", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnBuatDiskusi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuatDiskusi.setForeground(Color.WHITE);
        btnBuatDiskusi.setBounds(40, 80, 220, 40);
        btnBuatDiskusi.addActionListener(e -> {
            String topik = JOptionPane.showInputDialog(this, "Masukkan judul topik diskusi Anda:");
            if (topik != null && !topik.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Topik '" + topik + "' berhasil diunggah!");
            }
        });
        bgPanel.add(btnBuatDiskusi);

        // ==========================================
        // CARD DISKUSI FORUM (RoundedPanel + Avatar)
        // ==========================================
        panelForumCard = new RoundedPanel(15, Color.WHITE, true);
        panelForumCard.setBounds(40, 140, 810, 330);
        panelForumCard.setLayout(null);
        bgPanel.add(panelForumCard);

        // Avatar Bulat (Menggunakan inisial "AR" untuk Ahmad Rizki)
        avatarPengirim = new AvatarBulat("AR", new Color(52, 152, 219));
        avatarPengirim.setBounds(25, 25, 55, 55);
        panelForumCard.add(avatarPengirim);

        lblNamaPengirim = new JLabel("Ahmad Rizki");
        lblNamaPengirim.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaPengirim.setForeground(new Color(26, 26, 46));
        lblNamaPengirim.setBounds(95, 25, 250, 20);
        panelForumCard.add(lblNamaPengirim);

        lblRolePengirim = new JLabel("Siswa - Kelas VIII-A");
        lblRolePengirim.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRolePengirim.setForeground(Color.GRAY);
        lblRolePengirim.setBounds(95, 45, 150, 15);
        panelForumCard.add(lblRolePengirim);

        lblPostTime = new JLabel("Diposting pada: Hari ini, 13:40 WIB");
        lblPostTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPostTime.setForeground(Color.GRAY);
        lblPostTime.setBounds(95, 60, 250, 15);
        panelForumCard.add(lblPostTime);

        // Isi Teks Diskusi
        txtKontenDiskusi = new JTextArea("Teman-teman, ada yang sudah paham pengerjaan soal "
                + "aljabar nomor 4 di halaman 45 tidak ya? Saya agak bingung pada bagian "
                + "pemfaktoran persamaan kuadratnya. Boleh minta kisi-kisinya di kolom balasan?");
        txtKontenDiskusi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKontenDiskusi.setWrapStyleWord(true);
        txtKontenDiskusi.setLineWrap(true);
        txtKontenDiskusi.setEditable(false);
        txtKontenDiskusi.setBackground(new Color(250, 250, 250));
        txtKontenDiskusi.setBounds(25, 100, 760, 120);
        panelForumCard.add(txtKontenDiskusi);

        // Tombol Balas
        btnBalasPost = new RoundedButton("BALAS DISKUSI", 8, new Color(15, 52, 96), new Color(10, 36, 66));
        btnBalasPost.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBalasPost.setForeground(Color.WHITE);
        btnBalasPost.setBounds(25, 260, 160, 40);
        btnBalasPost.addActionListener(e -> {
            String jawaban = JOptionPane.showInputDialog(this, "Kirim balasan Anda:");
            if (jawaban != null && !jawaban.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Balasan berhasil dikirim ke forum!");
            }
        });
        panelForumCard.add(btnBalasPost);

        // Tombol Laporkan Postingan
        btnLaporkan = new RoundedButton("LAPORKAN", 8, new Color(108, 117, 125), new Color(198, 40, 40));
        btnLaporkan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLaporkan.setForeground(Color.WHITE);
        btnLaporkan.setBounds(200, 260, 120, 40);
        btnLaporkan.addActionListener(e -> JOptionPane.showMessageDialog(this, "Postingan dilaporkan ke moderator kelas."));
        panelForumCard.add(btnLaporkan);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmForumSiswa().setVisible(true));
    }
}