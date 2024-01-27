package org.protege.editor.owl.model.hierarchy;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Aug 2017
 */
public class DisplayRelationshipsInClassHierarchyAction extends ProtegeOWLRadioButtonAction {

    @Override
    protected void update() {
        setSelected(ClassHierarchyPreferences.get().isDisplayRelationships());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean displayRelationships = isSelected();
        ClassHierarchyPreferences.get().setDisplayRelationships(displayRelationships);
        setDisplayedRelationships();
        getOWLWorkspace().refreshComponents();
    }

    private void setDisplayedRelationships() {
        boolean displayRelationships = ClassHierarchyPreferences.get().isDisplayRelationships();
        AssertedClassHierarchyProvider provider = (AssertedClassHierarchyProvider) getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
        Set<OWLObjectProperty> displayedProperties;
        if (displayRelationships) {
            displayedProperties = getOWLModelManager().getActiveOntology()
                                                                                     .getObjectPropertiesInSignature(
                                                                                             Imports.INCLUDED);
        }
        else {
            displayedProperties = Collections.emptySet();
        }
        provider.setDisplayedRelationships(displayedProperties);
    }

    @Override
    public void dispose() throws Exception {

    }

    @Override
    public void initialise() throws Exception {
        update();
        setDisplayedRelationships();
    }
}
