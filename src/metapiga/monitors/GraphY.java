// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.awt.Dimension;
import metapiga.utilities.Tools;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GraphY extends JPanel
{
    private final boolean negative;
    private final int yStep;
    private double minValue;
    private double scale;
    
    public GraphY(final boolean negative, final int yStep) {
        this.negative = negative;
        this.yStep = yStep;
        this.minValue = Double.MAX_VALUE;
        this.scale = 1.0;
    }
    
    public void setValues(final double minValue, final double scale) {
        this.minValue = minValue;
        this.scale = scale;
        this.repaint();
    }
    
    public void paintComponent(final Graphics g) {
        g.setColor(new Color(0, 0, 0));
        final Dimension dim = this.getSize();
        g.fillRect(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
        g.setColor(Color.cyan);
        final int H = this.getHeight();
        final int xAxis = this.getWidth() - 5;
        final int yAxis = H - 20;
        g.drawLine(xAxis, 0, xAxis, yAxis);
        for (int i = this.yStep; i < yAxis; i += this.yStep) {
            g.drawLine(xAxis - 1, i, xAxis + 1, i);
            final double lik = this.negative ? (this.scale * (i - H / 10) + this.minValue) : (this.scale * (H - H / 10 - i) + this.minValue);
            final String s = Tools.doubletoString(lik, 0);
            final int sl = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
            final int sh = (int)g.getFontMetrics().getStringBounds(s, g).getHeight() / 2;
            g.drawString(s, xAxis - 2 - sl, i + sh);
        }
    }
}
