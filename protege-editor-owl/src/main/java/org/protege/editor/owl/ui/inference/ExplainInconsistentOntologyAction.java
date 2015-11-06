package org.protege.editor.owl.ui.inference;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyManager;

import java.awt.event.ActionEvent;

public class ExplainInconsistentOntologyAction extends ProtegeOWLAction {
	private static final long serialVersionUID = -3932851787552935976L;

	public void initialise() throws Exception {

	}
	
	public void actionPerformed(ActionEvent e) {
		InconsistentOntologyManager.get(getOWLModelManager()).explain();
	}
	
	public void dispose() throws Exception {

	}
}
