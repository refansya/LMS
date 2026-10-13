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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmMapel extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- SISI KIRI: FORM INPUT MAPEL ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblKodeMapel, lblNamaMapel, lblKkm;
    private JTextField txtKodeMapel, txtNamaMapel, txtKkm;
    private RoundedButton btnSimpan, btnReset;

    // --- SISI KANAN: TABEL DATA MAPEL ---
    private RoundedPanel panelTabel;
    private JLabel lblTabelTitle;
    private JTable tabelMapel;
    private JScrollPane scrollPaneTabel;
    private RoundedButton btnUbah, btnHapus;
    private DefaultTableModel tableModel;

    public FrmMapel() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Mata Pelajaran");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
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
        bgPanel.setBounds(0, 0, 1000, 650);
        bgPanel.setLayout(null);
        getContentPane().add(bgPanel);

        // Header Title
        lblHeaderTitle = new JLabel("MANAJEMEN MATA PELAJARAN");
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
        // FORM INPUT (RoundedPanel Kiri)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        lblFormTitle = new JLabel("Form Input Mapel");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Kode Mapel
        lblKodeMapel = new JLabel("Kode Mapel (Contoh: MP-001)");
        lblKodeMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKodeMapel.setForeground(new Color(15, 52, 96));
        lblKodeMapel.setBounds(25, 70, 300, 20);
        panelForm.add(lblKodeMapel);

        txtKodeMapel = new JTextField();
        txtKodeMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKodeMapel.setBounds(25, 95, 300, 35);
        txtKodeMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtKodeMapel);

        // Nama Mapel
        lblNamaMapel = new JLabel("Nama Mata Pelajaran");
        lblNamaMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNamaMapel.setForeground(new Color(15, 52, 96));
        lblNamaMapel.setBounds(25, 150, 300, 20);
        panelForm.add(lblNamaMapel);

        txtNamaMapel = new JTextField();
        txtNamaMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNamaMapel.setBounds(25, 175, 300, 35);
        txtNamaMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNamaMapel);

        // KKM
        lblKkm = new JLabel("Nilai KKM");
        lblKkm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKkm.setForeground(new Color(15, 52, 96));
        lblKkm.setBounds(25, 230, 300, 20);
        panelForm.add(lblKkm);

        txtKkm = new JTextField();
        txtKkm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKkm.setBounds(25, 255, 300, 35);
        txtKkm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtKkm);

        // Tombol Simpan
        btnSimpan = new RoundedButton("SIMPAN MAPEL", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setBounds(25, 340, 300, 40);
        btnSimpan.addActionListener(e -> aksiSimpan());
        panelForm.add(btnSimpan);

        // Tombol Reset
        btnReset = new RoundedButton("RESET", 10, new Color(149, 165, 166), new Color(127, 140, 141));
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setForeground(Color.WHITE);
        btnReset.setBounds(25, 395, 300, 40);
        btnReset.addActionListener(e -> aksiReset());
        panelForm.add(btnReset);

        // ==========================================
        // TABEL DATA (RoundedPanel Kanan)
        // ==========================================
        panelTabel = new RoundedPanel(20, Color.WHITE, true);
        panelTabel.setBounds(410, 80, 540, 490);
        panelTabel.setLayout(null);
        bgPanel.add(panelTabel);

        lblTabelTitle = new JLabel("Daftar Mata Pelajaran");
        lblTabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabelTitle.setForeground(new Color(26, 26, 46));
        lblTabelTitle.setBounds(25, 20, 250, 25);
        panelTabel.add(lblTabelTitle);

        String[] kolom = {"No", "Kode Mapel", "Nama Mata Pelajaran", "KKM"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelMapel = new JTable(tableModel);

        // Data awal simulasi
        tableModel.addRow(new Object[]{"1", "MTK-01", "Matematika", "75"});
        tableModel.addRow(new Object[]{"2", "BIN-02", "Bahasa Indonesia", "75"});
        tableModel.addRow(new Object[]{"3", "IPA-03", "Ilmu Pengetahuan Alam", "70"});

        scrollPaneTabel = new JScrollPane(tabelMapel);
        scrollPaneTabel.setBounds(25, 65, 490, 330);
        panelTabel.add(scrollPaneTabel);

        // Tombol Ubah/Edit Mapel
        btnUbah = new RoundedButton("EDIT MAPEL", 10, new Color(241, 196, 15), new Color(230, 126, 34));
        btnUbah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUbah.setForeground(Color.WHITE);
        btnUbah.setBounds(260, 415, 120, 35);
        btnUbah.addActionListener(e -> aksiUbah());
        panelTabel.add(btnUbah);

        // Tombol Hapus
        btnHapus = new RoundedButton("HAPUS", 10, new Color(231, 76, 60), new Color(192, 41, 43));
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setBounds(395, 415, 120, 35);
        btnHapus.addActionListener(e -> aksiHapus());
        panelTabel.add(btnHapus);
    }

    private void aksiSimpan() {
        String kode = txtKodeMapel.getText();
        String nama = txtNamaMapel.getText();
        String kkm = txtKkm.getText();
        
        if (!kode.isEmpty() && !nama.isEmpty() && !kkm.isEmpty()) {
            int noBaru = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(noBaru), kode, nama, kkm});
            aksiReset();
        }
    }

    private void aksiReset() {
        txtKodeMapel.setText("");
        txtNamaMapel.setText("");
        txtKkm.setText("");
    }

    private void aksiUbah() {
        int barisTerpilih = tabelMapel.getSelectedRow();
        if (barisTerpilih != -1) {
            txtKodeMapel.setText(tableModel.getValueAt(barisTerpilih, 1).toString());
            txtNamaMapel.setText(tableModel.getValueAt(barisTerpilih, 2).toString());
            txtKkm.setText(tableModel.getValueAt(barisTerpilih, 3).toString());
        }
    }

    private void aksiHapus() {
        int barisTerpilih = tabelMapel.getSelectedRow();
        if (barisTerpilih != -1) {
            tableModel.removeRow(barisTerpilih);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
        }
    }

    // Bagian main & Generated code bawaan Netbeans
    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmMapel().setVisible(true));
    }
}