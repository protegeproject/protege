package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/10/2012
 */
public class StyledStringSelection implements Transferable {

    public static final String RTF_MIME_TYPE = "application/rtf";

    public static final String HTML_MIME_TYPE = "text/html";

    public static final String PLAIN_TEXT_MIME_TYPE = "text/plain";

    public static final DataFlavor RTF_DATA_FLAVOR = new DataFlavor(RTF_MIME_TYPE, "Rich Text Format (RTF)");

    public static final DataFlavor HTML_DATA_FLAVOR = new DataFlavor(HTML_MIME_TYPE, "Hyper Text Markup Language (HTML)");

    public static final DataFlavor PLAIN_TEXT_DATA_FLAVOR = new DataFlavor(PLAIN_TEXT_MIME_TYPE, "Plain Text");

    private static DataFlavor[] DATA_FLAVORS = new DataFlavor[]{RTF_DATA_FLAVOR, HTML_DATA_FLAVOR, PLAIN_TEXT_DATA_FLAVOR};


    private StyledString styledString;

    public StyledStringSelection(StyledString styledString) {
        this.styledString = styledString;
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.  The array should be ordered according to preference
     * for providing the data (from most richly descriptive to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        return DATA_FLAVORS;
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        String mimeType = flavor.getMimeType();
        return flavor.isMimeTypeEqual(RTF_MIME_TYPE) || flavor.isMimeTypeEqual(HTML_MIME_TYPE) || flavor.isMimeTypeEqual(PLAIN_TEXT_MIME_TYPE);
    }

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     * @param flavor the requested flavor for the data
     * @throws java.io.IOException if the data is no longer available
     *                             in the requested flavor.
     * @throws java.awt.datatransfer.UnsupportedFlavorException
     *                             if the requested data flavor is
     *                             not supported.
     * @see java.awt.datatransfer.DataFlavor#getRepresentationClass
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        final String textRepresentation;
        if (flavor.isMimeTypeEqual(RTF_MIME_TYPE)) {
            textRepresentation = styledString.toRTF();
        }
        else if (flavor.isMimeTypeEqual(HTML_MIME_TYPE)) {
            textRepresentation = styledString.toHTML();
        }
        else {
            textRepresentation = styledString.toPlainText();
        }

        if (flavor.getRepresentationClass().equals(String.class)) {
            return textRepresentation;
        }
        else if (flavor.getRepresentationClass().equals(InputStream.class)) {
            return new ByteArrayInputStream(textRepresentation.getBytes());
        }
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
