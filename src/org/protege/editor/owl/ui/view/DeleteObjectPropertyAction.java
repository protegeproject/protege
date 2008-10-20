package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteObjectPropertyAction extends AbstractDeleteEntityAction<OWLObjectProperty> {


    public DeleteObjectPropertyAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLObjectProperty> propSetProvider) {
        super("Delete selected properties",
              OWLIcons.getIcon("property.object.delete.png"),
              owlEditorKit,
              owlEditorKit.getModelManager().getOWLObjectPropertyHierarchyProvider(),
              propSetProvider);
    }


    protected String getPluralDescription() {
        return "properties";
    }
}
