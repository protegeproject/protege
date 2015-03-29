package org.protege.editor.owl.model.repository.extractors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConsumer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class RdfExtractorConsumer implements RDFConsumer {
    private Set<String> ontologyProperties        = new HashSet<String>();
    
    private IRI xmlBase;
    private Set<String> possibleOntologyNames     = new HashSet<String>();
    private Map<String, String> nameToVersionMap  = new HashMap<String, String>();
    private Set<String> notPossibleOntologyNames  = new HashSet<String>();
    
    public RdfExtractorConsumer() {
        ontologyProperties.add(OWLRDFVocabulary.OWL_IMPORTS.getIRI().toString());
    }
    
    public void addOntologyProperty(String property) {
        ontologyProperties.add(property);
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
    
    public IRI getXmlBase() {
		return xmlBase;
	}

    public void logicalURI(IRI logicalURI) {
    	xmlBase = logicalURI;
    }


    public void statementWithResourceValue(String subject, String predicate, String object) {
        if (ontologyProperties.contains(predicate)) {
            notPossibleOntologyNames.add(object);
            possibleOntologyNames.remove(object);
        }
        else if (predicate.equals(OWLRDFVocabulary.RDF_TYPE.toString()) &&
                 object.equals("http://www.w3.org/2002/07/owl#OntologyProperty")) {
            ontologyProperties.add(subject);
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

    public void endModel() {

    }

    public void includeModel(String logicalURI, String physicalURI) {

    }

    public void startModel(IRI physicalURI) {

    }

    public void statementWithLiteralValue(String subject, String predicate, String object, String language, String datatype)  {

    }

    @Override
    public void statementWithResourceValue(IRI subject, IRI predicate,
            IRI object) {
        statementWithResourceValue(subject.toString(), predicate.toString(), object.toString());
    }

    @Override
    public void statementWithLiteralValue(IRI subject, IRI predicate,
            String object, String language, IRI datatype) {
    }

    public IRI remapIRI(IRI i) {
        return i;
    }

    @Override
    public String remapOnlyIfRemapped(String i) {
        return i;
    }

    @Override
    public void addPrefix(String abbreviation, String value) {
    }


}
