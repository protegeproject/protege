package org.protege.editor.owl.model.hierarchy.tabbed;

import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
            final URI defaultDocURI = ontology.getOntologyID().getDefaultDocumentIRI().toURI();
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
