package org.protege.editor.owl.model.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 05/11/15
 *
 * This class is a temporary hack.
 */
public class IOUtils {

    private static Logger logger = LoggerFactory.getLogger(IOUtils.class);

    private static final String ZIP_FILE_EXTENSION = ".zip";

    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    private static final Pattern CONTENT_DISPOSITION_FILE_NAME_PATTERN = Pattern.compile(".*filename=\"([^\\s;]*)\".*");

    private static final int CONTENT_DISPOSITION_FILE_NAME_PATTERN_GROUP = 1;

    /**
     * A convenience method that obtains an input stream from a URI.
     * This method sets up the correct request type and wraps the input
     * stream within a buffered input stream
     * @param documentURI The URI from which the input stream should be returned
     * @return The input stream obtained from the URI
     * @throws java.io.IOException if there was an <code>IOException</code> in obtaining the input stream from the URI.
     */
    public static InputStream getInputStream(URI documentURI, boolean connectionAcceptHTTPCompression, int connectionTimeout) throws IOException {
        URLConnection conn = getUrlConnection(documentURI, connectionAcceptHTTPCompression, connectionTimeout);
        String contentEncoding = conn.getContentEncoding();
        InputStream is = getInputStreamFromContentEncoding(conn, contentEncoding);
        if (isZipName(documentURI, conn)) {
            ZipInputStream zis = new ZipInputStream(is);
            zis.getNextEntry();
            is = new BufferedInputStream(zis);
        }
        return is;
    }

    public static URLConnection getUrlConnection(URI documentURI,
                                                  boolean connectionAcceptHTTPCompression,
                                                  int connectionTimeout) throws IOException {
        String requestType = getRequestTypes();
        URLConnection conn = documentURI.toURL().openConnection();
        conn.addRequestProperty("Accept", requestType);
        if (connectionAcceptHTTPCompression) {
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        }
        conn.setConnectTimeout(connectionTimeout);
        return conn;
    }

    private static String getRequestTypes() {
        return "application/rdf+xml, application/xml; q=0.5, text/xml; q=0.3, */*; q=0.2";
    }

    private static InputStream getInputStreamFromContentEncoding(URLConnection conn, String contentEncoding) throws IOException {
        InputStream is;
        if ("gzip".equals(contentEncoding)) {
            logger.debug("URL connection input stream is compressed using gzip");
            is = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
        }
        else if ("deflate".equals(contentEncoding)) {
            logger.debug("URL connection input stream is compressed using deflate");
            is = new BufferedInputStream(new InflaterInputStream(conn.getInputStream(), new Inflater(true)));
        }
        else {
            is = new BufferedInputStream(conn.getInputStream());
        }
        return is;
    }

    private static boolean isZipName(URI documentIRI, URLConnection connection) {
        if(isZipFileName(documentIRI.toString())) {
            return true;
        }
        else {
            String fileName = getFileNameFromContentDisposition(connection);
            return fileName != null && isZipFileName(fileName);
        }
    }

    private static String getFileNameFromContentDisposition(URLConnection connection) {
        String contentDispositionHeaderValue = connection.getHeaderField(CONTENT_DISPOSITION_HEADER);
        if(contentDispositionHeaderValue != null) {
            Matcher matcher = CONTENT_DISPOSITION_FILE_NAME_PATTERN.matcher(contentDispositionHeaderValue);
            if(matcher.matches()) {
                return matcher.group(CONTENT_DISPOSITION_FILE_NAME_PATTERN_GROUP);
            }
        }
        return null;
    }

    private static boolean isZipFileName(String fileName) {
        return fileName.toLowerCase().endsWith(ZIP_FILE_EXTENSION);
    }
}
