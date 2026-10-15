package lms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmKuisGuru extends JFrame {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private JLabel lblInfo, lblStat;
    private DefaultTableModel modelKuis, modelHasil;
    private JTable tabelKuis, tabelHasil;
    private RoundedButton btnBuat, btnUbah, btnHapus, btnSoal;

    public FrmKuisGuru() {
        initUI();
        muatKuis(0);

        if (Session.idGuru == 0) {
            btnBuat.setEnabled(false);
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            btnSoal.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data guru, jadi belum bisa mengelola kuis.\n"
              + "Minta administrator menambahkan Anda di menu Data Guru.",
                "Data Guru Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Manajemen Kuis Guru", "MANAJEMEN KUIS & EVALUASI",
            () -> new FrmMenuGuru().setVisible(true));

        // ---------- KIRI: daftar kuis ----------
        RoundedPanel kiri = new RoundedPanel(20, Color.WHITE, true);
        kiri.setBounds(40, 80, 440, 520);
        kiri.setLayout(null);
        bg.add(kiri);

        JLabel l1 = new JLabel("Kuis Saya");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l1.setForeground(UI.DARK);
        l1.setBounds(25, 18, 250, 25);
        kiri.add(l1);

        modelKuis = UI.modelTabel("ID", "No", "Judul Kuis", "Kelas", "Status", "Soal", "Dikerjakan");
        tabelKuis = UI.tabel(modelKuis);
        tabelKuis.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelKuis.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabelKuis.getColumnModel().getColumn(2).setPreferredWidth(55);
        tabelKuis.getColumnModel().getColumn(3).setPreferredWidth(75);
        tabelKuis.getColumnModel().getColumn(4).setPreferredWidth(35);
        tabelKuis.getColumnModel().getColumn(5).setPreferredWidth(60);
        JScrollPane spK = new JScrollPane(tabelKuis);
        spK.setBounds(25, 60, 390, 360);
        kiri.add(spK);

        btnBuat = UI.tombol(kiri, "BUAT KUIS", UI.HIJAU, UI.HIJAU_H, 25, 432, 125, 34, this::aksiBuat);
        btnUbah = UI.tombol(kiri, "UBAH", UI.KUNING, UI.KUNING_H, 160, 432, 125, 34, this::aksiUbah);
        btnHapus = UI.tombol(kiri, "HAPUS", UI.MERAH, UI.MERAH_H, 295, 432, 120, 34, this::aksiHapus);
        btnSoal = UI.tombol(kiri, "KELOLA SOAL", UI.NAVY, new Color(10, 36, 66), 25, 474, 390, 34, this::aksiSoal);

        // ---------- KANAN: hasil pengerjaan ----------
        RoundedPanel kanan = new RoundedPanel(20, Color.WHITE, true);
        kanan.setBounds(500, 80, 450, 520);
        kanan.setLayout(null);
        bg.add(kanan);

        JLabel l2 = new JLabel("Hasil Pengerjaan Siswa");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l2.setForeground(UI.DARK);
        l2.setBounds(25, 18, 300, 25);
        kanan.add(l2);

        lblInfo = new JLabel("Pilih kuis di sebelah kiri.");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(25, 44, 400, 36);
        kanan.add(lblInfo);

        modelHasil = UI.modelTabel("ID", "No", "Nama Siswa", "Nilai", "Waktu Mengerjakan");
        tabelHasil = UI.tabel(modelHasil);
        tabelHasil.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelHasil.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabelHasil.getColumnModel().getColumn(2).setPreferredWidth(55);
        tabelHasil.getColumnModel().getColumn(3).setPreferredWidth(140);
        JScrollPane spH = new JScrollPane(tabelHasil);
        spH.setBounds(25, 88, 400, 350);
        kanan.add(spH);

        lblStat = new JLabel(" ");
        lblStat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStat.setForeground(UI.NAVY);
        lblStat.setBounds(25, 452, 400, 22);
        kanan.add(lblStat);

        // pasang listener terakhir, setelah semua model siap
        tabelKuis.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) muatHasil();
        });
    }

    private static String teks(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /** 85.00 -> "85", 87.50 -> "87.5" */
    private static String fmt(BigDecimal n) {
        if (n == null) return "0";
        BigDecimal r = n.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return (r.scale() < 0 ? r.setScale(0) : r).toPlainString();
    }

    private static LocalDateTime parseWaktu(String s) {
        if (!s.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) return null;
        try {
            return LocalDateTime.parse(s.replace(' ', 'T'));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Jumlah siswa yang sudah mengerjakan kuis ini (nilai kuis tersimpan di tb_nilai). */
    private int jumlahHasil(int idKuis) {
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return 0; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM tb_nilai WHERE id_kuis=? AND jenis_nilai='Kuis'")) {
                ps.setInt(1, idKuis);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return 0;
        }
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** @param pilihId id_kuis yang dipilih ulang setelah reload (0 = tidak ada) */
    private void muatKuis(int pilihId) {
        modelKuis.setRowCount(0);
        if (Session.idGuru == 0) return;
        String sql = "SELECT q.id_kuis, q.judul, COALESCE(k.nama_kelas,'-') AS kelas, "
                   + "CASE WHEN NOW() < q.tanggal_mulai THEN 'Akan datang' "
                   + "WHEN NOW() > q.tanggal_selesai THEN 'Selesai' ELSE 'Berlangsung' END AS st, "
                   + "(SELECT COUNT(*) FROM tb_soal s WHERE s.id_kuis = q.id_kuis) AS soal, "
                   + "(SELECT COUNT(*) FROM tb_nilai n WHERE n.id_kuis = q.id_kuis AND n.jenis_nilai='Kuis') AS kerja, "
                   + "(SELECT COUNT(*) FROM tb_siswa x WHERE x.id_kelas = q.id_kelas) AS total "
                   + "FROM tb_kuis q LEFT JOIN tb_kelas k ON q.id_kelas = k.id_kelas "
                   + "WHERE q.id_guru = ? ORDER BY q.tanggal_mulai DESC";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        modelKuis.addRow(new Object[]{rs.getInt("id_kuis"), no++, rs.getString("judul"),
                            rs.getString("kelas"), rs.getString("st"), rs.getInt("soal"),
                            rs.getInt("kerja") + "/" + rs.getInt("total")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        if (pilihId != 0) {
            for (int i = 0; i < modelKuis.getRowCount(); i++) {
                if ((Integer) modelKuis.getValueAt(i, 0) == pilihId) {
                    tabelKuis.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private void muatHasil() {
        modelHasil.setRowCount(0);
        lblStat.setText(" ");
        int idKuis = UI.idTerpilih(tabelKuis, modelKuis);
        if (idKuis == 0) {
            lblInfo.setText("Pilih kuis di sebelah kiri.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            int idKelas = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT q.judul, COALESCE(m.nama_mapel,'-') AS mapel, q.durasi, "
                  + "COALESCE(DATE_FORMAT(q.tanggal_mulai,'%d-%m-%Y %H:%i'),'-') AS mulai, "
                  + "COALESCE(DATE_FORMAT(q.tanggal_selesai,'%d-%m-%Y %H:%i'),'-') AS selesai, q.id_kelas "
                  + "FROM tb_kuis q LEFT JOIN tb_mapel m ON q.id_mapel = m.id_mapel "
                  + "WHERE q.id_kuis=? AND q.id_guru=?")) {
                ps.setInt(1, idKuis);
                ps.setInt(2, Session.idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    idKelas = rs.getInt("id_kelas");
                    lblInfo.setText("<html><b>" + rs.getString("judul") + "</b><br>" + rs.getString("mapel")
                        + " | " + rs.getInt("durasi") + " menit | " + rs.getString("mulai")
                        + " s/d " + rs.getString("selesai") + "</html>");
                }
            }

            BigDecimal jumlah = BigDecimal.ZERO, maks = null, min = null;
            int kerja = 0, total = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT s.nama_siswa, n.nilai, "
                  + "DATE_FORMAT(n.tanggal_input,'%d-%m-%Y %H:%i') AS waktu, s.id_siswa "
                  + "FROM tb_siswa s LEFT JOIN tb_nilai n ON n.id_siswa = s.id_siswa "
                  + "AND n.id_kuis = ? AND n.jenis_nilai = 'Kuis' "
                  + "WHERE s.id_kelas = ? ORDER BY s.nama_siswa")) {
                ps.setInt(1, idKuis);
                ps.setInt(2, idKelas);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        total++;
                        BigDecimal nilai = rs.getBigDecimal("nilai");
                        if (nilai != null) {
                            kerja++;
                            jumlah = jumlah.add(nilai);
                            if (maks == null || nilai.compareTo(maks) > 0) maks = nilai;
                            if (min == null || nilai.compareTo(min) < 0) min = nilai;
                        }
                        modelHasil.addRow(new Object[]{rs.getInt("id_siswa"), no++, rs.getString("nama_siswa"),
                            nilai == null ? "-" : fmt(nilai),
                            nilai == null ? "Belum mengerjakan" : rs.getString("waktu")});
                    }
                }
            }
            if (total == 0) {
                lblStat.setText("Kelas ini belum memiliki siswa.");
            } else if (kerja == 0) {
                lblStat.setText("Dikerjakan 0/" + total);
            } else {
                lblStat.setText("Dikerjakan " + kerja + "/" + total
                    + "  |  Rata-rata " + fmt(jumlah.divide(new BigDecimal(kerja), 2, RoundingMode.HALF_UP))
                    + "  |  Maks " + fmt(maks) + "  |  Min " + fmt(min));
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    // ------------------------------------------------------------------
    //  AKSI - KUIS
    // ------------------------------------------------------------------
    private void aksiBuat() {
        DialogKuis d = new DialogKuis(0);
        d.setVisible(true);
        if (d.tersimpan) muatKuis(0);
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabelKuis, modelKuis);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kuis yang ingin diubah di tabel.");
            return;
        }
        DialogKuis d = new DialogKuis(id);
        d.setVisible(true);
        if (d.tersimpan) muatKuis(id);
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabelKuis, modelKuis);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kuis yang ingin dihapus di tabel.");
            return;
        }
        String judul = UI.teksTerpilih(tabelKuis, modelKuis, 2);
        String jmlSoal = UI.teksTerpilih(tabelKuis, modelKuis, 5);
        String jmlKerja = UI.teksTerpilih(tabelKuis, modelKuis, 6).split("/")[0];
        if (!UI.konfirmasiHapus(this,
                "Hapus kuis \"" + judul + "\"?\n\n"
              + jmlSoal + " soal dan " + jmlKerja + " hasil pengerjaan siswa (beserta nilainya) juga akan dihapus.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            con.setAutoCommit(false);
            try {
                // nilai kuis dihapus lebih dulu; jika tidak, FK akan mengubahnya jadi nilai "manual" yatim
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM tb_nilai WHERE id_kuis=? AND jenis_nilai='Kuis' "
                      + "AND id_kuis IN (SELECT id_kuis FROM tb_kuis WHERE id_guru=?)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, Session.idGuru);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM tb_kuis WHERE id_kuis=? AND id_guru=?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, Session.idGuru);
                    ps.executeUpdate(); // tb_soal ikut terhapus (ON DELETE CASCADE)
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        muatKuis(0);
        JOptionPane.showMessageDialog(this, "Kuis berhasil dihapus.");
    }

    private void aksiSoal() {
        int id = UI.idTerpilih(tabelKuis, modelKuis);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu kuis yang soalnya ingin dikelola.");
            return;
        }
        String judul = UI.teksTerpilih(tabelKuis, modelKuis, 2);
        new DialogSoal(id, judul, jumlahHasil(id) > 0).setVisible(true);
        muatKuis(id); // jumlah soal di tabel ikut diperbarui
    }

    // ==================================================================
    //  DIALOG BUAT / UBAH KUIS
    // ==================================================================
    private class DialogKuis extends JDialog {

        private final int idEdit;
        private boolean tersimpan = false;
        private JTextField txtJudul, txtDurasi, txtMulai, txtSelesai;
        private JTextArea txtDesk;
        private JComboBox<UI.Item> cmbMapel, cmbKelas;
        private JLabel lblKunci;

        DialogKuis(int idEdit) {
            super(FrmKuisGuru.this, idEdit == 0 ? "Buat Kuis Baru" : "Ubah Kuis", true);
            this.idEdit = idEdit;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(350, 520));
            setContentPane(p);

            txtJudul = UI.field(p, "Judul Kuis *", 12);
            cmbMapel = new JComboBox<>();
            UI.combo(p, "Mata Pelajaran *", 68, cmbMapel);
            cmbKelas = new JComboBox<>();
            UI.combo(p, "Kelas *", 124, cmbKelas);
            txtDurasi = UI.field(p, "Durasi * (menit, 1 - 300)", 180);
            txtMulai = UI.field(p, "Dibuka * (format: 2026-10-31 07:00)", 236);
            txtSelesai = UI.field(p, "Ditutup * (format: 2026-10-31 23:59)", 292);
            txtDesk = UI.area(p, "Deskripsi / Petunjuk", 348, 60);

            lblKunci = new JLabel("Kelas & mapel dikunci karena kuis sudah dikerjakan siswa.");
            lblKunci.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblKunci.setForeground(Color.GRAY);
            lblKunci.setBounds(25, 432, 300, 16);
            lblKunci.setVisible(false);
            p.add(lblKunci);

            UI.tombol(p, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 458, 145, 42, this::simpan);
            UI.tombol(p, "BATAL", UI.ABU, UI.ABU_H, 180, 458, 145, 42, this::dispose);

            muatPilihan();
            if (idEdit == 0) {
                txtDurasi.setText("60");
                LocalDateTime sekarang = LocalDateTime.now().withSecond(0).withNano(0);
                txtMulai.setText(sekarang.format(FORMAT_WAKTU));
                txtSelesai.setText(sekarang.plusDays(7).withHour(23).withMinute(59).format(FORMAT_WAKTU));
            } else {
                muatData();
            }
            pack();
            setLocationRelativeTo(FrmKuisGuru.this);
        }

        private void muatPilihan() {
            cmbMapel.removeAllItems();
            cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
            cmbKelas.removeAllItems();
            cmbKelas.addItem(new UI.Item(0, "- Pilih kelas -"));
            int mapelGuru = 0;
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (Statement st = con.createStatement()) {
                    try (ResultSet rs = st.executeQuery("SELECT id_mapel, nama_mapel FROM tb_mapel ORDER BY nama_mapel")) {
                        while (rs.next()) cmbMapel.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                    try (ResultSet rs = st.executeQuery("SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                        while (rs.next()) cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("SELECT id_mapel FROM tb_guru WHERE id_guru=?")) {
                    ps.setInt(1, Session.idGuru);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) mapelGuru = rs.getInt(1); // 0 jika NULL
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
            }
            UI.pilihItem(cmbMapel, mapelGuru);
        }

        private void muatData() {
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT judul, deskripsi, durasi, "
                      + "DATE_FORMAT(tanggal_mulai,'%Y-%m-%d %H:%i') AS mulai, "
                      + "DATE_FORMAT(tanggal_selesai,'%Y-%m-%d %H:%i') AS selesai, id_mapel, id_kelas "
                      + "FROM tb_kuis WHERE id_kuis=? AND id_guru=?")) {
                    ps.setInt(1, idEdit);
                    ps.setInt(2, Session.idGuru);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return;
                        txtJudul.setText(rs.getString("judul"));
                        txtDurasi.setText(String.valueOf(rs.getInt("durasi")));
                        txtMulai.setText(rs.getString("mulai") == null ? "" : rs.getString("mulai"));
                        txtSelesai.setText(rs.getString("selesai") == null ? "" : rs.getString("selesai"));
                        txtDesk.setText(rs.getString("deskripsi") == null ? "" : rs.getString("deskripsi"));
                        UI.pilihItem(cmbMapel, rs.getInt("id_mapel"));
                        UI.pilihItem(cmbKelas, rs.getInt("id_kelas"));
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
            }
            if (jumlahHasil(idEdit) > 0) { // nilai sudah tercatat -> mapel & kelas tidak boleh berubah
                cmbMapel.setEnabled(false);
                cmbKelas.setEnabled(false);
                lblKunci.setVisible(true);
            }
        }

        private String validasi() {
            String judul = txtJudul.getText().trim();
            String durasi = txtDurasi.getText().trim();
            if (judul.isEmpty()) return "Judul kuis wajib diisi.";
            if (judul.length() > 150) return "Judul maksimal 150 karakter.";
            if (((UI.Item) cmbMapel.getSelectedItem()).id == 0) return "Pilih mata pelajaran.";
            if (((UI.Item) cmbKelas.getSelectedItem()).id == 0) return "Pilih kelas.";
            if (!durasi.matches("\\d{1,3}") || Integer.parseInt(durasi) < 1 || Integer.parseInt(durasi) > 300) {
                return "Durasi harus angka 1 - 300 menit.";
            }
            LocalDateTime mulai = parseWaktu(txtMulai.getText().trim());
            LocalDateTime selesai = parseWaktu(txtSelesai.getText().trim());
            if (mulai == null) return "Waktu dibuka harus berformat 2026-10-31 07:00 dan tanggalnya valid.";
            if (selesai == null) return "Waktu ditutup harus berformat 2026-10-31 23:59 dan tanggalnya valid.";
            if (!selesai.isAfter(mulai)) return "Waktu ditutup harus setelah waktu dibuka.";
            if (idEdit == 0 && selesai.isBefore(LocalDateTime.now())) return "Waktu ditutup tidak boleh di masa lalu.";
            return null;
        }

        private void simpan() {
            String err = validasi();
            if (err != null) { UI.peringatan(this, err); return; }

            String judul = txtJudul.getText().trim();
            String desk = txtDesk.getText().trim();
            int durasi = Integer.parseInt(txtDurasi.getText().trim());
            Timestamp mulai = Timestamp.valueOf(parseWaktu(txtMulai.getText().trim()));
            Timestamp selesai = Timestamp.valueOf(parseWaktu(txtSelesai.getText().trim()));
            int idMapel = ((UI.Item) cmbMapel.getSelectedItem()).id;
            int idKelas = ((UI.Item) cmbKelas.getSelectedItem()).id;

            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                if (idEdit == 0) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_kuis (judul, deskripsi, durasi, tanggal_mulai, tanggal_selesai, "
                          + "id_guru, id_mapel, id_kelas) VALUES (?,?,?,?,?,?,?,?)")) {
                        ps.setString(1, judul);
                        ps.setString(2, desk.isEmpty() ? null : desk);
                        ps.setInt(3, durasi);
                        ps.setTimestamp(4, mulai);
                        ps.setTimestamp(5, selesai);
                        ps.setInt(6, Session.idGuru);
                        ps.setInt(7, idMapel);
                        ps.setInt(8, idKelas);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_kuis SET judul=?, deskripsi=?, durasi=?, tanggal_mulai=?, tanggal_selesai=?, "
                          + "id_mapel=?, id_kelas=? WHERE id_kuis=? AND id_guru=?")) {
                        ps.setString(1, judul);
                        ps.setString(2, desk.isEmpty() ? null : desk);
                        ps.setInt(3, durasi);
                        ps.setTimestamp(4, mulai);
                        ps.setTimestamp(5, selesai);
                        ps.setInt(6, idMapel);
                        ps.setInt(7, idKelas);
                        ps.setInt(8, idEdit);
                        ps.setInt(9, Session.idGuru);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
                return;
            }
            tersimpan = true;
            JOptionPane.showMessageDialog(this, idEdit == 0
                ? "Kuis berhasil dibuat! Tambahkan soal lewat tombol KELOLA SOAL." : "Kuis berhasil diperbarui!");
            dispose();
        }
    }

    // ==================================================================
    //  DIALOG KELOLA SOAL
    // ==================================================================
    private class DialogSoal extends JDialog {

        private final int idKuis;
        private final boolean terkunci;
        private int idSoalEdit = 0; // 0 = tambah, >0 = edit (id_soal)

        private DefaultTableModel modelSoal;
        private JTable tabelSoal;
        private JLabel lblTotal;
        private JTextArea txtTanya;
        private JTextField txtA, txtB, txtC, txtD, txtSkor;
        private JComboBox<String> cmbJawab;
        private RoundedButton btnSimpanSoal, btnHapusSoal;

        DialogSoal(int idKuis, String judul, boolean terkunci) {
            super(FrmKuisGuru.this, "Kelola Soal", true);
            this.idKuis = idKuis;
            this.terkunci = terkunci;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(870, 550));
            setContentPane(p);

            JLabel judulLbl = new JLabel("Soal untuk: " + judul);
            judulLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            judulLbl.setForeground(UI.DARK);
            judulLbl.setBounds(20, 12, 820, 24);
            p.add(judulLbl);

            // ---- kiri: daftar soal ----
            modelSoal = UI.modelTabel("ID", "No", "Pertanyaan", "Jawaban", "Skor");
            tabelSoal = UI.tabel(modelSoal);
            tabelSoal.getColumnModel().getColumn(0).setPreferredWidth(35);
            tabelSoal.getColumnModel().getColumn(1).setPreferredWidth(310);
            tabelSoal.getColumnModel().getColumn(2).setPreferredWidth(60);
            tabelSoal.getColumnModel().getColumn(3).setPreferredWidth(50);
            JScrollPane sp = new JScrollPane(tabelSoal);
            sp.setBounds(20, 48, 470, 380);
            p.add(sp);

            lblTotal = new JLabel(" ");
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTotal.setForeground(UI.NAVY);
            lblTotal.setBounds(20, 434, 470, 18);
            p.add(lblTotal);

            JLabel lblKunci = new JLabel("Soal dikunci karena kuis sudah dikerjakan siswa.");
            lblKunci.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblKunci.setForeground(new Color(192, 41, 43));
            lblKunci.setBounds(20, 454, 470, 16);
            lblKunci.setVisible(terkunci);
            p.add(lblKunci);

            btnHapusSoal = UI.tombol(p, "HAPUS SOAL", UI.MERAH, UI.MERAH_H, 20, 490, 150, 38, this::hapusSoal);
            UI.tombol(p, "TUTUP", UI.ABU, UI.ABU_H, 180, 490, 100, 38, this::dispose);

            // ---- kanan: form soal ----
            JPanel f = new JPanel(null);
            f.setBackground(Color.WHITE);
            f.setBounds(510, 40, 340, 440);
            p.add(f);

            txtTanya = UI.area(f, "Pertanyaan *", 0, 70);
            txtA = UI.field(f, "Opsi A *", 95);
            txtB = UI.field(f, "Opsi B *", 151);
            txtC = UI.field(f, "Opsi C *", 207);
            txtD = UI.field(f, "Opsi D *", 263);
            cmbJawab = new JComboBox<>(new String[]{"A", "B", "C", "D"});
            UI.combo(f, "Jawaban Benar *", 319, cmbJawab);
            txtSkor = UI.field(f, "Skor (1 - 100)", 375);

            btnSimpanSoal = UI.tombol(p, "SIMPAN SOAL", UI.HIJAU, UI.HIJAU_H, 535, 490, 160, 38, this::simpanSoal);
            UI.tombol(p, "FORM BARU", UI.NAVY, new Color(10, 36, 66), 705, 490, 120, 38, this::resetForm);

            if (terkunci) {
                btnSimpanSoal.setEnabled(false);
                btnHapusSoal.setEnabled(false);
            }

            tabelSoal.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) isiFormDariTabel();
            });

            resetForm();
            muatSoal();
            pack();
            setLocationRelativeTo(FrmKuisGuru.this);
        }

        private void muatSoal() {
            modelSoal.setRowCount(0);
            int total = 0, skor = 0;
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id_soal, pertanyaan, jawaban_benar, skor FROM tb_soal WHERE id_kuis=? ORDER BY id_soal")) {
                    ps.setInt(1, idKuis);
                    try (ResultSet rs = ps.executeQuery()) {
                        int no = 1;
                        while (rs.next()) {
                            total++;
                            skor += rs.getInt("skor");
                            modelSoal.addRow(new Object[]{rs.getInt("id_soal"), no++,
                                rs.getString("pertanyaan").replaceAll("\\s+", " "),
                                rs.getString("jawaban_benar"), rs.getInt("skor")});
                        }
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
            }
            lblTotal.setText("Total: " + total + " soal  |  Skor maksimal: " + skor);
        }

        private void resetForm() {
            idSoalEdit = 0;
            txtTanya.setText("");
            txtA.setText("");
            txtB.setText("");
            txtC.setText("");
            txtD.setText("");
            txtSkor.setText("10");
            cmbJawab.setSelectedIndex(0);
            btnSimpanSoal.setText("SIMPAN SOAL");
            tabelSoal.clearSelection();
            txtTanya.requestFocus();
        }

        private void isiFormDariTabel() {
            int id = UI.idTerpilih(tabelSoal, modelSoal);
            if (id == 0) return;
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT pertanyaan, opsi_a, opsi_b, opsi_c, opsi_d, jawaban_benar, skor "
                      + "FROM tb_soal WHERE id_soal=? AND id_kuis=?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, idKuis);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return;
                        txtTanya.setText(rs.getString("pertanyaan"));
                        txtA.setText(rs.getString("opsi_a"));
                        txtB.setText(rs.getString("opsi_b"));
                        txtC.setText(rs.getString("opsi_c"));
                        txtD.setText(rs.getString("opsi_d"));
                        cmbJawab.setSelectedItem(rs.getString("jawaban_benar"));
                        txtSkor.setText(String.valueOf(rs.getInt("skor")));
                    }
                }
                idSoalEdit = id;
                btnSimpanSoal.setText("UPDATE SOAL");
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
            }
        }

        private String validasi() {
            if (txtTanya.getText().trim().isEmpty()) return "Pertanyaan wajib diisi.";
            String[] opsi = {txtA.getText().trim(), txtB.getText().trim(), txtC.getText().trim(), txtD.getText().trim()};
            String[] nama = {"A", "B", "C", "D"};
            for (int i = 0; i < 4; i++) {
                if (opsi[i].isEmpty()) return "Opsi " + nama[i] + " wajib diisi.";
                if (opsi[i].length() > 255) return "Opsi " + nama[i] + " maksimal 255 karakter.";
            }
            String skor = txtSkor.getText().trim();
            if (!skor.matches("\\d{1,3}") || Integer.parseInt(skor) < 1 || Integer.parseInt(skor) > 100) {
                return "Skor harus angka 1 - 100.";
            }
            return null;
        }

        private void simpanSoal() {
            String err = validasi();
            if (err != null) { UI.peringatan(this, err); return; }

            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                if (idSoalEdit == 0) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO tb_soal (pertanyaan, opsi_a, opsi_b, opsi_c, opsi_d, jawaban_benar, skor, id_kuis) "
                          + "VALUES (?,?,?,?,?,?,?,?)")) {
                        isiParameter(ps);
                        ps.setInt(8, idKuis);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE tb_soal SET pertanyaan=?, opsi_a=?, opsi_b=?, opsi_c=?, opsi_d=?, "
                          + "jawaban_benar=?, skor=? WHERE id_soal=? AND id_kuis=?")) {
                        isiParameter(ps);
                        ps.setInt(8, idSoalEdit);
                        ps.setInt(9, idKuis);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
                return;
            }
            boolean baru = (idSoalEdit == 0);
            resetForm();
            muatSoal();
            if (!baru) JOptionPane.showMessageDialog(this, "Soal berhasil diperbarui.");
        }

        /** Parameter 1-7 yang sama untuk INSERT dan UPDATE. */
        private void isiParameter(PreparedStatement ps) throws SQLException {
            ps.setString(1, txtTanya.getText().trim());
            ps.setString(2, txtA.getText().trim());
            ps.setString(3, txtB.getText().trim());
            ps.setString(4, txtC.getText().trim());
            ps.setString(5, txtD.getText().trim());
            ps.setString(6, cmbJawab.getSelectedItem().toString());
            ps.setInt(7, Integer.parseInt(txtSkor.getText().trim()));
        }

        private void hapusSoal() {
            int id = UI.idTerpilih(tabelSoal, modelSoal);
            if (id == 0) {
                JOptionPane.showMessageDialog(this, "Pilih dulu soal yang ingin dihapus di tabel.");
                return;
            }
            if (!UI.konfirmasiHapus(this, "Hapus soal nomor " + UI.teksTerpilih(tabelSoal, modelSoal, 1)
                    + "?\nTindakan ini tidak bisa dibatalkan.")) return;
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_soal WHERE id_soal=? AND id_kuis=?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, idKuis);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
                return;
            }
            resetForm();
            muatSoal();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmKuisGuru().setVisible(true));
    }
}