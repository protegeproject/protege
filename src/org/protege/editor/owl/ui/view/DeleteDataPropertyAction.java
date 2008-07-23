package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.OWLDataProperty;

import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteDataPropertyAction extends AbstractDeleteEntityAction<OWLDataProperty> {

    private OWLModelManagerTree<OWLDataProperty> tree;


    public DeleteDataPropertyAction(OWLEditorKit owlEditorKit, OWLModelManagerTree<OWLDataProperty> tree) {
        super("Delete selected properties", OWLIcons.getIcon("property.Data.delete.png"), owlEditorKit);
        this.tree = tree;
    }


    protected Set<OWLDataProperty> getSelectedEntities() {
        return new HashSet<OWLDataProperty>(tree.getSelectedOWLObjects());
    }


    protected OWLObjectHierarchyProvider<OWLDataProperty> getHierarchyProvider() {
        return getOWLEditorKit().getModelManager().getOWLDataPropertyHierarchyProvider();
    }


    protected String getPluralDescription() {
        return "properties";
    }
}

