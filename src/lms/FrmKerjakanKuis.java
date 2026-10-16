package lms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Dialog modal untuk mengerjakan kuis pilihan ganda.
 * Alur: muat kuis + soal -> hitung batas waktu -> siswa menjawab -> nilai disimpan ke tb_nilai.
 */
public class FrmKerjakanKuis extends JDialog {

    private static final String[] HURUF = {"A", "B", "C", "D"};

    private static class Soal {
        int id, skor;
        String pertanyaan, benar;
        String[] opsi = new String[4];
    }

    private final int idKuis;
    private String judul;
    private int durasi, idMapel, idGuru;
    private LocalDateTime tutup;
    private final List<Soal> soal = new ArrayList<>();
    private String[] jawab;             // jawaban siswa per soal (A-D), null = kosong

    private boolean siap = false;       // data valid & dialog boleh ditampilkan
    private boolean selesai = false;    // nilai sudah tersimpan
    private boolean otomatisDicoba = false;
    private boolean sedangTampil = false;

    private int posisi = 0;
    private long akhirMs;
    private Timer timer;

    private JLabel lblTimer, lblNomor, lblTerjawab;
    private JTextArea txtPertanyaan;
    private final JRadioButton[] radio = new JRadioButton[4];
    private final ButtonGroup grup = new ButtonGroup();
    private RoundedButton btnPrev, btnNext;
    private final List<JButton> navBtn = new ArrayList<>();

    public FrmKerjakanKuis(JFrame parent, int idKuis) {
        super(parent, "LMS UPTD SMPN 33 Depok - Mengerjakan Kuis", true);
        this.idKuis = idKuis;
        siap = muatData(parent);
        if (!siap) return;
        initUI(parent);
        tampilSoal();
        mulaiTimer();
    }

    public boolean siap() { return siap; }

    // ------------------------------------------------------------------
    //  LOAD DATA (diverifikasi ulang ke database, jangan percaya daftar di layar)
    // ------------------------------------------------------------------
    private boolean muatData(JFrame parent) {
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            UI.peringatan(parent, "Akun ini belum terhubung dengan data siswa / kelas.");
            return false;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(parent); return false; }

