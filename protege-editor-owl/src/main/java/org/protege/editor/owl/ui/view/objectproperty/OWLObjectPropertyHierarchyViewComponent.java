package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.renderer.*;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLObjectPropertyHierarchyViewComponent
        extends AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty>
        implements SelectionDriver {

    @Override
    protected void performExtraInitialisation() throws Exception {
        super.performExtraInitialisation();
        getAssertedTree().setPopupMenuId(new PopupMenuId("[AssertedObjectPropertyHierarchy]"));
    }

    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider();
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLObjectProperty>> getInferredHierarchyProvider() {
        return Optional.of(getOWLModelManager().getOWLHierarchyManager().getInferredOWLObjectPropertyHierarchyProvider());
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
        return new AddChildIcon(new OWLObjectPropertyIcon());
    }


    protected Icon getSibIcon() {
        return new AddSiblingIcon(new OWLObjectPropertyIcon());
    }


    protected Icon getDeleteIcon() {
        return new DeleteEntityIcon(new OWLObjectPropertyIcon(OWLEntityIcon.FillType.HOLLOW));
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public Optional<OWLObject> getSelection() {
        return Optional.ofNullable(getSelectedEntity());
    }
}
