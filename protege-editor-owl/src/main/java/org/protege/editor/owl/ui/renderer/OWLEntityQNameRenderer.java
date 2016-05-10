package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.prefix.PrefixUtilities;
import org.protege.editor.owl.ui.renderer.prefix.PrefixBasedRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.Namespaces;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityQNameRenderer extends AbstractOWLEntityRenderer implements PrefixBasedRenderer {

    private final DefaultPrefixManager prefixManager = new DefaultPrefixManager();

	public void initialise() {
    	for(Namespaces ns : Namespaces.values()) {
            String prefixName = ns.getPrefixName();
            String prefixIRI = ns.getPrefixIRI();
            prefixManager.setPrefix(prefixName + ":", prefixIRI);
        }
        PrefixManager localPrefixes = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
        for(String prefixName : localPrefixes.getPrefixNames()) {
            String prefixIRI = localPrefixes.getPrefix(prefixName);
            prefixManager.setPrefix(prefixName, prefixIRI);
        }
    }
    
    @Override
    public void ontologiesChanged() {
    	initialise();
    }


    public String render(IRI iri) {
        try {
        	String s = prefixManager.getPrefixIRI(iri);
            if (s != null) {
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
