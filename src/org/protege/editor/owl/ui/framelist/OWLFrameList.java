package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.frame.*;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLFrameList<R extends OWLObject> extends JList implements LinkedObjectComponent, DropTargetListener {

    private static final Logger logger = Logger.getLogger(OWLFrameList.class.getName());

    public static final String DELETE_ACTION_MAP_KEY = "DEL";


    public static final int BUTTON_DIMENSION = 14;

    public static final int BUTTON_MARGIN = 3;

    private OWLEditorKit editorKit;

    private OWLFrame<R> frame;

    private OWLFrameListener listener;

    private Cursor defaultCursor;


    // A flag to indicate whether or not sections
    // should be shown in the list
    private boolean showSections = true;

    private JPopupMenu popupMenu;

    private Point lastMouseDownPoint;

    private List<OWLFrameListPopupMenuAction<R>> actions;


    public OWLFrameList(OWLEditorKit editorKit, OWLFrame<R> frame) {
        this.editorKit = editorKit;
        this.frame = frame;
        defaultCursor = getCursor();
        setCellRenderer(new OWLFrameListRenderer(editorKit));
        listener = new OWLFrameListener() {
            /**
             * Gets called when the frame content has
             * been changed.  Usually because a row has
             * been added or removed.
             */
            public void frameContentChanged() throws Exception {
                refillRows();
            }
        };
        frame.addFrameListener(listener);

        setCellRenderer(new OWLFrameListRenderer(editorKit));

        LinkedObjectComponentMediator mediator = new LinkedObjectComponentMediator(editorKit, this);

        setFixedCellHeight(-1);

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                lastMouseDownPoint = e.getPoint();
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }


            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
                else if (e.getClickCount() == 1) {
                    checkForButtonPressed(e.getPoint());
                }
                else if (e.getClickCount() == 2) {
                    handleEdit();
                }
            }
        });

        setupDeleteHandler();
        setupKeyboardAddHandler();

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });

        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        createPopupMenu();
    }


    private void createPopupMenu() {
        actions = new ArrayList<OWLFrameListPopupMenuAction<R>>();
        popupMenu = new JPopupMenu();
        addToPopupMenu(new SwitchToDefiningOntologyAction());
        setupMenuItem(new PullIntoActiveOntologyAction());
        setupMenuItem(new MoveAxiomsToOntologyAction());
    }


    private void showPopupMenu(MouseEvent e) {
        for (OWLFrameListPopupMenuAction action : actions) {
            action.updateState();
        }
        popupMenu.show(this, e.getX(), e.getY());
    }


    public void addToPopupMenu(OWLFrameListPopupMenuAction<R> action) {
        setupMenuItem(action);
        popupMenu.add(action);
    }


    public void setupMenuItem(OWLFrameListPopupMenuAction<R> action) {
//        action.setup(editorKit, this);
        try {
            action.initialise();
            actions.add(action);
        }
        catch (Exception e) {
            logger.throwing(getClass().getName(), "setupMenuItem", e);
        }
    }


    private void setupKeyboardAddHandler() {
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ADD");
        getActionMap().put("ADD", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
    }


    public void setRootObject(R rootObject) {
        frame.setRootObject(rootObject);
    }


    public R getRootObject() {
        return frame.getRootObject();
    }


    public void dispose() {
        frame.removeFrameListener(listener);
        for (OWLFrameListPopupMenuAction<R> action : actions) {
            try {
                action.dispose();
            }
            catch (Exception e) {
                logger.throwing(getClass().getName(), "dispose", e);
            }
        }
    }


    private void refillRows() {
        List<OWLFrameObject> rows = new ArrayList<OWLFrameObject>();
        for (OWLFrameSection<R, ? extends Object, ? extends Object> section : frame.getFrameSections()) {
            if (showSections) {
                rows.add(section);
            }
            for (OWLFrameSectionRow row : section.getRows()) {
                rows.add(row);
            }
        }
        if (rows.size() > 100) {
            setFixedCellHeight(24);
        }
        else {
            setFixedCellHeight(-1);
        }
        setListData(rows.toArray());
    }


    private void setupDeleteHandler() {
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE_ACTION_MAP_KEY);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,
                                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                                                 "DEL");

        getActionMap().put(DELETE_ACTION_MAP_KEY, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
    }


    private void handleDelete() {
        int [] selIndices = getSelectedIndices();

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (int selIndex : selIndices) {
            Object val = getModel().getElementAt(selIndex);
            if (val instanceof OWLFrameSectionRow) {
                OWLFrameSectionRow row = (OWLFrameSectionRow) val;
                changes.addAll(row.getDeletionChanges());
            }
        }
        editorKit.getOWLModelManager().applyChanges(changes);
    }


    private void handleEdit() {
        final Object val = getSelectedValue();
        if (val instanceof OWLFrameObject) {
            if (val instanceof OWLFrameSection) {
                if (!((OWLFrameSection) val).canAddRows()) {
                    return;
                }
            }
            else {
                if (!((OWLFrameSectionRow) val).isEditable()) {
                    return;
                }
            }
            OWLFrameObject row = (OWLFrameObject) val;
            showEditorDialog(row, new EditHandler() {
                public void handleEditFinished(OWLFrameSectionRowObjectEditor editor) {
                    editor.getHandler().handleEditingFinished(editor.getEditedObjects());
                }
            });
        }
    }


    private void showEditorDialog(final OWLFrameObject frameObject, final EditHandler handler) {
        // If we don't have any editing component then just return
        final OWLFrameSectionRowObjectEditor editor = frameObject.getEditor();
        if (editor == null) {
            return;
        }

        if (editor instanceof JWindow) {
            ((JWindow) editor).setVisible(true);
            return;
        }
        if (editor instanceof Wizard) {
            ((Wizard) editor).showModalDialog();
            handler.handleEditFinished(editor);
            return;
        }

        // Create the editing component dialog - we use an option pane
        // so that the buttons and keyboard actions are what are expected
        // by the user.
        JComponent editorComponent = editor.getEditorComponent();
        final JOptionPane optionPane = new JOptionPane(editorComponent,
                                                       JOptionPane.PLAIN_MESSAGE,
                                                       JOptionPane.OK_CANCEL_OPTION) {
            public void selectInitialValue() {
                // This is overriden so that the option pane dialog default button
                // doesn't get the focus.
            }
        };
        final JDialog dlg = optionPane.createDialog(getParent(), null);
        // The editor shouldn't be modal (or should it?)
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handler.handleEditFinished(editor);
                }
                setSelectedValue(frameObject, true);
            }
        });

        dlg.setVisible(true);
    }


    private void checkForButtonPressed(Point pt) {
        int index = locationToIndex(pt);
        if (index == -1) {
            return;
        }
        if (getModel().getElementAt(index) instanceof OWLFrameSection) {
            Rectangle addRect = getAddButtonRect(index);
            if (addRect != null && addRect.contains(pt)) {
                handleEdit();
            }
        }
        else {
            Rectangle expRect = getExplainButtonRect(index);
            if (expRect != null && expRect.contains(pt)) {
                handleExplain();
                return;
            }

            Rectangle delRect = getDeleteButtonRect(index);
            if (delRect != null && delRect.contains(pt)) {
                handleDelete();
                return;
            }
            Rectangle editRect = getEditButtonRect(index);
            if (editRect != null && editRect.contains(pt)) {
                handleEdit();
            }
        }
    }


    private Rectangle getDeleteButtonRect(int row) {
        Object object = getModel().getElementAt(row);
        if (!(object instanceof OWLFrameSectionRow)) {
            return null;
        }
        OWLFrameSectionRow sectionRow = (OWLFrameSectionRow) object;
        if (!sectionRow.isDeletable()) {
            return null;
        }
        Rectangle r = getCellBounds(row, row);
        int x = r.x + getParent().getWidth() - 18 - BUTTON_MARGIN;
        int y = r.y + BUTTON_MARGIN;
        int w = BUTTON_DIMENSION;
        int h = BUTTON_DIMENSION;
        return new Rectangle(x, y, w, h);
    }


    private Rectangle getExplainButtonRect(int row) {
        Object object = getModel().getElementAt(row);
        if (!(object instanceof OWLFrameSectionRow)) {
            return null;
        }
        OWLFrameSectionRow sectionRow = (OWLFrameSectionRow) object;
        if (!sectionRow.isInferred()) {
            return null;
        }
        Rectangle r = getCellBounds(row, row);
        int x = r.x + getParent().getWidth() - 18 - BUTTON_MARGIN;
        int y = r.y + BUTTON_MARGIN;
        int w = BUTTON_DIMENSION;
        int h = BUTTON_DIMENSION;
        return new Rectangle(x, y, w, h);
    }


    private Rectangle getEditButtonRect(int row) {
        Object object = getModel().getElementAt(row);
        if (!(object instanceof OWLFrameSectionRow)) {
            return null;
        }
        OWLFrameSectionRow sectionRow = (OWLFrameSectionRow) object;
        if (!sectionRow.isEditable()) {
            return null;
        }
        Rectangle r = getCellBounds(row, row);
        int x = r.x + getParent().getWidth() - 36 - BUTTON_MARGIN;
        int y = r.y + BUTTON_MARGIN;
        int w = BUTTON_DIMENSION;
        int h = BUTTON_DIMENSION;
        return new Rectangle(x, y, w, h);
    }


    private Rectangle getAddButtonRect(int row) {
        if (frame.getRootObject() == null) {
            return null;
        }
        Object object = getModel().getElementAt(row);
        if (!(object instanceof OWLFrameSection)) {
            return null;
        }
        OWLFrameSection section = (OWLFrameSection) object;
        if (!section.canAddRows()) {
            return null;
        }

        Rectangle r = getCellBounds(row, row);
        int x = r.x;
        Graphics2D g2 = (Graphics2D) getGraphics();
        x += getSectionFont().getStringBounds(section.getLabel(), g2.getFontRenderContext()).getBounds().width;
        x += 5;
        int y = r.y + BUTTON_MARGIN;
        int w = BUTTON_DIMENSION;
        int h = BUTTON_DIMENSION;
        return new Rectangle(x, y, w, h);
    }


    private Font sectionFont;


    private Font getSectionFont() {
        if (sectionFont == null) {
            float fontSize = getGraphics().getFont().getSize() * 0.85f;
            sectionFont = getGraphics().getFont().deriveFont(Font.PLAIN, fontSize);
        }
        return sectionFont;
    }


    public String getToolTipText(MouseEvent event) {
        int index = locationToIndex(event.getPoint());
        if (index == -1) {
            return null;
        }
        Rectangle addButtonRect = getAddButtonRect(index);
        if (addButtonRect != null) {
            if (addButtonRect.contains(event.getPoint())) {
                return "Add...";
            }
        }

        Rectangle editButtonRect = getEditButtonRect(index);
        if (editButtonRect != null) {
            if (editButtonRect.contains(event.getPoint())) {
                return "Edit...";
            }
        }

        Rectangle explainButtonRect = getExplainButtonRect(index);
        if (explainButtonRect != null) {
            if (explainButtonRect.contains(event.getPoint())) {
                return "Explain inference";
            }
        }


        Rectangle deleteButtonRect = getDeleteButtonRect(index);
        if (deleteButtonRect != null) {
            if (deleteButtonRect.contains(event.getPoint())) {
                return "Delete";
            }
        }

        Object element = getModel().getElementAt(index);
        if (element instanceof OWLFrameSectionRow) {
            OWLFrameSectionRow sectionRow = ((OWLFrameSectionRow) element);
            if (sectionRow.getOntology() != null) {
                UIHelper helper = new UIHelper(editorKit);
                StringBuilder rendering = new StringBuilder();
                rendering.append("<html><body>");
                if (sectionRow.getRoot() != null && !sectionRow.getRoot().equals(getRootObject())) {
                    rendering.append("[Inherited from <b>");
                    rendering.append(editorKit.getOWLModelManager().getRendering(sectionRow.getRoot()));
                    rendering.append("</b>]<br>");
                }
                rendering.append("Asserted in ");
                String ontList = helper.getHTMLOntologyList(Collections.singleton(((OWLFrameSectionRow) element).getOntology()));
                rendering.append(ontList);
                rendering.append("</body></html>");
                return rendering.toString();
            }
            else {
                return "Inferred";
            }
        }
        return null;
    }


    private void handleExplain() {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Painting of the list.  This paints the add/remove buttons etc.


    private Stroke buttonStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);


    private static void setAntialised(Graphics g, boolean b) {
        if (b) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setAntialised(g, true);
        int index = 0;

        Rectangle clip;

        if (getParent() instanceof JViewport) {
            JViewport vp = (JViewport) getParent();
            clip = vp.getViewRect();
        }
        else {
            clip = new Rectangle(getSize());
        }

        for (int i = 0; i < getModel().getSize(); i++) {
            Rectangle cellRect = getCellBounds(i, i);
            if (cellRect.intersects(clip)) {
                Object row = getModel().getElementAt(i);
                Point mousePos = getMousePosition();
                if (row instanceof OWLFrameSection) {
                    Rectangle r = getCellBounds(index, index);
                    g.setColor(Color.GRAY);
                    String s = ((OWLFrameSection) row).getLabel();
                    g.setFont(getSectionFont());
                    int y = r.y + (BUTTON_DIMENSION - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent() + BUTTON_MARGIN + 1;
                    g.drawString(s, 2, y);
                    int x = g.getFontMetrics().getStringBounds(s, g).getBounds().width + 5;

                    g.setColor(Color.LIGHT_GRAY);
                    Stroke oldStroke = ((Graphics2D) g).getStroke();
                    ((Graphics2D) g).setStroke(buttonStroke);

                    Rectangle addButtonRect = getAddButtonRect(index);
                    if (addButtonRect != null) {
                        if (mousePos != null && addButtonRect != null) {
                            if (addButtonRect.contains(mousePos)) {
                                g.setColor(Color.GREEN.darker());
                                setToolTipText("Add...");
                            }
                        }
                        int xR = addButtonRect.x;
                        int yR = addButtonRect.y;
                        int wR = addButtonRect.width;
                        int hR = addButtonRect.height;
                        g.fillOval(xR, yR, wR, hR);
                        g.setColor(Color.WHITE);
                        g.drawLine(xR + 4, yR + hR / 2, xR + wR - 4, yR + hR / 2);
                        g.drawLine(xR + wR / 2, yR + 4, xR + wR / 2, yR + hR - 4);
                        x += BUTTON_DIMENSION;
                    }

                    ((Graphics2D) g).setStroke(oldStroke);
                    g.setColor(Color.GRAY);
                    x += BUTTON_MARGIN;
                    int lineY = r.y + BUTTON_DIMENSION / 2 + BUTTON_MARGIN;
                    g.drawLine(x, lineY, getWidth() - 2, lineY);
                }
                else {
                    Stroke oldStroke = ((Graphics2D) g).getStroke();
                    ((Graphics2D) g).setStroke(buttonStroke);
                    Point mousePosition = getMousePosition();
                    g.setColor(Color.LIGHT_GRAY);
                    Rectangle deleteButtonRect = getDeleteButtonRect(index);
                    int x;
                    int y;
                    int w;
                    int h;
                    if (deleteButtonRect != null) {
                        if (mousePosition != null) {
                            if (deleteButtonRect.contains(mousePosition)) {
                                g.setColor(Color.RED.darker());
                                setToolTipText("Delete row");
                            }
                        }
                        x = deleteButtonRect.x;
                        y = deleteButtonRect.y;
                        w = deleteButtonRect.width;
                        h = deleteButtonRect.height;
                        g.fillOval(x, y, w, h);
                        g.setColor(Color.WHITE);
                        g.drawLine(x + 4, y + 4, x + w - 4, y + h - 4);
                        g.drawLine(x + w - 4, y + 4, x + 4, y + h - 4);
                    }

                    if (row instanceof OWLFrameSectionRow) {
                        if (((OWLFrameSectionRow) row).isInferred()) {
                            Rectangle explainButtonRect = getExplainButtonRect(index);
                            if (explainButtonRect != null) {
                                if (mousePosition != null) {
                                    if (explainButtonRect.contains(mousePosition)) {
                                        g.setColor(Color.MAGENTA.darker());
                                        setToolTipText("Explain");
                                    }
                                }
                                x = explainButtonRect.x;
                                y = explainButtonRect.y;
                                w = explainButtonRect.width;
                                h = explainButtonRect.height;
                                g.fillOval(x, y, w, h);
                                g.setColor(Color.WHITE);
                                g.drawString("?", x + w / 2 - 1, y + h - 2);
                            }
                        }
                    }

                    g.setColor(Color.LIGHT_GRAY);
                    Rectangle editButtonRect = getEditButtonRect(index);
                    if (editButtonRect != null) {
                        if (mousePosition != null) {
                            if (editButtonRect.contains(mousePosition)) {
                                g.setColor(Color.BLUE.darker());
                                setToolTipText("Edit row...");
                            }
                        }
                        x = editButtonRect.x;
                        y = editButtonRect.y;
                        w = editButtonRect.width;
                        h = editButtonRect.height;
                        g.fillOval(x, y, w, h);
                        g.setColor(Color.WHITE);
                        g.drawLine(x + 4, y + h / 2, x + w - 5, y + 4);
                        g.drawLine(x + w - 5, y + 4, x + w - 5, y + h - 4);
                        g.drawLine(x + 4, y + h / 2, x + w - 5, y + h - 4);
                        ((Graphics2D) g).setStroke(oldStroke);
                    }
                }
            }
            index++;
        }

//        // Drop row
//        if (dropRow != -1) {
//            Color oldColor = g.getColor();
//            Stroke oldStroke = ((Graphics2D) g).getStroke();
//            Rectangle r = getCellRect(dropRow, 0, true);
//            r.width = getWidth();
//            Stroke s = new BasicStroke(2.0f);
//            ((Graphics2D) g).setStroke(s);
//            g.setColor(UIManager.getDefaults().getColor("Tree.selectionBorderColor"));
//            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g.drawRoundRect(r.x + 2, r.y + 1, r.width - 4, r.height - 2, 7, 7);
//            g.setColor(oldColor);
//            ((Graphics2D) g).setStroke(oldStroke);
//        }
        if (dragOver) {
            Point mousePos = getMousePosition();
            if (mousePos == null) {
                return;
            }
            int in = locationToIndex(mousePos);
            Rectangle rb = getCellBounds(in, in);
            g.setColor(getSelectionBackground());
            g.drawRoundRect(rb.x, rb.y, rb.width - 4, rb.height, 2, 2);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private OWLObject linkedObject;


    public JComponent getComponent() {
        return this;
    }


    public OWLObject getLinkedObject() {
        return linkedObject;
    }


    /**
     * Gets the location of the mouse relative to the
     * rendering cell that it is over.
     */
    public Point getMouseCellLocation() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int index = locationToIndex(mouseLoc);
        Rectangle cellRect = getCellBounds(index, index);
        return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
    }


    public Rectangle getMouseCellRect() {
        Point loc = getMousePosition();
        if (loc == null) {
            return null;
        }
        int index = locationToIndex(loc);
        return getCellBounds(index, index);
    }


    public void setLinkedObject(OWLObject object) {
        linkedObject = object;
        if (linkedObject != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            setCursor(defaultCursor);
        }
    }

    // DnD Stuff

    boolean dragOver;


    public void dragEnter(DropTargetDragEvent dtde) {
        dragOver = true;
        repaint();
    }


    public void dragOver(DropTargetDragEvent dtde) {
        dragOver = true;
        repaint();
    }


    public void dropActionChanged(DropTargetDragEvent dtde) {
    }


    public void dragExit(DropTargetEvent dte) {
        dragOver = false;
        repaint();
    }


    public void drop(DropTargetDropEvent dtde) {
        if (dtde.getTransferable().isDataFlavorSupported(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
            try {
                List<OWLObject> object = (List<OWLObject>) dtde.getTransferable().getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
                OWLFrameObject<R, ? extends OWLAxiom, ? extends Object> frameObject;
                frameObject = (OWLFrameObject<R, ? extends OWLAxiom, ? extends Object>) getModel().getElementAt(
                        locationToIndex(dtde.getLocation()));
                dtde.dropComplete(frameObject.dropObjects(object));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        dragOver = false;
    }


    private interface EditHandler {

        void handleEditFinished(OWLFrameSectionRowObjectEditor editor);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Common popup menu items
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class MoveAxiomsToOntologyAction extends OWLFrameListPopupMenuAction<R> implements OWLOntologyListMenu.OntologySelectedHandler {

        protected String getName() {
            return "Move axiom to ontology...";
        }


        protected void initialise() throws Exception {

        }


        protected void dispose() throws Exception {

        }


        protected void updateState() {
            for (Object val : getFrameList().getSelectedValues()) {
                if (!(val instanceof OWLFrameSectionRow)) {
                    setEnabled(false);
                    return;
                }
                else {
                    if (((OWLFrameSectionRow) val).getOntology() == null) {
                        setEnabled(false);
                        return;
                    }
                }
            }
            setEnabled(true);
        }


        public void actionPerformed(ActionEvent e) {
            String title = "Select ontology to move to";
            OWLOntologyListMenu menu = new OWLOntologyListMenu(title,
                                                               editorKit,
                                                               editorKit.getOWLModelManager().getOntologies(),
                                                               this);
            menu.show(OWLFrameList.this, lastMouseDownPoint.x, lastMouseDownPoint.y);
        }


        public void ontologySelected(OWLOntology ontology) {
            moveAxiomsToOntology(ontology);
        }


        private void moveAxiomsToOntology(OWLOntology ontology) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (OWLFrameSectionRow row : getSelectedRows()) {
                OWLAxiom ax = row.getAxiom();
                OWLOntology currentOnt = row.getOntology();
                changes.add(new RemoveAxiom(currentOnt, ax));
                changes.add(new AddAxiom(ontology, ax));
            }
            getOWLModelManager().applyChanges(changes);
        }
    }


    private class PullIntoActiveOntologyAction extends OWLFrameListPopupMenuAction<R> {

        protected String getName() {
            return "Pull into active ontology";
        }


        protected void initialise() throws Exception {

        }


        protected void dispose() throws Exception {
        }


        protected void updateState() {
            for (OWLFrameSectionRow row : getSelectedRows()) {
                if (row.getOntology() == null || row.getOntology().equals(editorKit.getOWLModelManager().getActiveOntology()))
                {
                    setEnabled(false);
                    return;
                }
            }
            setEnabled(!getSelectedRows().isEmpty());
        }


        public void actionPerformed(ActionEvent e) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (OWLFrameSectionRow row : getSelectedRows()) {
                OWLAxiom ax = row.getAxiom();
                OWLOntology currentOnt = row.getOntology();
                changes.add(new RemoveAxiom(currentOnt, ax));
                changes.add(new AddAxiom(editorKit.getOWLModelManager().getActiveOntology(), ax));
            }
            getOWLModelManager().applyChanges(changes);
        }
    }


    private class SwitchToDefiningOntologyAction extends OWLFrameListPopupMenuAction<R> {

        protected String getName() {
            return "Switch to defining ontology";
        }


        protected void initialise() throws Exception {
        }


        protected void dispose() throws Exception {
        }


        private OWLOntology getSelectedRowOntology() {
            Object selVal = getFrameList().getSelectedValue();
            if (selVal instanceof OWLFrameSectionRow) {
                return ((OWLFrameSectionRow) selVal).getOntology();
            }
            else {
                return null;
            }
        }


        protected void updateState() {
            setEnabled(getSelectedRowOntology() != null);
        }


        public void actionPerformed(ActionEvent e) {
            getOWLModelManager().setActiveOntology(getSelectedRowOntology());
        }
    }
}
