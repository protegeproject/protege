package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.syntax.RDFConsumer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.xml.sax.SAXException;

public class RdfExtractorConsumer implements RDFConsumer {
    private Set<String> possibleOntologyNames     = new HashSet<String>();
    private Map<String, String> nameToVersionMap  = new HashMap<String, String>();
    private Set<String> notPossibleOntologyNames  = new HashSet<String>();
    private Set<String> ontologyProperties        = new HashSet<String>();
    
    private OWLOntologyID id;
    
    public RdfExtractorConsumer() {
        ontologyProperties.add(OWLRDFVocabulary.OWL_IMPORTS.getIRI().toString());
    }
    
    public OWLOntologyID getOntologyID() {
        if (possibleOntologyNames.size() != 1) {
            return null;
        }
        String name = possibleOntologyNames.iterator().next();
        String version = nameToVersionMap.get(name);
        if (version == null) {
            return new OWLOntologyID(IRI.create(name));
        }
        else {
            return new OWLOntologyID(IRI.create(name), IRI.create(version));
        }
    }

    public void addModelAttribte(String key, String value) throws SAXException {

    }

    public void endModel() throws SAXException {

    }

    public void includeModel(String logicalURI, String physicalURI) throws SAXException {

    }

    public void logicalURI(String logicalURI) throws SAXException {

    }

    public void startModel(String physicalURI) throws SAXException {

    }

    public void statementWithLiteralValue(String subject, String predicate, String object, String language, String datatype) throws SAXException {

    }

    public void statementWithResourceValue(String subject, String predicate, String object) throws SAXException {
        if (ontologyProperties.contains(predicate)) {
            notPossibleOntologyNames.add(object);
            possibleOntologyNames.remove(object);
        }
        else if (predicate.equals(OWLRDFVocabulary.RDF_TYPE.toString()) &&
                   object.equals(OWLRDFVocabulary.OWL_ONTOLOGY.toString()) &&
                   !notPossibleOntologyNames.contains(subject)) {
            possibleOntologyNames.add(subject);
        }
        else if (predicate.equals(OWLRDFVocabulary.OWL_VERSION_IRI.toString())) {
            nameToVersionMap.put(subject, object);
        }
    }

}
