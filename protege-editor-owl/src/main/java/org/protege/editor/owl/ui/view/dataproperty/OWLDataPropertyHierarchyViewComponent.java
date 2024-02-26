package org.protege.editor.owl.ui.view.dataproperty;

import java.awt.Component;
import java.util.Optional;

import javax.swing.Icon;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.renderer.AddChildIcon;
import org.protege.editor.owl.ui.renderer.AddSiblingIcon;
import org.protege.editor.owl.ui.renderer.DeleteEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLDataPropertyIcon;
import org.protege.editor.owl.ui.renderer.OWLEntityIcon;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyViewComponent
        extends AbstractOWLPropertyHierarchyViewComponent<OWLDataProperty>
        implements SelectionDriver {



    protected boolean isOWLDataPropertyView() {
        return true;
    }

    @Override
    protected void performExtraInitialisation() throws Exception {
        super.performExtraInitialisation();
        getAssertedTree().setPopupMenuId(new PopupMenuId("[AssertedDataPropertyHierarchy]"));
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
        return new AddChildIcon(new OWLDataPropertyIcon());
    }


    protected Icon getSibIcon() {
        return new AddSiblingIcon(new OWLDataPropertyIcon());
    }


    protected Icon getDeleteIcon() {
        return new DeleteEntityIcon(new OWLDataPropertyIcon(OWLEntityIcon.FillType.HOLLOW));
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
