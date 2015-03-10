package org.protege.editor.core.ui.laf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/03/15
 */
public class ProtegeScrollBarUI extends ScrollBarUI {

    public static final int DIM = 12;
    private AdjustmentListener adjustmentListener = new AdjustmentListener() {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            scrollBar.repaint();
        }
    };;

    private Point mouseDown;

    private Point lastDrag;

    private MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            mouseOver = true;
            scrollBar.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseOver = false;
            scrollBar.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDown = e.getPoint();
            lastDrag = mouseDown;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            handleDragBy(e.getX() - lastDrag.x, e.getY() - lastDrag.y);
            lastDrag = e.getPoint();
        }
    };;


    public static ComponentUI createUI(JComponent x) {
        return new ProtegeScrollBarUI();
    }

    private JScrollBar scrollBar;

    private boolean mouseOver;

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);
        scrollBar = (JScrollBar) c;

        c.addMouseListener(mouseListener);

        ((JScrollBar) c).addAdjustmentListener(adjustmentListener);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        ((JScrollBar) c).removeAdjustmentListener(adjustmentListener);
    }

    private void handleDragBy(int dX, int dY) {
        if(scrollBar.getOrientation() == Adjustable.VERTICAL) {
            scrollBar.setValue(scrollBar.getValue() + dY);
        }
        else {
            scrollBar.setValue(scrollBar.getValue() + dX);
        }
    }

    /**
     * Returns the specified component's preferred size appropriate for
     * the look and feel.  If <code>null</code> is returned, the preferred
     * size will be calculated by the component's layout manager instead
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this
     * method returns <code>null</code>.
     *
     * @param c the component whose preferred size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     * @see javax.swing.JComponent#getPreferredSize
     * @see java.awt.LayoutManager#preferredLayoutSize
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        if(scrollBar.getOrientation() == Adjustable.VERTICAL) {
            return new Dimension(DIM, c.getHeight());
        }
        else {
            return new Dimension(c.getWidth(), DIM);
        }

    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        paintTrack(g, c, new Rectangle(0, 0, c.getWidth(), c.getHeight()));
        int visibleAmount = scrollBar.getVisibleAmount();
        BoundedRangeModel model = scrollBar.getModel();

        int max = model.getMaximum() - model.getExtent();
        int min = model.getMinimum();
        int pos = model.getValue();
        Rectangle thumbRect;
        if(scrollBar.getOrientation() == JScrollBar.VERTICAL) {
            int range = c.getHeight();
            int size = (int) ((1.0 * visibleAmount / model.getMaximum()) * range);
            double percentage = (pos * 1.0 / (max - min));
            int thumbY = (int) (percentage * (range - size));
            thumbRect = new Rectangle(2, thumbY, DIM - 4, size);
        }
        else {
            int range = c.getWidth();
            int size = (int) ((1.0 * visibleAmount / model.getMaximum()) * range);
            double offset = (pos * 1.0 / (max - min));
            int thumbX = (int) (offset * (range - size));
            thumbRect = new Rectangle(thumbX, 2, size, DIM - 4);
        }
        paintThumb(g, c, thumbRect);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(new Color(240, 240, 240));
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g.setColor(new Color(160, 160, 160));
        if (scrollBar.getOrientation() == Adjustable.VERTICAL) {
            g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y + trackBounds.height);
        }
        else {
            g.drawLine(trackBounds.x, trackBounds.y, trackBounds.x + trackBounds.width, trackBounds.y);
            g.drawLine(trackBounds.x + trackBounds.width, trackBounds.y, trackBounds.x + trackBounds.width, trackBounds.y + trackBounds.height);
        }
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (mouseOver) {
            g.setColor(Color.GRAY);
        }
        else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);

    }
}
