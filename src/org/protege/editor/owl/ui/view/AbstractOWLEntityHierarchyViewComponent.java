package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLObjectComparatorAdapter;
import org.protege.editor.owl.ui.action.OWLObjectHierarchyDeleter;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 23, 2009<br><br>
 */
public abstract class AbstractOWLEntityHierarchyViewComponent<E extends OWLEntity> extends AbstractOWLSelectionViewComponent
 implements Findable<E>, Deleteable {


    private OWLObjectTree<E> tree;

    private TreeSelectionListener listener;

    private OWLObjectHierarchyDeleter<E> hierarchyDeleter;


    final public void initialiseView() throws Exception {
        setLayout(new BorderLayout(7, 7));

        tree = new OWLModelManagerTree<E>(getOWLEditorKit(), getHierarchyProvider());

        // ordering based on default, but putting Nothing at the top
        final Comparator<OWLObject> comp = getOWLModelManager().getOWLObjectComparator();
        tree.setOWLObjectComparator(new OWLObjectComparatorAdapter<E>(comp) {
            public int compare(E o1, E o2) {
                if (isNothing(o1)) {
                    return -1;
                }
                else if (isNothing(o2)) {
                    return 1;
                }
                else {
                    String rendering1 = getOWLModelManager().getRendering(o1);
                    String rendering2 = getOWLModelManager().getRendering(o2);
                    return rendering1.compareTo(rendering2);
                }
            }
        });

        // render keywords should be on now for class expressions
        final TreeCellRenderer treeCellRenderer = tree.getCellRenderer();
        if (treeCellRenderer instanceof OWLCellRenderer){
            ((OWLCellRenderer) treeCellRenderer).setHighlightKeywords(true);
        }

        initSelectionManagement();
        add(ComponentFactory.createScrollPane(tree));
        performExtraInitialisation();
        E entity = getSelectedEntity();
        if (entity != null) {
            setGlobalSelection(entity);
        }
        tree.getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
            }


            public void treeNodesInserted(TreeModelEvent e) {
                ensureSelection();
            }


            public void treeNodesRemoved(TreeModelEvent e) {
                ensureSelection();
            }


            public void treeStructureChanged(TreeModelEvent e) {
                ensureSelection();
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                transmitSelection();
            }
        });
        hierarchyDeleter = new OWLObjectHierarchyDeleter<E>(getOWLEditorKit(),
                                                                   getHierarchyProvider(),
                                                                   new OWLEntitySetProvider<E>() {
                                                                       public Set<E> getEntities() {
                                                                           return new HashSet<E>(tree.getSelectedOWLObjects());
                                                                       }
                                                                   },
                                                                   getCollectiveTypeName());
    }


    protected abstract void performExtraInitialisation() throws Exception;


    protected abstract OWLObjectHierarchyProvider<E> getHierarchyProvider();


    /**
     * Override with the name of the entities to be used in the Edit | Delete menu - eg "classes"
     * @return String the name of the entities
     */
    protected String getCollectiveTypeName(){
        return "entities";
    }


    public void setSelectedEntity(E entity) {
        tree.setSelectedOWLObject(entity);
    }


    public void setSelectedEntities(Set<E> entities) {
        tree.setSelectedOWLObjects(entities);
    }


    public E getSelectedEntity() {
        return tree.getSelectedOWLObject();
    }


    public Set<E> getSelectedEntities() {
        return new HashSet<E>(tree.getSelectedOWLObjects());
    }


    private boolean isNothing(E o1) {
        return o1.getIRI().equals(OWLRDFVocabulary.OWL_NOTHING.getIRI());
    }


    private void ensureSelection() {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                final E entity = getSelectedEntity();
                if (entity != null) {
                    E treeSel = tree.getSelectedOWLObject();
                    if (treeSel == null || !treeSel.equals(entity)) {
                        tree.setSelectedOWLObject(entity);
                    }
                }
            }
        });
    }


    public boolean requestFocusInWindow() {
        return tree.requestFocusInWindow();
    }


    protected OWLObjectTree<E> getTree() {
        return tree;
    }




    private void initSelectionManagement() {
        // Hook up a selection listener so that we can transmit our
        // selection to the main selection model

        listener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                transmitSelection();
            }
        };
        tree.addTreeSelectionListener(listener);
    }


    protected void transmitSelection() {
        deletableChangeListenerMediator.fireStateChanged(this);

        E selEntity = getSelectedEntity();
        if (selEntity != null) {
            final View view = getView();
            if (view != null && !view.isPinned()){
                view.setPinned(true); // so that we don't follow the selection
                setGlobalSelection(selEntity);
                view.setPinned(false);
            }
            else{
                setGlobalSelection(selEntity);
            }
        }

        updateHeader(selEntity);
    }


    protected E updateView(E selEntity) {
        if (tree.getSelectedOWLObject() == null) {
            if (selEntity != null) {
                tree.setSelectedOWLObject(selEntity);
            }
            else {
                // Don't need to do anything - both null
            }
        }
        else {
            if (!tree.getSelectedOWLObject().equals(selEntity)) {
                tree.setSelectedOWLObject(selEntity);
            }
        }

        return selEntity;
    }


    public void disposeView() {
        // Dispose of the tree selection listener
        if (tree != null) {
            tree.removeTreeSelectionListener(listener);
            tree.dispose();
        }
    }


    protected OWLObject getObjectToCopy() {
        return tree.getSelectedOWLObject();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of Deleteable
    //
    /////////////////////////////////////////////////////////////////////////////////////

    private ChangeListenerMediator deletableChangeListenerMediator = new ChangeListenerMediator();


    public void addChangeListener(ChangeListener listener) {
        deletableChangeListenerMediator.addChangeListener(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        deletableChangeListenerMediator.removeChangeListener(listener);
    }


    public void handleDelete() {
        hierarchyDeleter.performDeletion();
    }


    public boolean canDelete() {
        return !tree.getSelectedOWLObjects().isEmpty();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of Findable
    //
    /////////////////////////////////////////////////////////////////////////////////////


// will need to be overridden
//    public java.util.List<E> find(String match) {
//        return new ArrayList<E>(getOWLModelManager().getEntityFinder().getMatchingOWLClasses(match));
//    }


    public void show(E owlEntity) {
        getTree().setSelectedOWLObject(owlEntity);
    }
}
