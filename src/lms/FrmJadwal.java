package lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Admin: mengatur jadwal pelajaran (tb_jadwal).
 * Pilihan mapel dan guru mengikuti penugasan mengajar (tb_guru_mengajar) pada kelas yang dipilih,
 * dan jadwal yang bentrok (kelas, guru, atau ruangan) ditolak.
 */
public class FrmJadwal extends JFrame {

    private static final String[] HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat"};
    private static final String POLA_JAM = "([01]\\d|2[0-3]):[0-5]\\d";

    private JLabel lblFormTitle, lblInfo;
    private JComboBox<UI.Item> cmbKelas, cmbMapel, cmbGuru, cmbFilter;
    private JComboBox<String> cmbHari;
    private JTextField txtMulai, txtSelesai, txtRuangan;
    private DefaultTableModel model;
    private JTable tabel;
    private RoundedButton btnSimpan;

    private int idEdit = 0;            // 0 = tambah, >0 = edit (id_jadwal)
    private boolean sedangIsi = false; // true saat combo diisi lewat kode (jangan picu cascade)

    public FrmJadwal() {
        initUI();
        muatKelas();
        resetForm();
        muatTabel();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Jadwal Pelajaran", "MANAJEMEN JADWAL PELAJARAN");

        // ---------- FORM ----------
        RoundedPanel panelForm = UI.panelForm(bg);
        lblFormTitle = UI.judulForm(panelForm, "Form Input Jadwal");

        cmbKelas = new JComboBox<>();
        UI.combo(panelForm, "Kelas *", 52, cmbKelas);

        cmbHari = new JComboBox<>(HARI);
        UI.combo(panelForm, "Hari *", 108, cmbHari);

        txtMulai = fieldKecil(panelForm, "Jam Mulai * (07:30)", 25, 164, 140);
        txtSelesai = fieldKecil(panelForm, "Jam Selesai * (09:00)", 185, 164, 140);

        cmbMapel = new JComboBox<>();
        UI.combo(panelForm, "Mata Pelajaran *", 220, cmbMapel);

        cmbGuru = new JComboBox<>();
        UI.combo(panelForm, "Guru Pengajar *", 276, cmbGuru);

        txtRuangan = UI.field(panelForm, "Ruangan (opsional)", 332);

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 11));
        lblInfo.setForeground(java.awt.Color.GRAY);
        lblInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInfo.setBounds(25, 392, 300, 48);
        panelForm.add(lblInfo);

        btnSimpan = UI.tombol(panelForm, "SIMPAN", UI.HIJAU, UI.HIJAU_H, 25, 458, 145, 42, this::aksiSimpan);
        UI.tombol(panelForm, "BATAL / RESET", UI.ABU, UI.ABU_H, 180, 458, 145, 42, this::resetForm);

        // mapel mengikuti kelas, guru mengikuti kelas + mapel
        cmbKelas.addActionListener(e -> {
            if (sedangIsi) return;
            int k = idDari(cmbKelas);
            muatMapel(k, 0);
            muatGuru(k, 0, 0);
        });
        cmbMapel.addActionListener(e -> {
            if (sedangIsi) return;
            muatGuru(idDari(cmbKelas), idDari(cmbMapel), 0);
        });

        // ---------- TABEL ----------
        RoundedPanel panelTabel = UI.panelTabel(bg, "Jadwal Pelajaran");
        cmbFilter = new JComboBox<>();
        cmbFilter.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        cmbFilter.setBackground(java.awt.Color.WHITE);
        cmbFilter.setBounds(285, 17, 230, 28);
        cmbFilter.addActionListener(e -> { if (!sedangIsi) muatTabel(); });
        panelTabel.add(cmbFilter);

        model = UI.modelTabel("ID", "No", "Kelas", "Hari", "Jam", "Mata Pelajaran", "Guru", "Ruangan");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(105);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(105);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(55);
        UI.tambahTabel(panelTabel, tabel);

        UI.tombol(panelTabel, "UBAH DATA", UI.KUNING, UI.KUNING_H, 260, 458, 120, 38, this::aksiUbah);
        UI.tombol(panelTabel, "HAPUS", UI.MERAH, UI.MERAH_H, 395, 458, 120, 38, this::aksiHapus);
    }

    private JTextField fieldKecil(JPanel p, String teksLabel, int x, int y, int w) {
        JLabel l = new JLabel(teksLabel);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        l.setForeground(UI.NAVY);
        l.setBounds(x, y, w + 20, 16);
        p.add(l);

        JTextField t = new JTextField();
        t.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        t.setBounds(x, y + 17, w, 28);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        p.add(t);
        return t;
    }

    private static int idDari(JComboBox<UI.Item> cb) {
        UI.Item it = (UI.Item) cb.getSelectedItem();
        return it == null ? 0 : it.id;
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatKelas() {
        sedangIsi = true;
        cmbKelas.removeAllItems();
        cmbKelas.addItem(new UI.Item(0, "- Pilih kelas -"));
        cmbFilter.removeAllItems();
        cmbFilter.addItem(new UI.Item(0, "Semua kelas"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                while (rs.next()) {
                    cmbKelas.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    cmbFilter.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        } finally {
            sedangIsi = false;
        }
    }

    /** Mapel yang diajar di kelas tersebut (dari penugasan mengajar). */
    private void muatMapel(int idKelas, int pilih) {
        sedangIsi = true;
        cmbMapel.removeAllItems();
        cmbMapel.addItem(new UI.Item(0, "- Pilih mata pelajaran -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            if (idKelas > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT m.id_mapel, m.nama_mapel FROM tb_guru_mengajar gm "
                      + "JOIN tb_mapel m ON gm.id_mapel = m.id_mapel WHERE gm.id_kelas = ? ORDER BY m.nama_mapel")) {
                    ps.setInt(1, idKelas);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) cmbMapel.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                }
            }
            if (pilih > 0 && !ada(cmbMapel, pilih)) { // data lama di luar penugasan tetap bisa ditampilkan
                try (PreparedStatement ps = con.prepareStatement("SELECT nama_mapel FROM tb_mapel WHERE id_mapel=?")) {
                    ps.setInt(1, pilih);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) cmbMapel.addItem(new UI.Item(pilih, rs.getString(1) + " (di luar penugasan)"));
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        } finally {
            UI.pilihItem(cmbMapel, pilih);
            sedangIsi = false;
        }
        perbaruiInfo(idKelas);
    }

    /** Guru yang ditugaskan mengajar mapel itu di kelas itu. */
    private void muatGuru(int idKelas, int idMapel, int pilih) {
        sedangIsi = true;
        cmbGuru.removeAllItems();
        cmbGuru.addItem(new UI.Item(0, "- Pilih guru -"));
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            if (idKelas > 0 && idMapel > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT g.id_guru, g.nama_guru FROM tb_guru_mengajar gm "
                      + "JOIN tb_guru g ON gm.id_guru = g.id_guru WHERE gm.id_kelas = ? AND gm.id_mapel = ? "
                      + "ORDER BY g.nama_guru")) {
                    ps.setInt(1, idKelas);
                    ps.setInt(2, idMapel);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) cmbGuru.addItem(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                }
            }
            if (pilih > 0 && !ada(cmbGuru, pilih)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT nama_guru FROM tb_guru WHERE id_guru=?")) {
                    ps.setInt(1, pilih);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) cmbGuru.addItem(new UI.Item(pilih, rs.getString(1) + " (di luar penugasan)"));
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        } finally {
            UI.pilihItem(cmbGuru, pilih);
            if (pilih == 0 && cmbGuru.getItemCount() == 2) cmbGuru.setSelectedIndex(1); // hanya satu guru
            sedangIsi = false;
        }
    }

    private static boolean ada(JComboBox<UI.Item> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) if (cb.getItemAt(i).id == id) return true;
        return false;
    }

    private void perbaruiInfo(int idKelas) {
        if (idKelas == 0) {
            lblInfo.setText("<html>Pilih kelas lebih dulu. Mapel dan guru yang muncul mengikuti penugasan mengajar.</html>");
        } else if (cmbMapel.getItemCount() <= 1) {
            lblInfo.setText("<html><font color='#c0392b'>Belum ada guru yang ditugaskan mengajar di kelas ini. "
                + "Atur dulu di Data Guru &gt; ATUR MENGAJAR.</font></html>");
        } else {
            lblInfo.setText("<html>Mapel dan guru mengikuti penugasan mengajar di kelas ini.</html>");
        }
    }

    private void muatTabel() {
        model.setRowCount(0);
        int idFilter = idDari(cmbFilter);
        String sql = "SELECT j.id_jadwal, k.nama_kelas, j.hari, j.jam_mulai, j.jam_selesai, "
                   + "COALESCE(m.nama_mapel,'-') AS mapel, COALESCE(g.nama_guru,'-') AS guru, "
                   + "COALESCE(j.ruangan,'-') AS ruangan "
                   + "FROM tb_jadwal j JOIN tb_kelas k ON j.id_kelas = k.id_kelas "
                   + "LEFT JOIN tb_mapel m ON j.id_mapel = m.id_mapel "
                   + "LEFT JOIN tb_guru g ON j.id_guru = g.id_guru "
                   + (idFilter > 0 ? "WHERE j.id_kelas = ? " : "")
                   + "ORDER BY k.tingkat, k.nama_kelas, FIELD(j.hari,'Senin','Selasa','Rabu','Kamis','Jumat','Sabtu'), "
                   + "j.jam_mulai";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (idFilter > 0) ps.setInt(1, idFilter);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id_jadwal"), no++, rs.getString("nama_kelas"),
                            rs.getString("hari"), jam(rs.getString("jam_mulai")) + " - " + jam(rs.getString("jam_selesai")),
                            rs.getString("mapel"), rs.getString("guru"), rs.getString("ruangan")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    /** "07:30:00" -> "07:30" */
    private static String jam(String s) {
        return s == null ? "-" : (s.length() >= 5 ? s.substring(0, 5) : s);
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void resetForm() {
        idEdit = 0;
        sedangIsi = true;
        cmbKelas.setSelectedIndex(0);
        cmbHari.setSelectedIndex(0);
        sedangIsi = false;
        txtMulai.setText("07:00");
        txtSelesai.setText("08:30");
        txtRuangan.setText("");
        muatMapel(0, 0);
        muatGuru(0, 0, 0);
        lblFormTitle.setText("Form Input Jadwal");
        btnSimpan.setText("SIMPAN");
        if (tabel != null) tabel.clearSelection();
    }

    private void aksiUbah() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu jadwal yang ingin diubah di tabel.");
            return;
        }
        int idKelas, idMapel, idGuru;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_kelas, id_mapel, id_guru, hari, jam_mulai, jam_selesai, ruangan "
                  + "FROM tb_jadwal WHERE id_jadwal=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    idKelas = rs.getInt("id_kelas");
                    idMapel = rs.getInt("id_mapel"); // 0 jika NULL
                    idGuru = rs.getInt("id_guru");
                    cmbHari.setSelectedItem(rs.getString("hari"));
                    txtMulai.setText(jam(rs.getString("jam_mulai")));
                    txtSelesai.setText(jam(rs.getString("jam_selesai")));
                    txtRuangan.setText(rs.getString("ruangan") == null ? "" : rs.getString("ruangan"));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        sedangIsi = true;
        UI.pilihItem(cmbKelas, idKelas);
        sedangIsi = false;
        muatMapel(idKelas, idMapel);
        muatGuru(idKelas, idMapel, idGuru);
        idEdit = id;
        lblFormTitle.setText("Edit Jadwal");
        btnSimpan.setText("UPDATE");
    }

    private String validasi() {
        if (idDari(cmbKelas) == 0) return "Pilih kelas.";
        if (idDari(cmbMapel) == 0) return "Pilih mata pelajaran.";
        if (idDari(cmbGuru) == 0) return "Pilih guru pengajar.";
        String mulai = txtMulai.getText().trim(), selesai = txtSelesai.getText().trim();
        if (!mulai.matches(POLA_JAM)) return "Jam mulai harus berformat HH:mm, contoh 07:30.";
        if (!selesai.matches(POLA_JAM)) return "Jam selesai harus berformat HH:mm, contoh 09:00.";
        if (!parse(selesai).isAfter(parse(mulai))) return "Jam selesai harus setelah jam mulai.";
        if (txtRuangan.getText().trim().length() > 30) return "Ruangan maksimal 30 karakter.";
        return null;
    }

    private static LocalTime parse(String s) {
        try {
            return LocalTime.parse(s);
        } catch (DateTimeParseException e) {
            return LocalTime.MIDNIGHT;
        }
    }

    /**
     * Mencari jadwal lain yang bertabrakan waktu pada hari yang sama.
     * @return pesan bentrok, atau null jika aman
     */
    private String cekBentrok(Connection con, int idKelas, int idGuru, String hari, Time mulai, Time selesai,
                              String ruangan) throws SQLException {
        String dasar = "SELECT %s FROM tb_jadwal j JOIN tb_kelas k ON j.id_kelas = k.id_kelas "
                     + "LEFT JOIN tb_mapel m ON j.id_mapel = m.id_mapel "
                     + "LEFT JOIN tb_guru g ON j.id_guru = g.id_guru "
                     + "WHERE j.hari = ? AND j.jam_mulai < ? AND j.jam_selesai > ? AND j.id_jadwal <> ? AND %s LIMIT 1";
        String kolom = "k.nama_kelas, COALESCE(m.nama_mapel,'-') AS mapel, COALESCE(g.nama_guru,'-') AS guru, "
                     + "j.jam_mulai, j.jam_selesai";

        // 1) kelas yang sama
        try (PreparedStatement ps = con.prepareStatement(String.format(dasar, kolom, "j.id_kelas = ?"))) {
            isiWaktu(ps, hari, mulai, selesai);
            ps.setInt(5, idKelas);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return "Kelas " + rs.getString("nama_kelas") + " sudah punya jadwal "
                    + rs.getString("mapel") + " pukul " + jam(rs.getString("jam_mulai")) + " - "
                    + jam(rs.getString("jam_selesai")) + " pada hari " + hari + ".";
            }
        }
        // 2) guru yang sama
        try (PreparedStatement ps = con.prepareStatement(String.format(dasar, kolom, "j.id_guru = ?"))) {
            isiWaktu(ps, hari, mulai, selesai);
            ps.setInt(5, idGuru);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("guru") + " sudah mengajar " + rs.getString("mapel")
                    + " di kelas " + rs.getString("nama_kelas") + " pukul " + jam(rs.getString("jam_mulai"))
                    + " - " + jam(rs.getString("jam_selesai")) + " pada hari " + hari + ".";
            }
        }
        // 3) ruangan yang sama (jika diisi)
        if (!ruangan.isEmpty()) {
            try (PreparedStatement ps = con.prepareStatement(String.format(dasar, kolom, "j.ruangan = ?"))) {
                isiWaktu(ps, hari, mulai, selesai);
                ps.setString(5, ruangan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "Ruangan " + ruangan + " sudah dipakai kelas " + rs.getString("nama_kelas")
                        + " pukul " + jam(rs.getString("jam_mulai")) + " - " + jam(rs.getString("jam_selesai"))
                        + " pada hari " + hari + ".";
                }
            }
        }
        return null;
    }

    /** Parameter 1-4 yang sama untuk ketiga pemeriksaan bentrok. */
    private void isiWaktu(PreparedStatement ps, String hari, Time mulai, Time selesai) throws SQLException {
        ps.setString(1, hari);
        ps.setTime(2, selesai);   // jadwal lain dimulai sebelum jadwal ini selesai
        ps.setTime(3, mulai);     // ...dan selesai setelah jadwal ini mulai
        ps.setInt(4, idEdit);     // jadwal yang sedang diedit tidak dihitung
    }

    private void aksiSimpan() {
        String err = validasi();
        if (err != null) { UI.peringatan(this, err); return; }

        int idKelas = idDari(cmbKelas), idMapel = idDari(cmbMapel), idGuru = idDari(cmbGuru);
        String hari = cmbHari.getSelectedItem().toString();
        Time mulai = Time.valueOf(parse(txtMulai.getText().trim()));
        Time selesai = Time.valueOf(parse(txtSelesai.getText().trim()));
        String ruangan = txtRuangan.getText().trim();

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }

            if (!Mengajar.boleh(con, idGuru, idMapel, idKelas)) {
                UI.peringatan(this, "Guru ini belum ditugaskan mengajar mata pelajaran tersebut di kelas ini.\n"
                    + "Atur dulu di Data Guru > ATUR MENGAJAR.");
                return;
            }
            String bentrok = cekBentrok(con, idKelas, idGuru, hari, mulai, selesai, ruangan);
            if (bentrok != null) { UI.peringatan(this, "Jadwal bentrok:\n" + bentrok); return; }

            String sql = (idEdit == 0)
                ? "INSERT INTO tb_jadwal (id_kelas, id_mapel, id_guru, hari, jam_mulai, jam_selesai, ruangan) "
                  + "VALUES (?,?,?,?,?,?,?)"
                : "UPDATE tb_jadwal SET id_kelas=?, id_mapel=?, id_guru=?, hari=?, jam_mulai=?, jam_selesai=?, "
                  + "ruangan=? WHERE id_jadwal=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idKelas);
                ps.setInt(2, idMapel);
                ps.setInt(3, idGuru);
                ps.setString(4, hari);
                ps.setTime(5, mulai);
                ps.setTime(6, selesai);
                if (ruangan.isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, ruangan);
                if (idEdit != 0) ps.setInt(8, idEdit);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, idEdit == 0
                ? "Jadwal berhasil ditambahkan." : "Jadwal berhasil diperbarui.");
            resetForm();
            muatTabel();
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void aksiHapus() {
        int id = UI.idTerpilih(tabel, model);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu jadwal yang ingin dihapus di tabel.");
            return;
        }
        String kelas = UI.teksTerpilih(tabel, model, 2);
        String hari = UI.teksTerpilih(tabel, model, 3);
        String jam = UI.teksTerpilih(tabel, model, 4);
        String mapel = UI.teksTerpilih(tabel, model, 5);
        if (!UI.konfirmasiHapus(this, "Hapus jadwal " + mapel + " kelas " + kelas + "\n" + hari + ", " + jam
              + "?\n\nTindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_jadwal WHERE id_jadwal=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            if (idEdit == id) resetForm();
            muatTabel();
            JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus.");
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmJadwal().setVisible(true));
    }
}