package org.protege.editor.owl.ui;

import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-Mar-2007<br><br>
 */
public class OWLEntitiesTab extends OWLWorkspaceViewsTab {

    private OWLEntityDisplayProvider provider = new OWLEntityDisplayProvider() {
        public boolean canDisplay(OWLEntity owlEntity) {
            return true;
        }


        public JComponent getDisplayComponent() {
            return OWLEntitiesTab.this;
        }
    };


    public void initialise() {
        super.initialise();
        getOWLEditorKit().getWorkspace().registerOWLEntityDisplayProvider(provider);
    }


    public void dispose() {
        super.dispose();
        getOWLEditorKit().getWorkspace().unregisterOWLEntityDisplayProvider(provider);
    }
}
