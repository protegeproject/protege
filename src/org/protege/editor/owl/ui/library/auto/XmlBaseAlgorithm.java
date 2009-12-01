package org.protege.editor.owl.ui.library.auto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 21, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class XmlBaseAlgorithm implements Algorithm {
    private static Logger log = Logger.getLogger(XmlBaseAlgorithm.class);
    
    private InputStream is;

    private URI xmlBase;


    public XmlBaseAlgorithm(InputStream is) {
        this.is = is;
        this.xmlBase = null;
    }
    
    public Set<URI> getSuggestions(File f) {
        try {
            is = new FileInputStream(f);
            xmlBase = null;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(is, new MyHandler());
        }
        catch (Throwable t) {
            ;
        }
        if (xmlBase == null) {
            return Collections.emptySet();
        }
        else {
            return Collections.singleton(xmlBase);
        }
    }


    private class MyHandler extends DefaultHandler {

        
        @Override
        public void startElement(String namespaceURI,
                                 String localName,
                                 String qName,
                                 Attributes atts)
        throws SAXException {
            for (int i = 0; i < atts.getLength(); i++) {                	
                if (atts.getQName(i).equals("xml:base")) {
                    try {
                        xmlBase = new URI(atts.getValue(i));
                        throw new ParseCompletedException();
                    }
                    catch (URISyntaxException e) {
                        log.error("Exception caught", e);
                    }
                }
            }
            throw new SAXException("No xml base");
        }
    }
    
    public static class ParseCompletedException extends SAXException {
        private static final long serialVersionUID = -3132857052804332468L;
        
    }
}

