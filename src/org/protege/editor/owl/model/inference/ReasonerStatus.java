package org.protege.editor.owl.model.inference;

public enum ReasonerStatus {
	NO_REASONER_FACTORY_CHOSEN("No Reasoner set. Select a reasoner from the Reasoner menu",
                               false, "Choose a reasoner from the reasoner menu below.",
                               false, "Choose a reasoner from the reasoner menu below."),

    REASONER_NOT_INITIALIZED("To use the reasoner click Reasoner->Start Reasoner",
                             true, "<html>Starts a new reasoner and initializes a cache of reasoning results<br>" +
                                   "including the inferred class hierarchy and the inferred types of</br><br>" +
                                   "of individuals.</br></html>",
                             false, "No reasoner running. Nothing to synchronize."),

	INITIALIZATION_IN_PROGRESS("Reasoner Initialization in Progress",
                               false, "Waiting for reasoner to initialize.  No reasoner actions enabled.",
                               false, "Waiting for reasoner to initialize.  No reasoner actions enabled."),
                               
	INITIALIZED("Reasoner active",
                false, "Reasoner already running.  Re-initialization not required",
                false, "Reasoner already running and synchronized with the ontology.  Synchronization not required"),

	OUT_OF_SYNC("Reasoner state out of sync with active ontology",
                false, "<html>Reasoner already running and does not need to be initialized.<br>" +
                       "However the reasoner has not taken recent changes to the ontology</br><br>" +
                       "into account so synchronization of the reasoner (menu item below)</br><br>" +
                       "is suggested.</br></html>",
                true, "<html>The current reasoner is active but has not taken into account the recent<br>" +
                      "changes to the ontology.  In this mode, reasoning results may be inaccurate.</br><br>" +
                      "Pushing this button will resynchronize the reasoner with the ontology leading</br><br>" +
                      "to inferences that are once again accurate.</br></html>");
	
    private String description;
    private boolean enableInitialization;
    private String initializationTooltip;
    private boolean enableSynchronization;
    private String synchronizationTooltip;

	private ReasonerStatus(String description, 
						   boolean enableInitialization, String initializationTooltip,
						   boolean enableSynchronization, String synchronizationTooltip) {
		this.description = description;
        this.enableInitialization = enableInitialization;
        this.initializationTooltip = initializationTooltip;
        this.enableSynchronization = enableSynchronization;
        this.synchronizationTooltip = synchronizationTooltip;
	}

	public String getDescription() {
		return description;
	}

	public boolean isEnableInitialization() {
		return enableInitialization;
	}

	public String getInitializationTooltip() {
		return initializationTooltip;
	}

	public boolean isEnableSynchronization() {
		return enableSynchronization;
	}

	public String getSynchronizationTooltip() {
		return synchronizationTooltip;
	}
	
}