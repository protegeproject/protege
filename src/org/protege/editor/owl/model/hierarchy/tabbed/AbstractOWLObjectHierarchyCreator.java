package org.protege.editor.owl.model.hierarchy.tabbed;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.ProtegeApplication;
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
public abstract class AbstractOWLObjectHierarchyCreator {

    private OWLDataFactory dataFactory;

    private OWLOntology ontology;

    private List<Edge> edges;


    public AbstractOWLObjectHierarchyCreator(OWLDataFactory dataFactory, OWLOntology ontology, List<Edge> edges) {
        this.dataFactory = dataFactory;
        this.ontology = ontology;
        this.edges = edges;
    }


    public OWLOntology getOntology() {
        return ontology;
    }


    public List<OWLOntologyChange> createHierarchy() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (Edge e : edges) {
            if (e.isRoot()) {
                changes.add(getChange(e.getChild(), dataFactory));
            }
            else {
                changes.add(getChange(e.getChild(), e.getParent(), dataFactory));
            }
        }
        return changes;
    }


    protected URI getURI(String s) {
        try {
            return new URI(ontology.getURI().getScheme(), ontology.getURI().getSchemeSpecificPart(), s);
        }
        catch (Exception e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
        return null;
    }


    public abstract OWLOntologyChange getChange(String objName, OWLDataFactory dataFactory);


    public abstract OWLOntologyChange getChange(String childName, String parentName, OWLDataFactory dataFactory);
}
