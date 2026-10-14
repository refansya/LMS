package lms;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * Menyimpan file upload (materi, tugas) ke folder "uploads" di folder kerja aplikasi.
 * Di database cukup disimpan path relatifnya, contoh: uploads/materi/1699999999999_bab1.pdf
 */
public class Storage {

    public static final String BASE = "uploads";
    public static final long MAKS_BYTE = 25L * 1024 * 1024; // 25 MB

    private static final Set<String> EKSTENSI = new HashSet<>(Arrays.asList(
        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt",
        "jpg", "jpeg", "png", "mp4", "zip"));

    public static String ekstensi(String nama) {
        int i = nama.lastIndexOf('.');
        return (i < 0) ? "" : nama.substring(i + 1).toLowerCase();
    }

    public static boolean ekstensiDiizinkan(String nama) {
        return EKSTENSI.contains(ekstensi(nama));
    }

    /** Menyalin file ke uploads/<subfolder>/ dengan nama unik. Mengembalikan path relatif. */
    public static String simpan(File sumber, String subfolder) throws IOException {
        Path dir = Paths.get(BASE, subfolder);
        Files.createDirectories(dir);
        String aman = sumber.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        String nama = System.currentTimeMillis() + "_" + aman;
        Files.copy(sumber.toPath(), dir.resolve(nama), StandardCopyOption.REPLACE_EXISTING);
        return BASE + "/" + subfolder + "/" + nama;
    }

    public static File file(String pathRelatif) {
        return (pathRelatif == null || pathRelatif.isEmpty()) ? null : new File(pathRelatif);
    }

    /** Menghapus file dari disk. Aman dipanggil dengan null. */
    public static boolean hapus(String pathRelatif) {
        File f = file(pathRelatif);
        return f != null && f.exists() && f.delete();
    }

    /** Nama file asli tanpa awalan angka waktu. */
    public static String namaAsli(String pathRelatif) {
        if (pathRelatif == null) return "";
        String nama = pathRelatif.substring(pathRelatif.lastIndexOf('/') + 1);
        int i = nama.indexOf('_');
        return (i > 0 && nama.substring(0, i).matches("\\d+")) ? nama.substring(i + 1) : nama;
    }

    /** Membuka file dengan aplikasi bawaan Windows (PDF reader, Word, dst). */
    public static void buka(Component parent, String pathRelatif) {
        File f = file(pathRelatif);
        if (f == null || !f.exists()) {
            JOptionPane.showMessageDialog(parent,
                "File tidak ditemukan di folder penyimpanan:\n" + pathRelatif,
                "File Hilang", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (!Desktop.isDesktopSupported()) throw new IOException("Desktop tidak didukung");
            Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                "Tidak bisa membuka file:\n" + f.getAbsolutePath(),
                "Gagal Membuka", JOptionPane.ERROR_MESSAGE);
        }
    }
}