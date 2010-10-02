package org.protege.editor.owl.model.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.reasoner.InferenceType;

public class ReasonerPreferences {
    public enum OptionalInferenceTask {
	    // Class Property Inferences
	    SHOW_CLASS_UNSATISFIABILITY(true, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERRED_EQUIVALENT_CLASSES(true, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERRED_SUPER_CLASSES(true, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERED_CLASS_MEMBERS(true, InferenceType.CLASS_ASSERTIONS),
	    SHOW_INFERRED_INHERITED_ANONYMOUS_CLASSES(false, InferenceType.CLASS_HIERARCHY),
	
	    // Object Property Inferences
	    SHOW_OBJECT_PROPERTY_UNSATISFIABILITY(true, InferenceType.OBJECT_PROPERTY_HIERARCHY),
	    SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS(false, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERRED_OBJECT_PROPERTY_RANGES(false, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES(true, InferenceType.OBJECT_PROPERTY_HIERARCHY),
	    SHOW_INFERRED_SUPER_OBJECT_PROPERTIES(true, InferenceType.OBJECT_PROPERTY_HIERARCHY),
	    SHOW_INFERRED_INVERSE_PROPERTIES(true, InferenceType.OBJECT_PROPERTY_HIERARCHY),
	
	    // Datatype Property Inferences
	    SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS(false, InferenceType.CLASS_HIERARCHY),
	    SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES(true, InferenceType.DATA_PROPERTY_HIERARCHY),
	    SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES(true, InferenceType.DATA_PROPERTY_HIERARCHY),
	
	    // Individual Inferences
	    SHOW_INFERRED_TYPES(true, InferenceType.CLASS_ASSERTIONS),
	    SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS(true, InferenceType.OBJECT_PROPERTY_ASSERTIONS),
	    SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS(false, InferenceType.DATA_PROPERTY_ASSERTIONS)
	    ;
	
	    private boolean enabledByDefault;
	    private InferenceType suggestedInferenceType;
	
	    private OptionalInferenceTask(boolean enabledByDefault, InferenceType suggestedInferenceType) {
	        this.enabledByDefault = enabledByDefault;
	        this.suggestedInferenceType = suggestedInferenceType;
	    }
	
	    public String getKey() {
	        return toString();
	    }
	
	    public boolean getEnabledByDefault() {
	        return enabledByDefault;
	    }
	    
	    public InferenceType getSuggestedInferenceType() {
	    	return suggestedInferenceType;
	    }
	}

	public static final String PREFERENCES_SET_KEY = "INFERENCE_PREFS_SET";
    public static final String DEFAULT_REASONER_ID = "DEFAULT_REASONER_ID";
    
    /* package */ static Preferences getPreferences() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        return prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
    }
    
    private DisplayedInferencePreferences displayed = new DisplayedInferencePreferences();
    private PrecomputedInferencePreferences precompute = new PrecomputedInferencePreferences();
    private String defaultReasonerId;
    private List<ReasonerPreferencesListener> listeners = new ArrayList<ReasonerPreferencesListener>();


    public String getDefaultReasonerId() {
        return defaultReasonerId;
    }

    public void setDefaultReasonerId(String defaultReasonerId) {
        this.defaultReasonerId = defaultReasonerId;
    }

    public void load() {
        Preferences prefs = getPreferences();                                                                                                                        

    	displayed.load(this);
    	precompute.load(prefs);
    	
    	defaultReasonerId = prefs.getString(DEFAULT_REASONER_ID, NoOpReasonerInfo.NULL_REASONER_ID);
    }

    public void save() {
    	Preferences prefs = getPreferences();
    	
    	displayed.save(this);
    	precompute.save(prefs);
    	
        prefs.putString(DEFAULT_REASONER_ID, defaultReasonerId);
    }
    
    public void addListener(ReasonerPreferencesListener listener) {
    	listeners.add(listener);
    }
    
    public void removeListener(ReasonerPreferencesListener listener) {
    	listeners.remove(listener);
    }
    
    public void fireChanged() {
    	for (ReasonerPreferencesListener listener : listeners) {
    		listener.preferencesChanged();
    	}
    }
    
    
    
    public Set<InferenceType> getPrecomputedInferences() {
    	return precompute.getPrecomputedInferences();
    }
    
    public void requestPrecomputedInferences(String requestor, Set<InferenceType> types) {
		precompute.requestPrecomputedInferences(requestor, types);
		fireChanged();
	}

	public Set<String> getRequestors(InferenceType type) {
		return precompute.getRequestors(type);
	}

	public Set<InferenceType> getRequired() {
		return precompute.getRequired();
	}

	public void setRequired(InferenceType type, boolean isRequired) {
		precompute.setRequired(type, isRequired);
		fireChanged();
	}

	public Set<InferenceType> getDisallowed() {
		return precompute.getDisallowed();
	}

	public void setDisallowed(InferenceType type, boolean isDisallowed) {
		precompute.setDisallowed(type, isDisallowed);
		fireChanged();
	}

	public boolean isShowInferences() {
        return displayed.isShowInferences();
    }

    public void setShowInferences(boolean showInferences) {
        displayed.setShowInferences(showInferences);
		fireChanged();
    }
    
    public void startClock(OptionalInferenceTask task) {
    	displayed.startClock(task);
    }

    public void stopClock(OptionalInferenceTask task) {
    	displayed.stopClock(task);
    }

    public int getTimeInTask(OptionalInferenceTask task) {
    	return displayed.getTimeInTask(task);
    }

    public int getAverageTimeInTask(OptionalInferenceTask task) {
    	return displayed.getAverageTimeInTask(task);
    }

    public boolean isEnabled(OptionalInferenceTask task) {
    	return displayed.isEnabled(task);
    }

    public void setEnabled(OptionalInferenceTask task, boolean enabled) {
    	displayed.setEnabled(task, enabled);
		fireChanged();
    }

    public void executeTask(OptionalInferenceTask task, Runnable implementation)  {
    	displayed.executeTask(task, implementation);
    }


}
