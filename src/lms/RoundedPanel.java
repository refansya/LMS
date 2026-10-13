package lms;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
 
public class RoundedPanel extends JPanel {
    private int radius;
    private Color borderColor;
    private boolean pakaiShadow;
 
    public RoundedPanel(int radius) {
        this(radius, new Color(224, 224, 224), false);
    }
 
    public RoundedPanel(int radius, Color borderColor, boolean pakaiShadow) {
        this.radius = radius;
        this.borderColor = borderColor;
        this.pakaiShadow = pakaiShadow;
        setOpaque(false);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        int w = getWidth(), hgt = getHeight();
 
        if (pakaiShadow) {
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fill(new RoundRectangle2D.Float(3, 4, w - 6, hgt - 6, radius, radius));
        }
 
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, hgt - 1, radius, radius));
 
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, hgt - 1, radius, radius));
 
        g2.dispose();
        super.paintComponent(g);
    }
}
