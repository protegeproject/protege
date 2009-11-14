package org.protege.editor.owl.model.repository.extractors;

import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.syntax.RDFConsumer;
import org.xml.sax.SAXException;

public class RdfExtractorConsumer implements RDFConsumer {
    
    private OWLOntologyID id;
    
    public OWLOntologyID getOntologyID() {
        return id;
    }

    public void addModelAttribte(String key, String value) throws SAXException {
        // TODO Auto-generated method stub

    }

    public void endModel() throws SAXException {
        // TODO Auto-generated method stub

    }

    public void includeModel(String logicalURI, String physicalURI) throws SAXException {
        // TODO Auto-generated method stub

    }

    public void logicalURI(String logicalURI) throws SAXException {
        // TODO Auto-generated method stub

    }

    public void startModel(String physicalURI) throws SAXException {
        // TODO Auto-generated method stub

    }

    public void statementWithLiteralValue(String subject, String predicate, String object, String language, String datatype) throws SAXException {
        // TODO Auto-generated method stub

    }

    public void statementWithResourceValue(String subject, String predicate, String object) throws SAXException {
        // TODO Auto-generated method stub

    }

}
