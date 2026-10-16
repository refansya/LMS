package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Absensi Saya (siswa): rekap Hadir/Sakit/Izin/Alpha dan riwayat kehadiran per tanggal.
 * Halaman ini hanya untuk dilihat; pengisian absensi dilakukan guru.
 */
public class FrmAbsensiSiswa extends JFrame {

    private static final String SEMUA = "Semua bulan";
    private static final Locale ID = Locale.of("id", "ID");
    private static final DateTimeFormatter FMT_HARI = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy", ID);
    private static final DateTimeFormatter FMT_BULAN = DateTimeFormatter.ofPattern("MMMM yyyy", ID);

    private static final Color C_HADIR = new Color(39, 174, 96);
    private static final Color C_SAKIT = new Color(41, 128, 185);
    private static final Color C_IZIN = new Color(142, 68, 173);
    private static final Color C_ALPHA = new Color(192, 57, 43);

    private static class Catatan {
        LocalDate tanggal;
        String status, keterangan;
    }

    private final List<Catatan> semua = new ArrayList<>();
    private final List<YearMonth> daftarBulan = new ArrayList<>();

    private JComboBox<String> cmbBulan;
    private JLabel lblHadir, lblSakit, lblIzin, lblAlpha, lblPersen;
    private JProgressBar bar;
    private DefaultTableModel model;
    private JTable tabel;
    private boolean sedangMuat = false;

    public FrmAbsensiSiswa() {
        initUI();
        if (Session.idSiswa == 0) {
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatData();
        tampilkan();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Absensi Saya", "ABSENSI SAYA",
            () -> new FrmMenuSiswa().setVisible(true));

        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 910, 520);
        p.setLayout(null);
        bg.add(p);

        UI.judulForm(p, "Rekap Kehadiran");

        cmbBulan = new JComboBox<>(new String[]{SEMUA});
        cmbBulan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbBulan.setBackground(Color.WHITE);
        cmbBulan.setBounds(655, 14, 230, 28);
        cmbBulan.addActionListener(e -> { if (!sedangMuat) tampilkan(); });
        p.add(cmbBulan);

        // ---------- 4 KARTU STATUS ----------
        lblHadir = kartu(p, 25, "Hadir", C_HADIR);
        lblSakit = kartu(p, 243, "Sakit", C_SAKIT);
        lblIzin = kartu(p, 461, "Izin", C_IZIN);
        lblAlpha = kartu(p, 679, "Alpha", C_ALPHA);

        // ---------- PERSENTASE ----------
        lblPersen = new JLabel(" ");
        lblPersen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPersen.setForeground(UI.NAVY);
        lblPersen.setBounds(25, 148, 860, 20);
        p.add(lblPersen);

        bar = new JProgressBar(0, 100);
        bar.setForeground(C_HADIR);
        bar.setBounds(25, 172, 860, 14);
        p.add(bar);

        // ---------- RIWAYAT ----------
        JLabel lblRiwayat = new JLabel("Riwayat Kehadiran");
        lblRiwayat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRiwayat.setForeground(UI.DARK);
        lblRiwayat.setBounds(25, 198, 300, 22);
        p.add(lblRiwayat);

