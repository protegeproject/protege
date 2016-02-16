package org.protege.editor.owl.model.inference;

import org.protege.editor.core.prefs.Preferences;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.util.*;

public class PrecomputedInferencePreferences {
    private Set<InferenceType>             required   = EnumSet.noneOf(InferenceType.class);
    private Set<InferenceType>             disallowed = EnumSet.noneOf(InferenceType.class);
    private Map<InferenceType,Set<String>> requested  = new EnumMap<>(InferenceType.class);

    public Set<InferenceType> getPrecomputedInferences() {
    	Set<InferenceType> precompute = EnumSet.noneOf(InferenceType.class);
    	for (InferenceType type : InferenceType.values()) {
    		Set<String> requestors = requested.get(type);
    		if (required.contains(type) || (requestors != null && !requestors.isEmpty())) {
    			precompute.add(type);
    		}
    	}
    	precompute.removeAll(disallowed);
        return precompute;
    }
    
    public void requestPrecomputedInferences(String requestor, Set<InferenceType> types) {
    	for (InferenceType type : InferenceType.values()) {
    		Set<String> requestors = requested.get(type);
    		if (types.contains(type)) {
    			if (requestors == null) {
    				requestors = new TreeSet<>();
    				requested.put(type, requestors);
    			}
    			requestors.add(requestor);
    		}
    		else if (requestors != null) {
    			requestors.remove(requestor);
    		}
    	}
    }
    
    public Set<String> getRequestors(InferenceType type) {
    	return requested.get(type);
    }
    
    public Set<InferenceType> getRequired() {
		return Collections.unmodifiableSet(required);
	}

	public void setRequired(EnumSet<InferenceType> required) {
		this.required = EnumSet.copyOf(required);
	}
	
	public void setRequired(InferenceType type, boolean isRequired) {
		if (isRequired) {
			required.add(type);
		}
		else {
			required.remove(type);
		}
	}

	public Set<InferenceType> getDisallowed() {
		return disallowed;
	}

	public void setDisallowed(InferenceType type,  boolean isDisallowed) {
		if (isDisallowed) {
			disallowed.add(type);
		}
		else {
			disallowed.remove(type);
		}
	}

	public void load(Preferences prefs) {
        required.clear();
        disallowed.clear();
        for (InferenceType type : InferenceType.values()) {
            if (prefs.getBoolean(getRequiredInferenceName(type), false)) {
                required.add(type);
            }
            if (prefs.getBoolean(getDisallowedInferenceName(type), false)) {
                disallowed.add(type);
            }
        }
    }
    
    public void save(Preferences prefs) {
        for (InferenceType type : InferenceType.values()) {
            prefs.putBoolean(getRequiredInferenceName(type), required.contains(type));
            prefs.putBoolean(getDisallowedInferenceName(type), disallowed.contains(type));
        }
    }

    private String getRequiredInferenceName(InferenceType type) {
        return "Require_" + type.toString();
    }
    
    private String getDisallowedInferenceName(InferenceType type) {
        return "Disallow_" + type.toString();
    }
}
