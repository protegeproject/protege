package org.protege.editor.owl.model.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

public class URIUtilities {

    private static Logger logger = LoggerFactory.getLogger(URIUtilities.class);
      
    /*
     * Stolen from the owlapi - AbstractOWLParser.getInputStream.  I would have used
     * the owlapi method but it is not public or static.
     */
    public static InputStream getInputStream(URI uri) throws IOException {
        String requestType = getRequestTypes();
        URLConnection conn = uri.toURL().openConnection();
        conn.addRequestProperty("Accept", requestType);
//        if (IOProperties.getInstance().isConnectionAcceptHTTPCompression()) {
//            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
//        }
//        conn.setConnectTimeout(IOProperties.getInstance().getConnectionTimeout());
        InputStream is;
        if ("gzip".equals(conn.getContentEncoding())) { // test works OK even if CE is null
            logger.debug("URL connection input stream is compressed using gzip");
            is = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
        } else if ("deflate".equals(conn.getContentEncoding())) {
            logger.debug("URL connection input stream is compressed using deflate");
            is = new BufferedInputStream(new InflaterInputStream(conn.getInputStream(), new Inflater(true)));
        } else {
            is = new BufferedInputStream(conn.getInputStream());
        }
        if (uri.toString().endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(is);
            zis.getNextEntry();
            is = new BufferedInputStream(zis);
        }
        return is;
    }
    
    private static String getRequestTypes() {
        return "application/rdf+xml, application/xml; q=0.5, text/xml; q=0.3, */*; q=0.2";
    }

}
