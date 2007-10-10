package org.protege.editor.owl.ui.view;

import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteObjectPropertyAction extends AbstractDeleteEntityAction<OWLObjectProperty> {

    private OWLModelManagerTree<OWLObjectProperty> tree;


    public DeleteObjectPropertyAction(OWLEditorKit owlEditorKit, OWLModelManagerTree<OWLObjectProperty> tree) {
        super("Delete selected properties", OWLIcons.getIcon("property.object.delete.png"), owlEditorKit);
        this.tree = tree;
    }


    protected Set<OWLObjectProperty> getSelectedEntities() {
        return new HashSet<OWLObjectProperty>(tree.getSelectedOWLObjects());
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        return getOWLEditorKit().getOWLModelManager().getOWLObjectPropertyHierarchyProvider();
    }


    protected String getPluralDescription() {
        return "properties";
    }
}
