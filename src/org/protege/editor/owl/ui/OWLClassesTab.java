package org.protege.editor.owl.ui;

import javax.swing.JComponent;

import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassesTab extends OWLWorkspaceViewsTab {

    String loc = "A";

    private OWLEntityDisplayProvider provider = new OWLEntityDisplayProvider() {
        public boolean canDisplay(OWLEntity owlEntity) {
            if (owlEntity instanceof OWLClass) {
                return true;
            }
            else {
                return false;
            }
        }


        public JComponent getDisplayComponent() {
            return OWLClassesTab.this;
        }
    };


    public void initialise() {
        super.initialise();
        getOWLEditorKit().getOWLWorkspace().registerOWLEntityDisplayProvider(provider);
    }


    public void dispose() {
        getOWLEditorKit().getOWLWorkspace().unregisterOWLEntityDisplayProvider(provider);
        super.dispose();
    }
}
