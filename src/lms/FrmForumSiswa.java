package lms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Forum Diskusi Kelas (siswa): melihat diskusi di kelas sendiri, membalas, membuat diskusi baru,
 * dan menghapus diskusi milik sendiri. Data di tb_forum dan tb_forum_balasan.
 */
public class FrmForumSiswa extends JFrame {

    private static final int MAKS_JUDUL = 150;
    private static final int MAKS_ISI = 2000;
    private static final int MAKS_BALASAN = 1000;

    private static final DateTimeFormatter FMT_LENGKAP = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter FMT_SINGKAT = DateTimeFormatter.ofPattern("dd-MM HH:mm");

    private static class Diskusi {
        int id, idUser, jmlBalasan;
        String judul, isi, penulis, role;
        LocalDateTime tanggal, aktivitas;
    }

    private final Map<Integer, Diskusi> daftar = new LinkedHashMap<>();

    // daftar (kiri)
    private DefaultTableModel model;
    private JTable tabel;
    private JLabel lblRingkasan;

    // detail (kanan)
    private AvatarBulat avatar;
    private JLabel lblPenulis, lblMeta, lblJudul, lblBalasan;
    private JTextArea txtIsi, txtBalas;
    private JEditorPane panelBalasan;
    private JScrollPane spBalasan;
    private RoundedButton btnKirim, btnHapus;

    private int idDetail = 0;
    private boolean sedangMuat = false;

    public FrmForumSiswa() {
        initUI();
        if (Session.idUser == 0 || Session.idKelas == 0) {
            kosongkanDetail();
            btnBuat.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                "Akun ini belum terhubung dengan data siswa atau belum masuk ke sebuah kelas.\n"
              + "Minta administrator melengkapinya di menu Data Siswa.",
                "Data Siswa Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        muatTabel(0);
    }

    private RoundedButton btnBuat;

    // ------------------------------------------------------------------
    //  UI
    // ------------------------------------------------------------------
    private void initUI() {
        JPanel bg = UI.siapkanFrame(this, "Forum Diskusi", "FORUM DISKUSI KELAS",
            () -> new FrmMenuSiswa().setVisible(true));

        // ---------- DAFTAR DISKUSI (kiri) ----------
        RoundedPanel panelList = new RoundedPanel(20, Color.WHITE, true);
        panelList.setBounds(40, 80, 420, 520);
        panelList.setLayout(null);
        bg.add(panelList);

        JLabel judulList = new JLabel("Diskusi Kelas");
        judulList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judulList.setForeground(UI.DARK);
        judulList.setBounds(25, 18, 180, 25);
        panelList.add(judulList);

        lblRingkasan = new JLabel("", SwingConstants.RIGHT);
        lblRingkasan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRingkasan.setForeground(Color.GRAY);
        lblRingkasan.setBounds(195, 22, 200, 18);
        panelList.add(lblRingkasan);

        model = UI.modelTabel("ID", "Judul Diskusi", "Penulis", "Balasan", "Aktivitas");
        tabel = UI.tabel(model); // indeks tampilan: 0 Judul, 1 Penulis, 2 Balasan, 3 Aktivitas
        tabel.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(50);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(80);
        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);
        tabel.getColumnModel().getColumn(2).setCellRenderer(tengah);
        JScrollPane spList = new JScrollPane(tabel);
        spList.setBounds(25, 56, 370, 400);
        panelList.add(spList);

        btnBuat = UI.tombol(panelList, "+ DISKUSI BARU", UI.HIJAU, UI.HIJAU_H, 25, 468, 195, 36, this::aksiBuat);
        UI.tombol(panelList, "MUAT ULANG", UI.NAVY, new Color(10, 36, 66), 230, 468, 165, 36,
            () -> muatTabel(idDetail));

        // ---------- DETAIL DISKUSI (kanan) ----------
        RoundedPanel panelDetail = new RoundedPanel(20, Color.WHITE, true);
        panelDetail.setBounds(480, 80, 470, 520);
        panelDetail.setLayout(null);
        bg.add(panelDetail);

        avatar = new AvatarBulat("?", UI.NAVY);
        avatar.setBounds(25, 18, 48, 48);
        panelDetail.add(avatar);

        lblPenulis = new JLabel();
        lblPenulis.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPenulis.setForeground(UI.DARK);
        lblPenulis.setBounds(85, 20, 360, 22);
        panelDetail.add(lblPenulis);

        lblMeta = new JLabel();
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMeta.setForeground(Color.GRAY);
        lblMeta.setBounds(85, 43, 360, 18);
        panelDetail.add(lblMeta);

        lblJudul = new JLabel();
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblJudul.setForeground(UI.NAVY);
        lblJudul.setVerticalAlignment(SwingConstants.TOP);
        lblJudul.setBounds(25, 74, 420, 40);
        panelDetail.add(lblJudul);

        txtIsi = new JTextArea();
        txtIsi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtIsi.setEditable(false);
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        txtIsi.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JScrollPane spIsi = new JScrollPane(txtIsi);
        spIsi.setBounds(25, 116, 420, 80);
        spIsi.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spIsi);

