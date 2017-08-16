package org.protege.editor.owl.ui.tree;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLObject;

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


    @SuppressWarnings("unchecked")
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        clearRelationship();
        setRelationshipsDisplayed(false);
        Object renderedValue;
        if (value instanceof OWLObjectTreeNode){
            OWLObjectTreeNode<OWLObject> node = (OWLObjectTreeNode<OWLObject>) value;
            setEquivalentObjects(node.getEquivalentObjects());
            OWLObject owlObject = node.getOWLObject();
            renderedValue = owlObject;
            boolean displayRelationships = false;
            if(tree instanceof OWLModelManagerTree) {
                displayRelationships = !((OWLModelManagerTree) tree).getProvider().getDisplayedRelationships().isEmpty();
            }
            setRelationshipsDisplayed(displayRelationships && owlObject != null && !owlObject.isTopEntity());
            node.getRelationship().ifPresent(this::setRelationship);
        }
        else {
            renderedValue = value;
        }
        return super.getTreeCellRendererComponent(tree, renderedValue, selected, expanded, leaf, row, hasFocus);
    }
}
