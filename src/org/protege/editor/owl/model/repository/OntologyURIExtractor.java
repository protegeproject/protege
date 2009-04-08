package org.protege.editor.owl.model.repository;

import org.protege.editor.owl.model.util.URIUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 20, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that can be used to extract an ontology URI
 * from an RDF/XML or OWL/XML file:
 * - first look for a valid Ontology element
 * - otherwise use the xml:base
 * - if there is no explicit xml:base, then this will be the document URI.
 */
public class OntologyURIExtractor {

    private URI physicalURI;

    private URI ontologyURI;

    private boolean startElementPresent;

    private Map<String, String> namespaceMap;

    private String defaultNamespace;


    public OntologyURIExtractor(URI physicalURI) {
        this.physicalURI = physicalURI;
        // Set the base URI to the physical URI for cases where there
        // isn't an explicit xml:base
        this.ontologyURI = physicalURI;
        namespaceMap = new HashMap<String, String>();
    }


    public String getDefaultNamespace() {
        return defaultNamespace;
    }


    public Map<String, String> getNamespaceMap() {
        return namespaceMap;
    }


    public URI getOntologyURI() {
        try {
            final InputStream is = URIUtilities.getInputStream(physicalURI);
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, new DefaultHandler() {

                private boolean searchingForOWLOntology = false;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    try {
                        if (!searchingForOWLOntology){
                            if (qName.equals("rdf:RDF")){ // RDF/XML
                                startElementPresent = true;
                                handleNamespaces(attributes);
                                ontologyURI = new URI(attributes.getValue("xml:base"));
                                String s = ontologyURI.toString().trim();
                                if (s.endsWith("#")) {
                                    ontologyURI = new URI(s.substring(0, s.length() - 1));
                                }
                                searchingForOWLOntology = true;
                            }
                            else if (qName.equals("Ontology")){ // OWL XML
                                startElementPresent = true;
                                handleNamespaces(attributes);
                                String uriString = attributes.getValue("URI");
                                if (uriString != null){
                                    ontologyURI = new URI(uriString);
                                }
                            }
                        }
                        else{
                            if (qName.equals("owl:Ontology")){
                                String ontID = attributes.getValue("rdf:about");
                                if (!ontID.equals("")){
                                    ontologyURI = new URI(ontID);
                                }
                            }
                            searchingForOWLOntology = false;
                        }

                        if (!searchingForOWLOntology){
                            // Close the input stream, which will eventually cause
                            // the parser to stop parsing and throw an exception
                            try {
                                is.close();
                            }
                            catch (IOException e) {
                                // do nothing
                            }

                            // Throw a SAX exception, because this will stop
                            // parsing here and now
                            throw new SAXException("Exit exception");
                        }
                    }
                    catch (URISyntaxException e) {
                        // Don't care about this exception.  We
                        // will just return the baseURI, or physicalURI
                        // if we couldn't get hold of the base.
                    }
                }
            });
            if (defaultNamespace == null){
                defaultNamespace = ontologyURI + "#";
            }
        }
        catch (Exception e) {
            // We expect there to be an exception,
        }
        return ontologyURI;
    }


    private void handleNamespaces(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            String attQName = attributes.getQName(i);
            if (attQName.startsWith("xmlns")) {
                int colonIndex = attQName.indexOf(':');
                if (colonIndex == -1) {
                    defaultNamespace = attributes.getValue(i);
                }
                else {
                    String prefix = attQName.substring(colonIndex + 1, attQName.length());
                    namespaceMap.put(prefix, attributes.getValue(i));
                }
            }
        }
    }


    public boolean isStartElementPresent() {
        return startElementPresent;
    }
}
