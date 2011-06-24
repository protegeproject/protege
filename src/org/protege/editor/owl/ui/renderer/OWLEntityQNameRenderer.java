package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.prefix.PrefixUtilities;
import org.protege.editor.owl.ui.renderer.prefix.PrefixBasedRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.PrefixManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityQNameRenderer extends AbstractOWLEntityRenderer implements PrefixBasedRenderer {
	private PrefixManager prefixManager;

	public void initialise() {
    	prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
    }
    
    @Override
    public void ontologiesChanged() {
    	initialise();
    }


    public String render(IRI iri) {
        try {
        	String s = prefixManager.getPrefixIRI(iri);
            if (s != null) {
            	if (s.startsWith(":")) {
            		s = s.substring(1);
            	}
                return s;
            }
            else {
                // No mapping
            	return iri.toQuotedString();
            }
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }

    public boolean isConfigurable() {
    	return false;
    }

    public boolean configure(OWLEditorKit eKit) {
    	throw new IllegalStateException("This renderer is not configurable");
    }
    
    protected void disposeRenderer() {
        // do nothing
    }
}
