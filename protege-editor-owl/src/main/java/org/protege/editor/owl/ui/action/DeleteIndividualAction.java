package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.util.OWLEntityDeleter;
import org.protege.editor.owl.ui.renderer.DeleteEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLIndividualIcon;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteIndividualAction extends OWLSelectionViewAction {

    private OWLEditorKit owlEditorKit;

    private OWLEntitySetProvider<OWLNamedIndividual> indSetProvider;


    public DeleteIndividualAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLNamedIndividual> indSetProvider) {
        super("Delete individual(s)", new DeleteEntityIcon(new OWLIndividualIcon(OWLEntityIcon.FillType.HOLLOW)));
        this.owlEditorKit = owlEditorKit;
        this.indSetProvider = indSetProvider;
    }


    public void updateState() {
        setEnabled(!indSetProvider.getEntities().isEmpty());
    }


    public void dispose() {
    }


    public void actionPerformed(ActionEvent e) {
        OWLEntityDeleter.deleteEntities(indSetProvider.getEntities(), owlEditorKit.getOWLModelManager());
    }
}
