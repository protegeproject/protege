package org.protege.editor.owl.model.hierarchy.tabbed;

import java.util.List;

import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassHierarchyCreator extends AbstractOWLObjectHierarchyCreator {

    private OWLClass root;


    public OWLClassHierarchyCreator(OWLDataFactory dataFactory, OWLClass root, OWLOntology ontology, List<Edge> edges) {
        super(dataFactory, ontology, edges);
        this.root = root;
    }


    public OWLOntologyChange getChange(String objName, OWLDataFactory dataFactory) {
        return new AddAxiom(getOntology(),
                            dataFactory.getOWLSubClassAxiom(dataFactory.getOWLClass(getURI(objName)), root));
    }


    public OWLOntologyChange getChange(String childName, String parentName, OWLDataFactory dataFactory) {
        return new AddAxiom(getOntology(),
                            dataFactory.getOWLSubClassAxiom(dataFactory.getOWLClass(getURI(childName)),
                                                            dataFactory.getOWLClass(getURI(parentName))));
    }
}
