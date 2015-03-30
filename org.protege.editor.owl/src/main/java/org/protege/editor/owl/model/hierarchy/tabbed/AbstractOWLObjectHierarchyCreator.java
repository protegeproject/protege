package org.protege.editor.owl.model.hierarchy.tabbed;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

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


    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }


    public OWLOntology getOntology() {
        return ontology;
    }


    protected List<OWLOntologyChange> hierarchyCreationStart() {
        return Collections.emptyList();
    }


    public List<OWLOntologyChange> createHierarchy() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(hierarchyCreationStart());
        for (Edge e : edges) {
            if (e.isRoot()) {
                changes.add(getChange(e.getChild(), dataFactory));
            }
            else {
                changes.add(getChange(e.getChild(), e.getParent(), dataFactory));
            }
        }
        changes.addAll(hierarchyCreationEnd());
        return changes;
    }


    protected List<OWLOntologyChange> hierarchyCreationEnd() {
        return Collections.emptyList();
    }


    protected IRI getIRI(String s) {
        try {
            final URI defaultDocURI = ontology.getOntologyID().getDefaultDocumentIRI().get().toURI();
            return IRI.create(new URI(defaultDocURI.getScheme(), defaultDocURI.getSchemeSpecificPart(), s));
        }
        catch (Exception e) {
            ProtegeApplication.getErrorLog().logError(e);
        }
        return null;
    }


    public abstract OWLOntologyChange getChange(String objName, OWLDataFactory dataFactory);


    public abstract OWLOntologyChange getChange(String childName, String parentName, OWLDataFactory dataFactory);
}
