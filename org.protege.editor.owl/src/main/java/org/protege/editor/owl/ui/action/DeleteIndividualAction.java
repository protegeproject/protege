package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.OWLEntityRemover;
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
        super("Delete individual(s)", OWLIcons.getIcon("individual.delete.png"));
        this.owlEditorKit = owlEditorKit;
        this.indSetProvider = indSetProvider;
    }


    @Override
    public void updateState() {
        setEnabled(!indSetProvider.getEntities().isEmpty());
    }


    @Override
    public void dispose() {
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        OWLEntityRemover remover = new OWLEntityRemover(owlEditorKit
                .getModelManager().getOntologies());
        for (OWLNamedIndividual ind : indSetProvider.getEntities()) {
            ind.accept(remover);
        }
        owlEditorKit.getModelManager().applyChanges(remover.getChanges());
    }
}