        lblBalasan = new JLabel();
        lblBalasan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBalasan.setForeground(UI.NAVY);
        lblBalasan.setBounds(25, 204, 420, 18);
        panelDetail.add(lblBalasan);

        panelBalasan = new JEditorPane();
        panelBalasan.setContentType("text/html");
        panelBalasan.setEditable(false);
        panelBalasan.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        panelBalasan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spBalasan = new JScrollPane(panelBalasan);
        spBalasan.setBounds(25, 226, 420, 168);
        spBalasan.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spBalasan);

        txtBalas = new JTextArea();
        txtBalas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBalas.setLineWrap(true);
        txtBalas.setWrapStyleWord(true);
        txtBalas.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JScrollPane spBalas = new JScrollPane(txtBalas);
        spBalas.setBounds(25, 402, 420, 52);
        spBalas.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        panelDetail.add(spBalas);

        btnKirim = UI.tombol(panelDetail, "KIRIM BALASAN", UI.NAVY, new Color(10, 36, 66),
            25, 464, 200, 38, this::aksiBalas);
        btnHapus = UI.tombol(panelDetail, "HAPUS DISKUSI", UI.MERAH, UI.MERAH_H,
            245, 464, 200, 38, this::aksiHapus);

        // dipasang paling akhir supaya semua komponen detail sudah ada
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!sedangMuat && !e.getValueIsAdjusting()) tampilDetail();
        });
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    /** @param pilihId diskusi yang dipilih ulang setelah reload (0 = pilih yang paling atas) */
    private void muatTabel(int pilihId) {
        if (Session.idUser == 0 || Session.idKelas == 0) return;

        String sql = "SELECT f.id_forum, f.judul, f.isi, f.id_user, f.tanggal_post, "
                   + "COALESCE(u.nama_lengkap,'(akun dihapus)') AS penulis, COALESCE(u.role,'') AS role, "
                   + "(SELECT COUNT(*) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum) AS jml, "
                   + "COALESCE((SELECT MAX(b.tanggal) FROM tb_forum_balasan b WHERE b.id_forum = f.id_forum), "
                   + "f.tanggal_post) AS aktivitas "
                   + "FROM tb_forum f LEFT JOIN tb_user u ON f.id_user = u.id_user "
                   + "WHERE f.id_kelas = ? ORDER BY aktivitas DESC, f.id_forum DESC";

        List<Diskusi> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Session.idKelas);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Diskusi d = new Diskusi();
                        d.id = rs.getInt("id_forum");
                        d.judul = rs.getString("judul");
                        d.isi = rs.getString("isi");
                        d.idUser = rs.getInt("id_user"); // 0 jika NULL
                        d.penulis = rs.getString("penulis");
                        d.role = rs.getString("role");
                        d.jmlBalasan = rs.getInt("jml");
                        Timestamp tp = rs.getTimestamp("tanggal_post");
                        d.tanggal = tp == null ? null : tp.toLocalDateTime();
                        Timestamp ta = rs.getTimestamp("aktivitas");
                        d.aktivitas = ta == null ? d.tanggal : ta.toLocalDateTime();
                        hasil.add(d);
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }

        daftar.clear();
        sedangMuat = true;
        model.setRowCount(0);
        int barisPilih = -1;
        for (Diskusi d : hasil) {
            daftar.put(d.id, d);
            if (d.id == pilihId) barisPilih = model.getRowCount();
            model.addRow(new Object[]{d.id, d.judul, d.penulis, d.jmlBalasan,
                d.aktivitas == null ? "-" : FMT_SINGKAT.format(d.aktivitas)});
        }
        sedangMuat = false;

        lblRingkasan.setText(hasil.size() + " diskusi");

        if (barisPilih == -1 && pilihId == 0 && model.getRowCount() > 0) barisPilih = 0;
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
        idDetail = 0;
        avatar.setText("?");
        lblPenulis.setText("");
        lblMeta.setText("");
        lblJudul.setText("<html><body style='width:420px'><i>"
            + (daftar.isEmpty() ? "Belum ada diskusi di kelas ini. Mulai dengan tombol + DISKUSI BARU."
                                : "Pilih diskusi di daftar untuk membacanya.") + "</i></body></html>");
        txtIsi.setText("");
        lblBalasan.setText("");
        panelBalasan.setText("");
        txtBalas.setText("");
        btnKirim.setEnabled(false);
        btnHapus.setEnabled(false);
        btnHapus.setVisible(false);
    }

    private void tampilDetail() {
        int id = UI.idTerpilih(tabel, model);
        Diskusi d = daftar.get(id);
        if (d == null) { kosongkanDetail(); return; }

        if (id != idDetail) txtBalas.setText(""); // pindah diskusi -> buang draf balasan
        idDetail = id;

        avatar.setText(inisial(d.penulis));
        lblPenulis.setText(d.penulis + (d.idUser == Session.idUser ? "  (Kamu)" : ""));
        lblMeta.setText(labelRole(d.role) + "  \u2022  " + (d.tanggal == null ? "-" : FMT_LENGKAP.format(d.tanggal)));
        lblJudul.setText("<html><body style='width:420px'>" + esc(d.judul) + "</body></html>");
        txtIsi.setText(d.isi);
        txtIsi.setCaretPosition(0);

        boolean milikSendiri = d.idUser == Session.idUser;
        btnHapus.setVisible(milikSendiri);
        btnHapus.setEnabled(milikSendiri);
        btnKirim.setEnabled(true);

        muatBalasan(id);
        SwingUtilities.invokeLater(() -> panelBalasan.setCaretPosition(0));
    }

    private void muatBalasan(int idForum) {
        StringBuilder html = new StringBuilder("<html><body>");
        int n = 0;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT b.isi, b.tanggal, b.id_user, COALESCE(u.nama_lengkap,'(akun dihapus)') AS nama, "
                  + "COALESCE(u.role,'') AS role FROM tb_forum_balasan b "
                  + "LEFT JOIN tb_user u ON b.id_user = u.id_user "
                  + "WHERE b.id_forum = ? ORDER BY b.tanggal, b.id_balasan")) {
                ps.setInt(1, idForum);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        n++;
                        Timestamp tg = rs.getTimestamp("tanggal");
                        String role = rs.getString("role");
                        String warna = "guru".equals(role) ? "#27ae60" : "admin".equals(role) ? "#e94560" : "#0f3460";
                        html.append("<b>").append(esc(rs.getString("nama"))).append("</b>")
                            .append(rs.getInt("id_user") == Session.idUser ? " (Kamu)" : "")
                            .append(" <font color='").append(warna).append("'>[").append(labelRole(role)).append("]</font> ")
                            .append("<font color='gray' size='2'>")
                            .append(tg == null ? "" : FMT_LENGKAP.format(tg.toLocalDateTime())).append("</font><br>")
                            .append(esc(rs.getString("isi")).replace("\n", "<br>")).append("<hr>");
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        if (n == 0) html.append("<font color='gray'><i>Belum ada balasan. Jadilah yang pertama membalas!</i></font>");
        html.append("</body></html>");
        panelBalasan.setText(html.toString());
        lblBalasan.setText("Balasan (" + n + ")");
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void aksiBalas() {
        Diskusi d = daftar.get(idDetail);
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Pilih dulu diskusi yang ingin dibalas.");
            return;
        }
        String isi = txtBalas.getText().trim();
        if (isi.isEmpty()) {
            UI.peringatan(this, "Tulis balasan terlebih dahulu.");
            return;
        }
        if (isi.length() > MAKS_BALASAN) {
            UI.peringatan(this, "Balasan maksimal " + MAKS_BALASAN + " karakter (sekarang " + isi.length() + ").");
            return;
        }
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO tb_forum_balasan (id_forum, id_user, isi) "
                  + "SELECT f.id_forum, ?, ? FROM tb_forum f WHERE f.id_forum = ? AND f.id_kelas = ?")) {
                ps.setInt(1, Session.idUser);
                ps.setString(2, isi);
                ps.setInt(3, d.id);
                ps.setInt(4, Session.idKelas);
                if (ps.executeUpdate() == 0) { // diskusi sudah dihapus penulisnya
                    UI.peringatan(this, "Diskusi ini sudah tidak tersedia. Daftar akan dimuat ulang.");
                    muatTabel(0);
                    return;
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        txtBalas.setText("");
        muatTabel(d.id); // urutan daftar berubah (aktivitas terbaru naik ke atas)
        SwingUtilities.invokeLater(() ->
            panelBalasan.setCaretPosition(panelBalasan.getDocument().getLength())); // gulir ke balasan terbaru
    }

    private void aksiBuat() {
        DialogDiskusi dlg = new DialogDiskusi();
        dlg.setVisible(true);
        if (dlg.idBaru != 0) muatTabel(dlg.idBaru);
    }

    private void aksiHapus() {
        Diskusi d = daftar.get(idDetail);
        if (d == null) return;
        if (d.idUser != Session.idUser) {
            UI.peringatan(this, "Kamu hanya bisa menghapus diskusi milikmu sendiri.");
            return;
        }
        if (!UI.konfirmasiHapus(this,
                "Hapus diskusi \"" + d.judul + "\"?\n\n"
              + d.jmlBalasan + " balasan di dalamnya juga akan dihapus.\n"
              + "Tindakan ini tidak bisa dibatalkan.")) return;

        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM tb_forum WHERE id_forum = ? AND id_user = ?")) {
                ps.setInt(1, d.id);
                ps.setInt(2, Session.idUser);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return;
        }
        idDetail = 0;
        muatTabel(0);
        JOptionPane.showMessageDialog(this, "Diskusi berhasil dihapus.");
    }

    // ==================================================================
    //  DIALOG DISKUSI BARU
    // ==================================================================
    private class DialogDiskusi extends JDialog {

        private int idBaru = 0;
        private final JTextField txtJudul;
        private final JTextArea txtIsiBaru;

        DialogDiskusi() {
            super(FrmForumSiswa.this, "Buat Diskusi Baru", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            JPanel p = new JPanel(null);
            p.setBackground(Color.WHITE);
            p.setPreferredSize(new Dimension(350, 335));
            setContentPane(p);

            txtJudul = UI.field(p, "Judul Diskusi * (maks. " + MAKS_JUDUL + " karakter)", 12);
            txtIsiBaru = UI.area(p, "Isi / Pertanyaan *", 68, 160);

            UI.tombol(p, "KIRIM", UI.HIJAU, UI.HIJAU_H, 25, 268, 145, 42, this::kirim);
            UI.tombol(p, "BATAL", UI.ABU, UI.ABU_H, 180, 268, 145, 42, this::dispose);

            pack();
            setLocationRelativeTo(FrmForumSiswa.this);
        }

        private void kirim() {
            String judul = txtJudul.getText().trim();
            String isi = txtIsiBaru.getText().trim();
            if (judul.isEmpty()) { UI.peringatan(this, "Judul diskusi wajib diisi."); return; }
            if (judul.length() > MAKS_JUDUL) { UI.peringatan(this, "Judul maksimal " + MAKS_JUDUL + " karakter."); return; }
            if (isi.isEmpty()) { UI.peringatan(this, "Isi diskusi wajib diisi."); return; }
            if (isi.length() > MAKS_ISI) {
                UI.peringatan(this, "Isi maksimal " + MAKS_ISI + " karakter (sekarang " + isi.length() + ").");
                return;
            }
            try (Connection con = Koneksi.getKoneksi()) {
                if (con == null) { UI.koneksiGagal(this); return; }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_forum (judul, isi, id_user, id_kelas) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, judul);
                    ps.setString(2, isi);
                    ps.setInt(3, Session.idUser);
                    ps.setInt(4, Session.idKelas);
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) {
                        if (k.next()) idBaru = k.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                UI.errorDb(this, ex);
                return;
            }
            JOptionPane.showMessageDialog(this, "Diskusi berhasil dibuat!");
            dispose();
        }
    }

    // ------------------------------------------------------------------
    //  HELPER
    // ------------------------------------------------------------------
    private static String labelRole(String role) {
        if ("guru".equals(role)) return "Guru";
        if ("admin".equals(role)) return "Admin";
        if ("siswa".equals(role)) return "Siswa";
        return "-";
    }

    /** "Andi Pratama" -> "AP". */
    private static String inisial(String nama) {
        if (nama == null || nama.trim().isEmpty()) return "?";
        String[] kata = nama.trim().split("\\s+");
        String h = kata[0].substring(0, 1);
        if (kata.length > 1) h += kata[1].substring(0, 1);
        return h.toUpperCase();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmForumSiswa().setVisible(true));
    }
}