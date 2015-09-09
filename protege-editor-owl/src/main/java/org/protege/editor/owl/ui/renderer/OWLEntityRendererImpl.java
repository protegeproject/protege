package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A simple renderer that renderers an entity using its URI fragment
 * if it exists, and if not, renders the full URI.
 */
public class OWLEntityRendererImpl extends AbstractOWLEntityRenderer {

    private Map<IRI, String> wellKnownRenderings = new HashMap<>();


    public void initialise() {
        for(OWLRDFVocabulary vocabulary : OWLRDFVocabulary.values()) {
            addOWLRDFVocabulary(vocabulary);
        }
        for(OWL2Datatype dt : OWL2Datatype.values()) {
            wellKnownRenderings.put(dt.getIRI(), dt.getPrefixedName());
        }
    }

    private void addOWLRDFVocabulary(OWLRDFVocabulary vocabulary) {
        wellKnownRenderings.put(vocabulary.getIRI(), vocabulary.getPrefixedName());
    }

    private String getSubstringFromLastCharacter(String iriString, char lastChar) {
        int index = iriString.lastIndexOf(lastChar);
        if(index != -1 && index < iriString.length()) {
            return RenderingEscapeUtils.getEscapedRendering(iriString.substring(index + 1));
        }
        else {
            return null;
        }
    }

    public String render(IRI iri) {
        try {
            String wellKnownName = wellKnownRenderings.get(iri);
            if(wellKnownName != null) {
                return wellKnownName;
            }
            String iriString = iri.toString();
            String fragment = getSubstringFromLastCharacter(iriString, '#');
            if(fragment != null) {
                return fragment;
            }
            String pathElement = getSubstringFromLastCharacter(iriString, '/');
            if(pathElement != null) {
                return pathElement;
            }
            return RenderingEscapeUtils.getEscapedRendering(iri.toQuotedString());
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
