package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.ClassHierarchyPreferences;

/**
 * Author: Damien Goutte-Gattat<br>
 * German BioImaging e.V.<br>
 * Date: Feb 28, 2026<br>
 * 
 * damien@gerbi-gmb.de
 */
public class DisplayHierarchyFromOntologyRootsAction extends ProtegeOWLRadioButtonAction {

    private static final long serialVersionUID = 7026387619377969353L;

    @Override
    public void actionPerformed(ActionEvent e) {
        ClassHierarchyPreferences.get().setDisplayFromOntologyRoots(isSelected());
        setDisplayFromOntologyRoots();
        getOWLWorkspace().refreshComponents();
    }

    @Override
    public void initialise() {
        update();
        setDisplayFromOntologyRoots();
    }

    @Override
    public void dispose() throws Exception {
    }

    @Override
    protected void update() {
        setSelected(ClassHierarchyPreferences.get().isDisplayFromOntologyRoots());
    }

    private void setDisplayFromOntologyRoots() {
        boolean displayFromRoots = ClassHierarchyPreferences.get().isDisplayFromOntologyRoots();
        AssertedClassHierarchyProvider provider = (AssertedClassHierarchyProvider) getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
        provider.setDisplayFromOntologyRoots(displayFromRoots);
    }
}
