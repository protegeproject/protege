package org.protege.editor.owl.ui.view.dataproperty;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import javax.swing.*;
import java.util.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyViewComponent extends AbstractOWLPropertyHierarchyViewComponent<OWLDataProperty> {

    /**
     * 
     */
    private static final long serialVersionUID = 6315136229629963526L;


    protected boolean isOWLDataPropertyView() {
        return true;
    }


    protected OWLObjectHierarchyProvider<OWLDataProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider();
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLDataProperty>> getInferredHierarchyProvider() {
        return Optional.of(getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider());
    }

    protected OWLSubPropertyAxiom getSubPropertyAxiom(OWLDataProperty child, OWLDataProperty parent) {
        return getOWLDataFactory().getOWLSubDataPropertyOfAxiom(child, parent);
    }


    protected boolean canAcceptDrop(Object child, Object parent) {
        return child instanceof OWLDataProperty;
    }


    protected OWLEntityCreationSet<OWLDataProperty> createProperty() {
        return getOWLWorkspace().createOWLDataProperty();
    }


    protected Icon getSubIcon() {
        return OWLIcons.getIcon("property.data.addsub.png");
    }


    protected Icon getSibIcon() {
        return OWLIcons.getIcon("property.data.addsib.png");
    }


    protected Icon getDeleteIcon() {
        return OWLIcons.getIcon("property.data.delete.png");
    }
}