        model = UI.modelTabel("ID", "No", "Hari / Tanggal", "Status", "Keterangan");
        tabel = UI.tabel(model); // indeks tampilan: 0 No, 1 Tanggal, 2 Status, 3 Keterangan
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(260);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(440);

        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        tabel.getColumnModel().getColumn(0).setCellRenderer(tengah);
        tabel.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                if (!sel) comp.setForeground(warna(String.valueOf(v)));
                return comp;
            }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(25, 224, 860, 240);
        p.add(sp);

        JLabel hint = new JLabel("Persentase kehadiran = jumlah hari Hadir dibagi seluruh hari yang tercatat. "
            + "Data diisi oleh guru; hubungi wali kelas jika ada yang tidak sesuai.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(25, 474, 860, 18);
        p.add(hint);
    }

    /** Kartu angka kecil; mengembalikan label angkanya. */
    private JLabel kartu(JPanel parent, int x, String judul, Color warna) {
        RoundedPanel k = new RoundedPanel(15, new Color(245, 246, 250), false);
        k.setBounds(x, 55, 205, 80);
        k.setLayout(null);
        parent.add(k);

        JLabel angka = new JLabel("0", SwingConstants.CENTER);
        angka.setFont(new Font("Segoe UI", Font.BOLD, 30));
        angka.setForeground(warna);
        angka.setBounds(0, 10, 205, 40);
        k.add(angka);

        JLabel teks = new JLabel(judul, SwingConstants.CENTER);
        teks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teks.setForeground(Color.GRAY);
        teks.setBounds(0, 52, 205, 18);
        k.add(teks);
        return angka;
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatData() {
        semua.clear();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT tanggal, status, keterangan FROM tb_absensi WHERE id_siswa = ? ORDER BY tanggal DESC")) {
                ps.setInt(1, Session.idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Catatan c = new Catatan();
                        Date d = rs.getDate("tanggal");
                        c.tanggal = d.toLocalDate();
                        c.status = rs.getString("status");
                        c.keterangan = rs.getString("keterangan");
                        semua.add(c);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        // pilihan bulan: hanya bulan yang punya catatan, terbaru di atas
        Set<YearMonth> bulan = new LinkedHashSet<>();
        for (Catatan c : semua) bulan.add(YearMonth.from(c.tanggal));
        daftarBulan.clear();
        daftarBulan.addAll(bulan);

        sedangMuat = true;
        cmbBulan.removeAllItems();
        cmbBulan.addItem(SEMUA);
        for (YearMonth ym : daftarBulan) cmbBulan.addItem(kapital(FMT_BULAN.format(ym.atDay(1))));
        sedangMuat = false;
    }

    // ------------------------------------------------------------------
    //  TAMPILAN (rekap + tabel mengikuti filter bulan)
    // ------------------------------------------------------------------
    private void tampilkan() {
        int idx = cmbBulan.getSelectedIndex();
        YearMonth filter = (idx <= 0 || idx > daftarBulan.size()) ? null : daftarBulan.get(idx - 1);

        model.setRowCount(0);
        int hadir = 0, sakit = 0, izin = 0, alpha = 0, no = 1;
        for (Catatan c : semua) {
            if (filter != null && !YearMonth.from(c.tanggal).equals(filter)) continue;
            switch (c.status) {
                case "Hadir": hadir++; break;
                case "Sakit": sakit++; break;
                case "Izin": izin++; break;
                case "Alpha": alpha++; break;
                default: break;
            }
            model.addRow(new Object[]{0, no++, kapital(FMT_HARI.format(c.tanggal)), c.status,
                c.keterangan == null || c.keterangan.trim().isEmpty() ? "-" : c.keterangan});
        }

        lblHadir.setText(String.valueOf(hadir));
        lblSakit.setText(String.valueOf(sakit));
        lblIzin.setText(String.valueOf(izin));
        lblAlpha.setText(String.valueOf(alpha));

        int total = hadir + sakit + izin + alpha;
        if (total == 0) {
            bar.setValue(0);
            lblPersen.setText(semua.isEmpty() ? "Belum ada data absensi yang tercatat."
                : "Tidak ada catatan absensi pada bulan ini.");
        } else {
            double persen = hadir * 100.0 / total;
            bar.setValue((int) Math.round(persen));
            lblPersen.setText(String.format(ID, "Persentase Kehadiran: %.1f%%   (%d hari hadir dari %d hari tercatat)",
                persen, hadir, total));
        }
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static Color warna(String status) {
        switch (status) {
            case "Hadir": return C_HADIR;
            case "Sakit": return C_SAKIT;
            case "Izin": return C_IZIN;
            default: return C_ALPHA;
        }
    }

    /** "senin, 05-10-2026" -> "Senin, 05-10-2026". */
    private static String kapital(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmAbsensiSiswa().setVisible(true));
    }
}