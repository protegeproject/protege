package org.protege.editor.owl.model.repository;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
 * Date: Apr 20, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that can be used to extract an ontology URI
 * from an RDF/XML file.  An ontology URI corresponds to the xml:base of
 * an RDF/XML document that contains an ontology.  If there is no
 * explicit xml:base, then the ontology URI will correspond to the
 * document URI.
 */
public class OntologyURIExtractor {

    private URI physicalURI;

    private URI baseURI;

    private boolean startElementPresent;

    private Map<String, String> namespaceMap;

    private String defaultNamespace;


    public OntologyURIExtractor(URI physicalURI) {
        this.physicalURI = physicalURI;
        // Set the base URI to the physical URI for cases where there
        // isn't an explict xml:base
        this.baseURI = physicalURI;
        namespaceMap = new HashMap<String, String>();
    }


    public String getDefaultNamespace() {
        return defaultNamespace;
    }


    public Map<String, String> getNamespaceMap() {
        return namespaceMap;
    }


    public URI getOntologyURI() {
        // We actually just want the base of the document
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            URL url = physicalURI.toURL();
            final InputStream is = url.openStream();
            parser.parse(is, new DefaultHandler() {

                private boolean extract = true;


                public void startElement(String uri, String localName, String qName, Attributes attributes) throws
                                                                                                            SAXException {
                    startElementPresent = true;
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
                    if (extract) {
                        try {
                            baseURI = new URI(attributes.getValue("xml:base"));
                            String s = baseURI.toString().trim();
                            if (s.endsWith("#")) {
                                baseURI = new URI(s.substring(0, s.length() - 1));
                            }
                            // Close the input stream, which will eventually cause
                            // the parser to stop parsing and throw an exception
                            is.close();
                            // Throw a SAX exception, because this will stop
                            // paring here and now
                            throw new SAXException("Exit exception");
                        }
                        catch (Exception e) {
                            // Don't care about this exception.  We
                            // will just return the baseURI, or physicalURI
                            // if we couldn't get hold of the base.
                        }
                    }
                    extract = false;
                    throw new SAXException("Exit exception");
                }
            });
            defaultNamespace = baseURI + "#";
            return baseURI;
        }
        catch (Exception e) {
            // We actually don't care that there is
            // an exception,
            return baseURI;
        }
    }


    public boolean isStartElementPresent() {
        return startElementPresent;
    }
}
