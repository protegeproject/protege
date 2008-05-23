package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AnnotationAxiomsStrategy implements AxiomSelectionStrategy {

    private Set<URI> uris = new HashSet<URI>();

    public String getName() {
        return "Entity annotation axioms using a particular URI";
    }

    public void setURIs(Set<URI> annotationURIs){
        this.uris = annotationURIs;
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> onts) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : onts){
            for (OWLEntityAnnotationAxiom ax : ont.getAxioms(AxiomType.ENTITY_ANNOTATION)){
                if (uris.contains(ax.getAnnotation().getAnnotationURI())){
                    axioms.add(ax);
                }
            }
        }        
        return axioms;
    }

}
