package lms;

import javax.swing.JButton;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
 
public class RoundedButton extends JButton {
    private int radius;
    private Color warnaNormal, warnaHover;
    private boolean sedangHover = false;
 
    public RoundedButton(String teks, int radius, Color warnaNormal, Color warnaHover) {
        super(teks);
        this.radius = radius;
        this.warnaNormal = warnaNormal;
        this.warnaHover = warnaHover;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { sedangHover = true; repaint(); }
            public void mouseExited(java.awt.event.MouseEvent e) { sedangHover = false; repaint(); }
        });
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(sedangHover ? warnaHover : warnaNormal);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
}
