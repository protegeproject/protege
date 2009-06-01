package org.protege.editor.owl.model.repository;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owl.rdf.turtle.TurtleOntologyFormat;
import org.protege.editor.owl.model.io.SyntaxGuesser;
import org.protege.editor.owl.model.util.URIUtilities;
import org.semanticweb.owl.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLOntologyFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
public class OntologyIRIExtractor {

    private URI physicalURI;

    private IRI ontologyIRI;

    private boolean startElementPresent;

    private Map<String, String> namespaceMap;

    private String defaultNamespace;


    public OntologyIRIExtractor(URI physicalURI) {
        this.physicalURI = physicalURI;
        // Set the base URI to the physical URL for cases where there
        // isn't an explicit xml:base
        ontologyIRI = IRI.create(physicalURI);
        namespaceMap = new HashMap<String, String>();
    }


    public IRI getOntologyIRI() {
        try {
            InputStream is = URIUtilities.getInputStream(physicalURI);

            SyntaxGuesser syntaxGuesser = new SyntaxGuesser();
            OWLOntologyFormat syntax = syntaxGuesser.getSyntax(is);
            is.close();

            is = URIUtilities.getInputStream(physicalURI);

            if (syntax instanceof OWLXMLOntologyFormat || syntax instanceof RDFXMLOntologyFormat){
                readXML(is);
            }
            else if (syntax instanceof ManchesterOWLSyntaxOntologyFormat){
                readMOS(is);
            }
            else if (syntax instanceof OWLFunctionalSyntaxOntologyFormat){
                readFOS(is);
            }
            else if (syntax instanceof TurtleOntologyFormat){
                readTurtle(is);
            }
            // KRSS does not appear to support an ontology URI

            if (defaultNamespace == null){
                defaultNamespace = ontologyIRI + "#";
            }
        }
        catch (Exception e) {
            // We expect there to be an exception,
        }
        return ontologyIRI;
    }


    private void readTurtle(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Pattern p = Pattern.compile("<(.*)> [rdf:type a] owl:Ontology.*");
        boolean finished = false;
        while (!finished){
            String line = reader.readLine().trim();
            if (line.length() > 0){
                Matcher matcher = p.matcher(line);
                if (matcher.matches()){
                    setIRI(matcher.group(1));
                    startElementPresent = true;
                    finished = true;
                }
                else if (!(line.startsWith("@prefix") || line.startsWith("@base"))){
                    finished = true;
                }
            }
        }
    }


    private void readFOS(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Pattern p = Pattern.compile("Ontology\\(<(.*)>");
        boolean finished = false;
        while (!finished){
            String line = reader.readLine().trim();
            if (line.length() > 0){
                Matcher matcher = p.matcher(line);
                if (matcher.matches()){
                    setIRI(matcher.group(1));
                    startElementPresent = true;
                    finished = true;
                }
                else if (!line.startsWith("Namespace(")){
                    finished = true;
                }
            }
        }
    }


    private void readMOS(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Pattern p = Pattern.compile("Ontology: <(.*)>");
        boolean finished = false;
        while (!finished){
            String line = reader.readLine().trim();
            if (line.length() > 0){
                Matcher matcher = p.matcher(line);
                if (matcher.matches()){
                    setIRI(matcher.group(1));
                    startElementPresent = true;
                    finished = true;
                }
                else if (!line.startsWith("Namespace:")){
                    finished = true;
                }
            }
        }
    }


    private void readXML(final InputStream is) throws Exception {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(is, new DefaultHandler() {

            private boolean searchingForOWLOntology = false;

            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                try {
                    if (!searchingForOWLOntology){
                        if (qName.equals("rdf:RDF")){ // RDF/XML
                            startElementPresent = true;
                            handleNamespaces(attributes);
                            setIRI(attributes.getValue("xml:base"));
                            String s = ontologyIRI.toString().trim();
                            if (s.endsWith("#")) {
                                setIRI(s.substring(0, s.length() - 1));
                            }
                            searchingForOWLOntology = true;
                        }
                        else if (qName.equals("Ontology")){ // OWL XML
                            startElementPresent = true;
                            handleNamespaces(attributes);
                            String uriString = attributes.getValue("URI");
                            if (uriString != null){
                                setIRI(uriString);
                            }
                        }
                    }
                    else{
                        if (qName.equals("owl:Ontology")){
                            String ontID = attributes.getValue("rdf:about");
                            if (!ontID.equals("")){
                                setIRI(ontID);
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
    }


    private void setIRI(String s) throws URISyntaxException {
        URI uri = new URI(s);
        ontologyIRI = IRI.create(uri);
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
