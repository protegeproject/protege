package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.prefix.PrefixUtilities;
import org.protege.editor.owl.ui.renderer.prefix.PrefixBasedRenderer;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.PrefixManager;

import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixedOWLEntityAnnotationValueRenderer extends OWLEntityAnnotationValueRenderer implements PrefixBasedRenderer {

	private PrefixManager prefixManager;
    
    public void initialise() {
    	prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
    	super.initialise();
    }
    
    @Override
    public void ontologiesChanged() {
    	prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
    }
    
    public String render(OWLEntity entity) {
    	String shortForm = getProvider().getShortForm(entity);
    	final String uriStr = entity.getIRI().toString();

    	for (Map.Entry<String, String> prefixName2PrefixEntry : prefixManager.getPrefixName2PrefixMap().entrySet()) {
    		String prefixName = prefixName2PrefixEntry.getKey();
			if (!shortForm.startsWith(prefixName)) {
				String prefix = prefixName2PrefixEntry.getValue();
				if (uriStr.startsWith(prefix)){
                    if (!prefixName.equals(":")) {
                        return escape(prefixName + shortForm);
                    }
                    else {
                        return escape(shortForm);
                    }
                }
			}
			else {
				return escape(shortForm);
			}
		}
    	return entity.getIRI().toQuotedString();
    }
}