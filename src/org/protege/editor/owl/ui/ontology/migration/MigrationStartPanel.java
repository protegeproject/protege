package org.protege.editor.owl.ui.ontology.migration;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @deprecated
 */
public class MigrationStartPanel extends AbstractOWLWizardPanel {

    public static final String ID = MigrationStartPanel.class.getName();


    public MigrationStartPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Ontology migration", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("This wizard will migrate the classes, properties and individuals that " + "are contained in the active ontology (" + getOWLModelManager().getActiveOntology() + ") to " + "a new ontology.  This operation is typically used to create a new version of an ontology. However, " + "the current version of the ontology is left untouched.\n" + "Press 'Contine' to perform the migration.");
    }
}
