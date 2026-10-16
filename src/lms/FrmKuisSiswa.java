package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Kuis Saya (siswa): daftar kuis kelas, detail, dan tombol untuk memulai pengerjaan.
 * Pengerjaan sendiri ada di {@link FrmKerjakanKuis}.
 */
public class FrmKuisSiswa extends JFrame {

    private static final String BELUM = "Belum Dikerjakan";
    private static final String BELUM_DIBUKA = "Belum Dibuka";
    private static final String SELESAI = "Selesai";
    private static final String DITUTUP = "Ditutup";

    private static final String[] FILTER_TEKS = {"Semua status", "Belum dikerjakan", "Belum dibuka", "Selesai", "Ditutup"};
    private static final String[] FILTER_KUNCI = {null, BELUM, BELUM_DIBUKA, SELESAI, DITUTUP};

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static class Kuis {
        int id, durasi, jmlSoal, totalSkor, kkm;
        String judul, deskripsi, mapel, guru, ketNilai;
        LocalDateTime mulai, selesai;
        Double nilai;   // null = belum pernah mengerjakan

        boolean sudahSelesai() { return nilai != null; }
        boolean belumDibuka() { return mulai != null && mulai.isAfter(LocalDateTime.now()); }
        boolean ditutup() { return selesai != null && selesai.isBefore(LocalDateTime.now()); }

        String kunci() {
            if (sudahSelesai()) return SELESAI;
            if (ditutup()) return DITUTUP;
            if (belumDibuka()) return BELUM_DIBUKA;
            return BELUM;
        }

        int urutan() {
            switch (kunci()) {
                case BELUM: return 0;
                case BELUM_DIBUKA: return 1;
                case SELESAI: return 2;
                default: return 3;
            }
        }
    }

    private final Map<Integer, Kuis> daftar = new LinkedHashMap<>();

    // daftar (kiri)
    private JTextField txtCari;
    private JComboBox<String> cmbFilter;
    private DefaultTableModel model;
    private JTable tabel;
    private JLabel lblRingkasan;

    // detail (kanan)
    private JLabel lblJudul, lblMeta, lblDibuka, lblDitutup, lblInfo, lblHasil, lblHint;
    private JTextArea txtDeskripsi;
    private RoundedButton btnMulai;

    private boolean sedangMuat = false;

