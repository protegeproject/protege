package org.protege.editor.core.ui.list;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class MList extends JList {

    private MListCellRenderer ren;

    private MListDeleteButton deleteButton;

    private MListEditButton editButton;

    private MListAddButton addButton;

    private static final Stroke BUTTON_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private boolean mouseDown;

    private static final int BUTTON_DIMENSION = 16;

    private static final int BUTTON_MARGIN = 2;

    private static Font SECTION_HEADER_FONT = new Font("Lucida Grande", Font.PLAIN, 10);

    private static final Color itemBackgroundColor = new Color(240, 245, 240);


    private List<MListButton> editAndDeleteButtons;

    private List<MListButton> deleteButtonOnly;


    public MList() {
        ListCellRenderer renderer = getCellRenderer();
        ren = new MListCellRenderer();
        ren.setContentRenderer(renderer);
        super.setCellRenderer(ren);
        deleteButton = new MListDeleteButton(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
        addButton = new MListAddButton(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAdd();
            }
        });
        editButton = new MListEditButton(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public int lastIndex = 0;

            public void mouseMoved(MouseEvent e) {
                // more efficient than repainting the whole component every time the mouse moves
                Point pt = getMousePosition();
                int index = locationToIndex(pt);
                // only repaint all the cells the mouse has moved over
                repaint(getCellBounds(Math.min(index, lastIndex), Math.max(index, lastIndex)));
                lastIndex = index;
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
            }

            public void mouseReleased(MouseEvent e) {
                handleMouseClick(e);
                mouseDown = false;
            }

            public void mouseExited(MouseEvent event) {
                // leave the component cleanly
                repaint();
            }
        });
        setFixedCellHeight(-1);
        deleteButtonOnly = new ArrayList<MListButton>();
        deleteButtonOnly.add(deleteButton);
        editAndDeleteButtons = new ArrayList<MListButton>();
        editAndDeleteButtons.add(editButton);
        editAndDeleteButtons.add(deleteButton);
    }


    public void setCellRenderer(ListCellRenderer cellRenderer) {
        if (ren == null) {
            super.setCellRenderer(cellRenderer);
        }
        else {
            ren.setContentRenderer(cellRenderer);
        }
    }


    protected void handleAdd() {
        if (getSelectedValue() instanceof MListItem) {
            MListItem item = (MListItem) getSelectedValue();
            item.handleDelete();
        }
    }


    protected void handleDelete() {
        if (getSelectedValue() instanceof MListItem) {
            MListItem item = (MListItem) getSelectedValue();
            item.handleDelete();
        }
    }


    protected void handleEdit() {
        if (getSelectedValue() instanceof MListItem) {
            MListItem item = (MListItem) getSelectedValue();
            item.handleDelete();
        }
    }


    private void handleMouseClick(MouseEvent e) {
        for (MListButton button : getButtons(locationToIndex(e.getPoint()))) {
            if (button.getBounds().contains(e.getPoint())) {
                button.getActionListener().actionPerformed(new ActionEvent(button,
                                                                           ActionEvent.ACTION_PERFORMED,
                                                                           button.getName()));
                return;
            }
        }
    }


    public boolean getScrollableTracksViewportWidth() {
        return true;
    }


    protected List<MListButton> getButtons(Object value) {
        if (value instanceof MListSectionHeader) {
            return getSectionButtons((MListSectionHeader) value);
        }
        else if (value instanceof MListItem) {
            return getListItemButtons((MListItem) value);
        }
        else {
            return Collections.emptyList();
        }
    }


    protected List<MListButton> getSectionButtons(MListSectionHeader header) {
        List<MListButton> buttons = new ArrayList<MListButton>();
        if (header.canAdd()) {
            buttons.add(addButton);
        }
        return buttons;
    }


    protected List<MListButton> getListItemButtons(MListItem item) {
        if (item.isDeleteable()) {
            if (item.isEditable()) {
                return editAndDeleteButtons;
            }
            else {
                return deleteButtonOnly;
            }
        }
        return Collections.emptyList();
    }


    protected Color getItemBackgroundColor(MListItem item) {
        return itemBackgroundColor;
    }


    private class MListCellRenderer implements ListCellRenderer {


        private ListCellRenderer contentRenderer;

        private DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {

            // We now modify the component so that it has a nice border and background
            if (value instanceof MListSectionHeader) {
                JLabel label = (JLabel) defaultListCellRenderer.getListCellRendererComponent(list,
                                                                                             " ",
                                                                                             index,
                                                                                             isSelected,
                                                                                             cellHasFocus);
                label.setBorder(BorderFactory.createCompoundBorder(createPaddingBorder(list,
                                                                                       " ",
                                                                                       index,
                                                                                       isSelected,
                                                                                       cellHasFocus),
                                                                   BorderFactory.createEmptyBorder(2, 2, 2, 2)));
                return label;
            }

            JComponent component = (JComponent) contentRenderer.getListCellRendererComponent(list,
                                                                                             value,
                                                                                             index,
                                                                                             isSelected,
                                                                                             cellHasFocus);
            component.setOpaque(true);
            if (value instanceof MListItem) {
                Border border = BorderFactory.createCompoundBorder(createPaddingBorder(list,
                                                                                       value,
                                                                                       index,
                                                                                       isSelected,
                                                                                       cellHasFocus),
                                                                   createListItemBorder(list,
                                                                                        value,
                                                                                        index,
                                                                                        isSelected,
                                                                                        cellHasFocus));
                int buttonSpan = getButtons(value).size() * (BUTTON_DIMENSION + 2) + BUTTON_MARGIN * 2;
                border = BorderFactory.createCompoundBorder(border,
                                                            BorderFactory.createEmptyBorder(1, 1, 1, buttonSpan));
                component.setBorder(border);
                if (!isSelected) {
                    component.setBackground(getItemBackgroundColor((MListItem) value));
                }
            }
            if (isSelected) {
                component.setBackground(list.getSelectionBackground());
            }
            return component;
        }


        public void setContentRenderer(ListCellRenderer renderer) {
            this.contentRenderer = renderer;
        }
    }


    protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected,
                                         boolean cellHasFocus) {
        int bottomMargin = 1;
        if (list.getFixedCellHeight() == -1) {
            if (getModel().getSize() > index + 1) {
                if (getModel().getElementAt(index + 1) instanceof MListSectionHeader) {
                    bottomMargin = 20;
                }
            }
        }
        return BorderFactory.createMatteBorder(1, 1, bottomMargin, 1, Color.WHITE);
    }


    protected Border createListItemBorder(JList list, Object value, int index, boolean isSelected,
                                          boolean cellHasFocus) {
        return BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY);
    }


    private List<MListButton> getButtons(int index) {
        if(index < 0) {
            return Collections.emptyList();
        }
        Object obj = getModel().getElementAt(index);
        List<MListButton> buttons = getButtons(obj);
        Rectangle rowBounds = getCellBounds(index, index);
        
        if (obj instanceof MListSectionHeader) {
            MListSectionHeader section = (MListSectionHeader) obj;
            Rectangle nameBounds = getGraphics().getFontMetrics(SECTION_HEADER_FONT).getStringBounds(section.getName(),
                                                                                                     getGraphics()).getBounds();
            int x = 7 + nameBounds.width + 2;
            for (MListButton button : buttons) {
                button.setBounds(new Rectangle(x, rowBounds.y + 2, BUTTON_DIMENSION, BUTTON_DIMENSION));
                x += BUTTON_DIMENSION;
                x += 2;
                button.setRowObject(obj);
            }
        }
        else if (obj instanceof MListItem) {
            int x = rowBounds.width - 2;
            for (MListButton button : buttons) {
                x -= BUTTON_DIMENSION;
                x -= 2;
                button.setBounds(new Rectangle(x, rowBounds.y + 2, BUTTON_DIMENSION, BUTTON_DIMENSION));
                button.setRowObject(obj);
            }
        }
        return buttons;
    }


    public String getToolTipText(MouseEvent event) {
        Point mousePos = getMousePosition();
        if (mousePos == null) {
            return null;
        }
        for (MListButton button : getButtons(locationToIndex(mousePos))) {
            if (button.getBounds().contains(mousePos)) {
                return button.getName();
            }
        }
        int index = locationToIndex(event.getPoint());
        if (index == -1) {
            return null;
        }
        Object val = getModel().getElementAt(index);
        if (val instanceof MListItem) {
            return ((MListItem) val).getTooltip();
        }
        return null;
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Color oldColor = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Paint buttons
        Stroke oldStroke = g2.getStroke();
        Rectangle clipBound = g.getClipBounds();
        boolean paintedSomeRows = false;
        boolean useQuartz = Boolean.getBoolean(System.getProperty("-Dapple.awt.graphics.UseQuartz"));
        
        for (int index = 0; index < getModel().getSize(); index++) {
            Rectangle rowBounds = getCellBounds(index, index);
            if (!rowBounds.intersects(clipBound)) {
                if (paintedSomeRows) {
                    break;
                }
                continue;
            }
            paintedSomeRows = true;
            List<MListButton> buttons = getButtons(index);
            int endOfButtonRun = -1;
            for (MListButton button : buttons) {
                Rectangle buttonBounds = button.getBounds();
                if (buttonBounds.intersects(clipBound)) {
                    g2.setColor(getButtonColor(button));
                    if (!useQuartz) {
                        g2.fillOval(buttonBounds.x, buttonBounds.y, buttonBounds.width + 1, buttonBounds.height + 1);
                    }
                    else {
                        g2.fillOval(buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
                    }
                    g2.setColor(Color.WHITE);
                    Stroke curStroke = g2.getStroke();
                    g2.setStroke(BUTTON_STROKE);
                    button.paintButtonContent(g2);
                    g2.setStroke(curStroke);
//                    g2.translate(-buttonBounds.x, -buttonBounds.y);
                }
                endOfButtonRun = buttonBounds.x + buttonBounds.width + BUTTON_MARGIN;
            }
            if (getModel().getElementAt(index) instanceof MListSectionHeader) {
                MListSectionHeader header = (MListSectionHeader) getModel().getElementAt(index);
                if (isSelectedIndex(index)) {
                    g2.setColor(getSelectionForeground());
                }
                else {
                    g2.setColor(Color.GRAY);
                }
                int indent = 4;
                int baseLine = rowBounds.y + (BUTTON_DIMENSION + BUTTON_MARGIN - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                Font oldFont = g2.getFont();
                g2.setFont(SECTION_HEADER_FONT);
                g2.drawString(header.getName(), 1 + indent, baseLine);
                g2.setFont(oldFont);
                if (endOfButtonRun == -1) {
                    endOfButtonRun = g2.getFontMetrics(SECTION_HEADER_FONT).getStringBounds(header.getName(),
                                                                                            g2).getBounds().width + BUTTON_MARGIN * 2;
                }
                int midLine = rowBounds.y + (BUTTON_DIMENSION + BUTTON_MARGIN) / 2;
//                g2.drawLine(endOfButtonRun, midLine, getWidth() - 2, midLine);
            }
        }
        g.setColor(oldColor);
        g2.setStroke(oldStroke);
    }


    private Color getButtonColor(MListButton button) {
        Point pt = getMousePosition();
        if (pt == null) {
            return button.getBackground();
        }
        if (button.getBounds().contains(pt)) {
            if (mouseDown) {
                return Color.DARK_GRAY;
            }
            else {
                return button.getRollOverColor();
            }
        }
        return button.getBackground();
    }

    public static class TestItem implements MListItem {

        private String s;


        public TestItem(String s) {
            this.s = s;
        }


        public String toString() {
            return s;
        }


        public boolean isEditable() {
            return true;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
            return false;
        }


        public String getTooltip() {
            return "tooltip";
        }
    }


    public static class TestHeader implements MListSectionHeader {

        public String getName() {
            return "This is a test section";
        }


        public boolean canAdd() {
            return true;
        }


        public void handleAdd() {
        }
    }


    public static void main(String[] args) {
        MList list = new MList();
        JFrame frame = new JFrame();
        frame.setContentPane(list);
        frame.setVisible(true);

        list.setListData(new Object []{new TestHeader(), new TestItem("A"), new TestItem("B"), new TestHeader(), new TestItem(
                "C"), "NOT MLI"});
    }
}
