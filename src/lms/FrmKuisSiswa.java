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

public class FrmKuisSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL INFORMASI INSTRUKSI KUIS ---
    private RoundedPanel panelInstruksi;
    private JLabel lblKuisTitle, lblInfoMataPelajaran, lblWaktu, lblButirSoal;
    private JTextArea txtAreaAturan;
    private RoundedButton btnMulaiKuis;

    public FrmKuisSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Pra-Kuis Ujian");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 520);
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
        bgPanel.setBounds(0, 0, 850, 520);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("SESI UJIAN & KUIS HARIAN");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("BATALKAN", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(690, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuSiswa().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // PANEL INSTRUKSI UTAMA (RoundedPanel)
        // ==========================================
        panelInstruksi = new RoundedPanel(20, Color.WHITE, true);
        panelInstruksi.setBounds(40, 80, 760, 370);
        panelInstruksi.setLayout(null);
        bgPanel.add(panelInstruksi);

        lblKuisTitle = new JLabel("Evaluasi 01: Persamaan Linier Dua Variabel (SPLDV)");
        lblKuisTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblKuisTitle.setForeground(new Color(15, 52, 96));
        lblKuisTitle.setBounds(30, 25, 600, 25);
        panelInstruksi.add(lblKuisTitle);

        lblInfoMataPelajaran = new JLabel("Matematika - Kelas VIII-A");
        lblInfoMataPelajaran.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoMataPelajaran.setForeground(Color.GRAY);
        lblInfoMataPelajaran.setBounds(30, 50, 300, 20);
        panelInstruksi.add(lblInfoMataPelajaran);

        lblWaktu = new JLabel("Durasi: 60 Menit");
        lblWaktu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblWaktu.setForeground(new Color(26, 26, 46));
        lblWaktu.setBounds(30, 95, 200, 20);
        panelInstruksi.add(lblWaktu);

        lblButirSoal = new JLabel("Jumlah: 20 Soal Pilihan Ganda");
        lblButirSoal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblButirSoal.setForeground(new Color(26, 26, 46));
        lblButirSoal.setBounds(250, 95, 250, 20);
        panelInstruksi.add(lblButirSoal);

        // Area Peraturan Kuis
        txtAreaAturan = new JTextArea(
                "PENTING / TATA TERTIB SEBELUM MEMULAI KUIS:\n" +
                "1. Kerjakan kuis ini secara mandiri tanpa menggunakan kalkulator atau bantuan internet.\n" +
                "2. Waktu pengerjaan akan terus berjalan sejak Anda menekan tombol \"MULAI KUIS NOW\".\n" +
                "3. Jangan menutup atau merefresh tab browser / aplikasi selama pengerjaan kuis berlangsung.\n" +
                "4. Sistem akan otomatis mensubmit jawaban ketika sisa durasi pengerjaan Anda habis."
        );
        txtAreaAturan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAreaAturan.setForeground(Color.DARK_GRAY);
        txtAreaAturan.setEditable(false);
        txtAreaAturan.setBackground(new Color(245, 246, 250));
        txtAreaAturan.setBounds(30, 135, 700, 120);
        txtAreaAturan.setLineWrap(true);
        txtAreaAturan.setWrapStyleWord(true);
        panelInstruksi.add(txtAreaAturan);

        // Tombol Mulai Kuis (Hijau emerald sukses)
        btnMulaiKuis = new RoundedButton("MULAI KUIS NOW", 12, new Color(46, 204, 113), new Color(39, 174, 96));
        btnMulaiKuis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMulaiKuis.setForeground(Color.WHITE);
        btnMulaiKuis.setBounds(30, 285, 250, 50);
        btnMulaiKuis.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Apakah Anda yakin ingin memulai kuis ini sekarang?", 
                    "Konfirmasi Mulai", 
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Memulai sesi kuis ujian. Selamat mengerjakan!");
                // Sisi navigasi ke Form Kuis Aktif siswa diletakkan di bawah ini
            }
        });
        panelInstruksi.add(btnMulaiKuis);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmKuisSiswa().setVisible(true));
    }
}