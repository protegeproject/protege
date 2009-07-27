package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLObjectPropertyHierarchyViewComponent extends AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty> {

    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider();
    }


    protected OWLSubPropertyAxiom getSubPropertyAxiom(OWLObjectProperty child, OWLObjectProperty parent) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(child, parent);
    }


    protected boolean canAcceptDrop(Object child, Object parent) {
        return child instanceof OWLObjectProperty;
    }


    protected OWLEntityCreationSet<OWLObjectProperty> createProperty() {
        return getOWLWorkspace().createOWLObjectProperty();
    }


    protected Icon getSubIcon() {
        return OWLIcons.getIcon("property.object.addsub.png");
    }


    protected Icon getSibIcon() {
        return OWLIcons.getIcon("property.object.addsib.png");
    }


    protected Icon getDeleteIcon() {
        return OWLIcons.getIcon("property.object.delete.png");
    }
}
