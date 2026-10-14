package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
        Session.clear(); // setiap kembali ke halaman login, session dibersihkan
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new java.awt.Dimension(800, 550));
        getContentPane().setLayout(null);

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

        containerPanel = new RoundedPanel(20, new Color(240, 242, 245), true);
        containerPanel.setBounds(200, 30, 400, 460);
        containerPanel.setLayout(null);
        bgPanel.add(containerPanel);

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

        lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(15, 52, 96));
        lblUser.setBounds(40, 100, 200, 20);
        containerPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(40, 125, 320, 35);
        containerPanel.add(txtUsername);

        lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(15, 52, 96));
        lblPass.setBounds(40, 175, 200, 20);
        containerPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(40, 200, 320, 35);
        containerPanel.add(txtPassword);

        JLabel lblRole = new JLabel("Login Sebagai");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(new Color(15, 52, 96));
        lblRole.setBounds(40, 245, 200, 20);
        containerPanel.add(lblRole);

        cmbRole = new javax.swing.JComboBox<>(new String[]{"admin", "guru", "siswa"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRole.setBounds(40, 268, 320, 35);
        containerPanel.add(cmbRole);

        btnMasuk = new RoundedButton("MASUK", 10, new Color(245, 89, 112), new Color(220, 60, 85));
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setBounds(40, 320, 320, 40);
        btnMasuk.addActionListener(e -> prosesLogin());
        containerPanel.add(btnMasuk);

        btnKeluar = new RoundedButton("KELUAR", 10, new Color(108, 117, 125), new Color(80, 85, 90));
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBounds(40, 370, 320, 40);
        btnKeluar.addActionListener(e -> System.exit(0));
        containerPanel.add(btnKeluar);

        // Tekan Enter = klik MASUK
        getRootPane().setDefaultButton(btnMasuk);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this,
                    "Tidak bisa konek ke database.\nPastikan MySQL di XAMPP sudah running!",
                    "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT id_user, username, role, nama_lengkap, status "
                       + "FROM tb_user WHERE username=? AND password=? AND role=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this,
                            "Username, password, atau role salah!",
                            "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                        return;
                    }

                    if (!"aktif".equalsIgnoreCase(rs.getString("status"))) {
                        JOptionPane.showMessageDialog(this,
                            "Akun Anda nonaktif. Hubungi administrator.",
                            "Akun Nonaktif", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    Session.muat(con,
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("nama_lengkap"));
                }
            }

            switch (role) {
                case "admin": new FrmMenuAdmin().setVisible(true); break;
                case "guru":  new FrmMenuGuru().setVisible(true);  break;
                case "siswa": new FrmMenuSiswa().setVisible(true); break;
            }
            this.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}