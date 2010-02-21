package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOWLWizardPanel extends AbstractWizardPanel {


    /**
     * 
     */
    private static final long serialVersionUID = 1936870456254240648L;


    public AbstractOWLWizardPanel(Object id, String title, OWLEditorKit owlEditorKit) {
        super(id, title, owlEditorKit);
    }


    public OWLModelManager getOWLModelManager() {
        return (OWLModelManager) getModelManager();
    }


    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }
}
