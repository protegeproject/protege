package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyFormat;
import org.semanticweb.owl.vocab.DublinCoreVocabulary;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class AnnotationURIList extends MList {

    private OWLEditorKit owlEditorKit;

    private URI uriBeingAdded;


    public AnnotationURIList(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    protected void handleAdd() {
        handleAddURI();
    }


    private void handleAddURI() {
        try {
            String uriString = JOptionPane.showInputDialog(this,
                                                           "Please specify an annotation URI",
                                                           "Annotation URI",
                                                           JOptionPane.PLAIN_MESSAGE);
            if (uriString != null) {
                URI uri = new URI(uriString);
                if (!uri.isAbsolute()) {
                    uri = URI.create(owlEditorKit.getModelManager().getActiveOntology().getURI() + "#" + uri.toString());
                }
                uriBeingAdded = uri;
            }
            rebuildAnnotationURIList();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method obtains URIs which are typed as annotation properties, but which
     * are not used in any annotations.  The reason for this temporary method (which
     * is a hack and should be deleted when the OWL 1.1 spec is fixed) is that OWL
     * 1.0 allows an ontology to contain statements such as  p rdf:type owl:AnnotationProperty.
     * Unfortunately, the current state of the OWL 1.1 spec (01/08/07) causes these simple type
     * triples get ignored - the set of annotation URIs for an OWL 1.1 ontology contains only the URIs that
     * are used in annotations.  This isn't particularly useful.  Therefore, the RDFXML ontology
     * format has a space to 'tuck' these annotations when an RDF/XML file is parsed.  We
     * therefore retrieve these annotation URIs and make them available to the user.
     */
    private Set<URI> getAnnotationURIsViaHack() {
        Set<URI> result = new HashSet<URI>();
        for (OWLOntology ont : owlEditorKit.getModelManager().getActiveOntologies()) {
            OWLOntologyFormat format = owlEditorKit.getModelManager().getOWLOntologyManager().getOntologyFormat(ont);
            if (format instanceof RDFXMLOntologyFormat) {
                RDFXMLOntologyFormat rdfxmlOntologyFormat = (RDFXMLOntologyFormat) format;
                result.addAll(rdfxmlOntologyFormat.getAnnotationURIs());
            }
        }
        return result;
    }


    public void rebuildAnnotationURIList() {
        // Custom
        // Built in
        // Dublin core

        java.util.List list = new ArrayList();

        java.util.List<URIItem> custom = new ArrayList<URIItem>();
        Set<URI> customURIs = new HashSet<URI>();
        for (OWLOntology ont : owlEditorKit.getModelManager().getActiveOntologies()) {
            customURIs.addAll(ont.getAnnotationURIs());
        }
        customURIs.addAll(getAnnotationURIsViaHack());
        if (uriBeingAdded != null) {
            customURIs.add(uriBeingAdded);
        }
        customURIs.removeAll(OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES);
        customURIs.removeAll(DublinCoreVocabulary.ALL_URIS);
        for (URI uri : customURIs) {
            custom.add(new URIItem(uri));
        }
        list.add(new MListSectionHeader() {
            public String getName() {
                return "Custom annotation URIs";
            }


            public boolean canAdd() {
                return true;
            }
        });


        Collections.sort(custom);
        list.addAll(custom);

        list.add(new MListSectionHeader() {
            public String getName() {
                return "Built in annotation URIs";
            }


            public boolean canAdd() {
                return false;
            }
        });

        java.util.List<URIItem> builtIn = new ArrayList<URIItem>();
        for (URI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES) {
            builtIn.add(new URIItem(uri));
        }
        Collections.sort(builtIn);
        list.addAll(builtIn);


        list.add(new MListSectionHeader() {
            public String getName() {
                return "Dublin Core annotation URIs";
            }


            public boolean canAdd() {
                return false;
            }
        });

        java.util.List<URIItem> dublinCore = new ArrayList<URIItem>();
        for (URI uri : DublinCoreVocabulary.ALL_URIS) {
            dublinCore.add(new URIItem(uri));
        }
        Collections.sort(dublinCore);
        list.addAll(dublinCore);


        setListData(list.toArray());
        if (uriBeingAdded != null) {
            setSelectedURI(uriBeingAdded);
        }
        else {
            setSelectedURI(OWLRDFVocabulary.RDFS_COMMENT.getURI());
        }
    }


    public void setSelectedURI(URI uri) {
        for (int i = 0; i < getModel().getSize(); i++) {
            Object o = getModel().getElementAt(i);
            if (o instanceof URIItem) {
                URIItem item = (URIItem) o;
                if (item.uri.equals(uri)) {
                    setSelectedValue(item, false);
                    break;
                }
            }
        }
    }


    public URI getSelectedURI() {
        Object selVal = getSelectedValue();
        if (selVal instanceof URIItem) {
            return ((URIItem) selVal).uri;
        }
        return null;
    }

    public Set<URI> getSelectedURIs() {
        Set<URI> uris = new HashSet<URI>();
        for (Object selVal : getSelectedValues()){
            if (selVal instanceof URIItem) {
                uris.add(((URIItem) selVal).uri);
            }
        }
        return uris;
    }

    private class URIItem implements MListItem, Comparable<URIItem> {

        private URI uri;


        public URIItem(URI uri) {
            this.uri = uri;
        }


        public String toString() {
            return owlEditorKit.getModelManager().getURIRendering(uri);
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return false;
        }


        public boolean handleDelete() {
            return false;
        }


        public String getTooltip() {
            return uri.toString();
        }


        public int compareTo(URIItem o) {
            return this.toString().compareTo(o.toString());
        }
    }
}
