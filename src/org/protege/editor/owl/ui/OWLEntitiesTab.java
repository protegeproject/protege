package org.protege.editor.owl.ui;

import javax.swing.JComponent;

import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.semanticweb.owl.model.OWLEntity;


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
        getOWLEditorKit().getOWLWorkspace().registerOWLEntityDisplayProvider(provider);
    }


    public void dispose() {
        super.dispose();
        getOWLEditorKit().getOWLWorkspace().unregisterOWLEntityDisplayProvider(provider);
    }
}
