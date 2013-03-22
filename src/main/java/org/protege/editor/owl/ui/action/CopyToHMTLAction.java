package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.OWLEditorKitOntologyShortFormProvider;
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider;
import org.protege.editor.owl.ui.renderer.context.OWLObjectRenderingContext;
import org.protege.editor.owl.ui.renderer.styledstring.OWLObjectStyledStringRenderer;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.protege.editor.owl.ui.renderer.styledstring.StyledStringSelection;
import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owlapi.model.OWLObject;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 15/10/2012
 */
public class CopyToHMTLAction extends FocusedComponentAction<Copyable> {


    protected boolean canPerform() {
        return getCurrentTarget().canCopy();
    }


    protected Class<Copyable> initialiseAction() {
        return Copyable.class;
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLObject> objects = getCurrentTarget().getObjectsToCopy();
        if (objects.isEmpty()) {
            // Shouldn't really happen, but just in case
            return;
        }
        // Push the objects on to the clip board
        ViewClipboard clipboard = ViewClipboard.getInstance();
        clipboard.getClipboard().setContents(new TransferableOWLObject(getOWLModelManager(), objects), null);


        new TransferableOWLObject(getOWLModelManager(), objects);

        OWLEditorKitShortFormProvider sfp = new OWLEditorKitShortFormProvider(getOWLEditorKit());
        OWLEditorKitOntologyShortFormProvider ontSfp = new OWLEditorKitOntologyShortFormProvider(getOWLEditorKit());
        OWLObjectRenderingContext renderingContext = new OWLObjectRenderingContext(sfp, ontSfp);
        OWLObjectStyledStringRenderer renderer = new OWLObjectStyledStringRenderer(renderingContext);

        StyledString.Builder complete = new StyledString.Builder();
        for (OWLObject owlObject : objects) {
            StyledString styledString = renderer.getRendering(owlObject);
            complete.appendStyledString(styledString);
            complete.appendNewLine();
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StyledStringSelection(complete.build()), null);
        // Actually, we could put text on to the system clipboard
        // OWLObject should be serializable!!!
    }


    private static class HtmlSelection implements Transferable {

        private static ArrayList<DataFlavor> htmlFlavors = new ArrayList<DataFlavor>();


        static {

            try {

                htmlFlavors.add(new DataFlavor("application/rtf;class=java.lang.String"));

                htmlFlavors.add(new DataFlavor("text/html;class=java.lang.String"));

                htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));

                htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));

            }
            catch (ClassNotFoundException ex) {

                ex.printStackTrace();

            }

        }


        private String html;

        private String rtf;


        public HtmlSelection(String html, String rtf) {
            this.html = html;
            this.rtf = rtf;
        }


        public DataFlavor[] getTransferDataFlavors() {

            return (DataFlavor[]) htmlFlavors.toArray(new DataFlavor[htmlFlavors.size()]);

        }


        public boolean isDataFlavorSupported(DataFlavor flavor) {

            return htmlFlavors.contains(flavor);

        }


        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

            String toRet = html;
            if (flavor.isMimeTypeEqual("application/rtf")) {
                toRet = rtf;
            }
            if (String.class.equals(flavor.getRepresentationClass())) {

                return toRet;

            }
            else if (Reader.class.equals(flavor.getRepresentationClass())) {

                return new StringReader(toRet);

            }
            else if (InputStream.class.equals(flavor.getRepresentationClass())) {

                return new StringBufferInputStream(toRet);

            }

            throw new UnsupportedFlavorException(flavor);

        }

    }


}
