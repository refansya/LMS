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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class FrmMenuAdmin extends javax.swing.JFrame {

    private static final Color HIJAU = new Color(39, 174, 96);
    private static final Color ORANYE = new Color(212, 143, 0);
    private static final Color MERAH = new Color(192, 57, 43);
    private static final Color NAVY = new Color(15, 52, 96);

    // 1. Deklarasi Komponen Sidebar (Kiri)
    private JPanel sidebarPanel;
    private AvatarBulat avatarProfile;
    private JLabel lblNamaAdmin, lblRole;
    private RoundedButton btnDashboard, btnDataGuru, btnDataSiswa, btnKelas, btnMapel, btnJadwal, btnPengumuman, btnLaporan, btnKeluar;

    // 2. Deklarasi Komponen Main Content (Kanan)
    private JPanel mainContentPanel;
    private JLabel lblHeaderTitle, lblTanggal;

    // 4 Kotak Statistik Dashboard
    private RoundedPanel cardSiswa, cardGuru, cardKelas, cardMapel;
    private JLabel lblSiswaVal, lblGuruVal, lblKelasVal, lblMapelVal;
    private JLabel lblSiswaHint, lblGuruHint, lblKelasHint, lblMapelHint;

    // Kartu ringkasan
    private JLabel lblHadirAngka, lblHadirDesc, lblPerhatianDesc;
    private DefaultTableModel modelKelas;

    /**
     * Creates new form FrmMenuAdmin
     */
    public FrmMenuAdmin() {
        initComponentsCustom();
        muatDashboard();
    }

    private RoundedButton menuButton(String teks, int y) {
        RoundedButton b = new RoundedButton(teks, 10, new Color(32, 32, 48), new Color(233, 69, 96));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBounds(20, y, 210, 38);
        sidebarPanel.add(b);
        return b;
    }

    private void initComponentsCustom() {
        // --- WINDOW SETTINGS ---
        setTitle("LMS UPTD SMPN 33 Depok - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); getContentPane().setPreferredSize(new java.awt.Dimension(1000, 650)); pack();
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null); // Absolute layout

        // ==========================================
        // I. SIDEBAR PANEL (KIRI)
        // ==========================================
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Gradasi Gelap Elegan untuk Sidebar
                GradientPaint gp = new GradientPaint(0, 0, new Color(21, 21, 30), 0, getHeight(), new Color(32, 32, 48));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setBounds(0, 0, 250, 650);
        sidebarPanel.setLayout(null);
        getContentPane().add(sidebarPanel);

        // Avatar Profil Bulat (inisial akun yang login)
        String inisial = Session.inisial();
        avatarProfile = new AvatarBulat("?".equals(inisial) ? "A" : inisial, new Color(26, 26, 46));
        avatarProfile.setBounds(85, 40, 80, 80);
        sidebarPanel.add(avatarProfile);

        // Nama & Role Admin
        String nama = Session.namaLengkap == null || Session.namaLengkap.trim().isEmpty()
            ? "Administrator" : Session.namaLengkap;
        lblNamaAdmin = new JLabel(nama);
        lblNamaAdmin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNamaAdmin.setForeground(Color.WHITE);
        lblNamaAdmin.setHorizontalAlignment(SwingConstants.CENTER);
        lblNamaAdmin.setBounds(0, 130, 250, 20);
        sidebarPanel.add(lblNamaAdmin);

        lblRole = new JLabel("UPTD SMPN 33 Depok");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(150, 150, 170));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);
        lblRole.setBounds(0, 150, 250, 15);
        sidebarPanel.add(lblRole);

        // --- TOMBOL NAVIGASI SIDEBAR ---
        btnDashboard = menuButton("Dashboard", 185);
        btnDashboard.setBackground(new Color(40, 42, 58));

        btnDataGuru = menuButton("Data Guru", 227);
        btnDataGuru.addActionListener(e -> { new FrmDataGuru().setVisible(true); this.dispose(); });

        btnDataSiswa = menuButton("Data Siswa", 269);
        btnDataSiswa.addActionListener(e -> { new FrmDataSiswa().setVisible(true); this.dispose(); });

        btnKelas = menuButton("Kelas", 311);
        btnKelas.addActionListener(e -> { new FrmKelas().setVisible(true); this.dispose(); });

        btnMapel = menuButton("Mata Pelajaran", 353);
        btnMapel.addActionListener(e -> { new FrmMapel().setVisible(true); this.dispose(); });

        btnJadwal = menuButton("Jadwal Pelajaran", 395);
        btnJadwal.addActionListener(e -> { new FrmJadwal().setVisible(true); this.dispose(); });

        btnPengumuman = menuButton("Pengumuman", 437);
        btnPengumuman.addActionListener(e -> { new FrmPengumuman().setVisible(true); this.dispose(); });

        btnLaporan = menuButton("Laporan", 479);
        btnLaporan.addActionListener(e -> { new FrmLaporan().setVisible(true); this.dispose(); });

        // Tombol Keluar
        btnKeluar = new RoundedButton("LOGOUT", 10, new Color(198, 40, 40), new Color(255, 92, 119));
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBounds(20, 550, 210, 38);
        btnKeluar.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnKeluar);

        // ==========================================
        // II. MAIN CONTENT PANEL (KANAN)
        // ==========================================
        mainContentPanel = new JPanel();
        mainContentPanel.setBackground(new Color(245, 246, 250)); // Background abu-abu terang yang bersih
        mainContentPanel.setBounds(250, 0, 750, 650);
        mainContentPanel.setLayout(null);
        getContentPane().add(mainContentPanel);

        // Header: salam + tanggal
        lblHeaderTitle = new JLabel(salam() + ", " + nama + "!");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeaderTitle.setForeground(new Color(40, 42, 58));
        lblHeaderTitle.setBounds(30, 22, 690, 30);
        mainContentPanel.add(lblHeaderTitle);

        lblTanggal = new JLabel(tanggalHariIni() + "   |   Dashboard Utama LMS UPTD SMPN 33 Depok");
        lblTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTanggal.setForeground(Color.GRAY);
        lblTanggal.setBounds(30, 54, 690, 20);
        mainContentPanel.add(lblTanggal);

        // --- 4 KOTAK STATISTIK (RoundedPanel, bisa diklik) ---
        cardSiswa = kartuStat(30, "Total Siswa", () -> { new FrmDataSiswa().setVisible(true); dispose(); });
        lblSiswaVal = nilai(cardSiswa);
        lblSiswaHint = hint(cardSiswa);

        cardGuru = kartuStat(204, "Total Guru", () -> { new FrmDataGuru().setVisible(true); dispose(); });
        lblGuruVal = nilai(cardGuru);
        lblGuruHint = hint(cardGuru);

        cardKelas = kartuStat(378, "Total Kelas", () -> { new FrmKelas().setVisible(true); dispose(); });
        lblKelasVal = nilai(cardKelas);
        lblKelasHint = hint(cardKelas);

        cardMapel = kartuStat(552, "Mata Pelajaran", () -> { new FrmMapel().setVisible(true); dispose(); });
        lblMapelVal = nilai(cardMapel);
        lblMapelHint = hint(cardMapel);

        // --- Kehadiran hari ini ---
        RoundedPanel cardHadir = kartu(30, 215, 330, 175, "Kehadiran Hari Ini");
        lblHadirAngka = new JLabel("-");
        lblHadirAngka.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblHadirAngka.setForeground(NAVY);
        lblHadirAngka.setBounds(20, 45, 200, 52);
        cardHadir.add(lblHadirAngka);
        lblHadirDesc = deskripsi(cardHadir, 20, 104, 290, 60);

        // --- Perlu perhatian ---
        RoundedPanel cardPerhatian = kartu(382, 215, 330, 175, "Perlu Perhatian");
        lblPerhatianDesc = deskripsi(cardPerhatian, 20, 50, 290, 115);

        // --- Ringkasan per kelas ---
        RoundedPanel cardTabel = kartu(30, 410, 682, 215, "Ringkasan Per Kelas");
        modelKelas = new DefaultTableModel(new String[]{"Kelas", "Wali Kelas", "Siswa", "Presensi Hari Ini"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabel = new JTable(modelKelas);
        tabel.setRowHeight(24);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabel.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(260);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(220);
        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        tabel.getColumnModel().getColumn(2).setCellRenderer(tengah);
        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(20, 48, 642, 150);
        cardTabel.add(sp);
    }

    // ------------------------------------------------------------------
    //  KOMPONEN
    // ------------------------------------------------------------------
    private RoundedPanel kartuStat(int x, String judul, Runnable aksi) {
        RoundedPanel p = new RoundedPanel(15, Color.WHITE, true);
        p.setBounds(x, 95, 160, 105);
        p.setLayout(null);
        mainContentPanel.add(p);

        JLabel t = new JLabel(judul);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(Color.GRAY);
        t.setBounds(15, 52, 135, 18);
        p.add(t);

        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aksi.run(); }
        });
        return p;
    }

    private JLabel nilai(RoundedPanel p) {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        l.setForeground(new Color(26, 26, 46));
        l.setBounds(15, 12, 135, 36);
        p.add(l);
        return l;
    }

    private JLabel hint(RoundedPanel p) {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(Color.GRAY);
        l.setBounds(15, 74, 140, 18);
        p.add(l);
        return l;
    }

    private RoundedPanel kartu(int x, int y, int w, int h, String judul) {
        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(x, y, w, h);
        p.setLayout(null);
        mainContentPanel.add(p);

        JLabel t = new JLabel(judul);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(NAVY);
        t.setBounds(20, 14, w - 40, 25);
        p.add(t);
        return p;
    }

    private JLabel deskripsi(RoundedPanel p, int x, int y, int w, int h) {
        JLabel l = new JLabel("<html>Memuat data...</html>");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.DARK_GRAY);
        l.setVerticalAlignment(SwingConstants.TOP);
        l.setBounds(x, y, w, h);
        p.add(l);
        return l;
    }

    private static String salam() {
        int jam = LocalTime.now().getHour();
        if (jam < 11) return "Selamat pagi";
        if (jam < 15) return "Selamat siang";
        if (jam < 18) return "Selamat sore";
        return "Selamat malam";
    }

    private static String tanggalHariIni() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID")));
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String warna(String teks, Color c) {
        return "<font color='" + String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()) + "'>"
             + teks + "</font>";
    }

    // ------------------------------------------------------------------
    //  DATA
    // ------------------------------------------------------------------
    private int hitung(Connection con, String sql) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void muatDashboard() {
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) {
                String pesan = "<html>Tidak bisa konek ke database.<br>Pastikan MySQL di XAMPP sudah running.</html>";
                lblHadirDesc.setText(pesan);
                lblPerhatianDesc.setText(pesan);
                return;
            }
            int jumlahKelas = muatStatistik(con);
            muatKehadiran(con, jumlahKelas);
            muatPerhatian(con);
            muatRingkasanKelas(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /** Mengisi 4 kotak statistik; mengembalikan jumlah kelas. */
    private int muatStatistik(Connection con) {
        int kelas = 0;
        try {
            int siswa = hitung(con, "SELECT COUNT(*) FROM tb_siswa");
            int guru = hitung(con, "SELECT COUNT(*) FROM tb_guru");
            kelas = hitung(con, "SELECT COUNT(*) FROM tb_kelas");
            int mapel = hitung(con, "SELECT COUNT(*) FROM tb_mapel");
            lblSiswaVal.setText(String.valueOf(siswa));
            lblGuruVal.setText(String.valueOf(guru));
            lblKelasVal.setText(String.valueOf(kelas));
            lblMapelVal.setText(String.valueOf(mapel));

            int tanpaKelas = hitung(con, "SELECT COUNT(*) FROM tb_siswa WHERE id_kelas IS NULL");
            lblSiswaHint.setText(tanpaKelas > 0 ? tanpaKelas + " belum berkelas" : "Semua sudah berkelas");
            lblSiswaHint.setForeground(tanpaKelas > 0 ? ORANYE : HIJAU);

            int tanpaWali = hitung(con, "SELECT COUNT(*) FROM tb_kelas WHERE id_wali_kelas IS NULL");
            lblKelasHint.setText(tanpaWali > 0 ? tanpaWali + " tanpa wali kelas" : "Semua punya wali");
            lblKelasHint.setForeground(tanpaWali > 0 ? ORANYE : HIJAU);

            lblMapelHint.setText(" ");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            int belumMengajar = hitung(con, "SELECT COUNT(*) FROM tb_guru g WHERE NOT EXISTS "
                + "(SELECT 1 FROM tb_guru_mengajar gm WHERE gm.id_guru = g.id_guru)");
            lblGuruHint.setText(belumMengajar > 0 ? belumMengajar + " belum ditugaskan" : "Semua sudah ditugaskan");
            lblGuruHint.setForeground(belumMengajar > 0 ? ORANYE : HIJAU);
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblGuruHint.setText("Jalankan tb_guru_mengajar.sql");
            lblGuruHint.setForeground(MERAH);
        }
        return kelas;
    }

    private void muatKehadiran(Connection con, int jumlahKelas) {
        try {
            int hadir = 0, sakit = 0, izin = 0, alpha = 0;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT status, COUNT(*) FROM tb_absensi WHERE tanggal = CURDATE() GROUP BY status")) {
                while (rs.next()) {
                    switch (rs.getString(1)) {
                        case "Hadir": hadir = rs.getInt(2); break;
                        case "Sakit": sakit = rs.getInt(2); break;
                        case "Izin": izin = rs.getInt(2); break;
                        case "Alpha": alpha = rs.getInt(2); break;
                        default: break;
                    }
                }
            }
            int kelasIsi = hitung(con, "SELECT COUNT(DISTINCT id_kelas) FROM tb_absensi WHERE tanggal = CURDATE()");
            int total = hadir + sakit + izin + alpha;
            DayOfWeek hari = LocalDate.now().getDayOfWeek();
            boolean akhirPekan = hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY;

            if (total == 0) {
                lblHadirAngka.setText("-");
                lblHadirAngka.setForeground(Color.GRAY);
                lblHadirDesc.setText("<html>" + (akhirPekan ? "Hari ini akhir pekan." : "Belum ada presensi yang diisi hari ini.")
                    + "<br>Kelas yang sudah mengisi: " + kelasIsi + " dari " + jumlahKelas + "</html>");
            } else {
                double persen = hadir * 100.0 / total;
                lblHadirAngka.setText(String.format(Locale.US, "%.0f%%", persen));
                lblHadirAngka.setForeground(persen >= 90 ? HIJAU : persen >= 75 ? ORANYE : MERAH);
                lblHadirDesc.setText("<html>Hadir " + hadir + "  |  Sakit " + sakit + "  |  Izin " + izin
                    + "  |  Alpha " + alpha + "<br>Kelas yang sudah mengisi: " + kelasIsi + " dari " + jumlahKelas + "</html>");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblHadirDesc.setText("<html>Data presensi belum tersedia. Pastikan tabel tb_absensi sudah dibuat.</html>");
        }
    }

    private void muatPerhatian(Connection con) {
        StringBuilder sb = new StringBuilder("<html>");
        int baris = 0;
        try {
            int tanpaWali = hitung(con, "SELECT COUNT(*) FROM tb_kelas WHERE id_wali_kelas IS NULL");
            int kelasKosong = hitung(con, "SELECT COUNT(*) FROM tb_kelas k WHERE NOT EXISTS "
                + "(SELECT 1 FROM tb_siswa s WHERE s.id_kelas = k.id_kelas)");
            int siswaTanpaKelas = hitung(con, "SELECT COUNT(*) FROM tb_siswa WHERE id_kelas IS NULL");
            int guruTanpaTugas = -1;
            try {
                guruTanpaTugas = hitung(con, "SELECT COUNT(*) FROM tb_guru g WHERE NOT EXISTS "
                    + "(SELECT 1 FROM tb_guru_mengajar gm WHERE gm.id_guru = g.id_guru)");
            } catch (SQLException ex) {
                ex.printStackTrace(); // tb_guru_mengajar belum dibuat
            }

            if (tanpaWali > 0) { sb.append("&bull; ").append(warna("<b>" + tanpaWali + "</b> kelas belum punya wali kelas", ORANYE)).append("<br>"); baris++; }
            if (guruTanpaTugas > 0) { sb.append("&bull; ").append(warna("<b>" + guruTanpaTugas + "</b> guru belum ditugaskan mengajar", ORANYE)).append("<br>"); baris++; }
            if (siswaTanpaKelas > 0) { sb.append("&bull; ").append(warna("<b>" + siswaTanpaKelas + "</b> siswa belum masuk kelas", ORANYE)).append("<br>"); baris++; }
            if (kelasKosong > 0) { sb.append("&bull; ").append(warna("<b>" + kelasKosong + "</b> kelas belum memiliki siswa", ORANYE)).append("<br>"); baris++; }
            if (guruTanpaTugas < 0) { sb.append("&bull; ").append(warna("Tabel penugasan mengajar belum dibuat (jalankan tb_guru_mengajar.sql)", MERAH)).append("<br>"); baris++; }

            lblPerhatianDesc.setText(baris == 0
                ? "<html>" + warna("Semua data sudah lengkap.", HIJAU) + "</html>"
                : sb.append("</html>").toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblPerhatianDesc.setText("<html>Data tidak dapat dimuat.</html>");
        }
    }

    private void muatRingkasanKelas(Connection con) {
        modelKelas.setRowCount(0);
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT k.nama_kelas, COALESCE(g.nama_guru,'-') AS wali, "
              + "(SELECT COUNT(*) FROM tb_siswa s WHERE s.id_kelas = k.id_kelas) AS jml, "
              + "(SELECT COUNT(*) FROM tb_absensi a WHERE a.id_kelas = k.id_kelas AND a.tanggal = CURDATE()) AS isi, "
              + "(SELECT COUNT(*) FROM tb_absensi a WHERE a.id_kelas = k.id_kelas AND a.tanggal = CURDATE() "
              + "AND a.status = 'Hadir') AS hadir "
              + "FROM tb_kelas k LEFT JOIN tb_guru g ON k.id_wali_kelas = g.id_guru "
              + "ORDER BY k.tingkat, k.nama_kelas");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int jml = rs.getInt("jml"), isi = rs.getInt("isi"), hadir = rs.getInt("hadir");
                String presensi = jml == 0 ? "Belum ada siswa"
                    : isi == 0 ? "Belum diisi"
                    : (hadir * 100 / isi) + "% hadir (" + hadir + "/" + isi + ")";
                modelKelas.addRow(new Object[]{rs.getString("nama_kelas"), rs.getString("wali"), jml, presensi});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            new FrmMenuAdmin().setVisible(true);
        });
    }                   
                    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}