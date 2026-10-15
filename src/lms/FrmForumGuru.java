package lms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
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

public class FrmForumGuru extends JFrame {

    private JComboBox<UI.Item> cmbFilter;
    private JLabel lblJudul, lblMeta;
    private JTextArea txtIsi, txtDetailBalasan, txtBalas;
    private DefaultTableModel modelThread, modelBalasan;
    private JTable tabelThread, tabelBalasan;
    private RoundedButton btnBaru, btnHapusThread, btnKirim, btnHapusBalasan;

    /** Kelas yang berkaitan dengan guru ini (untuk filter dan tujuan thread). */
    private final List<UI.Item> kelasSaya = new ArrayList<>();
    /** Isi balasan lengkap per id_balasan (tabel hanya menampilkan satu baris ringkas). */
    private final Map<Integer, String> isiBalasan = new HashMap<>();

    public FrmForumGuru() {
        initUI();
        muatKelas();
        cmbFilter.addActionListener(e -> muatThread(0));
        muatThread(0);
        muatDetail();
    }

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Forum Diskusi Guru", "FORUM DISKUSI AKADEMIK",
            () -> new FrmMenuGuru().setVisible(true));

        // ---------- KIRI: daftar thread ----------
        RoundedPanel kiri = new RoundedPanel(20, Color.WHITE, true);
        kiri.setBounds(40, 80, 400, 520);
        kiri.setLayout(null);
        bg.add(kiri);

        JLabel l1 = new JLabel("Daftar Diskusi");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l1.setForeground(UI.DARK);
        l1.setBounds(25, 16, 250, 25);
        kiri.add(l1);

        cmbFilter = new JComboBox<>();
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBounds(25, 50, 350, 28);
        kiri.add(cmbFilter);

        modelThread = UI.modelTabel("ID", "No", "Judul Diskusi", "Kelas", "Balasan");
        tabelThread = UI.tabel(modelThread);
        tabelThread.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelThread.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelThread.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabelThread.getColumnModel().getColumn(3).setPreferredWidth(60);
        JScrollPane spT = new JScrollPane(tabelThread);
        spT.setBounds(25, 88, 350, 350);
        kiri.add(spT);

        btnBaru = UI.tombol(kiri, "+ DISKUSI BARU", UI.HIJAU, UI.HIJAU_H, 25, 458, 170, 38, this::aksiBuat);
        btnHapusThread = UI.tombol(kiri, "HAPUS DISKUSI", UI.MERAH, UI.MERAH_H, 205, 458, 170, 38, this::aksiHapusThread);

        // ---------- KANAN: isi thread + balasan ----------
        RoundedPanel kanan = new RoundedPanel(20, Color.WHITE, true);
        kanan.setBounds(460, 80, 490, 520);
        kanan.setLayout(null);
        bg.add(kanan);

        lblJudul = new JLabel("Pilih diskusi di sebelah kiri.");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblJudul.setForeground(UI.DARK);
        lblJudul.setBounds(25, 16, 440, 24);
        kanan.add(lblJudul);

        lblMeta = new JLabel(" ");
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMeta.setForeground(Color.GRAY);
        lblMeta.setBounds(25, 42, 440, 18);
        kanan.add(lblMeta);

        txtIsi = areaBaca(new Color(250, 250, 250));
        JScrollPane spIsi = new JScrollPane(txtIsi);
        spIsi.setBounds(25, 66, 440, 100);
        spIsi.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        kanan.add(spIsi);

        JLabel l2 = new JLabel("Balasan");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(UI.NAVY);
        l2.setBounds(25, 172, 200, 18);
        kanan.add(l2);

        modelBalasan = UI.modelTabel("ID", "No", "Pengirim", "Waktu", "Isi Balasan");
        tabelBalasan = UI.tabel(modelBalasan);
        tabelBalasan.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelBalasan.getColumnModel().getColumn(1).setPreferredWidth(115);
        tabelBalasan.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabelBalasan.getColumnModel().getColumn(3).setPreferredWidth(185);
        JScrollPane spB = new JScrollPane(tabelBalasan);
        spB.setBounds(25, 192, 440, 120);
        kanan.add(spB);

        txtDetailBalasan = areaBaca(Color.WHITE);
        JScrollPane spD = new JScrollPane(txtDetailBalasan);
        spD.setBounds(25, 318, 440, 44);
        spD.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        kanan.add(spD);

        JLabel l3 = new JLabel("Tulis balasan");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l3.setForeground(UI.NAVY);
        l3.setBounds(25, 368, 200, 16);
        kanan.add(l3);

        txtBalas = new JTextArea();
        txtBalas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBalas.setLineWrap(true);
        txtBalas.setWrapStyleWord(true);
        JScrollPane spBalas = new JScrollPane(txtBalas);
        spBalas.setBounds(25, 386, 440, 52);
        spBalas.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        kanan.add(spBalas);

        btnKirim = UI.tombol(kanan, "KIRIM BALASAN", UI.NAVY, new Color(10, 36, 66), 25, 448, 160, 36, this::aksiBalas);
        btnHapusBalasan = UI.tombol(kanan, "HAPUS BALASAN", UI.MERAH, UI.MERAH_H, 195, 448, 160, 36, this::aksiHapusBalasan);

        // listener dipasang terakhir, setelah semua komponen siap
        tabelThread.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) muatDetail();
        });
        tabelBalasan.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int id = UI.idTerpilih(tabelBalasan, modelBalasan);
            txtDetailBalasan.setText(id == 0 ? "" : isiBalasan.getOrDefault(id, ""));
            txtDetailBalasan.setCaretPosition(0);
        });
    }

    private JTextArea areaBaca(Color latar) {
        JTextArea a = new JTextArea();
        a.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setEditable(false);
        a.setBackground(latar);
        return a;
    }

    private static String namaPeran(String role) {
        if (role == null) return "";
        switch (role) {
            case "guru":  return "Guru";
            case "siswa": return "Siswa";
            case "admin": return "Admin";
            default:      return role;
        }
    }

    private static String ringkas(String s) {
        return s.replaceAll("\\s+", " ").trim();
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** Kelas guru: wali kelas / jadwal / tugas / materi / kuis. Jika belum ada, tampilkan semua kelas. */
    private void muatKelas() {
        kelasSaya.clear();
        cmbFilter.removeAllItems();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            if (Session.idGuru != 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT k.id_kelas, k.nama_kelas FROM tb_kelas k "
                      + "WHERE k.id_wali_kelas = ? "
                      + "OR k.id_kelas IN (SELECT id_kelas FROM tb_jadwal WHERE id_guru = ?) "
                      + "OR k.id_kelas IN (SELECT id_kelas FROM tb_tugas WHERE id_guru = ?) "
                      + "OR k.id_kelas IN (SELECT id_kelas FROM tb_materi WHERE id_guru = ?) "
                      + "OR k.id_kelas IN (SELECT id_kelas FROM tb_kuis WHERE id_guru = ?) "
                      + "ORDER BY k.tingkat, k.nama_kelas")) {
                    for (int i = 1; i <= 5; i++) ps.setInt(i, Session.idGuru);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) kelasSaya.add(new UI.Item(rs.getInt(1), rs.getString(2)));
                    }
                }
            }
            if (kelasSaya.isEmpty()) {
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas")) {
                    while (rs.next()) kelasSaya.add(new UI.Item(rs.getInt(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        cmbFilter.addItem(new UI.Item(0, "Semua kelas saya"));
        for (UI.Item k : kelasSaya) cmbFilter.addItem(k);
    }

    /** @param pilihId id_forum yang dipilih ulang setelah reload (0 = tidak ada) */
    private void muatThread(int pilihId) {
        modelThread.setRowCount(0);
        UI.Item f = (UI.Item) cmbFilter.getSelectedItem();
        String filter;
        if (f != null && f.id > 0) {
            filter = "f.id_kelas = ?";
        } else {
            StringBuilder ids = new StringBuilder();
            for (UI.Item k : kelasSaya) ids.append(ids.length() == 0 ? "" : ",").append(k.id); // angka, aman
            filter = ids.length() == 0 ? "f.id_kelas IS NULL"
                                       : "(f.id_kelas IS NULL OR f.id_kelas IN (" + ids + "))";
        }
        String sql = "SELECT f.id_forum, f.judul, COALESCE(k.nama_kelas,'Semua') AS kelas, "
                   + "(SELECT COUNT(*) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum) AS jml "
                   + "FROM tb_forum f LEFT JOIN tb_kelas k ON f.id_kelas = k.id_kelas "
                   + "WHERE " + filter + " "
                   + "ORDER BY COALESCE((SELECT MAX(b.tanggal) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum), "
                   + "f.tanggal_post) DESC";
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (f != null && f.id > 0) ps.setInt(1, f.id);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        modelThread.addRow(new Object[]{rs.getInt("id_forum"), no++, rs.getString("judul"),
                            rs.getString("kelas"), rs.getInt("jml")});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
        if (pilihId != 0) {
            for (int i = 0; i < modelThread.getRowCount(); i++) {
                if ((Integer) modelThread.getValueAt(i, 0) == pilihId) {
                    tabelThread.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
        muatDetail();
    }

    private void muatDetail() {
        modelBalasan.setRowCount(0);
        isiBalasan.clear();
        txtDetailBalasan.setText("");
        int id = UI.idTerpilih(tabelThread, modelThread);
        if (id == 0) {
            lblJudul.setText("Pilih diskusi di sebelah kiri.");
            lblMeta.setText(" ");
            txtIsi.setText("");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT f.judul, f.isi, DATE_FORMAT(f.tanggal_post,'%d-%m-%Y %H:%i') AS tgl, "
                  + "COALESCE(u.nama_lengkap,'(akun dihapus)') AS nama, u.role, "
                  + "COALESCE(k.nama_kelas,'Semua kelas') AS kelas "
                  + "FROM tb_forum f LEFT JOIN tb_user u ON f.id_user = u.id_user "
                  + "LEFT JOIN tb_kelas k ON f.id_kelas = k.id_kelas WHERE f.id_forum=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return;
                    lblJudul.setText(rs.getString("judul"));
                    lblMeta.setText(rs.getString("nama") + " (" + namaPeran(rs.getString("role")) + ")  |  "
                        + rs.getString("tgl") + "  |  " + rs.getString("kelas"));
                    txtIsi.setText(rs.getString("isi"));
                    txtIsi.setCaretPosition(0);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT b.id_balasan, COALESCE(u.nama_lengkap,'(akun dihapus)') AS nama, u.role, "
                  + "DATE_FORMAT(b.tanggal,'%d-%m-%Y %H:%i') AS waktu, b.isi "
                  + "FROM tb_forum_balasan b LEFT JOIN tb_user u ON b.id_user = u.id_user "
                  + "WHERE b.id_forum=? ORDER BY b.tanggal, b.id_balasan")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        int idB = rs.getInt("id_balasan");
                        isiBalasan.put(idB, rs.getString("isi"));
                        modelBalasan.addRow(new Object[]{idB, no++,
                            rs.getString("nama") + " (" + namaPeran(rs.getString("role")) + ")",
                            rs.getString("waktu"), ringkas(rs.getString("isi"))});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    /** Boleh dihapus jika milik sendiri, atau milik siswa (moderasi guru). */
    private boolean bolehHapus(int idUserPenulis, String rolePenulis) {
        return idUserPenulis == Session.idUser || "siswa".equals(rolePenulis);
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void aksiBuat() {
        DialogThread d = new DialogThread();
        d.setVisible(true);
        if (d.idBaru != 0) {
            cmbFilter.setSelectedIndex(0); // tampilkan semua agar thread baru pasti terlihat
            muatThread(d.idBaru);
        }
    }

    private void aksiHapusThread() {
        int id = UI.idTerpilih(tabelThread, modelThread);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu diskusi yang ingin dihapus.");
            return;
        }
        String judul = UI.teksTerpilih(tabelThread, modelThread, 2);
        String jml = UI.teksTerpilih(tabelThread, modelThread, 4);
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            int penulis = 0;
            String role = null;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT f.id_user, u.role FROM tb_forum f LEFT JOIN tb_user u ON f.id_user = u.id_user "
                  + "WHERE f.id_forum=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { muatThread(0); return; }
                    penulis = rs.getInt(1);
                    role = rs.getString(2);
                }
            }
            if (!bolehHapus(penulis, role)) {
                UI.peringatan(this, "Anda hanya bisa menghapus diskusi milik sendiri atau milik siswa.");
                return;
            }
            if (!UI.konfirmasiHapus(this, "Hapus diskusi \"" + judul + "\"?\n\n"
                    + jml + " balasan di dalamnya juga akan dihapus.\nTindakan ini tidak bisa dibatalkan.")) return;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_forum WHERE id_forum=?")) {
                ps.setInt(1, id);
                ps.executeUpdate(); // balasan ikut terhapus (ON DELETE CASCADE)
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        muatThread(0);
        JOptionPane.showMessageDialog(this, "Diskusi berhasil dihapus.");
    }

    private void aksiBalas() {
        int id = UI.idTerpilih(tabelThread, modelThread);
        if (id == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu diskusi yang ingin dibalas.");
            return;
        }
        String isi = txtBalas.getText().trim();
        if (isi.isEmpty()) { UI.peringatan(this, "Isi balasan tidak boleh kosong."); return; }
        if (isi.length() > 2000) { UI.peringatan(this, "Balasan maksimal 2000 karakter."); return; }

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tb_forum_balasan (id_forum, id_user, isi) VALUES (?,?,?)")) {
                ps.setInt(1, id);
                ps.setInt(2, Session.idUser);
                ps.setString(3, isi);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        txtBalas.setText("");
        muatThread(id); // jumlah balasan & urutan thread ikut diperbarui
    }

    private void aksiHapusBalasan() {
        int idB = UI.idTerpilih(tabelBalasan, modelBalasan);
        int idThread = UI.idTerpilih(tabelThread, modelThread);
        if (idB == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu balasan yang ingin dihapus di tabel.");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            int penulis = 0;
            String role = null;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT b.id_user, u.role FROM tb_forum_balasan b LEFT JOIN tb_user u ON b.id_user = u.id_user "
                  + "WHERE b.id_balasan=?")) {
                ps.setInt(1, idB);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { muatThread(idThread); return; }
                    penulis = rs.getInt(1);
                    role = rs.getString(2);
                }
            }
            if (!bolehHapus(penulis, role)) {
                UI.peringatan(this, "Anda hanya bisa menghapus balasan milik sendiri atau milik siswa.");
                return;
            }
            if (!UI.konfirmasiHapus(this, "Hapus balasan ini?\nTindakan ini tidak bisa dibatalkan.")) return;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM tb_forum_balasan WHERE id_balasan=?")) {
                ps.setInt(1, idB);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        muatThread(idThread);
    }

    // ==================================================================
    //  DIALOG DISKUSI BARU
    // ==================================================================
    private class DialogThread extends JDialog {

        private int idBaru = 0;
        private JTextField txtJudulBaru;
        private JComboBox<UI.Item> cmbKelasBaru;
        private JTextArea txtIsiBaru;

        DialogThread() {
            super(FrmForumGuru.this, "Mulai Diskusi Baru", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(350, 380));
            setContentPane(p);

            txtJudulBaru = UI.field(p, "Judul Diskusi *", 12);
            cmbKelasBaru = new JComboBox<>();
            cmbKelasBaru.addItem(new UI.Item(0, "Semua kelas"));
            for (UI.Item k : kelasSaya) cmbKelasBaru.addItem(k);
            UI.combo(p, "Ditujukan ke *", 68, cmbKelasBaru);
            txtIsiBaru = UI.area(p, "Isi Diskusi *", 124, 150);

            UI.tombol(p, "TERBITKAN", UI.HIJAU, UI.HIJAU_H, 25, 310, 145, 42, this::simpan);
            UI.tombol(p, "BATAL", UI.ABU, UI.ABU_H, 180, 310, 145, 42, this::dispose);

            pack();
            setLocationRelativeTo(FrmForumGuru.this);
        }

        private void simpan() {
            String judul = txtJudulBaru.getText().trim();
            String isi = txtIsiBaru.getText().trim();
            if (judul.isEmpty()) { UI.peringatan(this, "Judul diskusi wajib diisi."); return; }
            if (judul.length() > 150) { UI.peringatan(this, "Judul maksimal 150 karakter."); return; }
            if (isi.isEmpty()) { UI.peringatan(this, "Isi diskusi wajib diisi."); return; }
            int idKelas = ((UI.Item) cmbKelasBaru.getSelectedItem()).id;

            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_forum (judul, isi, id_user, id_kelas) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, judul);
                    ps.setString(2, isi);
                    ps.setInt(3, Session.idUser);
                    if (idKelas == 0) ps.setNull(4, Types.INTEGER); else ps.setInt(4, idKelas);
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) {
                        if (k.next()) idBaru = k.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
                return;
            }
            JOptionPane.showMessageDialog(this, "Diskusi berhasil diterbitkan!");
            dispose();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmForumGuru().setVisible(true));
    }
}