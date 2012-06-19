package org.protege.editor.owl.ui.inference;

import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;

public abstract class InterruptedReasonerListener implements OWLModelManagerListener {
	private OWLEditorKit owlEditorKit;
    private boolean cancelled = false;
    
    public InterruptedReasonerListener(OWLEditorKit owlEditorKit) {
    	this.owlEditorKit = owlEditorKit;
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.REASONER_INTERRUPTED)) {
			cancelled = true;
		}
		else if (event.isType(EventType.REASONER_CHANGED) && cancelled) {
			cancelled = false;
			onInterrupt();
			handleInterrupted();
		}
	}
	
	protected abstract void onInterrupt();
	
	
	private void handleInterrupted() {
		String message;
		boolean showDialog = true;
		OWLReasonerManager reasonerManager = owlEditorKit.getOWLModelManager().getOWLReasonerManager();
		ReasonerStatus status = reasonerManager.getReasonerStatus();
		switch (status) {
		case NO_REASONER_FACTORY_CHOSEN:
		case INITIALIZATION_IN_PROGRESS:
			message = "The reasoner is in a strange state.";
			break;
		case INITIALIZED:
			message = "The reasoner is active but may not be fully optimized";
			break;
		case INCONSISTENT:
			message = null;
			showDialog = false;  // this case will take care of itself.
			break;
		case OUT_OF_SYNC:
			message = "Reasoner is still not synchronized with the latest changes";
			break;
		case REASONER_NOT_INITIALIZED:
			message = "Reasoner initialization failed";
			break;
		default:
			throw new IllegalStateException("Programmer Error");
		}
		if (showDialog) {
			JOptionPane.showMessageDialog(owlEditorKit.getOWLWorkspace(),
					message,
					"Reasoning Task Cancelled",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void reset() {
		cancelled = false;
	}

}
