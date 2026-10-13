package lms;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.geom.Ellipse2D;
 
public class BadgeBulat extends JLabel {
    private Color warnaBadge;
 
    // Konstruktor titik notifikasi kecil (tanpa teks), contoh: notif lonceng
    public BadgeBulat(Color warnaBadge) {
        this.warnaBadge = warnaBadge;
        setOpaque(false);
        setPreferredSize(new Dimension(10, 10));
    }
 
    // Konstruktor badge dengan angka, contoh: jumlah pesan/tugas baru
    public BadgeBulat(String angka, Color warnaBadge) {
        super(angka, SwingConstants.CENTER);
        this.warnaBadge = warnaBadge;
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 10));
        setOpaque(false);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(warnaBadge);
        g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
        g2.dispose();
        super.paintComponent(g);
    }
}
