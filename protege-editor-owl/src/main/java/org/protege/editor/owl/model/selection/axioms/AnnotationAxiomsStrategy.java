package org.protege.editor.owl.model.selection.axioms;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AnnotationAxiomsStrategy extends AbstractAxiomSelectionStrategy {

    private Set<URI> uris = new HashSet<>();

    public static final String CHANGED_ANNOTATION_URIS = "change.annotation.uri";


    public String getName() {
        return "Entity annotation axioms using a particular URI";
    }

    public void setURIs(Set<URI> annotationURIs){
        this.uris = annotationURIs;
        notifyPropertyChange(CHANGED_ANNOTATION_URIS);
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
// @@TODO v3 port
//        for (OWLOntology ont : ontologies){
//            for (OWLEntityAnnotationAxiom ax : ont.getAxioms(AxiomType.ENTITY_ANNOTATION)){
//                if (uris.contains(ax.getAnnotation().getURI())){
//                    axioms.add(ax);
//                }
//            }
//        }
        return axioms;
    }

}
