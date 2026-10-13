package lms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Kumpulan helper tampilan agar form-form tidak mengulang kode yang sama.
 */
public class UI {

    /** Item untuk combo box: menyimpan id + teks yang tampil. */
    public static class Item {
        public final int id;
        public final String label;
        public Item(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    public static final Color NAVY = new Color(15, 52, 96);
    public static final Color DARK = new Color(26, 26, 46);

    public static final Color HIJAU = new Color(46, 204, 113);
    public static final Color HIJAU_H = new Color(39, 174, 96);
    public static final Color ABU = new Color(149, 165, 166);
    public static final Color ABU_H = new Color(127, 140, 141);
    public static final Color KUNING = new Color(241, 196, 15);
    public static final Color KUNING_H = new Color(230, 126, 34);
    public static final Color MERAH = new Color(231, 76, 60);
    public static final Color MERAH_H = new Color(192, 41, 43);

    private static final Font BOLD12 = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font PLAIN13 = new Font("Segoe UI", Font.PLAIN, 13);

    // ------------------------------------------------------------------
    //  KERANGKA HALAMAN
    // ------------------------------------------------------------------

    /** Menyiapkan JFrame 1000x650 + background gradasi + judul + tombol KEMBALI (ke menu admin). */
    public static JPanel siapkanFrame(JFrame f, String judulJendela, String judulHeader) {
        f.setTitle("LMS UPTD SMPN 33 Depok - " + judulJendela);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setResizable(false);
        f.getContentPane().setLayout(null);
        f.getContentPane().setPreferredSize(new Dimension(1000, 650));
        f.pack();
        f.setLocationRelativeTo(null);

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
        f.getContentPane().add(bg);

        JLabel h = new JLabel(judulHeader);
        h.setFont(new Font("Segoe UI", Font.BOLD, 22));
        h.setForeground(Color.WHITE);
        h.setBounds(40, 25, 600, 30);
        bg.add(h);

        tombol(bg, "KEMBALI", new Color(108, 117, 125), new Color(198, 40, 40), 840, 25, 110, 35, () -> {
            new FrmMenuAdmin().setVisible(true);
            f.dispose();
        });
        return bg;
    }

    /** Panel putih sisi kiri untuk form input. */
    public static RoundedPanel panelForm(JPanel bg) {
        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(40, 80, 350, 520);
        p.setLayout(null);
        bg.add(p);
        return p;
    }

    /** Panel putih sisi kanan untuk tabel, lengkap dengan judulnya. */
    public static RoundedPanel panelTabel(JPanel bg, String judul) {
        RoundedPanel p = new RoundedPanel(20, Color.WHITE, true);
        p.setBounds(410, 80, 540, 520);
        p.setLayout(null);
        bg.add(p);

        JLabel l = new JLabel(judul);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(DARK);
        l.setBounds(25, 18, 260, 25);
        p.add(l);
        return p;
    }

    public static JLabel judulForm(JPanel p, String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(DARK);
        l.setBounds(25, 15, 300, 25);
        p.add(l);
        return l;
    }

    // ------------------------------------------------------------------
    //  KOMPONEN FORM
    // ------------------------------------------------------------------

    public static JLabel label(JPanel p, String teks, int y) {
        JLabel l = new JLabel(teks);
        l.setFont(BOLD12);
        l.setForeground(NAVY);
        l.setBounds(25, y, 300, 16);
        p.add(l);
        return l;
    }

    /** Label + text field. Jarak antar field = 56 px. */
    public static JTextField field(JPanel p, String teksLabel, int y) {
        label(p, teksLabel, y);
        JTextField t = new JTextField();
        t.setFont(PLAIN13);
        t.setBounds(25, y + 17, 300, 28);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        p.add(t);
        return t;
    }

    /** Label + combo box yang sudah dibuat pemanggil. */
    public static void combo(JPanel p, String teksLabel, int y, JComboBox<?> cb) {
        label(p, teksLabel, y);
        cb.setFont(PLAIN13);
        cb.setBackground(Color.WHITE);
        cb.setBounds(25, y + 17, 300, 28);
        p.add(cb);
    }

    public static RoundedButton tombol(JPanel p, String teks, Color normal, Color hover,
                                       int x, int y, int w, int h, Runnable aksi) {
        RoundedButton b = new RoundedButton(teks, 10, normal, hover);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBounds(x, y, w, h);
        b.addActionListener(e -> aksi.run());
        p.add(b);
        return b;
    }

    public static void pilihItem(JComboBox<Item> cb, int id) {
        cb.setSelectedIndex(0);
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).id == id) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    // ------------------------------------------------------------------
    //  TABEL
    // ------------------------------------------------------------------

    /** Model tabel yang tidak bisa diedit langsung. Kolom pertama harus "ID" (akan disembunyikan). */
    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    public static JTable tabel(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.removeColumn(t.getColumnModel().getColumn(0)); // sembunyikan kolom ID
        return t;
    }

    public static void tambahTabel(JPanel p, JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBounds(25, 60, 490, 380);
        p.add(sp);
    }

    /** id (kolom model 0) dari baris terpilih, atau 0 jika tidak ada. */
    public static int idTerpilih(JTable t, DefaultTableModel m) {
        int r = t.getSelectedRow();
        if (r == -1) return 0;
        return (Integer) m.getValueAt(t.convertRowIndexToModel(r), 0);
    }

    /** Teks sel dari baris terpilih berdasarkan indeks kolom MODEL. */
    public static String teksTerpilih(JTable t, DefaultTableModel m, int kolomModel) {
        int r = t.getSelectedRow();
        if (r == -1) return "";
        return m.getValueAt(t.convertRowIndexToModel(r), kolomModel).toString();
    }

    public static JTextField kolomCari(JPanel p, Runnable reload) {
        JLabel l = new JLabel("Cari:");
        l.setFont(BOLD12);
        l.setForeground(NAVY);
        l.setBounds(295, 20, 40, 22);
        p.add(l);

        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setBounds(335, 17, 180, 28);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        t.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { reload.run(); }
            public void removeUpdate(DocumentEvent e) { reload.run(); }
            public void changedUpdate(DocumentEvent e) { reload.run(); }
        });
        p.add(t);
        return t;
    }

    // ------------------------------------------------------------------
    //  PESAN
    // ------------------------------------------------------------------

    public static void koneksiGagal(Component parent) {
        JOptionPane.showMessageDialog(parent,
            "Tidak bisa konek ke database. Pastikan MySQL di XAMPP sudah running!",
            "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
    }

    public static void errorDb(Component parent, SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(parent, "Terjadi kesalahan database:\n" + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void peringatan(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean konfirmasiHapus(Component parent, String pesan) {
        return JOptionPane.showConfirmDialog(parent, pesan, "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}