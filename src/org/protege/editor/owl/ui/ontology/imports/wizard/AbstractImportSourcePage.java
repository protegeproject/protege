package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractImportSourcePage extends AbstractOWLWizardPanel {


    public AbstractImportSourcePage(Object id, String title, OWLEditorKit owlEditorKit) {
        super(id, title, owlEditorKit);
    }


    final public void aboutToHidePanel() {
        ImportVerifier importVerifier = getImportVerifier();
        ((OntologyImportWizard) getWizard()).setImportVerifier(importVerifier);
    }


    public abstract ImportVerifier getImportVerifier();
}
