package org.protege.editor.owl.model.repository.extractors;

import com.google.common.base.Optional;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConsumer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RdfExtractorConsumer implements RDFConsumer {

    private Set<String> ontologyProperties        = new HashSet<>();
    
    private IRI      xmlBase;

    private Set<String> possibleOntologyNames     = new HashSet<>();

    private Map<String, String> nameToVersionMap  = new HashMap<>();

    private Set<String> notPossibleOntologyNames  = new HashSet<>();
    
    public RdfExtractorConsumer() {
        ontologyProperties.add(OWLRDFVocabulary.OWL_IMPORTS.getIRI().toString());
    }
    
    public Optional<OWLOntologyID> getOntologyID() {
        if (possibleOntologyNames.size() != 1) {
            return Optional.absent();
        }
        String name = possibleOntologyNames.iterator().next();
        String version = nameToVersionMap.get(name);
        if (version == null) {
            return Optional.of(new OWLOntologyID(Optional.of(IRI.create(name)), Optional.<IRI>absent()));
        }
        else {
            return Optional.of(new OWLOntologyID(Optional.of(IRI.create(name)), Optional.of(IRI.create(version))));
        }
    }

    public void logicalURI(IRI logicalURI) {
    	xmlBase = logicalURI;
    }

    @Override
    public void statementWithResourceValue(String subject, String predicate, String object) {
        if (ontologyProperties.contains(predicate)) {
            notPossibleOntologyNames.add(object);
            possibleOntologyNames.remove(object);
        }
        else if (predicate.equals(OWLRDFVocabulary.RDF_TYPE.toString()) &&
                object.equals("http://www.w3.org/2002/07/owl#OntologyProperty")) {
            ontologyProperties.add(subject.toString());
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

    public void statementWithResourceValue(IRI subject, IRI predicate, IRI object) {

    }
    
    public void addModelAttribte(String key, String value) throws SAXException {

    }

    public void endModel() {

    }

    public void includeModel(String logicalURI, String physicalURI) {

    }

    public void startModel(String physicalURI) throws SAXException {

    }

    @Override
    public void startModel(IRI iri) {

    }

    @Override
    public void statementWithLiteralValue(IRI iri, IRI iri1, String s, String s1, IRI iri2) {

    }

    public void statementWithLiteralValue(String subject, String predicate, String object, String language, String datatype) {

    }

    @Override
    public IRI remapIRI(IRI iri) {
        return iri;
    }

    @Override
    public String remapOnlyIfRemapped(String s) {
        return s;
    }

    @Override
    public void addPrefix(String s, String s1) {

    }
}
