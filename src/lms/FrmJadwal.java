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
import javax.swing.SwingConstants;

public class FrmJadwal extends javax.swing.JFrame {

    private JPanel bgPanel;
    private JLabel lblHeaderTitle;
    private RoundedButton btnKembali, btnAturJadwal;

    // Card Per-Hari (Senin - Jumat)
    private RoundedPanel cardSenin, cardSelasa, cardRabu, cardKamis, cardJumat;
    private JLabel lblSenin, lblSelasa, lblRabu, lblKamis, lblJumat;
    private JLabel detSenin, detSelasa, detRabu, detKamis, detJumat;

    public FrmJadwal() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("LMS UPTD SMPN 33 Depok - Jadwal Pelajaran");
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
        lblHeaderTitle = new JLabel("JADWAL PELAJARAN MINGGUAN");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setBounds(40, 25, 450, 30);
        bgPanel.add(lblHeaderTitle);

        // Tombol Atur Jadwal Baru
        btnAturJadwal = new RoundedButton("ATUR JADWAL BARU", 10, new Color(46, 204, 113), new Color(39, 174, 96));
        btnAturJadwal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAturJadwal.setForeground(Color.WHITE);
        btnAturJadwal.setBounds(650, 25, 170, 35);
        btnAturJadwal.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Membuka Form Konfigurasi Penjadwalan Baru...");
        });
        bgPanel.add(btnAturJadwal);

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
        // 5 KARTU HARI (Senin - Jumat)
        // ==========================================
        int cardWidth = 170;
        int cardHeight = 450;
        int startY = 90;

        // 1. SENIN
        cardSenin = new RoundedPanel(20, Color.WHITE, true);
        cardSenin.setBounds(40, startY, cardWidth, cardHeight);
        cardSenin.setLayout(null);
        bgPanel.add(cardSenin);

        lblSenin = new JLabel("SENIN");
        lblSenin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSenin.setForeground(new Color(233, 69, 96));
        lblSenin.setHorizontalAlignment(SwingConstants.CENTER);
        lblSenin.setBounds(0, 15, cardWidth, 25);
        cardSenin.add(lblSenin);

        detSenin = new JLabel("<html><center>Upacara Bendera<br><br><b>07.30 - 09.00</b><br>Matematika<br><br><b>09.15 - 11.00</b><br>B. Indonesia</center></html>");
        detSenin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detSenin.setForeground(Color.DARK_GRAY);
        detSenin.setHorizontalAlignment(SwingConstants.CENTER);
        detSenin.setBounds(10, 60, cardWidth - 20, 300);
        cardSenin.add(detSenin);

        // 2. SELASA
        cardSelasa = new RoundedPanel(20, Color.WHITE, true);
        cardSelasa.setBounds(225, startY, cardWidth, cardHeight);
        cardSelasa.setLayout(null);
        bgPanel.add(cardSelasa);

        lblSelasa = new JLabel("SELASA");
        lblSelasa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSelasa.setForeground(new Color(233, 69, 96));
        lblSelasa.setHorizontalAlignment(SwingConstants.CENTER);
        lblSelasa.setBounds(0, 15, cardWidth, 25);
        cardSelasa.add(lblSelasa);

        detSelasa = new JLabel("<html><center><b>07.00 - 09.00</b><br>IPA (Fisika)<br><br><b>09.15 - 11.00</b><br>B. Inggris<br><br><b>11.15 - 12.30</b><br>Seni Budaya</center></html>");
        detSelasa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detSelasa.setForeground(Color.DARK_GRAY);
        detSelasa.setHorizontalAlignment(SwingConstants.CENTER);
        detSelasa.setBounds(10, 60, cardWidth - 20, 300);
        cardSelasa.add(detSelasa);

        // 3. RABU
        cardRabu = new RoundedPanel(20, Color.WHITE, true);
        cardRabu.setBounds(410, startY, cardWidth, cardHeight);
        cardRabu.setLayout(null);
        bgPanel.add(cardRabu);

        lblRabu = new JLabel("RABU");
        lblRabu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRabu.setForeground(new Color(233, 69, 96));
        lblRabu.setHorizontalAlignment(SwingConstants.CENTER);
        lblRabu.setBounds(0, 15, cardWidth, 25);
        cardRabu.add(lblRabu);

        detRabu = new JLabel("<html><center><b>07.00 - 09.00</b><br>IPS Sejarah<br><br><b>09.15 - 11.00</b><br>Pendidikan Agama<br><br><b>11.15 - 12.30</b><br>PJOK</center></html>");
        detRabu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detRabu.setForeground(Color.DARK_GRAY);
        detRabu.setHorizontalAlignment(SwingConstants.CENTER);
        detRabu.setBounds(10, 60, cardWidth - 20, 300);
        cardRabu.add(detRabu);

        // 4. KAMIS
        cardKamis = new RoundedPanel(20, Color.WHITE, true);
        cardKamis.setBounds(595, startY, cardWidth, cardHeight);
        cardKamis.setLayout(null);
        bgPanel.add(cardKamis);

        lblKamis = new JLabel("KAMIS");
        lblKamis.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKamis.setForeground(new Color(233, 69, 96));
        lblKamis.setHorizontalAlignment(SwingConstants.CENTER);
        lblKamis.setBounds(0, 15, cardWidth, 25);
        cardKamis.add(lblKamis);

        detKamis = new JLabel("<html><center><b>07.00 - 09.00</b><br>Prakarya<br><br><b>09.15 - 11.00</b><br>Informatika<br><br><b>11.15 - 12.30</b><br>PPKn</center></html>");
        detKamis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detKamis.setForeground(Color.DARK_GRAY);
        detKamis.setHorizontalAlignment(SwingConstants.CENTER);
        detKamis.setBounds(10, 60, cardWidth - 20, 300);
        cardKamis.add(detKamis);

        // 5. JUMAT
        cardJumat = new RoundedPanel(20, Color.WHITE, true);
        cardJumat.setBounds(780, startY, cardWidth, cardHeight);
        cardJumat.setLayout(null);
        bgPanel.add(cardJumat);

        lblJumat = new JLabel("JUMAT");
        lblJumat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblJumat.setForeground(new Color(233, 69, 96));
        lblJumat.setHorizontalAlignment(SwingConstants.CENTER);
        lblJumat.setBounds(0, 15, cardWidth, 25);
        cardJumat.add(lblJumat);

        detJumat = new JLabel("<html><center>Senam & Dhuha<br><br><b>08.00 - 09.30</b><br>B. Sunda<br><br><b>09.45 - 11.00</b><br>Bimbingan Konseling</center></html>");
        detJumat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detJumat.setForeground(Color.DARK_GRAY);
        detJumat.setHorizontalAlignment(SwingConstants.CENTER);
        detJumat.setBounds(10, 60, cardWidth - 20, 300);
        cardJumat.add(detJumat);
    }

    private void initComponents() {}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FrmJadwal().setVisible(true));
    }
}