package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
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

    private final Logger logger = LoggerFactory.getLogger(XmlBaseAlgorithm.class);
    
    private InputStream is;

    private URI xmlBase;
    
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
        finally {
        	try {
        		is.close();
        	}
        	catch (Throwable t) {
        		;
        	}
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
                        throw new SAXParseCompletedException();
                    }
                    catch (URISyntaxException e) {
                        logger.error("URI syntax error", e);
                    }
                }
            }
            throw new SAXException("No xml base");
        }
    }
}

