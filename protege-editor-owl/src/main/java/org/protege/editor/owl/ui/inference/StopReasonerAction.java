package org.protege.editor.owl.ui.inference;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import java.awt.event.ActionEvent;

public class StopReasonerAction extends ProtegeOWLAction {
	private static final long serialVersionUID = 3727652674495540747L;

	@Override
	public void initialise() throws Exception {
		// is not called!
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
		reasonerManager.killCurrentReasoner();
		getOWLModelManager().fireEvent(EventType.REASONER_CHANGED);
	}

	@Override
	public void dispose() throws Exception {

	}

}
