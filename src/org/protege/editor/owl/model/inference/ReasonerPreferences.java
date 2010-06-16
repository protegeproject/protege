package org.protege.editor.owl.model.inference;

import java.util.EnumMap;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class ReasonerPreferences {
    public static final String PREFERENCES_SET_KEY = "INFERENCE_PREFS_SET";

    public static final String SHOW_INFERENCES_KEY = "SHOW_INFERENCES";
    
    private long startOperationTime;
    
	private boolean showInferences;
	
	private EnumMap<OptionalInferenceTask, Boolean>  enabledMap    = new EnumMap<OptionalInferenceTask, Boolean>(OptionalInferenceTask.class);
	private EnumMap<OptionalInferenceTask, Integer> clockMap       = new EnumMap<OptionalInferenceTask, Integer>(OptionalInferenceTask.class);
	private EnumMap<OptionalInferenceTask, Integer> countMap       = new EnumMap<OptionalInferenceTask, Integer>(OptionalInferenceTask.class);

	
	public enum OptionalInferenceTask {
	    // Class Property Inferences
	    SHOW_CLASS_UNSATISFIABILITY(true),
	    SHOW_INFERRED_EQUIVALENT_CLASSES(true),
	    SHOW_INFERRED_SUPER_CLASSES(true),
	    SHOW_INFERED_CLASS_MEMBERS(true),
	    SHOW_INFERRED_INHERITED_ANONYMOUS_CLASSES(false),
	    
	    // Object Property Inferences
	    SHOW_OBJECT_PROPERTY_UNSATISFIABILITY(true),
	    SHOW_INFERRED_OBJECT_PROPERTY_DOMAINS(false),
	    SHOW_INFERRED_OBJECT_PROPERTY_RANGES(false),
	    SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES(true),
	    SHOW_INFERRED_SUPER_OBJECT_PROPERTIES(true),
	    SHOW_INFERRED_INVERSE_PROPERTIES(true),
	    
	    // Datatype Property Inferences
	    SHOW_INFERRED_DATATYPE_PROPERTY_DOMAINS(false),
	    SHOW_INFERRED_EQUIVALENT_DATATYPE_PROPERTIES(true),
	    SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES(true),
	    
	    // Individual Inferences
	    SHOW_INFERRED_TYPES(true),
	    SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS(true),
	    SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS(false)
	    ;
	    
	    private boolean enabledByDefault;
	    
	    private OptionalInferenceTask(boolean enabledByDefault) {
	        this.enabledByDefault = enabledByDefault;
	    }
	    
	    public String getKey() {
            return toString();
        }
	    
	    public boolean getEnabledByDefault() {
	        return enabledByDefault;
	    }
	}
	
	public void load() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        showInferences = prefs.getBoolean(SHOW_INFERENCES_KEY, true);
        for (OptionalInferenceTask task : OptionalInferenceTask.values()) {
            enabledMap.put(task, prefs.getBoolean(task.getKey(), task.getEnabledByDefault()));
        }
	}
	
	public void save() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        prefs.putBoolean(SHOW_INFERENCES_KEY, showInferences);
        for (OptionalInferenceTask task : OptionalInferenceTask.values()) {
            prefs.putBoolean(task.getKey(), enabledMap.get(task));
        }
	}

	public boolean isShowInferences() {
		return showInferences;
	}

	public void setShowInferences(boolean showInferences) {
		this.showInferences = showInferences;
	}
	
	public void startClock(OptionalInferenceTask task) {
	    startOperationTime = System.currentTimeMillis();
	}
	
	public void stopClock(OptionalInferenceTask task) {
	    int duration = (int) (System.currentTimeMillis() - startOperationTime);
	    
	    countMap.put(task, getCallCount(task) + 1);
	    clockMap.put(task, getTimeInTask(task) + duration);
	}
	
	public int getTimeInTask(OptionalInferenceTask task) {
	    Integer duration = clockMap.get(task);
	    if (duration == null) {
	        duration = 0;
	    }
	    return duration;
	}

	public int getAverageTimeInTask(OptionalInferenceTask task) {
	    int count = getCallCount(task);
	    if (count == 0) {
	        return 0;
	    }
	    return getTimeInTask(task) / count;
	}
	
	private int getCallCount(OptionalInferenceTask task) {
	    Integer count = countMap.get(task);
	    if (count == null) {
	        count = 0;
	    }
	    return count;
	}
	
	public boolean isEnabled(OptionalInferenceTask task) {
	    return enabledMap.get(task);
	}
	
	public void setEnabled(OptionalInferenceTask task, boolean enabled) {
	    enabledMap.put(task, enabled);
	}
	
	public void executeTask(OptionalInferenceTask task, Runnable implementation)  {
	    if (isShowInferences() && isEnabled(task)) {
	        startClock(task);
	        try {
	            implementation.run();
	        }
	        catch (Throwable t) { // don't let exceptions spoil your day
	            ProtegeApplication.getErrorLog().logError(t);
	        }
	        stopClock(task);
	    }
	}


}
