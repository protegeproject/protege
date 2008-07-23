package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 22, 2008
 */
public class OWLEntitySelector<O extends OWLEntity> extends JPanel {


    private OWLObjectTree<O> tree;


    public OWLEntitySelector(OWLObjectTree<O> tree) {
        super(new BorderLayout());
        this.tree = tree;
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(200, 200));
        add(tree, BorderLayout.WEST);
    }

    public void addSelectionListener (TreeSelectionListener l){
        tree.addTreeSelectionListener(l);
    }

    public void removeSelectionListener (TreeSelectionListener l){
        tree.removeTreeSelectionListener(l);
    }


    public static <T extends OWLEntity> OWLEntitySelector<T> createEntitySelector(Class<T> type, OWLEditorKit eKit) {
        OWLObjectHierarchyProvider<T> hp = null;

        if (type.equals(OWLClass.class)){
            hp = (OWLObjectHierarchyProvider<T>)eKit.getModelManager().getOWLClassHierarchyProvider();
        }
        else if (type.equals(OWLObjectProperty.class)){
            hp = (OWLObjectHierarchyProvider<T>)eKit.getModelManager().getOWLObjectPropertyHierarchyProvider();
        }
        else if (type.equals(OWLDataProperty.class)){
            hp = (OWLObjectHierarchyProvider<T>)eKit.getModelManager().getOWLDataPropertyHierarchyProvider();
        }
        else if (type.equals(OWLIndividual.class)){
            throw new UnsupportedOperationException("Cannot create a selector for individuals yet");
        }


        if (hp != null){
            final Comparator<T> comp = eKit.getModelManager().getOWLObjectComparator();
            OWLObjectTree<T> tree = new OWLObjectTree<T>(eKit, hp, comp);
            final OWLCellRenderer cellRenderer = new OWLCellRenderer(eKit, false, true);
            cellRenderer.setTransparent();
            tree.setCellRenderer(cellRenderer);
            tree.setOpaque(false);
            tree.setBorder(new EmptyBorder(0, 0, 0, 0));
            return new OWLEntitySelector<T>(tree);
        }

        return null;
    }

    public Set<O> getSelectedObjects() {
        return new HashSet<O>(tree.getSelectedOWLObjects());
    }
}
