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
import javax.swing.JTextField;

public class FrmNilaiGuru extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- PANEL FORM INPUT NILAI ---
    private RoundedPanel panelInput;
    private JLabel lblTitleForm, lblNamaSiswa, lblTugas, lblKuis, lblUts, lblUas, lblRataRata, lblHasilRata;
    private JTextField txtNamaSiswa, txtTugas, txtKuis, txtUts, txtUas;
    private RoundedButton btnKalkulasi, btnSimpan;

    public FrmNilaiGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Input Nilai Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
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
        bgPanel.setBounds(0, 0, 800, 550);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("PENGINPUTAN NILAI AKADEMIK");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali
        btnKembali = new RoundedButton("KEMBALI", 10, new Color(108, 117, 125), new Color(198, 40, 40));
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setBounds(640, 25, 110, 35);
        btnKembali.addActionListener(e -> {
            new FrmMenuGuru().setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnKembali);

        // ==========================================
        // PANEL INPUT NILAI (RoundedPanel)
        // ==========================================
        panelInput = new RoundedPanel(20, Color.WHITE, true);
        panelInput.setBounds(40, 80, 710, 400);
        panelInput.setLayout(null);
        bgPanel.add(panelInput);

        lblTitleForm = new JLabel("Form Evaluasi Nilai Harian & Ujian");
        lblTitleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleForm.setForeground(new Color(26, 26, 46));
        lblTitleForm.setBounds(30, 20, 300, 25);
        panelInput.add(lblTitleForm);

        // Input Nama Siswa
        lblNamaSiswa = new JLabel("Nama Siswa");
        lblNamaSiswa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNamaSiswa.setBounds(30, 70, 150, 20);
        panelInput.add(lblNamaSiswa);

        txtNamaSiswa = new JTextField();
        txtNamaSiswa.setBounds(30, 95, 300, 35);
        panelInput.add(txtNamaSiswa);

        // Input Nilai Tugas
        lblTugas = new JLabel("Nilai Tugas (30%)");
        lblTugas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTugas.setBounds(30, 150, 130, 20);
        panelInput.add(lblTugas);

        txtTugas = new JTextField();
        txtTugas.setBounds(30, 175, 130, 35);
        panelInput.add(txtTugas);

        // Input Nilai Kuis
        lblKuis = new JLabel("Nilai Kuis (20%)");
        lblKuis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKuis.setBounds(200, 150, 130, 20);
        panelInput.add(lblKuis);

        txtKuis = new JTextField();
        txtKuis.setBounds(200, 175, 130, 35);
        panelInput.add(txtKuis);

        // Input Nilai UTS
        lblUts = new JLabel("Nilai UTS (25%)");
        lblUts.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUts.setBounds(30, 230, 130, 20);
        panelInput.add(lblUts);

        txtUts = new JTextField();
        txtUts.setBounds(30, 255, 130, 35);
        panelInput.add(txtUts);

        // Input Nilai UAS
        lblUas = new JLabel("Nilai UAS (25%)");
        lblUas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUas.setBounds(200, 230, 130, 20);
        panelInput.add(lblUas);

        txtUas = new JTextField();
        txtUas.setBounds(200, 255, 130, 35);
        panelInput.add(txtUas);

        // Area Hasil Rata-rata (Kanan)
        lblRataRata = new JLabel("NILAI AKHIR RATA-RATA");
        lblRataRata.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRataRata.setForeground(Color.GRAY);
        lblRataRata.setBounds(430, 95, 200, 20);
        panelInput.add(lblRataRata);

        lblHasilRata = new JLabel("0.0");
        lblHasilRata.setFont(new Font("Segoe UI", Font.BOLD, 72));
        lblHasilRata.setForeground(new Color(15, 52, 96));
        lblHasilRata.setBounds(430, 120, 250, 90);
        panelInput.add(lblHasilRata);

        // Tombol Kalkulasi Rata-rata
        btnKalkulasi = new RoundedButton("KALKULASI RATA-RATA", 10, new Color(15, 52, 96), new Color(10, 36, 66));
        btnKalkulasi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKalkulasi.setForeground(Color.WHITE);
        btnKalkulasi.setBounds(430, 245, 230, 45);
        btnKalkulasi.addActionListener(e -> {
            try {
                double tugas = Double.parseDouble(txtTugas.getText()) * 0.3;
                double kuis = Double.parseDouble(txtKuis.getText()) * 0.2;
                double uts = Double.parseDouble(txtUts.getText()) * 0.25;
                double uas = Double.parseDouble(txtUas.getText()) * 0.25;
                double total = tugas + kuis + uts + uas;
                lblHasilRata.setText(String.format("%.1f", total));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harap masukkan format angka nilai dengan benar!", "Eror Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelInput.add(btnKalkulasi);

        // Tombol Simpan Nilai
        btnSimpan = new RoundedButton("SIMPAN NILAI", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setBounds(30, 325, 630, 45);
        btnSimpan.addActionListener(e -> {
            if (txtNamaSiswa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama Siswa tidak boleh kosong!");
            } else {
                JOptionPane.showMessageDialog(this, "Nilai untuk " + txtNamaSiswa.getText() + " berhasil disimpan!");
            }
        });
        panelInput.add(btnSimpan);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmNilaiGuru().setVisible(true));
    }
}