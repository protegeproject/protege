package org.protege.editor.owl.ui.framelist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicListUI;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.axiom.AxiomAnnotationPanel;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameListener;
import org.protege.editor.owl.ui.frame.OWLFrameObject;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.preferences.GeneralPreferencesPanel;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.Cuttable;
import org.protege.editor.owl.ui.view.Deleteable;
import org.protege.editor.owl.ui.view.Pasteable;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveAxiom;

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
 * Date: 29-Jan-2007<br>
 * <br>
 * <p/> An OWLFrameList is a common component that displays sections and
 * section content. Most of the standard component in protege use this.
 */
public class OWLFrameList<R> extends MList
    implements LinkedObjectComponent, DropTargetListener, Copyable, Pasteable, Cuttable, Deleteable, RefreshableComponent {
	
    private static final Logger logger = Logger.getLogger(OWLFrameList.class);

    private static final Border inferredBorder = new OWLFrameListInferredSectionRowBorder();
    public static final Color INFERRED_BG_COLOR = new Color(255, 255, 215);

    public static final int BUTTON_DIMENSION = 14;
    public static final int BUTTON_MARGIN = 3;

    private OWLEditorKit editorKit;
    private OWLFrame<R> frame;
    private OWLFrameListener listener;
    private LinkedObjectComponentMediator mediator;

    private List<MListButton> inferredRowButtons;
    private AxiomAnnotationButton axiomAnnotationButton;

    private ChangeListenerMediator changeListenerMediator;
    private JPopupMenu popupMenu;
    private List<OWLFrameListPopupMenuAction<R>> actions;
    private OWLFrameListRenderer cellRenderer;

    private AxiomAnnotationPanel axiomAnnotationPanel;
    
    private ListSelectionListener selListener = new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent event) {
            handleSelectionEvent(event);
        }
    };

    private boolean axiomSelectionGlobal = true;


    public OWLFrameList(OWLEditorKit editorKit, OWLFrame<R> frame) {
        this.editorKit = editorKit;
        this.frame = frame;

        cellRenderer = new OWLFrameListRenderer(editorKit);
        setCellRenderer(cellRenderer);

        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        mediator = new LinkedObjectComponentMediator(editorKit, this);

        setupFrameListener();

        setupKeyboardHandlers();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                setFixedCellWidth(OWLFrameList.this
                        .getWidth());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        });

        createPopupMenu();

        inferredRowButtons = new ArrayList<MListButton>();
        inferredRowButtons.add(new ExplainButton(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invokeExplanationHandler();
            }
        }));

        axiomAnnotationButton = new AxiomAnnotationButton(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                invokeAxiomAnnotationHandler();
            }
        });

        changeListenerMediator = new ChangeListenerMediator();
        addListSelectionListener(selListener);
        
        setUI(new OWLFrameListUI());
    }


    public void refreshComponent() {
        refillRows();
    }

    public OWLFrame<R> getFrame() {
        return frame;
    }

    private void setupFrameListener() {
        listener = new OWLFrameListener() {
            public void frameContentChanged() throws Exception {
                refillRows();
            }
        };
        frame.addFrameListener(listener);
    }

    public void setHighlightUnsatifiableClasses(boolean b) {
        cellRenderer.setHighlightUnsatisfiableClasses(b);
    }

    public void setHighlightUnsatisfiableProperties(boolean b) {
        cellRenderer.setHighlightUnsatisfiableProperties(b);
    }

    public void setCrossedOutEntities(Set<OWLEntity> entities) {
        cellRenderer.setCrossedOutEntities(entities);
    }

    public void updateUI() {
    }

    protected Border createListItemBorder(JList list, Object value, int index,
                                          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof OWLFrameSectionRow) {
            OWLFrameSectionRow row = (OWLFrameSectionRow) value;
            if (row.isInferred()) {
                return inferredBorder;
            }
        }
        return super.createListItemBorder(list, value, index, isSelected,
                                          cellHasFocus);
    }

    protected List<MListButton> getButtons(Object value) {
        List<MListButton> buttons = new ArrayList<MListButton>(super.getButtons(value));
        if (value instanceof OWLFrameSectionRow) {
        	OWLFrameSectionRow frameRow = (OWLFrameSectionRow) value;
            buttons.add(axiomAnnotationButton);
            axiomAnnotationButton.setAnnotationPresent(isAnnotationPresent(frameRow));
            
            if (getExplanationManager().hasExplanation(frameRow.getAxiom())) {
                buttons.addAll(inferredRowButtons);
            }
        }
        if (value instanceof AbstractOWLFrameSectionRow) {
            List<MListButton> additional = ((AbstractOWLFrameSectionRow) value).getAdditionalButtons();
            if (!additional.isEmpty()) {
                buttons.addAll(additional);
            }
        }
        if (value instanceof AbstractOWLFrameSection) {
            buttons.addAll(((AbstractOWLFrameSection) value).getAdditionalButtons());
        }
        return buttons;
    }


    protected String getRowName(Object rowObject) {
        if (rowObject instanceof  OWLFrameSectionRow){
            return ((OWLFrameSectionRow) rowObject).getFrameSection().getRowLabel((OWLFrameSectionRow) rowObject);
        }
        return null;
    }


    protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof AbstractOWLFrameSectionRow) {
            if (((AbstractOWLFrameSectionRow) item).isInferred()) {
                return INFERRED_BG_COLOR;
            }
        }
        return super.getItemBackgroundColor(item);
    }

    private void setupKeyboardHandlers() {
        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE_SEL");
        am.put("DELETE_SEL", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ADD");
        am.put("ADD", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
    }

    public void setWrap(boolean b) {
        cellRenderer.setWrap(b);
    }


    private void showPopupMenu(MouseEvent e) {
        for (OWLFrameListPopupMenuAction action : actions) {
            action.updateState();
        }
        popupMenu.show(this, e.getX(), e.getY());
    }

    private void createPopupMenu() {
        actions = new ArrayList<OWLFrameListPopupMenuAction<R>>();
        popupMenu = new JPopupMenu();
        addToPopupMenu(new SwitchToDefiningOntologyAction<R>());
        addToPopupMenu(new PullIntoActiveOntologyAction<R>());
        addToPopupMenu(new MoveAxiomsToOntologyAction<R>());
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
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Exception thrown", e);
            }
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
        if (axiomAnnotationPanel != null){
            axiomAnnotationPanel.dispose();
        }
        removeListSelectionListener(selListener);
        frame.removeFrameListener(listener);
        for (OWLFrameListPopupMenuAction<R> action : actions) {
            try {
                action.dispose();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Exception thrown",  e);
                }
            }
        }
        frame.dispose();
    }

    private void refillRows() {
        List<OWLFrameObject> rows = new ArrayList<OWLFrameObject>();
        for (OWLFrameSection<R, ? extends Object, ? extends Object> section : frame
                .getFrameSections()) {
            rows.add(section);
            for (OWLFrameSectionRow row : section.getRows()) {
                rows.add(row);
            }
        }
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
        editorKit.getModelManager().applyChanges(changes);
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
                if (!((OWLFrameSection) val).canAdd()) {
                    return;
                }
            } else {
                if (!((OWLFrameSectionRow) val).isEditable()) {
                    return;
                }
            }
            OWLFrameObject row = (OWLFrameObject) val;
            showEditorDialog(row, new EditHandler() {
                public void handleEditFinished(OWLObjectEditor editor) {
                    editor.getHandler().handleEditingFinished(editor.getEditedObjects());
                }
            });
        }
    }


    protected void handleSelectionEvent(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (isAxiomSelectionSyncronized()){
                final Object sel = getSelectedValue();
                if (sel instanceof OWLFrameSectionRow){
                    final OWLFrameSectionRow row = (OWLFrameSectionRow) sel;
                    OWLAxiom ax = row.getAxiom();
                    if (ax != null){
                        editorKit.getWorkspace().getOWLSelectionModel().setSelectedAxiom(new OWLAxiomInstance(ax, row.getOntology()));
                    }
                }
            }
            changeListenerMediator.fireStateChanged(OWLFrameList.this);
        }
    }


    public boolean isAxiomSelectionSyncronized() {
        return axiomSelectionGlobal;
    }


    public void setAxiomSelectionSyncronized(boolean sync) {
        axiomSelectionGlobal = sync;
    }


    private void showEditorDialog(final OWLFrameObject frameObject,
                                  final EditHandler handler) {
        // If we don't have any editing component then just return
        final boolean isRowEditor = frameObject instanceof OWLFrameSectionRow;
        final OWLObjectEditor editor = frameObject.getEditor();
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
        final VerifyingOptionPane optionPane = new VerifyingOptionPane(editorComponent) {

            public void selectInitialValue() {
                // This is overriden so that the option pane dialog default
                // button
                // doesn't get the focus.
            }
        };
        final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
            public void verifiedStatusChanged(boolean verified) {
                optionPane.setOKEnabled(verified && frameObject.checkEditorResults(editor));
            }
        };
        // if the editor is verifying, will need to prevent the OK button from
        // being available
        if (editor instanceof VerifiedInputEditor) {
            ((VerifiedInputEditor) editor)
                    .addStatusChangedListener(verificationListener);
        }
        final Component parent = getDialogParent();
        final JDialog dlg = optionPane.createDialog(parent, null);
        // The editor shouldn't be modal (or should it?)
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.addComponentListener(new ComponentAdapter() {

            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                editorComponent.setPreferredSize(editorComponent.getSize());
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handler.handleEditFinished(editor);
                }
                setSelectedValue(frameObject, true);
                if (editor instanceof VerifiedInputEditor) {
                    ((VerifiedInputEditor) editor)
                            .removeStatusChangedListener(verificationListener);
                }
                // editor.dispose();
                if (isRowEditor) {
                    editor.dispose();
                }
            }
        });

        Object rootObject = null;

        if (frameObject instanceof OWLFrameSectionRow) {
            rootObject = ((OWLFrameSectionRow) frameObject).getFrameSection().getRootObject();
        }
        else if (frameObject instanceof OWLFrameSection) {
            rootObject = ((OWLFrameSection) frameObject).getRootObject();
        }

        if (rootObject instanceof OWLObject) {
            dlg.setTitle(editorKit.getModelManager().getRendering((OWLObject)rootObject));
        }
        else if (rootObject != null) {
            dlg.setTitle(rootObject.toString());
        }

        dlg.setVisible(true);
    }

    private Component getDialogParent() {
        // @@TODO move prefs somewhere more central
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        return prefs.getBoolean(GeneralPreferencesPanel.DIALOGS_ALWAYS_CENTRED, false) ? SwingUtilities.getAncestorOfClass(Frame.class, getParent()) : getParent();
    }


    private void invokeExplanationHandler() {
        Object obj = getSelectedValue();
        if (!(obj instanceof OWLFrameSectionRow)) {
            return;
        }
        OWLFrameSectionRow row = (OWLFrameSectionRow) obj;
        OWLAxiom ax = row.getAxiom();
        if(getExplanationManager().hasExplanation(ax)) {
        	getExplanationManager().handleExplain((Frame) SwingUtilities.getAncestorOfClass(Frame.class, this), ax);
        }

    }
    
    protected ExplanationManager getExplanationManager() {
    	return editorKit.getModelManager().getExplanationManager();
    }


    private void invokeAxiomAnnotationHandler() {
        Object obj = getSelectedValue();
        if (!(obj instanceof OWLFrameSectionRow)) {
            return;
        }
        OWLFrameSectionRow row = (OWLFrameSectionRow) obj;
        OWLAxiom ax = row.getAxiom();

        if (axiomAnnotationPanel == null){
            axiomAnnotationPanel = new AxiomAnnotationPanel(editorKit);
        }
        axiomAnnotationPanel.setAxiomInstance(new OWLAxiomInstance(ax, row.getOntology()));
        new UIHelper(editorKit).showDialog("Annotations for " + ax.getAxiomType().toString(),
                                           axiomAnnotationPanel,
                                           JOptionPane.CLOSED_OPTION);
    }


    private boolean isAnnotationPresent(OWLFrameSectionRow row) {
        OWLAxiom ax = row.getAxiom();
        return (!ax.getAnnotations().isEmpty());
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JComponent getComponent() {
        return this;
    }

    public OWLObject getLinkedObject() {
        return mediator.getLinkedObject();
    }

    /**
     * Gets the location of the mouse relative to the rendering cell that it is
     * over.
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
    private boolean dragOver;

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
        if (dtde.getTransferable().isDataFlavorSupported(
                OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
            try {
                List<OWLObject> object = (List<OWLObject>) dtde.getTransferable().getTransferData(
                        OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
                OWLFrameObject<R, ? extends OWLAxiom, ? extends Object> frameObject;
                frameObject = (OWLFrameObject<R, ? extends OWLAxiom, ? extends Object>) getModel().getElementAt(locationToIndex(dtde.getLocation()));
                dtde.dropComplete(frameObject.dropObjects(object));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        dragOver = false;
    }


    private interface EditHandler {
        void handleEditFinished(OWLObjectEditor editor);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Pasteable
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean canPaste(List<OWLObject> objects) {
        if (getRootObject() == null) {
            return false;
        }
        return getSelectedValue() instanceof OWLFrameSection
               && ((OWLFrameSection) getSelectedValue())
                .canAcceptDrop(objects);
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

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Copyable
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean canCopy() {
        return getRootObject() != null && getSelectedIndex() != -1;
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

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Cuttable
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                    changes.add(new RemoveAxiom(row.getOntology(), row
                            .getAxiom()));
                }
            }
        }
        editorKit.getModelManager().applyChanges(changes);
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

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * An override of the BasicListUI. This is necessary because of the very
     * poor performance of the default Java implementation. Also, this list UI
     * uses a hybrid fixed/non-fixed cell size approach - specific to
     * AbstractOWLFrameSectionRow.
     */
    public class OWLFrameListUI extends BasicListUI {

        private Point lastMouseDownPoint;

        private int[] cumulativeCellHeight;


        // As BasicListUI is implemented with windows keystrokes, we need to
        // return a mouse listener that ignores the (bad) default toggle behaviour when Ctrl is pressed.
        // This would prevent mac users from using this very common key combination (right-click)
        // instead, add handling for the context menu and double click editing
        // Also must implement discontiguous multi-selection
        protected MouseInputListener createMouseInputListener() {

            return new MouseInputHandler(){

                boolean showingPopup = false;

                public void mousePressed(MouseEvent e) {
                    showingPopup = false;
                    lastMouseDownPoint = e.getPoint();
                    if (e.isPopupTrigger()) {
                        showingPopup = true;
                        showPopupMenu(e);
                    }
                    else if ((e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0){
                        int sel = locationToIndex(OWLFrameList.this, lastMouseDownPoint);
                        handleModifiedSelectionEvent(sel);
                    }
                    else{
                        super.mousePressed(e);
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showingPopup = true;
                        showPopupMenu(e);
                    }
                    else if (e.getClickCount() == 2) {
                        if (!showingPopup){
                            handleEdit();
                        }
                    }
                    else {
                        super.mouseReleased(e);
                    }
                }
            };
        }


        private void handleModifiedSelectionEvent(int index) {
            if (isSelectedIndex(index)){
                removeSelectionInterval(index, index);
            }
            else if (getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
                     getSelectedIndex() == -1){
                addSelectionInterval(index, index);
            }
        }


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
            /*
                * If both JList fixedCellWidth and fixedCellHeight have been set,
                * then initialize cellWidth and cellHeight, and set cellHeights to
                * null.
                */
            int fixedCellHeight = list.getFixedCellHeight();
            int fixedCellWidth = list.getFixedCellWidth();
            cellWidth = fixedCellWidth != -1 ? fixedCellWidth : -1;
            if (fixedCellHeight != -1) {
                cellHeight = fixedCellHeight;
                cellHeights = null;
            } else {
                cellHeight = -1;
                cellHeights = new int[list.getModel().getSize()];
            }
            /*
                * If either of JList fixedCellWidth and fixedCellHeight haven't
                * been set, then initialize cellWidth and cellHeights by scanning
                * through the entire model. Note: if the renderer is null, we just
                * set cellWidth and cellHeights[*] to zero, if they're not set
                * already.
                */
            if (fixedCellWidth == -1 || fixedCellHeight == -1) {
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
                        } else {
                            Component c = renderer
                                    .getListCellRendererComponent(list,
                                                                  value, index, false, false);
                            rendererPane.add(c);
                            Dimension cellSize = c.getPreferredSize();
                            if (fixedCellWidth == -1) {
                                cellWidth = Math.max(cellSize.width,
                                                     cellWidth);
                            }
                            if (fixedCellHeight == -1) {
                                cellHeights[index] = cellSize.height;
                            }
                        }
                        cumulativeHeight += cellHeights[index];
                        cumulativeCellHeight[index] = cumulativeHeight;
                    }
                } else {
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
            if (index < 0) {
                return new Rectangle();
            }
            maybeUpdateLayoutState();
            if (index >= cumulativeCellHeight.length) {
                return null;
            }
            Insets insets = list.getInsets();
            int x;
            int w;
            int y;
            int h;
            x = insets.left;
            if (index >= cellHeights.length) {
                y = 0;
            } else {
                y = cumulativeCellHeight[index] - cellHeights[index];
            }
            w = list.getWidth() - (insets.left + insets.right);
            h = cellHeights[index];
            return new Rectangle(x, y, w, h);
        }

        /**
         * Paint one List cell: compute the relevant state, get the "rubber
         * stamp" cell renderer component, and then use the CellRendererPane to
         * paint it. Subclasses may want to override this method rather than
         * paint().
         *
         * @see #paint
         */

        protected void paintCell(Graphics g, int row, Rectangle rowBounds,
                                 ListCellRenderer cellRenderer, ListModel dataModel,
                                 ListSelectionModel selModel, int leadIndex) {
            Object value = dataModel.getElementAt(row);
            boolean cellHasFocus = list.hasFocus() && row == leadIndex;
            boolean isSelected = selModel.isSelectedIndex(row);
            Component rendererComponent = cellRenderer
                    .getListCellRendererComponent(list, value, row,
                                                  isSelected, cellHasFocus);
            int cx = rowBounds.x;
            int cy = rowBounds.y;
            int cw = rowBounds.width;
            int ch = rowBounds.height;
            rendererPane.paintComponent(g, rendererComponent, list,
                                        cx, cy, cw, ch, true);
        }
    }
}
