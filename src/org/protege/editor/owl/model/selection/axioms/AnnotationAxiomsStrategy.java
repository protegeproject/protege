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
public class AnnotationAxiomsStrategy extends AbstractAxiomSelectionStrategy {

    private Set<URI> uris = new HashSet<URI>();

    public static final String CHANGED_ANNOTATION_URIS = "change.annotation.uri";


    public String getName() {
        return "Entity annotation axioms using a particular URI";
    }

    public void setURIs(Set<URI> annotationURIs){
        this.uris = annotationURIs;
        notifyPropertyChange(CHANGED_ANNOTATION_URIS);
    }

    public Set<OWLAxiom> getAxioms() {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : getOntologies()){
            for (OWLEntityAnnotationAxiom ax : ont.getAxioms(AxiomType.ENTITY_ANNOTATION)){
                if (uris.contains(ax.getAnnotation().getAnnotationURI())){
                    axioms.add(ax);
                }
            }
        }        
        return axioms;
    }

}
