package org.protege.editor.owl.ui.tree;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLEntityComparator;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelManagerTree<N extends OWLObject> extends OWLObjectTree<N> {

    private static final Logger logger = Logger.getLogger(OWLModelManagerTree.class);


    private OWLEditorKit owlEditorKit;

    private OWLModelManagerListener listener;

    private OWLEntityRendererListener rendererListener;


    public OWLModelManagerTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider) {
        super(owlEditorKit, provider, new OWLEntityComparator(owlEditorKit.getOWLModelManager()));
        this.owlEditorKit = owlEditorKit;
        setCellRenderer(new OWLObjectTreeCellRenderer(owlEditorKit));
        setHighlightKeywords(false);
        setupListener();
        installPopupMenu();
        setRowHeight(-1);
        getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             * @param e the event that characterizes the change.
             */
            public void valueChanged(TreeSelectionEvent e) {

            }
        });
    }


    public void setHighlightKeywords(boolean b) {
        TreeCellRenderer ren = getCellRenderer();
        if (ren instanceof OWLObjectTreeCellRenderer) {
            ((OWLObjectTreeCellRenderer) ren).setHighlightKeywords(b);
        }
    }


    public OWLModelManagerTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider, Set<N> rootObjects) {
        super(owlEditorKit, provider, rootObjects, new OWLEntityComparator(owlEditorKit.getOWLModelManager()));
        this.owlEditorKit = owlEditorKit;
        setCellRenderer(new OWLObjectTreeCellRenderer(owlEditorKit));
        setHighlightKeywords(false);
        setupListener();
        installPopupMenu();
        setRowHeight(-1);
    }


    private void setupListener() {
        listener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                    //fireNodeStructureChanged();
                    invalidate();
                    getOWLModelManager().getOWLEntityRenderer().addListener(rendererListener);
                }
            }
        };
        getOWLModelManager().addListener(listener);
        rendererListener = new OWLEntityRendererListener() {
            public void renderingChanged(OWLEntity entity, OWLEntityRenderer renderer) {
                try {
                    for (OWLObjectTreeNode<N> node : getNodes(entity)) {
                        DefaultTreeModel model = (DefaultTreeModel) getModel();
                        model.nodeStructureChanged(node);
                    }
                }
                catch (ClassCastException e) {

                }
            }
        };
        getOWLModelManager().getOWLEntityRenderer().addListener(rendererListener);
    }


    private void fireNodeStructureChanged() {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        model.nodeStructureChanged(rootNode);
        Enumeration e = rootNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            model.nodeStructureChanged((TreeNode) e.nextElement());
        }
    }


    private void installPopupMenu() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e);
            }


            public void mouseReleased(MouseEvent e) {
                handleMouseEvent(e);
            }
        });
    }


    protected void handleMouseEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            TreePath treePath = getPathForLocation(e.getX(), e.getY());
            if (treePath != null) {
                handlePopupMenuInvoked(treePath, e.getPoint());
            }
        }
    }


    protected void handlePopupMenuInvoked(TreePath path, Point pt) {
//        try {
//            OWLObjectTreeNode<N> node = (OWLObjectTreeNode<N>) path.getLastPathComponent();
//            OWLEntity owlEntity = node.getOWLObject();
//            final JMenu menu = new JMenu("Switch to defining ontology");
//            for(final OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
//                if (!ont.equals(getOWLModelManager().getActiveOntology())) {
//                    owlEntity.accept(new OWLEntityVisitor() {
//
//                        private void setupMenuItem(final OWLOntology ont) {
//                            menu.add(new AbstractAction(ont.getURI().toString()) {
//                                public void actionPerformed(ActionEvent e) {
//                                    getOWLModelManager().setActiveOntology(ont);
//                                }
//                            });
//                        }
//
//                        public void visit(OWLClass cls) {
//                            if(!ont.getAxioms(cls).isEmpty()) {
//                                setupMenuItem(ont);
//                            }
//                        }
//
//
//                        public void visit(OWLObjectProperty property) {
//                            if(!ont.getAxioms(property).isEmpty()) {
//                                setupMenuItem(ont);
//                            }
//                        }
//
//
//                        public void visit(OWLDataProperty property) {
//                            if(!ont.getAxioms(property).isEmpty()) {
//                                setupMenuItem(ont);
//                            }
//                        }
//
//
//                        public void visit(OWLAnonymousIndividual individual) {
////                            if(!ont.getAxioms(individual).isEmpty()) {
////                                setupMenuItem(ont);
////                            }
//                        }
//
//
//                        public void visit(OWLIndividual individual) {
//                            if(!ont.getAxioms(individual).isEmpty()) {
//                                setupMenuItem(ont);
//                            }
//                        }
//
//
//                        public void visit(OWLDataType dataType) {
//
//                        }
//                    });
//                }
//            }
//            if (menu.getSubElements().length > 0) {
//                JPopupMenu popupMenu = new JPopupMenu();
//                popupMenu.add(menu);
//                popupMenu.show(this, pt.x, pt.y);
//            }
//        }
//        catch (OWLException e) {
//            e.printStackTrace();
//        }
    }


    public void dispose() {
        super.dispose();
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().getOWLEntityRenderer().removeListener(rendererListener);
    }
}
