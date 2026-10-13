package lms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FrmJadwalSiswa extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- CARD JADWAL HARI SENIN (RoundedPanel) ---
    private RoundedPanel panelSenin;
    private JLabel lblSeninTitle;
    private JTable tabelSenin;
    private JScrollPane scrollSenin;
    private DefaultTableModel modelSenin;

    // --- CARD JADWAL HARI SELASA (RoundedPanel) ---
    private RoundedPanel panelSelasa;
    private JLabel lblSelasaTitle;
    private JTable tabelSelasa;
    private JScrollPane scrollSelasa;
    private DefaultTableModel modelSelasa;

    public FrmJadwalSiswa() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Jadwal Pelajaran");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 580);
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
        bgPanel.setBounds(0, 0, 950, 580);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("JADWAL PELAJARAN MINGGUAN");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
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

        String[] headerTabel = {"Jam", "Mapel", "Guru Pengampu"};

        // ==========================================
        // 1. CARD JADWAL SENIN (RoundedPanel)
        // ==========================================
        panelSenin = new RoundedPanel(15, Color.WHITE, true);
        panelSenin.setBounds(40, 85, 420, 430);
        panelSenin.setLayout(null);
        bgPanel.add(panelSenin);

        lblSeninTitle = new JLabel("Hari Senin");
        lblSeninTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSeninTitle.setForeground(new Color(15, 52, 96));
        lblSeninTitle.setBounds(25, 20, 150, 25);
        panelSenin.add(lblSeninTitle);

        modelSenin = new DefaultTableModel(headerTabel, 0);
        tabelSenin = new JTable(modelSenin);
        modelSenin.addRow(new Object[]{"07:30 - 09:00", "Matematika", "Sari Hartati, M.Pd."});
        modelSenin.addRow(new Object[]{"09:00 - 10:30", "Bahasa Indonesia", "Drs. Mulyono"});
        modelSenin.addRow(new Object[]{"10:30 - 11:00", "ISTIRAHAT", "-"});
        modelSenin.addRow(new Object[]{"11:00 - 12:30", "Bahasa Inggris", "Siti Aminah, S.Pd."});

        scrollSenin = new JScrollPane(tabelSenin);
        scrollSenin.setBounds(25, 65, 370, 340);
        panelSenin.add(scrollSenin);

        // ==========================================
        // 2. CARD JADWAL SELASA (RoundedPanel)
        // ==========================================
        panelSelasa = new RoundedPanel(15, Color.WHITE, true);
        panelSelasa.setBounds(490, 85, 420, 430);
        panelSelasa.setLayout(null);
        bgPanel.add(panelSelasa);

        lblSelasaTitle = new JLabel("Hari Selasa");
        lblSelasaTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSelasaTitle.setForeground(new Color(15, 52, 96));
        lblSelasaTitle.setBounds(25, 20, 150, 25);
        panelSelasa.add(lblSelasaTitle);

        modelSelasa = new DefaultTableModel(headerTabel, 0);
        tabelSelasa = new JTable(modelSelasa);
        modelSelasa.addRow(new Object[]{"07:30 - 09:00", "IPA Terpadu", "Dr. Hermawan"});
        modelSelasa.addRow(new Object[]{"09:00 - 10:30", "Pendidikan Agama", "Ustadz Hanafi"});
        modelSelasa.addRow(new Object[]{"10:30 - 11:00", "ISTIRAHAT", "-"});
        modelSelasa.addRow(new Object[]{"11:00 - 12:30", "Pendidikan Pancasila", "Budi Utomo, S.H."});

        scrollSelasa = new JScrollPane(tabelSelasa);
        scrollSelasa.setBounds(25, 65, 370, 340);
        panelSelasa.add(scrollSelasa);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmJadwalSiswa().setVisible(true));
    }
}