    public FrmKuisSiswa() {
        initUI();
        if (Session.idSiswa == 0 || Session.idKelas == 0) {
            kosongkanDetail();
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa atau belum masuk ke sebuah kelas.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Kuis Saya", "KUIS SAYA",
            () -> new FrmMenuSiswa().setVisible(true));

        // ---------- DAFTAR KUIS (kiri) ----------
        RoundedPanel panelList = new RoundedPanel(20, Color.WHITE, true);
        panelList.setBounds(40, 80, 540, 520);
        panelList.setLayout(null);
        bg.add(panelList);

        JLabel judulList = new JLabel("Daftar Kuis Kelas");
        judulList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judulList.setForeground(UI.DARK);
        judulList.setBounds(25, 18, 260, 25);
        panelList.add(judulList);

        model = UI.modelTabel("ID", "No", "Mapel", "Judul Kuis", "Batas Akhir", "Status");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(32);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(108);
        tabel.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setForeground(warnaStatus(String.valueOf(v)));
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                return comp;
            }
        });
        UI.tambahTabel(panelList, tabel);
        txtCari = UI.kolomCari(panelList, this::muatTabel);

        cmbFilter = new JComboBox<>(FILTER_TEKS);
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBounds(25, 455, 170, 28);
        cmbFilter.addActionListener(e -> muatTabel());
        panelList.add(cmbFilter);

        lblRingkasan = new JLabel("", SwingConstants.RIGHT);
        lblRingkasan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRingkasan.setForeground(Color.GRAY);
        lblRingkasan.setBounds(205, 455, 310, 28);
        panelList.add(lblRingkasan);

        // ---------- DETAIL KUIS (kanan) ----------
        RoundedPanel panelDetail = new RoundedPanel(20, Color.WHITE, true);
        panelDetail.setBounds(600, 80, 350, 520);
        panelDetail.setLayout(null);
        bg.add(panelDetail);

        UI.judulForm(panelDetail, "Detail Kuis");

        lblJudul = new JLabel();
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblJudul.setForeground(UI.NAVY);
        lblJudul.setVerticalAlignment(SwingConstants.TOP);
        lblJudul.setBounds(25, 48, 300, 42);
        panelDetail.add(lblJudul);

        lblMeta = new JLabel();
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta.setForeground(Color.GRAY);
        lblMeta.setBounds(25, 92, 300, 18);
        panelDetail.add(lblMeta);

        lblDibuka = new JLabel();
        lblDibuka.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDibuka.setForeground(UI.DARK);
        lblDibuka.setBounds(25, 114, 300, 18);
        panelDetail.add(lblDibuka);

        lblDitutup = new JLabel();
        lblDitutup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDitutup.setForeground(UI.DARK);
        lblDitutup.setBounds(25, 132, 300, 18);
        panelDetail.add(lblDitutup);

        lblInfo = new JLabel();
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfo.setForeground(UI.NAVY);
        lblInfo.setBounds(25, 152, 300, 18);
        panelDetail.add(lblInfo);

        UI.label(panelDetail, "Deskripsi", 178);
        txtDeskripsi = new JTextArea();
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDeskripsi.setEditable(false);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        JScrollPane spDesk = new JScrollPane(txtDeskripsi);
        spDesk.setBounds(25, 195, 300, 56);
        spDesk.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spDesk);

        UI.label(panelDetail, "Tata Tertib", 260);
        JTextArea aturan = new JTextArea(
            "1. Kerjakan secara mandiri.\n"
          + "2. Waktu berjalan sejak tombol MULAI ditekan dan tidak bisa dijeda.\n"
          + "3. Kuis hanya bisa dikerjakan satu kali.\n"
          + "4. Menutup jendela kuis = jawaban langsung dikumpulkan.\n"
          + "5. Saat waktu habis, jawaban dikumpulkan otomatis.");
        aturan.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        aturan.setForeground(Color.DARK_GRAY);
        aturan.setEditable(false);
        aturan.setLineWrap(true);
        aturan.setWrapStyleWord(true);
        aturan.setBackground(new Color(245, 246, 250));
        aturan.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        aturan.setBounds(25, 277, 300, 100);
        panelDetail.add(aturan);

        lblHasil = new JLabel();
        lblHasil.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHasil.setForeground(Color.DARK_GRAY);
        lblHasil.setVerticalAlignment(SwingConstants.TOP);
        lblHasil.setBounds(25, 386, 300, 40);
        panelDetail.add(lblHasil);

        btnMulai = UI.tombol(panelDetail, "MULAI KUIS", UI.HIJAU, UI.HIJAU_H,
            25, 435, 300, 45, this::aksiMulai);

        lblHint = new JLabel();
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setBounds(25, 486, 300, 20);
        panelDetail.add(lblHint);

        // dipasang paling akhir supaya semua komponen detail sudah ada
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!sedangMuat && !e.getValueIsAdjusting()) tampilDetail();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatTabel() {
        if (Session.idSiswa == 0 || Session.idKelas == 0) return;

        int pilihSebelumnya = UI.idTerpilih(tabel, model);
        String kata = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT k.id_kuis, k.judul, k.deskripsi, k.durasi, k.tanggal_mulai, k.tanggal_selesai, "
                   + "COALESCE(mp.nama_mapel,'-') AS mapel, COALESCE(mp.kkm,0) AS kkm, "
                   + "COALESCE(g.nama_guru,'-') AS guru, "
                   + "(SELECT COUNT(*) FROM tb_soal s WHERE s.id_kuis = k.id_kuis) AS jml_soal, "
                   + "(SELECT COALESCE(SUM(s.skor),0) FROM tb_soal s WHERE s.id_kuis = k.id_kuis) AS total_skor, "
                   + "(SELECT n.nilai FROM tb_nilai n WHERE n.id_kuis = k.id_kuis AND n.id_siswa = ? "
                   + "   AND n.jenis_nilai='Kuis' ORDER BY n.id_nilai DESC LIMIT 1) AS nilai, "
                   + "(SELECT n.keterangan FROM tb_nilai n WHERE n.id_kuis = k.id_kuis AND n.id_siswa = ? "
                   + "   AND n.jenis_nilai='Kuis' ORDER BY n.id_nilai DESC LIMIT 1) AS ket_nilai "
                   + "FROM tb_kuis k "
                   + "LEFT JOIN tb_mapel mp ON k.id_mapel = mp.id_mapel "
                   + "LEFT JOIN tb_guru g ON k.id_guru = g.id_guru "
                   + "WHERE k.id_kelas = ? AND (k.judul LIKE ? OR mp.nama_mapel LIKE ?)";

        List<Kuis> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idSiswa);
                ps.setInt(2, Session.idSiswa);
                ps.setInt(3, Session.idKelas);
                ps.setString(4, kata);
                ps.setString(5, kata);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Kuis k = new Kuis();
                        k.id = rs.getInt("id_kuis");
                        k.judul = rs.getString("judul");
                        k.deskripsi = rs.getString("deskripsi");
                        k.durasi = rs.getInt("durasi");
                        Timestamp tm = rs.getTimestamp("tanggal_mulai");
                        k.mulai = tm == null ? null : tm.toLocalDateTime();
                        Timestamp ts = rs.getTimestamp("tanggal_selesai");
                        k.selesai = ts == null ? null : ts.toLocalDateTime();
                        k.mapel = rs.getString("mapel");
                        k.kkm = rs.getInt("kkm");
                        k.guru = rs.getString("guru");
                        k.jmlSoal = rs.getInt("jml_soal");
                        k.totalSkor = rs.getInt("total_skor");
                        double n = rs.getDouble("nilai");
                        k.nilai = rs.wasNull() ? null : n;
                        k.ketNilai = rs.getString("ket_nilai");
                        hasil.add(k);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        // yang perlu dikerjakan dulu (batas terdekat di atas), yang ditutup paling bawah
        hasil.sort(Comparator.comparingInt(Kuis::urutan)
            .thenComparing(k -> k.selesai, Comparator.nullsLast(Comparator.naturalOrder())));

        daftar.clear();
        int belum = 0, selesai = 0;
        for (Kuis k : hasil) {
            daftar.put(k.id, k);
            if (BELUM.equals(k.kunci())) belum++;
            if (SELESAI.equals(k.kunci())) selesai++;
        }

        String kunciFilter = FILTER_KUNCI[cmbFilter.getSelectedIndex()];

        sedangMuat = true;
        model.setRowCount(0);
        int no = 1, barisPilih = -1;
        for (Kuis k : hasil) {
            if (kunciFilter != null && !kunciFilter.equals(k.kunci())) continue;
            if (k.id == pilihSebelumnya) barisPilih = model.getRowCount();
            model.addRow(new Object[]{k.id, no++, k.mapel, k.judul,
                k.selesai == null ? "-" : FMT.format(k.selesai), teksStatus(k)});
        }
        sedangMuat = false;

        lblRingkasan.setText(hasil.size() + " kuis  |  " + belum + " belum dikerjakan  |  " + selesai + " selesai");

        if (barisPilih == -1 && pilihSebelumnya == 0 && model.getRowCount() > 0) barisPilih = 0;
        if (barisPilih >= 0) {
            tabel.setRowSelectionInterval(barisPilih, barisPilih); // memicu tampilDetail()
        } else {
            kosongkanDetail();
        }
    }

    // ------------------------------------------------------------------
    //  DETAIL
    // ------------------------------------------------------------------
    private void kosongkanDetail() {
        lblJudul.setText("<html><body style='width:300px'><i>Pilih kuis di daftar untuk melihat detailnya.</i></body></html>");
        lblMeta.setText("");
        lblDibuka.setText("");
        lblDitutup.setText("");
        lblInfo.setText("");
        txtDeskripsi.setText("");
        lblHasil.setText("");
        lblHint.setText("");
        btnMulai.setEnabled(false);
    }

    private void tampilDetail() {
        Kuis k = daftar.get(UI.idTerpilih(tabel, model));
        if (k == null) { kosongkanDetail(); return; }

        lblJudul.setText("<html><body style='width:300px'>" + esc(k.judul) + "</body></html>");
        lblMeta.setText(k.mapel + "  \u2022  " + k.guru);
        lblDibuka.setText("Dibuka: " + (k.mulai == null ? "Langsung" : FMT.format(k.mulai)));
        lblDitutup.setText("Ditutup: " + (k.selesai == null ? "Tanpa batas" : FMT.format(k.selesai)));
        lblDitutup.setForeground(k.ditutup() && !k.sudahSelesai() ? new Color(192, 57, 43) : UI.DARK);
        lblInfo.setText("Durasi " + k.durasi + " menit  \u2022  " + k.jmlSoal + " soal  \u2022  skor " + k.totalSkor);

        txtDeskripsi.setText(k.deskripsi == null || k.deskripsi.trim().isEmpty()
            ? "(Tidak ada deskripsi)" : k.deskripsi);
        txtDeskripsi.setCaretPosition(0);

        if (k.sudahSelesai()) {
            boolean tuntas = k.nilai >= k.kkm;
            lblHasil.setText("<html><b>Nilai: " + fmtNilai(k.nilai) + "</b>  (KKM " + k.kkm + ") \u2013 "
                + "<font color='" + (tuntas ? "#27ae60" : "#c0392b") + "'><b>"
                + (tuntas ? "Tuntas" : "Belum tuntas") + "</b></font>"
                + (k.ketNilai == null ? "" : "<br>" + esc(k.ketNilai)) + "</html>");
        } else {
            lblHasil.setText("");
        }

        boolean bisaMulai = BELUM.equals(k.kunci()) && k.jmlSoal > 0;
        btnMulai.setEnabled(bisaMulai);
        btnMulai.setText(k.sudahSelesai() ? "SUDAH DIKERJAKAN" : "MULAI KUIS");

        if (k.sudahSelesai()) {
            lblHint.setText("Kuis hanya bisa dikerjakan satu kali.");
        } else if (k.ditutup()) {
            lblHint.setText("Waktu pengerjaan kuis sudah ditutup.");
        } else if (k.belumDibuka()) {
            lblHint.setText("Kuis baru bisa dikerjakan mulai " + FMT.format(k.mulai) + ".");
        } else if (k.jmlSoal == 0) {
            lblHint.setText("Guru belum menambahkan soal pada kuis ini.");
        } else {
            lblHint.setText("Pastikan kamu siap, waktu langsung berjalan.");
        }
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void aksiMulai() {
        Kuis k = daftar.get(UI.idTerpilih(tabel, model));
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kuis yang ingin dikerjakan.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Mulai kuis \"" + k.judul + "\" sekarang?\n\n"
              + "Waktu " + k.durasi + " menit akan langsung berjalan dan tidak bisa dijeda.\n"
              + "Kuis hanya bisa dikerjakan satu kali.",
                "Konfirmasi Mulai", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) return;

        FrmKerjakanKuis dlg = new FrmKerjakanKuis(this, k.id);
        if (dlg.siap()) dlg.setVisible(true); // modal: kembali ke sini setelah kuis selesai
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static String teksStatus(Kuis k) {
        return SELESAI.equals(k.kunci()) ? "Selesai (" + fmtNilai(k.nilai) + ")" : k.kunci();
    }

    private static Color warnaStatus(String teks) {
        if (teks.startsWith(SELESAI)) return new Color(39, 174, 96);
        if (teks.startsWith(DITUTUP)) return new Color(192, 57, 43);
        if (teks.startsWith(BELUM_DIBUKA)) return new Color(127, 140, 141);
        return new Color(211, 84, 0); // belum dikerjakan
    }

    private static String fmtNilai(double n) {
        return n == Math.rint(n) ? String.valueOf((int) n) : String.format("%.1f", n);
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmKuisSiswa().setVisible(true));
    }
}