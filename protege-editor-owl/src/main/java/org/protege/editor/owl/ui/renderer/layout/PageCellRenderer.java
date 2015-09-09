package org.protege.editor.owl.ui.renderer.layout;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.RendererWithInsets;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 14/11/2011
 * <p>
 *     A cell render for JLists and JTables that renders {@link Page} objects into the cells.
 * </p>
 */
public abstract class PageCellRenderer extends JPanel implements ListCellRenderer, TableCellRenderer, RendererWithInsets {

    private PageCache pageCache = new PageCache();

    private JComponent component;

    private LinkManager manager;

    private Page page;

    public PageCellRenderer() {
        this.manager = new LinkManager();
        setOpaque(false);
    }

    protected void invalidateCache() {
        pageCache.invalidate();
    }

    /**
     * Given the value being rendered, returns an object that represents a key for this value.  Subclasses may
     * override this method to provide an appropriate key.
     * @param value The value of the cell that is being rendered.  This is the object that is passed in the
     * {@link ListCellRenderer#getListCellRendererComponent(javax.swing.JList, Object, int, boolean, boolean)} method
     * for a JList, and {@link TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, Object, boolean, boolean, int, int)}
     * for a JTable.
     * @return An object which acts as a key for the value being rendered.  By default, this is the value itself.
     */
    protected Object getValueKey(Object value) {
        return value;
    }

    /**
     * Gets a {@link PageCacheKey} that can be used to cache a Page that is rendered for a particular value.
     * @param value The value being rendered.  This should be the value object passed to subclasses of this class in
     * the {@link #getListCellRendererComponent(javax.swing.JList, Object, int, boolean, boolean)} and the
     * {@link #getTableCellRendererComponent(javax.swing.JTable, Object, boolean, boolean, int, int)}.
     * @param isSelected A flag to specify whether or not the cell being rendered is selected.
     * @param isSelected A flag to specify whether or not the cell being rendered has the focus.
     * @return A {@link PageCacheKey} that captures a key for the value being rendered in combination with whether the
     * page that renderes the value is rendering the value for a selected and focused cell.
     */
    private PageCacheKey getPageCacheKey(Object value, boolean isSelected, boolean hasFocus) {
        return new PageCacheKey(getValueKey(value), isSelected, hasFocus);
    }


    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cells index.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see javax.swing.JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        manager.setComponent(list);
        component = list;
        PageCacheKey pageCacheKey = getPageCacheKey(value, isSelected, cellHasFocus);
        page = pageCache.getPage(pageCacheKey);
        Graphics2D graphics = (Graphics2D) list.getGraphics();
        if(page == null) {
            page = new Page();
            if (pageCacheKey != null) {
                pageCache.put(pageCacheKey, page);
            }
            fillPage(page, list, value, index, isSelected, cellHasFocus);
            int width = getMaxAvailablePageWidth(list, value, index, isSelected, cellHasFocus);
            page.setWidth(width);
            if (list.getGraphics() != null) {
                page.layout(graphics.getFontRenderContext());
            }
        }
        else {
            int width = getMaxAvailablePageWidth(list, value, index, isSelected, cellHasFocus);
            if(page.getWidth() != width) {
                page.setWidth(width);
                page.invalidateLayout();
                page.layout(graphics.getFontRenderContext());
            }
        }
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());


        int prefWidth = page.getWidth();
        int prefHeight = page.getHeight();
