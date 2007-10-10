package org.protege.editor.owl.ui.tree;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTreeCellRenderer extends OWLCellRenderer {

    public OWLObjectTreeCellRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        OWLObjectTreeNode node = (OWLObjectTreeNode) value;
        setEquivalentObjects(node.getEquivalentObjects());
        return (JComponent) super.getTreeCellRendererComponent(tree,
                                                               node.getOWLObject(),
                                                               selected,
                                                               expanded,
                                                               leaf,
                                                               row,
                                                               hasFocus);
    }
}
