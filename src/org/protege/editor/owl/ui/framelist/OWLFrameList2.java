package org.protege.editor.owl.ui.framelist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
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
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicListUI;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.InputVerificationStatusChangedListener;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameListener;
import org.protege.editor.owl.ui.frame.OWLFrameObject;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.VerifiedInputEditor;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.Cuttable;
import org.protege.editor.owl.ui.view.Deleteable;
import org.protege.editor.owl.ui.view.Pasteable;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.RemoveAxiom;

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
 * <p/> An OWLFrameList2 is a common component that displays sections and
 * section content. Most of the standard component in protege use this.
 */
public class OWLFrameList2<R extends Object> extends MList implements
		LinkedObjectComponent, DropTargetListener, Copyable, Pasteable,
		Cuttable, Deleteable, RefreshableComponent {
	private static final Logger logger = Logger.getLogger(OWLFrameList2.class);
	public static final int BUTTON_DIMENSION = 14;
	public static final int BUTTON_MARGIN = 3;
	private static Border inferredBorder = new OWLFrameListInferredSectionRowBorder();
	private OWLEditorKit editorKit;
	private OWLFrame<R> frame;
	private OWLFrameListener listener;
	private LinkedObjectComponentMediator mediator;
	private List<MListButton> inferredRowButtons;
	private ChangeListenerMediator changeListenerMediator;
	private JPopupMenu popupMenu;
	private Point lastMouseDownPoint;
	private List<OWLFrameListPopupMenuAction<R>> actions;
	private static final Color INFERRED_ROW_BG_COLOR = new Color(255, 255, 215);
	private OWLFrameListRenderer cellRenderer;
	private ExplanationHandler explanationHandler;

	public OWLFrameList2(OWLEditorKit editorKit, OWLFrame<R> frame) {
		this.editorKit = editorKit;
		this.frame = frame;
		this.cellRenderer = new OWLFrameListRenderer(editorKit);
		this.mediator = new LinkedObjectComponentMediator(editorKit, this);
		InputMap im = this.getInputMap(JComponent.WHEN_FOCUSED);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE_SEL");
		ActionMap am = this.getActionMap();
		am.put("DELETE_SEL", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				OWLFrameList2.this.handleDelete();
			}
		});
		this.setupFrameListener();
		this.setCellRenderer(this.cellRenderer);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				OWLFrameList2.this.lastMouseDownPoint = e.getPoint();
				if (e.isPopupTrigger()) {
					OWLFrameList2.this.showPopupMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					OWLFrameList2.this.showPopupMenu(e);
				} else if (e.getClickCount() == 2) {
					OWLFrameList2.this.handleEdit();
				}
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				OWLFrameList2.this.setFixedCellWidth(OWLFrameList2.this
						.getWidth());
			}
		});
		this.setupKeyboardAddHandler();
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				OWLFrameList2.this.repaint();
			}
		});
		this.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.createPopupMenu();
		this.inferredRowButtons = new ArrayList<MListButton>();
		this.inferredRowButtons.add(new ExplainButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OWLFrameList2.this.invokeExplanationHandler();
			}
		}));
		this.changeListenerMediator = new ChangeListenerMediator();
		this.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					OWLFrameList2.this.changeListenerMediator
							.fireStateChanged(OWLFrameList2.this);
				}
			}
		});
		this.setUI(new OWLFrameListUI());
		this.explanationHandler = new OWLFrameListExplanationHandler(editorKit);
	}

	public void refreshComponent() {
		this.refillRows();
	}

	public OWLFrame<R> getFrame() {
		return this.frame;
	}

	private void setupFrameListener() {
		this.listener = new OWLFrameListener() {
			public void frameContentChanged() throws Exception {
				OWLFrameList2.this.refillRows();
			}
		};
		this.frame.addFrameListener(this.listener);
	}

	public void setHighlightUnsatifiableClasses(boolean b) {
		this.cellRenderer.setHighlightUnsatisfiableClasses(b);
	}

	public void setHighlightUnsatisfiableProperties(boolean b) {
		this.cellRenderer.setHighlightUnsatisfiableProperties(b);
	}

	public void setCrossedOutEntities(Set<OWLEntity> entities) {
		this.cellRenderer.setCrossedOutEntities(entities);
	}

	public void setExplanationHandler(ExplanationHandler handler) {
		this.explanationHandler = handler;
	}

	@Override
	public void updateUI() {
	}

	@Override
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

	@Override
	protected List<MListButton> getButtons(Object value) {
		List<MListButton> buttons = new ArrayList<MListButton>(super
				.getButtons(value));
		if (value instanceof OWLFrameSectionRow) {
			if (((OWLFrameSectionRow) value).isInferred()) {
				buttons.addAll(this.inferredRowButtons);
			}
		}
		if (value instanceof AbstractOWLFrameSectionRow) {
			List<MListButton> additional = ((AbstractOWLFrameSectionRow) value)
					.getAdditionalButtons();
			if (!additional.isEmpty()) {
				buttons.addAll(additional);
			}
		}
		if (value instanceof AbstractOWLFrameSection) {
			buttons.addAll(((AbstractOWLFrameSection) value)
					.getAdditionalButtons());
		}
		return buttons;
	}

	@Override
	protected Color getItemBackgroundColor(MListItem item) {
		if (item instanceof AbstractOWLFrameSectionRow) {
			if (((AbstractOWLFrameSectionRow) item).isInferred()) {
				return INFERRED_ROW_BG_COLOR;
			}
		}
		return super.getItemBackgroundColor(item);
	}

	private void setupKeyboardAddHandler() {
		this.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ADD");
		this.getActionMap().put("ADD", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				OWLFrameList2.this.handleEdit();
			}
		});
	}

	public void setWrap(boolean b) {
		this.cellRenderer.setWrap(b);
	}

	public Point getLastMouseDownPoint() {
		return this.lastMouseDownPoint;
	}

	private void showPopupMenu(MouseEvent e) {
		for (OWLFrameListPopupMenuAction action : this.actions) {
			action.updateState();
		}
		this.popupMenu.show(this, e.getX(), e.getY());
	}

	private void createPopupMenu() {
		this.actions = new ArrayList<OWLFrameListPopupMenuAction<R>>();
		this.popupMenu = new JPopupMenu();
		this.addToPopupMenu(new SwitchToDefiningOntologyAction<R>());
		this.addToPopupMenu(new PullIntoActiveOntologyAction<R>());
		this.addToPopupMenu(new MoveAxiomsToOntologyAction<R>());
	}

	public void addToPopupMenu(OWLFrameListPopupMenuAction<R> action) {
		this.setupMenuItem(action);
		this.popupMenu.add(action);
	}

	private void setupMenuItem(OWLFrameListPopupMenuAction<R> action) {
		action.setup(this.editorKit, this);
		try {
			action.initialise();
			this.actions.add(action);
		} catch (Exception e) {
		    if (logger.isDebugEnabled()) {
		        logger.debug("Exception thrown", e);
		    }
		}
	}

	public void setRootObject(R rootObject) {
		this.frame.setRootObject(rootObject);
		this.changeListenerMediator.fireStateChanged(this);
	}

	public R getRootObject() {
		return this.frame.getRootObject();
	}

	public void dispose() {
		this.frame.removeFrameListener(this.listener);
		for (OWLFrameListPopupMenuAction<R> action : this.actions) {
			try {
				action.dispose();
			} catch (Exception e) {
			    if (logger.isDebugEnabled()) {
			        logger.debug("Exception thrown",  e);
			    }
			}
		}
		this.frame.dispose();
	}

	private void refillRows() {
		List<OWLFrameObject> rows = new ArrayList<OWLFrameObject>();
		for (OWLFrameSection<R, ? extends Object, ? extends Object> section : this.frame
				.getFrameSections()) {
			rows.add(section);
			for (OWLFrameSectionRow row : section.getRows()) {
				rows.add(row);
			}
		}
		this.setListData(rows.toArray());
	}

	public boolean canDelete() {
		return this.getSelectedIndex() != -1;
	}

	@Override
	public void handleDelete() {
		int[] selIndices = this.getSelectedIndices();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for (int selIndex : selIndices) {
			Object val = this.getModel().getElementAt(selIndex);
			if (val instanceof OWLFrameSectionRow) {
				OWLFrameSectionRow row = (OWLFrameSectionRow) val;
				changes.addAll(row.getDeletionChanges());
			}
		}
		this.editorKit.getOWLModelManager().applyChanges(changes);
	}

	@Override
	protected void handleAdd() {
		this.handleEdit();
	}

	@Override
	protected void handleEdit() {
		if (this.getRootObject() == null) {
			return;
		}
		final Object val = this.getSelectedValue();
		if (val instanceof OWLFrameObject) {
			if (val instanceof OWLFrameSection) {
				if (!((OWLFrameSection) val).canAddRows()) {
					return;
				}
			} else {
				if (!((OWLFrameSectionRow) val).isEditable()) {
					return;
				}
			}
			OWLFrameObject row = (OWLFrameObject) val;
			this.showEditorDialog(row, new EditHandler() {
				public void handleEditFinished(
						OWLFrameSectionRowObjectEditor editor) {
					editor.getHandler().handleEditingFinished(
							editor.getEditedObjects());
				}
			});
		}
	}

	private void showEditorDialog(final OWLFrameObject frameObject,
			final EditHandler handler) {
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
		final VerifyingOptionPane optionPane = new VerifyingOptionPane(
				editorComponent) {
			@Override
			public void selectInitialValue() {
				// This is overriden so that the option pane dialog default
				// button
				// doesn't get the focus.
			}
		};
		final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
			public void verifiedStatusChanged(boolean verified) {
				optionPane.setOKEnabled(verified);
			}
		};
		// if the editor is verifying, will need to prevent the OK button from
		// being available
		if (editor instanceof VerifiedInputEditor) {
			((VerifiedInputEditor) editor)
					.addStatusChangedListener(verificationListener);
		}
		final JDialog dlg = optionPane.createDialog(this.getParent(), null);
		// The editor shouldn't be modal (or should it?)
		dlg.setModal(false);
		dlg.setResizable(true);
		dlg.pack();
		dlg.setLocationRelativeTo(this.getParent());
		dlg.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				Object retVal = optionPane.getValue();
				editorComponent.setPreferredSize(editorComponent.getSize());
				if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
					handler.handleEditFinished(editor);
				}
				OWLFrameList2.this.setSelectedValue(frameObject, true);
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
			rootObject = ((OWLFrameSectionRow) frameObject).getFrameSection()
					.getRootObject();
		} else if (frameObject instanceof OWLFrameSection) {
			rootObject = ((OWLFrameSection) frameObject).getRootObject();
		}
		if (rootObject instanceof OWLObject) {
			dlg.setTitle(this.editorKit.getOWLModelManager()
					.getOWLObjectRenderer().render(
							(OWLObject) rootObject,
							this.editorKit.getOWLModelManager()
									.getOWLEntityRenderer()));
		} else if (rootObject != null) {
			dlg.setTitle(rootObject.toString());
		}
		dlg.setVisible(true);
	}

	private void invokeExplanationHandler() {
		Object obj = this.getSelectedValue();
		if (!(obj instanceof OWLFrameSectionRow)) {
			return;
		}
		OWLFrameSectionRow row = (OWLFrameSectionRow) obj;
		OWLAxiom ax = row.getAxiom();
		this.explanationHandler.handleExplain(ax);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public JComponent getComponent() {
		return this;
	}

	public OWLObject getLinkedObject() {
		return this.mediator.getLinkedObject();
	}

	/**
	 * Gets the location of the mouse relative to the rendering cell that it is
	 * over.
	 */
	public Point getMouseCellLocation() {
		Point mouseLoc = this.getMousePosition();
		if (mouseLoc == null) {
			return null;
		}
		int index = this.locationToIndex(mouseLoc);
		Rectangle cellRect = this.getCellBounds(index, index);
		return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
	}

	public Rectangle getMouseCellRect() {
		Point loc = this.getMousePosition();
		if (loc == null) {
			return null;
		}
		int index = this.locationToIndex(loc);
		return this.getCellBounds(index, index);
	}

	public void setLinkedObject(OWLObject object) {
		this.mediator.setLinkedObject(object);
	}

	// DnD Stuff
	private boolean dragOver;

	public void dragEnter(DropTargetDragEvent dtde) {
		this.dragOver = true;
		this.repaint();
	}

	public void dragOver(DropTargetDragEvent dtde) {
		this.dragOver = true;
		this.repaint();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
		this.dragOver = false;
		this.repaint();
	}

	public void drop(DropTargetDropEvent dtde) {
		if (dtde.getTransferable().isDataFlavorSupported(
				OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
			try {
				List<OWLObject> object = (List<OWLObject>) dtde
						.getTransferable().getTransferData(
								OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
				OWLFrameObject<R, ? extends OWLAxiom, ? extends Object> frameObject;
				frameObject = (OWLFrameObject<R, ? extends OWLAxiom, ? extends Object>) this
						.getModel().getElementAt(
								this.locationToIndex(dtde.getLocation()));
				dtde.dropComplete(frameObject.dropObjects(object));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.dragOver = false;
	}

	private interface EditHandler {
		void handleEditFinished(OWLFrameSectionRowObjectEditor editor);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Pasteable
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean canPaste(List<OWLObject> objects) {
		if (this.getRootObject() == null) {
			return false;
		}
		return this.getSelectedValue() instanceof OWLFrameSection
				&& ((OWLFrameSection) this.getSelectedValue())
						.canAcceptDrop(objects);
	}

	public void pasteObjects(List<OWLObject> objects) {
		Object selObject = this.getSelectedValue();
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
		return this.getRootObject() != null && this.getSelectedIndex() != -1;
	}

	public List<OWLObject> getObjectsToCopy() {
		List<OWLObject> manipulatableObjects = new ArrayList<OWLObject>();
		for (Object selObject : this.getSelectedValues()) {
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
		return !this.getCuttableObjects().isEmpty();
	}

	private List<OWLObject> getCuttableObjects() {
		List<OWLObject> manipulatableObjects = new ArrayList<OWLObject>();
		for (Object selObject : this.getSelectedValues()) {
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
		for (Object selObject : this.getSelectedValues()) {
			if (selObject instanceof OWLFrameSectionRow) {
				OWLFrameSectionRow row = (OWLFrameSectionRow) selObject;
				if (!row.isInferred()) {
					manipulatableObjects.addAll(row.getManipulatableObjects());
					changes.add(new RemoveAxiom(row.getOntology(), row
							.getAxiom()));
				}
			}
		}
		this.editorKit.getOWLModelManager().applyChanges(changes);
		return manipulatableObjects;
	}

	public boolean canPerformAction() {
		return !this.getCuttableObjects().isEmpty();
	}

	public void addChangeListener(ChangeListener changeListener) {
		this.changeListenerMediator.addChangeListener(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		this.changeListenerMediator.removeChangeListener(changeListener);
	}

	@Override
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
	private class OWLFrameListUI extends BasicListUI {
		private int[] cumulativeCellHeight;

		protected boolean isFixedCellHeightRow(int index) {
			Object value = OWLFrameList2.this.getModel().getElementAt(index);
			if (!(value instanceof AbstractOWLFrameSectionRow)) {
				return false;
			}
			AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
			if (index < OWLFrameList2.this.getModel().getSize() - 1) {
				if (OWLFrameList2.this.getModel().getElementAt(index + 1) instanceof AbstractOWLFrameSection) {
					return false;
				}
			}
			return row.isFixedHeight();
		}

		@Override
		protected void updateLayoutState() {
			this.cumulativeCellHeight = new int[this.list.getModel().getSize()];
			/*
			 * If both JList fixedCellWidth and fixedCellHeight have been set,
			 * then initialize cellWidth and cellHeight, and set cellHeights to
			 * null.
			 */
			int fixedCellHeight = this.list.getFixedCellHeight();
			int fixedCellWidth = this.list.getFixedCellWidth();
			this.cellWidth = fixedCellWidth != -1 ? fixedCellWidth : -1;
			if (fixedCellHeight != -1) {
				this.cellHeight = fixedCellHeight;
				this.cellHeights = null;
			} else {
				this.cellHeight = -1;
				this.cellHeights = new int[this.list.getModel().getSize()];
			}
			/*
			 * If either of JList fixedCellWidth and fixedCellHeight haven't
			 * been set, then initialize cellWidth and cellHeights by scanning
			 * through the entire model. Note: if the renderer is null, we just
			 * set cellWidth and cellHeights[*] to zero, if they're not set
			 * already.
			 */
			if (fixedCellWidth == -1 || fixedCellHeight == -1) {
				ListModel dataModel = this.list.getModel();
				int dataModelSize = dataModel.getSize();
				ListCellRenderer renderer = this.list.getCellRenderer();
				if (renderer != null) {
					int cumulativeHeight = 0;
					for (int index = 0; index < dataModelSize; index++) {
						Object value = dataModel.getElementAt(index);
						if (this.isFixedCellHeightRow(index)) {
							if (fixedCellHeight == -1) {
								this.cellHeights[index] = 22;
							}
						} else {
							Component c = renderer
									.getListCellRendererComponent(this.list,
											value, index, false, false);
							this.rendererPane.add(c);
							Dimension cellSize = c.getPreferredSize();
							if (fixedCellWidth == -1) {
								this.cellWidth = Math.max(cellSize.width,
										this.cellWidth);
							}
							if (fixedCellHeight == -1) {
								this.cellHeights[index] = cellSize.height;
							}
						}
						cumulativeHeight += this.cellHeights[index];
						this.cumulativeCellHeight[index] = cumulativeHeight;
					}
				} else {
					if (this.cellWidth == -1) {
						this.cellWidth = 0;
					}
					if (this.cellHeights == null) {
						this.cellHeights = new int[dataModelSize];
					}
					for (int index = 0; index < dataModelSize; index++) {
						this.cellHeights[index] = 0;
					}
				}
			}
		}

		@Override
		public Rectangle getCellBounds(JList list, int index1, int index2) {
			this.maybeUpdateLayoutState();
			int minIndex = Math.min(index1, index2);
			int maxIndex = Math.max(index1, index2);
			if (minIndex >= list.getModel().getSize()) {
				return null;
			}
			Rectangle minBounds = this.getCellBounds(list, minIndex);
			if (minBounds == null) {
				return null;
			}
			if (minIndex == maxIndex) {
				return minBounds;
			}
			Rectangle maxBounds = this.getCellBounds(list, maxIndex);
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
			this.maybeUpdateLayoutState();
			if (index >= this.cumulativeCellHeight.length) {
				return null;
			}
			Insets insets = list.getInsets();
			int x;
			int w;
			int y;
			int h;
			x = insets.left;
			if (index >= this.cellHeights.length) {
				y = 0;
			} else {
				y = this.cumulativeCellHeight[index] - this.cellHeights[index];
			}
			w = list.getWidth() - (insets.left + insets.right);
			h = this.cellHeights[index];
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
		@Override
		protected void paintCell(Graphics g, int row, Rectangle rowBounds,
				ListCellRenderer cellRenderer, ListModel dataModel,
				ListSelectionModel selModel, int leadIndex) {
			Object value = dataModel.getElementAt(row);
			boolean cellHasFocus = this.list.hasFocus() && row == leadIndex;
			boolean isSelected = selModel.isSelectedIndex(row);
			Component rendererComponent = cellRenderer
					.getListCellRendererComponent(this.list, value, row,
							isSelected, cellHasFocus);
			int cx = rowBounds.x;
			int cy = rowBounds.y;
			int cw = rowBounds.width;
			int ch = rowBounds.height;
			this.rendererPane.paintComponent(g, rendererComponent, this.list,
					cx, cy, cw, ch, true);
		}
	}
}
