package org.protege.editor.owl.model.inference;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class DisplayedInferencePreferences {
	
	public static final String USER_READABLE_ID = "Displayed Inference Preferences";
    public static final String SHOW_INFERENCES_KEY = "SHOW_INFERENCES";
    
	private long startOperationTime;

    private boolean showInferences;	
    private EnumSet<OptionalInferenceTask>           enabled       = EnumSet.noneOf(OptionalInferenceTask.class);
    private EnumMap<OptionalInferenceTask, Integer>  clockMap      = new EnumMap<>(OptionalInferenceTask.class);
    private EnumMap<OptionalInferenceTask, Integer>  countMap      = new EnumMap<>(OptionalInferenceTask.class);
 
    public void load(ReasonerPreferences p) {
    	Preferences prefs = ReasonerPreferences.getPreferences();
        showInferences = prefs.getBoolean(SHOW_INFERENCES_KEY, true);
        enabled.clear();
        for (OptionalInferenceTask task : OptionalInferenceTask.values()) {
        	if (prefs.getBoolean(task.getKey(), task.getEnabledByDefault())) {
        		enabled.add(task);
        	}
        }
        registerPrecomputedInferenceTypes(p);
    }
	
    public void save(ReasonerPreferences p) {
    	Preferences prefs = ReasonerPreferences.getPreferences();
        prefs.putBoolean(SHOW_INFERENCES_KEY, showInferences);
        for (OptionalInferenceTask task : OptionalInferenceTask.values()) {
            prefs.putBoolean(task.getKey(), enabled.contains(task));
        }
        registerPrecomputedInferenceTypes(p);
    }
    
    private void registerPrecomputedInferenceTypes(ReasonerPreferences p) {
    	Set<InferenceType> types = EnumSet.noneOf(InferenceType.class);
    	for (OptionalInferenceTask task : OptionalInferenceTask.values()) {
    		if (enabled.contains(task)) {
    			InferenceType type = task.getSuggestedInferenceType();
    			if (type != null) {
    				types.add(type);
    			}
    		}
    	}
    	p.requestPrecomputedInferences(USER_READABLE_ID, types);
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
        return enabled.contains(task);
    }

    public void setEnabled(OptionalInferenceTask task, boolean isEnabled) {
    	if (isEnabled) {
    		enabled.add(task);
    	}
    	else {
    		enabled.remove(task);
    	}
    }

    public void executeTask(OptionalInferenceTask task, Runnable implementation)  {
        if (isShowInferences() && isEnabled(task)) {
            startClock(task);
            try {
                implementation.run();                
            } catch (UnsupportedOperationException ignore) {
            	
            } finally {
            	stopClock(task);
            }
        }
    }
}

