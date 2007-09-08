package org.protege.editor.owl.ui.framelist;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicListUI;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.debugging.JustificationHierarchyProvider;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameListener;
import org.protege.editor.owl.ui.frame.OWLFrameObject;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeCellRenderer;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.Cuttable;
import org.protege.editor.owl.ui.view.Deleteable;
import org.protege.editor.owl.ui.view.Pasteable;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owl.debugging.DebuggerDescriptionGenerator;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLOntologyMerger;

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
public class OWLFrameList2<R extends OWLObject> extends MList implements LinkedObjectComponent, DropTargetListener, Copyable, Pasteable, Cuttable, Deleteable {

    private static final Logger logger = Logger.getLogger(OWLFrameList2.class.getName());

    public static final int BUTTON_DIMENSION = 14;

    public static final int BUTTON_MARGIN = 3;

    private OWLEditorKit editorKit;

    private OWLFrame<R> frame;

    private OWLFrameListener listener;

    private LinkedObjectComponentMediator mediator;

    private List<MListButton> inferredRowButtons;

    private ChangeListenerMediator changeListenerMediator;

    private Border inferredBorder = new Border() {

        private Stroke stroke = new BasicStroke(1.0f,
                                                BasicStroke.CAP_ROUND,
                                                BasicStroke.JOIN_ROUND,
                                                1.0f,
                                                new float[]{3.0f, 3.0f},
                                                1.0f);


        /**
         * Paints the border for the specified component with the specified
         * position and size.
         * @param c      the component for which this border is being painted
         * @param g      the paint graphics
         * @param x      the x position of the painted border
         * @param y      the y position of the painted border
         * @param width  the width of the painted border
         * @param height the height of the painted border
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color oldColor = g.getColor();
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(stroke);
            g2.drawRect(x, y, width, height);
            g2.setColor(oldColor);
            g2.setStroke(oldStroke);
        }


        /**
         * Returns the insets of the border.
         * @param c the component for which this border insets value applies
         */
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }


