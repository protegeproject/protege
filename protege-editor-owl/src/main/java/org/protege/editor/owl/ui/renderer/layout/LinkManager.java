package org.protege.editor.owl.ui.renderer.layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/11/2011
 * <p>
 *     Manages mouse interaction between a {@link Component} and a {@link Page} and in particular the links on that page.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class LinkManager {


    private JComponent component;

    private List<LinkBox> pageLinks = new ArrayList<>();

    private Page currentPage;

    private int currentPageXOffset = 0;

    private int currentPageYOffset = 0;

    private MouseMotionAdapter mouseMotionListener = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            handleMouseMoved(e);
            updateCursor();
        }
    };

    private MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            checkForLinkClick(e);
        }
    };

    public LinkManager() {
    }

    public void setComponent(JComponent component) {
        if(this.component != null && this.component != component) {
            this.component.removeMouseListener(mouseListener);
            this.component.removeMouseMotionListener(mouseMotionListener);
        }
        if (this.component != component) {
            this.component = component;
            this.component.addMouseListener(mouseListener);
            this.component.addMouseMotionListener(mouseMotionListener);
        }
    }

    public void clear(Rectangle rectangle) {
        for(Iterator<LinkBox> it = pageLinks.iterator(); it.hasNext(); ) {
            LinkBox link = it.next();
            if(link.getBounds().intersects(rectangle)) {
                it.remove();
            }
        }
    }

    public void add(LinkBox pageLink) {
        pageLinks.add(pageLink);
    }

    private void updateCursor() {
        if(component == null) {
            return;
        }
        Point pos = component.getMousePosition();
        if (pos == null) {
            return;
        }
        for (LinkBox pageLink : pageLinks) {
            if (pageLink.contains(pos.x, pos.y)) {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        component.setCursor(Cursor.getDefaultCursor());
    }

    private void checkForLinkClick(MouseEvent event) {
        if(component == null) {
            return;
        }
        Point pos = component.getMousePosition();
        if (pos == null) {
            return;
        }
        if (component instanceof JList) {
            JList list = (JList) component;
            int index = list.locationToIndex(pos);
            if(index != -1) {
                component.paintImmediately(list.getCellBounds(index, index));
            }
        }

        for(LinkBox link : pageLinks) {
            if(link.contains(pos.x, pos.y)) {
                link.getLink().activate(component, event);
            }
        }
    }

    public void clearCurrentPage() {
        currentPage = null;
    }

    public void setCurrentPage(Page page, int xOffset, int yOffset) {
        currentPage = page;
        currentPageXOffset = xOffset;
        currentPageYOffset = yOffset;
    }

    private void handleMouseMoved(MouseEvent e) {
        if(currentPage == null) {
            return;
        }
        e.translatePoint(-currentPageXOffset, -currentPageYOffset);
        currentPage.handleMouseMoved(e);
        e.translatePoint(currentPageXOffset, currentPageYOffset);
    }
}
