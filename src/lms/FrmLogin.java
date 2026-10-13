package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FrmLogin extends javax.swing.JFrame {

    private JPanel bgPanel;
    private RoundedPanel containerPanel;
    private JLabel lblTitle, lblSubtitle, lblUser, lblPass;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private javax.swing.JComboBox<String> cmbRole;
    private RoundedButton btnMasuk, btnKeluar;

    public FrmLogin() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // 1. Background Gradient Biru Tua Navy
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
        bgPanel.setBounds(0, 0, 800, 550);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // 2. Container Putih di Tengah (RoundedPanel)
        containerPanel = new RoundedPanel(20, new Color(240, 242, 245), true);
        containerPanel.setBounds(200, 30, 400, 460);
        containerPanel.setLayout(null);
        bgPanel.add(containerPanel);

        // Teks Judul
        lblTitle = new JLabel("SELAMAT DATANG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(26, 26, 46));
        lblTitle.setBounds(0, 30, 400, 30);
        containerPanel.add(lblTitle);

        lblSubtitle = new JLabel("LMS UPTD SMPN 33 Depok", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setBounds(0, 60, 400, 20);
        containerPanel.add(lblSubtitle);

        // Input Username
        lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(15, 52, 96));
        lblUser.setBounds(40, 100, 200, 20);
        containerPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(40, 125, 320, 35);
        containerPanel.add(txtUsername);

        // Input Password
        lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(15, 52, 96));
        lblPass.setBounds(40, 175, 200, 20);
        containerPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(40, 200, 320, 35);
        containerPanel.add(txtPassword);

        // Pilihan Role (Admin / Guru / Siswa)
        JLabel lblRole = new JLabel("Login Sebagai");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(new Color(15, 52, 96));
        lblRole.setBounds(40, 245, 200, 20);
        containerPanel.add(lblRole);

        cmbRole = new javax.swing.JComboBox<>(new String[]{"admin", "guru", "siswa"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRole.setBounds(40, 268, 320, 35);
        containerPanel.add(cmbRole);

        // 3. Tombol MASUK (validasi ke database sesuai role yang dipilih)
        btnMasuk = new RoundedButton("MASUK", 10, new Color(245, 89, 112), new Color(220, 60, 85));
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setBounds(40, 320, 320, 40);
        btnMasuk.addActionListener(e -> btnMasukActionPerformed());
        containerPanel.add(btnMasuk);

        // 4. Tombol KELUAR
        btnKeluar = new RoundedButton("KELUAR", 10, new Color(108, 117, 125), new Color(80, 85, 90));
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBounds(40, 370, 320, 40);
        btnKeluar.addActionListener(e -> System.exit(0));
        containerPanel.add(btnKeluar);
    }

    private void btnMasukActionPerformed() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString();

        System.out.println("[LOGIN] Mencoba login -> username: '" + username + "', role: '" + role + "'");

        if (username.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong!",
                "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            java.sql.Connection con = Koneksi.getKoneksi();
            if (con == null) {
                System.out.println("[LOGIN] GAGAL: koneksi database null. Cek XAMPP MySQL sudah running?");
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running!",
                    "Koneksi Gagal", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.sql.PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM tb_user WHERE username=? AND password=? AND LOWER(role)=LOWER(?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("[LOGIN] SUKSES login sebagai " + role + ", membuka dashboard...");
                switch (role) {
                    case "admin": new FrmMenuAdmin().setVisible(true); break;
                    case "guru": new FrmMenuGuru().setVisible(true); break;
                    case "siswa": new FrmMenuSiswa().setVisible(true); break;
                }
                this.dispose();
            } else {
                System.out.println("[LOGIN] GAGAL: tidak ada baris cocok di tb_user untuk kombinasi ini.");
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Username, password, atau role salah!\n(Cek lagi data di tabel tb_user)",
                    "Login Gagal", javax.swing.JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }
        } catch (Exception ex) {
            System.out.println("[LOGIN] EXCEPTION: " + ex);
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}