        /**
         * Returns whether or not the border is opaque.  If the border
         * is opaque, it is responsible for filling in it's own
         * background when painting.
         */
        public boolean isBorderOpaque() {
            return false;
        }
    };


    // A flag to indicate whether or not sections
    // should be shown in the list
    private boolean showSections = true;

    private JPopupMenu popupMenu;

    private Point lastMouseDownPoint;

    private List<OWLFrameListPopupMenuAction<R>> actions;

    private static final Color INFERRED_ROW_BG_COLOR = new Color(255, 255, 215);


    public OWLFrameList2(OWLEditorKit editorKit, OWLFrame<R> frame) {
        this.editorKit = editorKit;
        this.frame = frame;
        listener = new OWLFrameListener() {
            public void frameContentChanged() throws Exception {
                refillRows();
            }
        };
        frame.addFrameListener(listener);

        setCellRenderer(new OWLFrameListRenderer(editorKit));

        mediator = new LinkedObjectComponentMediator(editorKit, this);

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
                else if (e.getClickCount() == 2) {
                    handleEdit();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                setFixedCellWidth(getWidth());
            }
        });


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

        inferredRowButtons = new ArrayList<MListButton>();
        inferredRowButtons.add(new ExplainButton(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleExplain();
            }
        }));
        changeListenerMediator = new ChangeListenerMediator();
        addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    changeListenerMediator.fireStateChanged(OWLFrameList2.this);
                }
            }
        });

        setUI(new OWLFrameListUI());
    }


    public void updateUI() {

    }


    protected Border createListItemBorder(JList list, Object value, int index, boolean isSelected,
                                          boolean cellHasFocus) {
        if (value instanceof OWLFrameSectionRow) {
            OWLFrameSectionRow row = (OWLFrameSectionRow) value;
            if (row.isInferred()) {
                return inferredBorder;
            }
        }
        return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }


    protected List<MListButton> getButtons(Object value) {
        if (value instanceof OWLFrameSectionRow) {
            if (((OWLFrameSectionRow) value).isInferred()) {
                return inferredRowButtons;
            }
        }
        return super.getButtons(value);
    }


    protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof AbstractOWLFrameSectionRow) {
            if (((AbstractOWLFrameSectionRow) item).isInferred()) {
                return INFERRED_ROW_BG_COLOR;
            }
        }
        return super.getItemBackgroundColor(item);
    }


    private void setupKeyboardAddHandler() {
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ADD");
        getActionMap().put("ADD", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
    }


    private void createPopupMenu() {
        actions = new ArrayList<OWLFrameListPopupMenuAction<R>>();
        popupMenu = new JPopupMenu();
        addToPopupMenu(new SwitchToDefiningOntologyAction());
        addToPopupMenu(new PullIntoActiveOntologyAction());
        addToPopupMenu(new MoveAxiomsToOntologyAction());
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


    private void setupMenuItem(OWLFrameListPopupMenuAction<R> action) {
        action.setup(editorKit, this);
        try {
            action.initialise();
            actions.add(action);
        }
        catch (Exception e) {
            logger.throwing(getClass().getName(), "setupMenuItem", e);
        }
    }


    public void setRootObject(R rootObject) {
        frame.setRootObject(rootObject);
        changeListenerMediator.fireStateChanged(this);
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
        frame.dispose();
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
//        if (rows.size() > 100) {
//            setFixedCellHeight(24);
//        }
//        else {
//            setFixedCellHeight(-1);
//        }
        setListData(rows.toArray());
    }


    public boolean canDelete() {
        return getSelectedIndex() != -1;
    }


    public void handleDelete() {
        int[] selIndices = getSelectedIndices();

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


    protected void handleAdd() {
        handleEdit();
    }


    protected void handleEdit() {
        if (getRootObject() == null) {
            return;
        }
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
        final boolean isRowEditor = frameObject instanceof OWLFrameSectionRow;
        final OWLFrameSectionRowObjectEditor editor = frameObject.getEditor();
        if (editor == null) {
            return;
        }

        if (editor instanceof JWindow) {
            ((JWindow) editor).setVisible(true);
            return;
        }
        if (editor instanceof Wizard) {
            int ret = ((Wizard) editor).showModalDialog();
            if (ret == Wizard.FINISH_RETURN_CODE) {
                handler.handleEditFinished(editor);
            }
            return;
        }

        // Create the editing component dialog - we use an option pane
        // so that the buttons and keyboard actions are what are expected
        // by the user.
        final JComponent editorComponent = editor.getEditorComponent();
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
        dlg.pack();
        dlg.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                editorComponent.setPreferredSize(editorComponent.getSize());
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handler.handleEditFinished(editor);
                }
                setSelectedValue(frameObject, true);
//                editor.dispose();
                if (isRowEditor) {
                    editor.dispose();
                }
            }
        });
        OWLObject rootObject = null;
        if (frameObject instanceof OWLFrameSectionRow) {
            rootObject = ((OWLFrameSectionRow) frameObject).getFrameSection().getRootObject();
        }
        else if (frameObject instanceof OWLFrameSection) {
            rootObject = ((OWLFrameSection) frameObject).getRootObject();
        }
        dlg.setTitle(editorKit.getOWLModelManager().getOWLObjectRenderer().render(rootObject,
                                                                                  editorKit.getOWLModelManager().getOWLEntityRenderer()));

        dlg.setVisible(true);
    }


    private Font sectionFont;


    private Font getSectionFont() {
        if (sectionFont == null) {
            float fontSize = getGraphics().getFont().getSize() * 0.85f;
            sectionFont = getGraphics().getFont().deriveFont(Font.PLAIN, fontSize);
        }
        return sectionFont;
    }


    private void handleExplain() {
        try {
            Object obj = getSelectedValue();
            if (!(obj instanceof OWLFrameSectionRow)) {
                return;
            }
            OWLFrameSectionRow row = (OWLFrameSectionRow) obj;
            OWLAxiom ax = row.getAxiom();
            DebuggerDescriptionGenerator gen = new DebuggerDescriptionGenerator(editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory());
            ax.accept(gen);
            OWLDescription desc = gen.getDebuggerDescription();
            if (desc == null) {
                System.out.println("Can't currently handle explanation for " + ax);
                return;
            }
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLReasoner r = editorKit.getOWLModelManager().getOWLReasonerManager().createReasoner(man);
            OWLOntologyMerger merger = new OWLOntologyMerger(editorKit.getOWLModelManager().getOWLOntologyManager(),
                                                             true);
            BlackBoxOWLDebugger debugger = new BlackBoxOWLDebugger(man,
                                                                   merger.createMergedOntology(man,
                                                                                               URI.create(
                                                                                                       "http://semanticweb.org/ontology" + System.nanoTime())),
                                                                   r);
            Set<OWLAxiom> axs = debugger.getSOSForIncosistentClass(desc);
            StringBuilder sb = new StringBuilder();
            for (OWLAxiom justAx : axs) {
                String s = editorKit.getOWLModelManager().getOWLObjectRenderer().render(justAx,
                                                                                        editorKit.getOWLModelManager().getOWLEntityRenderer());

                sb.append(s);
                sb.append("\n\n");
            }
            ExpressionEditor<OWLAxiom> editor = new ExpressionEditor<OWLAxiom>(editorKit, new OWLExpressionChecker() {
                public void check(String text) throws OWLExpressionParserException, OWLException {
                }


                public Object createObject(String text) throws OWLExpressionParserException, OWLException {
                    return null;
                }
            });
            editor.setText(editorKit.getOWLModelManager().getRendering(ax));
//            JFrame textFrame = new JFrame();
//            textFrame.getContentPane().add(editor);
//            textFrame.pack();
//            textFrame.setVisible(true);
            int count = 0;
            for (OWLAxiom expAx : axs) {
                count++;
                System.out.println(count + ") " + expAx);
            }
            JustificationHierarchyProvider prov = new JustificationHierarchyProvider(editorKit.getOWLModelManager().getOWLOntologyManager(),
                                                                                     desc,
                                                                                     axs);
            OWLObjectTree<OWLAxiom> tree = new OWLObjectTree<OWLAxiom>(editorKit, prov);
            OWLObjectTreeCellRenderer ren = new OWLObjectTreeCellRenderer(editorKit);
            tree.setCellRenderer(ren);
            tree.setRowHeight(-1);
            tree.setOWLObjectComparator(new OWLAxiomComparator());
            JFrame frame = new JFrame("Explanation for " + ax);
            frame.getContentPane().add(tree);
//            tree.expandAll();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public JComponent getComponent() {
        return this;
    }


    public OWLObject getLinkedObject() {
        return mediator.getLinkedObject();
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
        mediator.setLinkedObject(object);
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
    // Pasteable
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canPaste(List<OWLObject> objects) {
        if (getRootObject() == null) {
            return false;
        }
        return getSelectedValue() instanceof OWLFrameSection && ((OWLFrameSection) getSelectedValue()).canAcceptDrop(
                objects);
    }


    public void pasteObjects(List<OWLObject> objects) {
        Object selObject = getSelectedValue();
        if (selObject instanceof OWLFrameSection) {
            OWLFrameSection section = (OWLFrameSection) selObject;
            if (section.canAcceptDrop(objects)) {
                section.dropObjects(objects);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Copyable
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCopy() {
        if (getRootObject() == null) {
            return false;
        }
        return getSelectedIndex() != -1;
    }


    public List<OWLObject> getObjectsToCopy() {
        List<OWLObject> manipulatableObjects = new ArrayList<OWLObject>();
        for (Object selObject : getSelectedValues()) {
            if (selObject instanceof OWLFrameSectionRow) {
                OWLFrameSectionRow row = (OWLFrameSectionRow) selObject;
                manipulatableObjects.addAll(row.getManipulatableObjects());
            }
        }
        return manipulatableObjects;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Cuttable
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCut() {
        return !getCuttableObjects().isEmpty();
    }


    private List<OWLObject> getCuttableObjects() {
        List<OWLObject> manipulatableObjects = new ArrayList<OWLObject>();
        for (Object selObject : getSelectedValues()) {
            if (selObject instanceof OWLFrameSectionRow) {
                OWLFrameSectionRow row = (OWLFrameSectionRow) selObject;
                manipulatableObjects.addAll(row.getManipulatableObjects());
            }
        }
        return manipulatableObjects;
    }


    public List<OWLObject> cutObjects() {
        List<OWLObject> manipulatableObjects = new ArrayList<OWLObject>();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (Object selObject : getSelectedValues()) {
            if (selObject instanceof OWLFrameSectionRow) {
                OWLFrameSectionRow row = (OWLFrameSectionRow) selObject;
                if (!row.isInferred()) {
                    manipulatableObjects.addAll(row.getManipulatableObjects());
                    changes.add(new RemoveAxiom(row.getOntology(), row.getAxiom()));
                }
            }
        }
        editorKit.getOWLModelManager().applyChanges(changes);
        return manipulatableObjects;
    }


    public boolean canPerformAction() {
        return !getCuttableObjects().isEmpty();
    }


    public void addChangeListener(ChangeListener changeListener) {
        changeListenerMediator.addChangeListener(changeListener);
    }


    public void removeChangeListener(ChangeListener changeListener) {
        changeListenerMediator.removeChangeListener(changeListener);
    }


    public void setLayoutOrientation(int layoutOrientation) {
        throw new OWLRuntimeException("NOT ALLOWED");
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
            menu.show(OWLFrameList2.this, lastMouseDownPoint.x, lastMouseDownPoint.y);
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
                if (row.getOntology() == null || row.getOntology().equals(editorKit.getOWLModelManager().getActiveOntology())) {
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


    private class OWLAxiomComparator extends OWLAxiomVisitorAdapter implements Comparator<OWLAxiom> {


        public void visit(OWLSubClassAxiom axiom) {
            result = 2;
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            result = 1;
        }


        public void visit(OWLDisjointClassesAxiom axiom) {
            result = 0;
        }


        private int result;


        public int compare(OWLAxiom o1, OWLAxiom o2) {
            result = 0;
            o1.accept(this);
            int result1 = result;
            o2.accept(this);
            int result2 = result;
            int diff = result2 - result1;
            if (diff != 0) {
                return diff;
            }
            else {
                return -1;
            }
        }
    }


    /**
     * An override of the BasicListUI.  This is necessary because of the
     * very poor performance of the default Java implementation.  Also,
     * this list UI uses a hybrid fixed/non-fixed cell size approach - specific
     * to AbstractOWLFrameSectionRow.
     */


    private class OWLFrameListUI extends BasicListUI {

        private int[] cumulativeCellHeight;


        protected boolean isFixedCellHeightRow(int index) {
            Object value = getModel().getElementAt(index);
            if (!(value instanceof AbstractOWLFrameSectionRow)) {
                return false;
            }
            AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            if (index < getModel().getSize() - 1) {
                if (getModel().getElementAt(index + 1) instanceof AbstractOWLFrameSection) {
                    return false;
                }
            }
            return row.isFixedHeight();
        }


        protected void updateLayoutState() {

            cumulativeCellHeight = new int[list.getModel().getSize()];

            /* If both JList fixedCellWidth and fixedCellHeight have been
             * set, then initialize cellWidth and cellHeight, and set
             * cellHeights to null.
             */
            int fixedCellHeight = list.getFixedCellHeight();
            int fixedCellWidth = list.getFixedCellWidth();

            cellWidth = (fixedCellWidth != -1) ? fixedCellWidth : -1;

            if (fixedCellHeight != -1) {
                cellHeight = fixedCellHeight;
                cellHeights = null;
            }
            else {
                cellHeight = -1;
                cellHeights = new int[list.getModel().getSize()];
            }

            /* If either of  JList fixedCellWidth and fixedCellHeight haven't
             * been set, then initialize cellWidth and cellHeights by
             * scanning through the entire model.  Note: if the renderer is
             * null, we just set cellWidth and cellHeights[*] to zero,
             * if they're not set already.
             */

            if ((fixedCellWidth == -1) || (fixedCellHeight == -1)) {

                ListModel dataModel = list.getModel();
                int dataModelSize = dataModel.getSize();
                ListCellRenderer renderer = list.getCellRenderer();

                if (renderer != null) {
                    int cumulativeHeight = 0;
                    for (int index = 0; index < dataModelSize; index++) {
                        Object value = dataModel.getElementAt(index);
                        if (isFixedCellHeightRow(index)) {
                            if (fixedCellHeight == -1) {
                                cellHeights[index] = 22;
                            }
                        }
                        else {
                            Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                            rendererPane.add(c);
                            Dimension cellSize = c.getPreferredSize();
                            if (fixedCellWidth == -1) {
                                cellWidth = Math.max(cellSize.width, cellWidth);
                            }
                            if (fixedCellHeight == -1) {
                                cellHeights[index] = cellSize.height;
                            }
                        }

                        cumulativeHeight += cellHeights[index];
                        cumulativeCellHeight[index] = cumulativeHeight;
                    }
                }
                else {
                    if (cellWidth == -1) {
                        cellWidth = 0;
                    }
                    if (cellHeights == null) {
                        cellHeights = new int[dataModelSize];
                    }
                    for (int index = 0; index < dataModelSize; index++) {
                        cellHeights[index] = 0;
                    }
                }
            }
        }


        public Rectangle getCellBounds(JList list, int index1, int index2) {
            maybeUpdateLayoutState();

            int minIndex = Math.min(index1, index2);
            int maxIndex = Math.max(index1, index2);

            if (minIndex >= list.getModel().getSize()) {
                return null;
            }

            Rectangle minBounds = getCellBounds(list, minIndex);

            if (minBounds == null) {
                return null;
            }
            if (minIndex == maxIndex) {
                return minBounds;
            }
            Rectangle maxBounds = getCellBounds(list, maxIndex);

            if (maxBounds != null) {
                if (minBounds.x != maxBounds.x) {
                    // Different columns
                    minBounds.y = 0;
                    minBounds.height = list.getHeight();
                }
                minBounds.add(maxBounds);
            }
            return minBounds;
        }


        /**
         * Gets the bounds of the specified model index, returning the resulting
         * bounds, or null if <code>index</code> is not valid.
         */
        private Rectangle getCellBounds(JList list, int index) {

            maybeUpdateLayoutState();

            if (index >= cumulativeCellHeight.length) {
                return null;
            }

            Insets insets = list.getInsets();
            int x;
            int w;
            int y = insets.top;
            int h;

            x = insets.left;

            if (index >= cellHeights.length) {
                y = 0;
            }
            else {
                y = cumulativeCellHeight[index] - cellHeights[index];
            }
            w = list.getWidth() - (insets.left + insets.right);
            h = cellHeights[index];

            return new Rectangle(x, y, w, h);
        }


        /**
         * Paint one List cell: compute the relevant state, get the "rubber stamp"
         * cell renderer component, and then use the CellRendererPane to paint it.
         * Subclasses may want to override this method rather than paint().
         * @see #paint
         */
        protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer,
                                 ListModel dataModel, ListSelectionModel selModel, int leadIndex) {
            Object value = dataModel.getElementAt(row);
            boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
            boolean isSelected = selModel.isSelectedIndex(row);

            Component rendererComponent = cellRenderer.getListCellRendererComponent(list,
                                                                                    value,
                                                                                    row,
                                                                                    isSelected,
                                                                                    cellHasFocus);

            int cx = rowBounds.x;
            int cy = rowBounds.y;
            int cw = rowBounds.width;
            int ch = rowBounds.height;

            rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
        }
    }
}
