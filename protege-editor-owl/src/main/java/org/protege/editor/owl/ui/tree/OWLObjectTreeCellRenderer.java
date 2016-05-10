package org.protege.editor.owl.ui.tree;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTreeCellRenderer extends OWLCellRenderer {


    public OWLObjectTreeCellRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        if (value instanceof OWLObjectTreeNode){
            OWLObjectTreeNode node = (OWLObjectTreeNode) value;
            setEquivalentObjects(node.getEquivalentObjects());
            value = node.getOWLObject();
        }
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
