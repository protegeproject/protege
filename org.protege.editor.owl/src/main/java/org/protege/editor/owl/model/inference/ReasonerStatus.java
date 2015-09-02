package org.protege.editor.owl.model.inference;

public enum ReasonerStatus {
	NO_REASONER_FACTORY_CHOSEN("No Reasoner set. Select a reasoner from the Reasoner menu",
			"Choose a reasoner from the reasoner menu below.", "Choose a reasoner from the reasoner menu below.") {
		@Override
		public boolean isEnableInitialization() {
			return false;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return false;
		}
		
		@Override
		public boolean isEnableStop() {
			return false;
		}
	},

	REASONER_NOT_INITIALIZED("To use the reasoner click Reasoner > Start reasoner",
			"<html>Starts a new reasoner and initializes a cache of reasoning results<br>" +
					"including the inferred class hierarchy and the inferred types of</br><br>" +
					"of individuals.</br></html>", "No reasoner running. Nothing to synchronize.") {
		@Override
		public boolean isEnableInitialization() {
			return true;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return false;
		}
		
		@Override
		public boolean isEnableStop() {
			return false;
		}
	},

	INITIALIZATION_IN_PROGRESS("Reasoner Initialization in Progress",
			"Waiting for reasoner to initialize.  No reasoner actions enabled.", "Waiting for reasoner to initialize.  No reasoner actions enabled.") {
		@Override
		public boolean isEnableInitialization() {
			return false;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return false;
		}
		
		@Override
		public boolean isEnableStop() {
			return false;
		}
	},

	INITIALIZED("Reasoner active",
			"Reasoner already running.  Re-initialization not required", "Reasoner already running and synchronized with the ontology.  Synchronization not required") {
		@Override
		public boolean isEnableInitialization() {
			return false;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return false;
		}
		
		@Override
		public boolean isEnableStop() {
			return true;
		}
	},

	INCONSISTENT("Reasoner active but the ontology is inconsistent",
			"Reasoner already running.  Re-initialization not required", "Reasoner already running and synchronized with the ontology.  Synchronization not required") {
		@Override
		public boolean isEnableInitialization() {
			return false;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return false;
		}
		
		@Override
		public boolean isEnableStop() {
			return true;
		}
	},

	OUT_OF_SYNC("Reasoner state out of sync with active ontology",
			"<html>Reasoner already running and does not need to be initialized.<br>" +
					"However the reasoner has not taken recent changes to the ontology</br><br>" +
					"into account so synchronization of the reasoner (menu item below)</br><br>" +
					"is suggested.</br></html>", "<html>The current reasoner is active but has not taken into account the recent<br>" +
					"changes to the ontology.  In this mode, reasoning results may be inaccurate.</br><br>" +
					"Pushing this button will resynchronize the reasoner with the ontology leading</br><br>" +
					"to inferences that are once again accurate.</br></html>") {
		@Override
		public boolean isEnableInitialization() {
			return false;
		}
		
		@Override
		public boolean isEnableSynchronization() {
			return true;
		}
		
		@Override
		public boolean isEnableStop() {
			return true;
		}
	};
	
    private String description;
    private String initializationTooltip;
    private String synchronizationTooltip;

	private ReasonerStatus(String description, 
						   String initializationTooltip, String synchronizationTooltip) {
		this.description = description;
        this.initializationTooltip = initializationTooltip;
        this.synchronizationTooltip = synchronizationTooltip;
	}

	public String getDescription() {
		return description;
	}

	public abstract boolean isEnableInitialization();

	public String getInitializationTooltip() {
		return initializationTooltip;
	}

	public abstract boolean isEnableSynchronization();

	public String getSynchronizationTooltip() {
		return synchronizationTooltip;
	}
	
	public abstract boolean isEnableStop();
	
}