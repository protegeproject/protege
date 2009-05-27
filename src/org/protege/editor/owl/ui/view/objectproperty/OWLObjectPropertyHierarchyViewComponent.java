package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.protege.editor.owl.ui.view.DeleteObjectPropertyAction;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
