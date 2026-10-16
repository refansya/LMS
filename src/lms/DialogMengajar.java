package lms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Admin: mengatur mapel dan kelas yang diajar seorang guru.
 * Kiri = daftar mapel (dengan ringkasan kelas yang sudah diajar), kanan = centang kelas untuk mapel terpilih.
 */
public class DialogMengajar extends JDialog {

    private final int idGuru;
    private final List<UI.Item> kelasSemua = new ArrayList<>();
    private final List<JCheckBox> cek = new ArrayList<>();

    private DefaultTableModel model;
    private JTable tabel;
    private JLabel lblMapel;
    private RoundedButton btnSimpan, btnSemua, btnKosong;

    private int idMapelAktif = 0;
    private String namaMapelAktif = "";
    private boolean dirty = false;
    private boolean sedangMuat = false;

    public DialogMengajar(JFrame parent, int idGuru, String namaGuru) {
        super(parent, "Penugasan Mengajar", true);
        this.idGuru = idGuru;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(780, 520));
        setContentPane(p);

        JLabel judul = new JLabel("Penugasan Mengajar: " + namaGuru);
        judul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judul.setForeground(UI.DARK);
        judul.setBounds(20, 12, 740, 24);
        p.add(judul);

        JLabel sub = new JLabel("Satu guru dapat mengajar beberapa mata pelajaran dan beberapa kelas.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        sub.setBounds(20, 36, 740, 18);
        p.add(sub);

        // ---------- KIRI: daftar mapel ----------
        model = UI.modelTabel("ID", "Mata Pelajaran", "Kelas Diajar");
        tabel = UI.tabel(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(220);
        JScrollPane sp = new JScrollPane(tabel);
        sp.setBounds(20, 66, 360, 380);
        p.add(sp);

        // ---------- KANAN: centang kelas ----------
        lblMapel = new JLabel("Pilih mata pelajaran di sebelah kiri.");
        lblMapel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMapel.setForeground(UI.NAVY);
        lblMapel.setBounds(400, 66, 360, 20);
        p.add(lblMapel);

        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 6));
        grid.setBackground(Color.WHITE);
        muatKelas();
        for (UI.Item k : kelasSemua) {
            JCheckBox c = new JCheckBox(k.label);
            c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            c.setBackground(Color.WHITE);
            c.setFocusPainted(false);
            c.setEnabled(false);
            c.addItemListener(e -> { if (!sedangMuat) dirty = true; });
            cek.add(c);
            grid.add(c);
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(grid, BorderLayout.NORTH);
        JScrollPane spKelas = new JScrollPane(wrapper);
        spKelas.setBounds(400, 92, 360, 310);
        spKelas.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        p.add(spKelas);

        btnSemua = UI.tombol(p, "PILIH SEMUA", UI.NAVY, new Color(10, 36, 66), 400, 410, 175, 36, () -> aturSemua(true));
        btnKosong = UI.tombol(p, "KOSONGKAN", UI.ABU, UI.ABU_H, 585, 410, 175, 36, () -> aturSemua(false));

        btnSimpan = UI.tombol(p, "SIMPAN PERUBAHAN", UI.HIJAU, UI.HIJAU_H, 400, 462, 200, 42, this::aksiSimpan);
        UI.tombol(p, "TUTUP", UI.ABU, UI.ABU_H, 610, 462, 150, 42, this::tutup);

        JLabel hint = new JLabel("<html>Klik <b>SIMPAN PERUBAHAN</b> setelah mencentang kelas. "
            + "Data tugas, kuis, dan materi yang sudah ada tidak ikut terhapus.</html>");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBounds(20, 456, 360, 40);
        p.add(hint);

        aktifkanForm(false);
        muatMapel();

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || sedangMuat) return;
            int id = UI.idTerpilih(tabel, model);
            if (id == 0 || id == idMapelAktif) return;
            if (dirty && idMapelAktif != 0) {
                int r = JOptionPane.showConfirmDialog(this,
                    "Simpan perubahan kelas untuk \"" + namaMapelAktif + "\"?",
                    "Perubahan Belum Disimpan", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (r == JOptionPane.YES_OPTION) simpan();
            }
            tampilMapel(id);
        });

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { tutup(); }
        });

        pack();
        setLocationRelativeTo(parent);
        if (model.getRowCount() > 0) tabel.setRowSelectionInterval(0, 0);
    }

    // ------------------------------------------------------------------
    //  LOAD DATA
    // ------------------------------------------------------------------
    private void muatKelas() {
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_kelas, nama_kelas FROM tb_kelas ORDER BY tingkat, nama_kelas");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) kelasSemua.add(new UI.Item(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        }
    }

    private void muatMapel() {
        sedangMuat = true;
        model.setRowCount(0);
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT m.id_mapel, m.nama_mapel, COALESCE((SELECT GROUP_CONCAT(k.nama_kelas "
                  + "ORDER BY k.tingkat, k.nama_kelas SEPARATOR ', ') FROM tb_guru_mengajar gm "
                  + "JOIN tb_kelas k ON gm.id_kelas = k.id_kelas "
                  + "WHERE gm.id_guru = ? AND gm.id_mapel = m.id_mapel), '-') AS kelas "
                  + "FROM tb_mapel m ORDER BY m.nama_mapel")) {
                ps.setInt(1, idGuru);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        } finally {
            sedangMuat = false;
        }
    }

    private void aktifkanForm(boolean aktif) {
        for (JCheckBox c : cek) c.setEnabled(aktif);
        btnSimpan.setEnabled(aktif);
        btnSemua.setEnabled(aktif);
        btnKosong.setEnabled(aktif);
    }

    private void tampilMapel(int idMapel) {
        idMapelAktif = idMapel;
        namaMapelAktif = UI.teksTerpilih(tabel, model, 1);
        lblMapel.setText("Kelas yang diajar untuk: " + namaMapelAktif);

        sedangMuat = true;
        for (JCheckBox c : cek) c.setSelected(false);
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return; }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_kelas FROM tb_guru_mengajar WHERE id_guru = ? AND id_mapel = ?")) {
                ps.setInt(1, idGuru);
                ps.setInt(2, idMapel);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idKelas = rs.getInt(1);
                        for (int i = 0; i < kelasSemua.size(); i++) {
                            if (kelasSemua.get(i).id == idKelas) cek.get(i).setSelected(true);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
        } finally {
            sedangMuat = false;
        }
        dirty = false;
        aktifkanForm(true);
    }

    // ------------------------------------------------------------------
    //  AKSI
    // ------------------------------------------------------------------
    private void aturSemua(boolean nilai) {
        for (JCheckBox c : cek) c.setSelected(nilai);
    }

    /** Menyimpan centang kelas untuk mapel yang sedang aktif. Mengembalikan true jika berhasil. */
    private boolean simpan() {
        if (idMapelAktif == 0) return false;
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) { UI.koneksiGagal(this); return false; }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM tb_guru_mengajar WHERE id_guru = ? AND id_mapel = ?")) {
                    ps.setInt(1, idGuru);
                    ps.setInt(2, idMapelAktif);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tb_guru_mengajar (id_guru, id_mapel, id_kelas) VALUES (?,?,?)")) {
                    for (int i = 0; i < kelasSemua.size(); i++) {
                        if (!cek.get(i).isSelected()) continue;
                        ps.setInt(1, idGuru);
                        ps.setInt(2, idMapelAktif);
                        ps.setInt(3, kelasSemua.get(i).id);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            UI.errorDb(this, ex);
            return false;
        }

        // perbarui ringkasan di baris mapel yang sama (tanpa memuat ulang seluruh tabel)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kelasSemua.size(); i++) {
            if (cek.get(i).isSelected()) sb.append(sb.length() == 0 ? "" : ", ").append(kelasSemua.get(i).label);
        }
        for (int r = 0; r < model.getRowCount(); r++) {
            if ((Integer) model.getValueAt(r, 0) == idMapelAktif) {
                model.setValueAt(sb.length() == 0 ? "-" : sb.toString(), r, 2);
                break;
            }
        }
        dirty = false;
        return true;
    }

    private void aksiSimpan() {
        if (idMapelAktif == 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu mata pelajaran di sebelah kiri.");
            return;
        }
        if (simpan()) JOptionPane.showMessageDialog(this, "Penugasan \"" + namaMapelAktif + "\" berhasil disimpan.");
    }

    private void tutup() {
        if (dirty && idMapelAktif != 0) {
            int r = JOptionPane.showConfirmDialog(this,
                "Simpan perubahan kelas untuk \"" + namaMapelAktif + "\" sebelum menutup?",
                "Perubahan Belum Disimpan", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (r == JOptionPane.CANCEL_OPTION || r == JOptionPane.CLOSED_OPTION) return;
            if (r == JOptionPane.YES_OPTION && !simpan()) return;
        }
        dispose();
    }
}