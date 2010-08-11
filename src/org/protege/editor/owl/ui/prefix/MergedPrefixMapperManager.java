package org.protege.editor.owl.ui.prefix;

import java.util.Map;

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

    private static final Logger logger = Logger.getLogger(MergedPrefixMapperManager.class);
    
    private OWLModelManager modelManager;
    private PrefixMapper mapper;


    public MergedPrefixMapperManager(OWLModelManager modelManager) {
    	this.modelManager = modelManager;
        mapper = new PrefixMapperImpl();
        reload();
    }


    public void reload() {
		OWLOntologyManager manager = modelManager.getOWLOntologyManager();
		for (OWLOntology ontology :  modelManager.getActiveOntologies()) {
			OWLOntologyFormat format = manager.getOntologyFormat(ontology);
			if (format != null && format instanceof PrefixOWLOntologyFormat) {
				PrefixOWLOntologyFormat prefixes = (PrefixOWLOntologyFormat) format;
				for (String name : prefixes.getPrefixNames()) {
					mapper.addPrefixMapping(name, prefixes.getPrefix(name));
				}
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
