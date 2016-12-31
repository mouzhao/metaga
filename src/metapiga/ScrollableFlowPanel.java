// 
// Decompiled by Procyon v0.5.30
// 

package metapiga;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.Scrollable;
import javax.swing.JPanel;

public class ScrollableFlowPanel extends JPanel implements Scrollable
{
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, this.getParent().getWidth(), height);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getWidth(), this.getPreferredHeight());
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
        final int hundredth = ((orientation == 1) ? this.getParent().getHeight() : this.getParent().getWidth()) / 100;
        return (hundredth == 0) ? 1 : hundredth;
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
        return (orientation == 1) ? this.getParent().getHeight() : this.getParent().getWidth();
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    private int getPreferredHeight() {
        int rv = 0;
        for (int k = 0, count = this.getComponentCount(); k < count; ++k) {
            final Component comp = this.getComponent(k);
            final Rectangle r = comp.getBounds();
            final int height = r.y + r.height;
            if (height > rv) {
                rv = height;
            }
        }
        rv += ((FlowLayout)this.getLayout()).getVgap();
        return rv;
    }
}