            LocalDateTime mulai;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT judul, durasi, tanggal_mulai, tanggal_selesai, id_mapel, id_guru "
                  + "FROM tb_kuis WHERE id_kuis=? AND id_kelas=?")) {
                ps.setInt(1, idKuis);
                ps.setInt(2, Session.idKelas);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        UI.peringatan(parent, "Kuis tidak ditemukan untuk kelas Anda.");
                        return false;
                    }
                    judul = rs.getString("judul");
                    durasi = Math.max(1, rs.getInt("durasi"));
                    Timestamp tm = rs.getTimestamp("tanggal_mulai");
                    mulai = tm == null ? null : tm.toLocalDateTime();
                    Timestamp ts = rs.getTimestamp("tanggal_selesai");
                    tutup = ts == null ? null : ts.toLocalDateTime();
                    idMapel = rs.getInt("id_mapel");   // 0 jika NULL
                    idGuru = rs.getInt("id_guru");
                }
            }

            LocalDateTime now = LocalDateTime.now();
            if (mulai != null && mulai.isAfter(now)) {
                UI.peringatan(parent, "Kuis ini belum dibuka.");
                return false;
            }
            if (tutup != null && tutup.isBefore(now)) {
                UI.peringatan(parent, "Waktu pengerjaan kuis ini sudah ditutup.");
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_nilai WHERE id_siswa=? AND id_kuis=? AND jenis_nilai='Kuis'")) {
                ps.setInt(1, Session.idSiswa);
                ps.setInt(2, idKuis);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        UI.peringatan(parent, "Kuis ini sudah pernah Anda kerjakan.");
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_soal, pertanyaan, opsi_a, opsi_b, opsi_c, opsi_d, jawaban_benar, skor "
                  + "FROM tb_soal WHERE id_kuis=? ORDER BY id_soal")) {
                ps.setInt(1, idKuis);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Soal s = new Soal();
                        s.id = rs.getInt("id_soal");
                        s.pertanyaan = rs.getString("pertanyaan");
                        s.opsi[0] = rs.getString("opsi_a");
                        s.opsi[1] = rs.getString("opsi_b");
                        s.opsi[2] = rs.getString("opsi_c");
                        s.opsi[3] = rs.getString("opsi_d");
                        s.benar = rs.getString("jawaban_benar");
                        s.skor = rs.getInt("skor");
                        soal.add(s);
                    }
                }
            }
            if (soal.isEmpty()) {
                UI.peringatan(parent, "Guru belum menambahkan soal pada kuis ini.");
                return false;
            }
        } catch (SQLException ex) {
            UI.errorDb(parent, ex);
            return false;
        }

        Collections.shuffle(soal); // urutan soal diacak per siswa
        jawab = new String[soal.size()];

        // batas waktu: durasi, tapi tidak boleh melewati waktu tutup kuis
        akhirMs = System.currentTimeMillis() + durasi * 60_000L;
        if (tutup != null) akhirMs = Math.min(akhirMs, Timestamp.valueOf(tutup).getTime());
        return true;
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI(JFrame parent) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setPreferredSize(new Dimension(1000, 650));
        pack();
        setLocationRelativeTo(parent);

        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(26, 26, 46), 0, getHeight(), new Color(15, 52, 96)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setBounds(0, 0, 1000, 650);
        bg.setLayout(null);
        getContentPane().add(bg);

        JLabel h = new JLabel("KUIS: " + judul);
        h.setFont(new Font("Segoe UI", Font.BOLD, 20));
        h.setForeground(Color.WHITE);
        h.setBounds(40, 25, 600, 30);
        bg.add(h);

        lblTimer = new JLabel("", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTimer.setForeground(Color.WHITE);
        lblTimer.setBounds(650, 22, 300, 36);
        bg.add(lblTimer);

        // ---------- PANEL SOAL (kiri) ----------
        RoundedPanel panelSoal = new RoundedPanel(20, Color.WHITE, true);
        panelSoal.setBounds(40, 80, 600, 520);
        panelSoal.setLayout(null);
        bg.add(panelSoal);

        lblNomor = new JLabel();
        lblNomor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNomor.setForeground(UI.NAVY);
        lblNomor.setBounds(30, 20, 540, 22);
        panelSoal.add(lblNomor);

        txtPertanyaan = new JTextArea();
        txtPertanyaan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPertanyaan.setEditable(false);
        txtPertanyaan.setLineWrap(true);
        txtPertanyaan.setWrapStyleWord(true);
        txtPertanyaan.setForeground(UI.DARK);
        txtPertanyaan.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JScrollPane spSoal = new JScrollPane(txtPertanyaan);
        spSoal.setBorder(BorderFactory.createEmptyBorder());
        spSoal.setBounds(30, 52, 540, 120);
        panelSoal.add(spSoal);

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            radio[i] = new JRadioButton();
            radio[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            radio[i].setOpaque(false);
            radio[i].setFocusPainted(false);
            radio[i].setBounds(30, 185 + i * 56, 540, 48);
            radio[i].addActionListener(e -> {
                if (sedangTampil) return;
                jawab[posisi] = HURUF[idx];
                perbaruiNavigator();
            });
            grup.add(radio[i]);
            panelSoal.add(radio[i]);
        }

        btnPrev = UI.tombol(panelSoal, "< SEBELUMNYA", UI.ABU, UI.ABU_H, 30, 452, 150, 42, () -> pindah(posisi - 1));
        btnNext = UI.tombol(panelSoal, "BERIKUTNYA >", UI.NAVY, new Color(10, 36, 66), 190, 452, 150, 42, () -> pindah(posisi + 1));

        // ---------- NAVIGATOR (kanan) ----------
        RoundedPanel panelNav = new RoundedPanel(20, Color.WHITE, true);
        panelNav.setBounds(660, 80, 290, 520);
        panelNav.setLayout(null);
        bg.add(panelNav);

        UI.judulForm(panelNav, "Navigasi Soal");

        lblTerjawab = new JLabel();
        lblTerjawab.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTerjawab.setForeground(Color.GRAY);
        lblTerjawab.setBounds(25, 42, 240, 18);
        panelNav.add(lblTerjawab);

        JPanel grid = new JPanel(new GridLayout(0, 5, 6, 6));
        grid.setBackground(Color.WHITE);
        for (int i = 0; i < soal.size(); i++) {
            final int idx = i;
            JButton b = new JButton(String.valueOf(i + 1));
            b.setPreferredSize(new Dimension(40, 36));
            b.setMargin(new java.awt.Insets(0, 0, 0, 0));
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210), 1));
            b.addActionListener(e -> pindah(idx));
            navBtn.add(b);
            grid.add(b);
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(grid, BorderLayout.NORTH);
        JScrollPane spNav = new JScrollPane(wrapper);
        spNav.setBorder(BorderFactory.createEmptyBorder());
        spNav.setBounds(25, 70, 245, 320);
        panelNav.add(spNav);

        JLabel legenda = new JLabel("<html><font color='#2ecc71'>\u25A0</font> terjawab &nbsp; "
            + "<font color='#95a5a6'>\u25A0</font> kosong &nbsp; <font color='#0f3460'>\u25A0</font> soal ini</html>");
        legenda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        legenda.setBounds(25, 400, 245, 18);
        panelNav.add(legenda);

        UI.tombol(panelNav, "SELESAI & KUMPULKAN", UI.HIJAU, UI.HIJAU_H, 25, 440, 240, 45, this::aksiSelesai);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { aksiKeluar(); }
        });
    }

    // ------------------------------------------------------------------
    //  TAMPILAN SOAL
    // ------------------------------------------------------------------
    private void tampilSoal() {
        Soal s = soal.get(posisi);
        sedangTampil = true;
        lblNomor.setText("Soal " + (posisi + 1) + " dari " + soal.size() + "   \u2022   skor " + s.skor);
        txtPertanyaan.setText(s.pertanyaan);
        txtPertanyaan.setCaretPosition(0);
        grup.clearSelection();
        for (int i = 0; i < 4; i++) {
            radio[i].setText("<html><body style='width:490px'><b>" + HURUF[i] + ".</b>&nbsp; "
                + esc(s.opsi[i]) + "</body></html>");
            if (HURUF[i].equals(jawab[posisi])) radio[i].setSelected(true);
        }
        sedangTampil = false;
        btnPrev.setEnabled(posisi > 0);
        btnNext.setEnabled(posisi < soal.size() - 1);
        perbaruiNavigator();
    }

    private void pindah(int ke) {
        if (ke < 0 || ke >= soal.size()) return;
        posisi = ke;
        tampilSoal();
    }

    private void perbaruiNavigator() {
        int terjawab = 0;
        for (int i = 0; i < navBtn.size(); i++) {
            JButton b = navBtn.get(i);
            boolean sudah = jawab[i] != null;
            if (sudah) terjawab++;
            if (i == posisi) {
                b.setBackground(UI.NAVY);
                b.setForeground(Color.WHITE);
            } else if (sudah) {
                b.setBackground(new Color(46, 204, 113));
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(236, 240, 241));
                b.setForeground(UI.DARK);
            }
        }
        lblTerjawab.setText("Terjawab: " + terjawab + " / " + soal.size());
    }

    // ------------------------------------------------------------------
    //  TIMER
    // ------------------------------------------------------------------
    private void mulaiTimer() {
        tickTimer();
        timer = new Timer(1000, e -> tickTimer());
        timer.start();
    }

    private void tickTimer() {
        long sisa = akhirMs - System.currentTimeMillis();
        if (sisa <= 0) {
            lblTimer.setText("Sisa waktu 00:00");
            if (!otomatisDicoba && !selesai) {
                otomatisDicoba = true; // hanya sekali, supaya tidak muncul dialog error berulang
                kirim(true);
            }
            return;
        }
        long detik = sisa / 1000;
        lblTimer.setText(String.format("Sisa waktu %02d:%02d", detik / 60, detik % 60));
        lblTimer.setForeground(detik <= 300 ? new Color(255, 107, 107) : Color.WHITE);
    }

    // ------------------------------------------------------------------
    //  KIRIM JAWABAN
    // ------------------------------------------------------------------
    private void aksiSelesai() {
        int kosong = 0;
        for (String j : jawab) if (j == null) kosong++;
        String pesan = kosong > 0
            ? "Masih ada " + kosong + " soal yang belum dijawab.\nKumpulkan sekarang?"
            : "Kumpulkan jawaban sekarang?";
        if (JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        kirim(false);
    }

    private void aksiKeluar() {
        if (JOptionPane.showConfirmDialog(this,
                "Keluar dari kuis akan langsung mengumpulkan jawaban yang sudah terisi.\nLanjutkan?",
                "Keluar dari Kuis", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        kirim(false);
    }

    private void kirim(boolean waktuHabis) {
        if (selesai) return;

        int benar = 0, skorDapat = 0, skorTotal = 0;
        for (int i = 0; i < soal.size(); i++) {
            Soal s = soal.get(i);
            skorTotal += s.skor;
            if (s.benar != null && s.benar.equals(jawab[i])) {
                benar++;
                skorDapat += s.skor;
            }
        }
        // dinormalisasi ke skala 0-100 supaya konsisten walau total skor soal bukan 100
        double nilai = skorTotal == 0 ? 0 : Math.round(skorDapat * 10000.0 / skorTotal) / 100.0;
        String ket = benar + "/" + soal.size() + " benar";

        boolean sudahAda = false;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM tb_nilai WHERE id_siswa=? AND id_kuis=? AND jenis_nilai='Kuis'")) {
                    ps.setInt(1, Session.idSiswa);
                    ps.setInt(2, idKuis);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        sudahAda = rs.getInt(1) > 0;
                    }
                }
                if (!sudahAda) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_nilai (id_siswa, id_guru, id_mapel, id_kuis, jenis_nilai, nilai, keterangan) "
                          + "VALUES (?,?,?,?,'Kuis',?,?)")) {
                        ps.setInt(1, Session.idSiswa);
                        if (idGuru == 0) ps.setNull(2, Types.INTEGER); else ps.setInt(2, idGuru);
                        if (idMapel == 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, idMapel);
                        ps.setInt(4, idKuis);
                        ps.setDouble(5, nilai);
                        ps.setString(6, ket);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            // dialog tetap terbuka dengan jawaban utuh, siswa bisa menekan KUMPULKAN lagi
            UI.errorDb(this, ex);
            return;
        }

        selesai = true;
        if (timer != null) timer.stop();

        if (sudahAda) {
            UI.peringatan(this, "Kuis ini sudah pernah dikerjakan, jawaban baru tidak disimpan.");
        } else {
            JOptionPane.showMessageDialog(this,
                (waktuHabis ? "Waktu habis! Jawaban dikumpulkan otomatis.\n\n" : "Jawaban berhasil dikumpulkan!\n\n")
              + "Nilai kamu: " + (nilai == Math.rint(nilai) ? String.valueOf((int) nilai) : String.format("%.2f", nilai))
              + "\nJawaban benar: " + ket,
                "Kuis Selesai", JOptionPane.INFORMATION_MESSAGE);
        }
        dispose();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}