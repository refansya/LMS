package lms;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.geom.Ellipse2D;
 
public class AvatarBulat extends JLabel {
    private Color warnaLatar;
 
    public AvatarBulat(String inisial, Color warnaLatar) {
        super(inisial, SwingConstants.CENTER);
        this.warnaLatar = warnaLatar;
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 13));
        setOpaque(false);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(warnaLatar);
        g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
        g2.dispose();
        super.paintComponent(g);
    }
}
