package lms;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ultro
 */
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

public class FrmDataGuru extends javax.swing.JFrame {

    // Panel Background Gradasi Utama
    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali;

    // --- SISI KIRI: FORM INPUT ---
    private RoundedPanel panelForm;
    private JLabel lblFormTitle, lblNip, lblNama, lblMapel;
    private JTextField txtNip, txtNama, txtMapel;
    private RoundedButton btnSimpan, btnReset;

    // --- SISI KANAN: TABEL DATA ---
    private RoundedPanel panelTabel;
    private JLabel lblTabelTitle;
    private JTable tabelGuru;
    private JScrollPane scrollPaneTabel;
    private RoundedButton btnUbah, btnHapus;
    private DefaultTableModel tableModel;
    /**
     * Creates new form FrmDataGuru
     */
    public FrmDataGuru() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        // --- WINDOW SETTINGS ---
        setTitle("LMS UPTD SMPN 33 Depok - Data Guru");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Menutup halaman ini saja, tidak mematikan program utama
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        // ==========================================
        // 1. PANEL BACKGROUND UTAMA (Gradasi Gelap LMS)
        // ==========================================
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
        lblHeaderTitle = new JLabel("PENGELOLAAN DATA GURU");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 400, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Kembali ke Menu Utama
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
        // 2. PANEL FORM INPUT (SISI KIRI)
        // ==========================================
        panelForm = new RoundedPanel(20, Color.WHITE, true);
        panelForm.setBounds(40, 80, 350, 490);
        panelForm.setLayout(null);
        bgPanel.add(panelForm);

        // Judul Form
        lblFormTitle = new JLabel("Form Input Guru");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(26, 26, 46));
        lblFormTitle.setBounds(25, 20, 200, 25);
        panelForm.add(lblFormTitle);

        // Input NIP
        lblNip = new JLabel("NIP / Kode Guru");
        lblNip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNip.setForeground(new Color(15, 52, 96));
        lblNip.setBounds(25, 70, 300, 20);
        panelForm.add(lblNip);

        txtNip = new JTextField();
        txtNip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNip.setBounds(25, 95, 300, 35);
        txtNip.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNip);

        // Input Nama
        lblNama = new JLabel("Nama Lengkap");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(new Color(15, 52, 96));
        lblNama.setBounds(25, 150, 300, 20);
        panelForm.add(lblNama);

        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNama.setBounds(25, 175, 300, 35);
        txtNama.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtNama);

        // Input Mata Pelajaran
        lblMapel = new JLabel("Mata Pelajaran");
        lblMapel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMapel.setForeground(new Color(15, 52, 96));
        lblMapel.setBounds(25, 230, 300, 20);
        panelForm.add(lblMapel);

        txtMapel = new JTextField();
        txtMapel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMapel.setBounds(25, 255, 300, 35);
        txtMapel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelForm.add(txtMapel);

        // Tombol Simpan
        btnSimpan = new RoundedButton("SIMPAN", 10, new Color(46, 204, 113), new Color(39, 174, 96));
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
        // 3. PANEL TABEL DATA (SISI KANAN)
        // ==========================================
        panelTabel = new RoundedPanel(20, Color.WHITE, true);
        panelTabel.setBounds(410, 80, 540, 490);
        panelTabel.setLayout(null);
        bgPanel.add(panelTabel);

        // Judul Tabel
        lblTabelTitle = new JLabel("Daftar Guru Terdaftar");
        lblTabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTabelTitle.setForeground(new Color(26, 26, 46));
        lblTabelTitle.setBounds(25, 20, 250, 25);
        panelTabel.add(lblTabelTitle);

        // Inisialisasi JTable & Model Kolom
        String[] kolom = {"No", "NIP / Kode", "Nama Guru", "Mata Pelajaran"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelGuru = new JTable(tableModel);
        
        // Memasukkan contoh data awal ke dalam tabel
        tableModel.addRow(new Object[]{"1", "1987021301", "Budi Susanto, M.Pd.", "Matematika"});
        tableModel.addRow(new Object[]{"2", "1991052402", "Siti Aminah, S.Pd.", "Bahasa Indonesia"});
        tableModel.addRow(new Object[]{"3", "1984110303", "Ahmad Fauzi, S.Si.", "Fisika"});

        scrollPaneTabel = new JScrollPane(tabelGuru);
        scrollPaneTabel.setBounds(25, 65, 490, 330);
        panelTabel.add(scrollPaneTabel);

        // Tombol Ubah
        btnUbah = new RoundedButton("UBAH DATA", 10, new Color(241, 196, 15), new Color(230, 126, 34));
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

    // --- FUNGSI AKSI TOMBOL (LOGIKA) ---
    
    private void aksiSimpan() {
        // Logika simpan data nanti dimasukkan di sini (koneksi database)
        String nip = txtNip.getText();
        String nama = txtNama.getText();
        String mapel = txtMapel.getText();
        
        if (!nip.isEmpty() && !nama.isEmpty() && !mapel.isEmpty()) {
            int noBaru = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{String.valueOf(noBaru), nip, nama, mapel});
            aksiReset();
        }
    }

    private void aksiReset() {
        txtNip.setText("");
        txtNama.setText("");
        txtMapel.setText("");
    }

    private void aksiUbah() {
        int barisTerpilih = tabelGuru.getSelectedRow();
        if (barisTerpilih != -1) {
            // Isi form dengan data dari baris tabel yang dipilih untuk siap diedit
            txtNip.setText(tableModel.getValueAt(barisTerpilih, 1).toString());
            txtNama.setText(tableModel.getValueAt(barisTerpilih, 2).toString());
            txtMapel.setText(tableModel.getValueAt(barisTerpilih, 3).toString());
        }
    }

    private void aksiHapus() {
        int barisTerpilih = tabelGuru.getSelectedRow();
        if (barisTerpilih != -1) {
            tableModel.removeRow(barisTerpilih);
            // Susun ulang nomor urut pada tabel
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(String.valueOf(i + 1), i, 0);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Gagal menerapkan tampilan Nimbus: " + ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FrmDataGuru().setVisible(true);
        });
    }
}
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        /* Create and display the form */
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
