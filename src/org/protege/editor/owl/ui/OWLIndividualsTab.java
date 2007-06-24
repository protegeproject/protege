package org.protege.editor.owl.ui;

import javax.swing.JComponent;

import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIndividualsTab extends OWLWorkspaceViewsTab {

    private OWLEntityDisplayProvider provider = new OWLEntityDisplayProvider() {
        public boolean canDisplay(OWLEntity owlEntity) {
            return owlEntity instanceof OWLIndividual;
        }


        public JComponent getDisplayComponent() {
            return OWLIndividualsTab.this;
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