//        if(list instanceof MList) {
//            prefWidth += 4;
//            prefHeight += 4;
//            page.setLocation(2, 2);
//        }
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        return this;
    }



    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     * <p>
     * The <code>TableCellRenderer</code> is also responsible for rendering the
     * the cell representing the table's current DnD drop location if
     * it has one. If this renderer cares about rendering
     * the DnD drop location, it should query the table directly to
     * see if the given row and column represent the drop location:
     * <pre>
     *     JTable.DropLocation dropLocation = table.getDropLocation();
     *     if (dropLocation != null
     *             && !dropLocation.isInsertRow()
     *             && !dropLocation.isInsertColumn()
     *             && dropLocation.getRow() == row
     *             && dropLocation.getColumn() == column) {
     *         // this cell represents the current drop location
     *         // so render it specially, perhaps with a different color
     *     }
     * </pre>
     * <p>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link javax.swing.JComponent#isPaintingForPrint()}.
     * @param    table        the <code>JTable</code> that is asking the
     * renderer to draw; can be <code>null</code>
     * @param    value        the value of the cell to be rendered.  It is
     * up to the specific renderer to interpret
     * and draw the value.  For example, if
     * <code>value</code>
     * is the string "true", it could be rendered as a
     * string or it could be rendered as a check
     * box that is checked.  <code>null</code> is a
     * valid value
     * @param    isSelected    true if the cell is to be rendered with the
     * selection highlighted; otherwise false
     * @param    hasFocus    if true, render cell appropriately.  For
     * example, put a special border on the cell, if
     * the cell can be edited, render in the color used
     * to indicate editing
     * @param    row     the row index of the cell being drawn.  When
     * drawing the header, the value of
     * <code>row</code> is -1
     * @param    column     the column index of the cell being drawn
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        manager.setComponent(table);
        component = table;
        page = new Page();
        fillPage(page, table, value, isSelected, hasFocus, row, column);
        getMaxAvailablePageWidth(page, table, value, isSelected, hasFocus, row, column);
        Graphics2D graphics = (Graphics2D) table.getGraphics();
        if (table.getGraphics() != null) {
            page.layout(graphics.getFontRenderContext());
        }
        setPreferredSize(page.getSize());
        return this;
    }

    /**
     * Fills a page in order to render a value in a JList.
     * @param page The page to fill.
     * @param list The JList
     * @param value The value being rendered
     * @param index The index of the value being rendered in the list.
     * @param isSelected Whether or not the cell containing the value being rendered is selected.
     * @param cellHasFocus Whether or not the cell containing the value being rendered has the focus.
     */
    protected abstract void fillPage(Page page, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);

    /**
     * Given a list and a value being rendered get the maximum available width for the page that renders the cell.
     *
     * @param list The JList
     * @param value The value being rendered
     * @param index The index in the list of the value being rendered
     * @param isSelected Whether or not the cell containing the value is selected.
     * @param cellHasFocus Whether or not the cell containing the value has the focus.
     * @return The maximum available width of the page for the given list.
     */
    protected abstract int getMaxAvailablePageWidth(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);



    /**
     * Fills a page in order to render a value in a JTable.
     * @param page The page to fill.
     * @param table The JTable
     * @param value The value being rendered
     * @param row The row index of the value being rendered in the table.
     * @param column The column index of the value being rendered in the table.
     * @param isSelected Whether or not the cell containing the value being rendered is selected.
     * @param hasFocus Whether or not the cell containing the value being rendered has the focus.
     */
    protected abstract void fillPage(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

    protected abstract int getMaxAvailablePageWidth(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        page.setLocation(getInsets().left, getInsets().top);
        Point mousePos = component.getMousePosition();
        if(mousePos != null) {
            int pageRelativeX = mousePos.x - page.getX() - getX();
            int pageRelativeY = mousePos.y - page.getY() - getY();
//            page.handleMouseMoved(new MouseEvent(component, 0, System.currentTimeMillis(), 0, pageRelativeX, pageRelativeY, 0, false, 0));
        }
        page.draw(g2);
        Rectangle clip = g.getClipBounds();
        clip.translate(getX(), getY());
        manager.clear(clip);
        manager.setCurrentPage(page, getX(), getY());
        for(LinkBox pageLink : page.getLinks()) {
            manager.add(pageLink.translate(getX(), getY()));
        }

    }
}
