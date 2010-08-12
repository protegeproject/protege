package org.protege.editor.owl.ui.prefix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergedPrefixMapperManager implements PrefixMapperManager {
    
    private OWLModelManager modelManager;
    private PrefixMapper mapper;
    boolean ignoreExtraDefaultPrefixes = true;


    public MergedPrefixMapperManager(OWLModelManager modelManager) {
    	this.modelManager = modelManager;
        mapper = new PrefixMapperImpl();
        reload();
    }


    public void setIgnoreExtraDefaultPrefixes(boolean ignoreExtraDefaultPrefixes) {
		this.ignoreExtraDefaultPrefixes = ignoreExtraDefaultPrefixes;
	}
    
    public boolean getIgnoreExtraDefaultPrefixes() {
		return ignoreExtraDefaultPrefixes;
	}
    
    public void reload() {
    	Map<OWLOntology, String> defaultPrefixMap = reloadAllButDefaultPrefixes();
    	handleDefaultPrefixes(defaultPrefixMap);
    }
    
    private Map<OWLOntology, String> reloadAllButDefaultPrefixes() {
		OWLOntologyManager manager = modelManager.getOWLOntologyManager();
		Map<OWLOntology, String> defaultPrefixMap = new  HashMap<OWLOntology, String>();
		for (OWLOntology ontology :  modelManager.getOntologies()) {
			OWLOntologyFormat format = manager.getOntologyFormat(ontology);
			if (format != null && format instanceof PrefixOWLOntologyFormat) {
				PrefixOWLOntologyFormat prefixes = (PrefixOWLOntologyFormat) format;
				for (String name : prefixes.getPrefixNames()) {
					if (name.equals("") || name.equals(":")) {
						defaultPrefixMap.put(ontology, prefixes.getPrefix(name));
					}
					else {
						mapper.addPrefixMapping(name, prefixes.getPrefix(name));
					}
				}
			}
		}

		return defaultPrefixMap;
    }

    private void handleDefaultPrefixes(Map<OWLOntology, String> defaultPrefixMap) {
		if (defaultPrefixMap.size() <= 1) {
			for (String value : defaultPrefixMap.values()) {
				mapper.addPrefixMapping(":", value);
			}
		}
		else if (!ignoreExtraDefaultPrefixes) {
			Set<OWLOntology> imports = new HashSet<OWLOntology>();
			for (OWLOntology ontology : modelManager.getOntologies()) {
				imports.addAll(ontology.getDirectImports());
			}
			OWLOntology notImported = null;
			for (OWLOntology ontology : defaultPrefixMap.keySet()) {
				if (!imports.contains(ontology)) {
					notImported = ontology;
				}
			}
			if (notImported != null) {
				mapper.addPrefixMapping("", defaultPrefixMap.get(notImported));
				defaultPrefixMap.remove(notImported);
			}
			int counter = 0;
			for (String value : defaultPrefixMap.values()) {
				String prefix;
				do {
					prefix = "p" + (counter++); 
				}
				while (mapper.getPrefix(prefix) != null);
				mapper.addPrefixMapping(prefix, value);
			}
		}
    }
    

    public void save() {
    	throw new UnsupportedOperationException("Can't save merged prefix mappings");
    }


    public PrefixMapper getMapper() {
        return mapper;
    }


    public void setPrefixes(Map<String, String> prefixValueMap) {
    	throw new UnsupportedOperationException("Can't modify merged prefix mappings");
    }


    public Map<String, String> getPrefixes() {
        return mapper.getPrefixMap();
    }
}
