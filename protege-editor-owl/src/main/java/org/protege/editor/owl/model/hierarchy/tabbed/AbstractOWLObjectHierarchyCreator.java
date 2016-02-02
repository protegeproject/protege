package org.protege.editor.owl.model.hierarchy.tabbed;

import com.google.common.base.Optional;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

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
        List<OWLOntologyChange> changes = new ArrayList<>();
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
            final Optional<IRI> defaultDocURI = ontology.getOntologyID().getDefaultDocumentIRI();
            return IRI.create(new URI(defaultDocURI.get().getScheme(), defaultDocURI.get().toURI().getSchemeSpecificPart(), s));
        }
        catch (Exception e) {
            LoggerFactory.getLogger(AbstractOWLObjectHierarchyCreator.class)
                    .error("An error occurred whilst creating an IRI: {}", e);
        }
        return null;
    }


    public abstract OWLOntologyChange getChange(String objName, OWLDataFactory dataFactory);


    public abstract OWLOntologyChange getChange(String childName, String parentName, OWLDataFactory dataFactory);
}
