package org.protege.editor.owl.ui.ontology.imports.missing;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.MissingImportHandler;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.IRI;

import javax.swing.*;
import java.io.File;
import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MissingImportHandlerUI implements MissingImportHandler {

    private static final Logger logger = Logger.getLogger(MissingImportHandlerUI.class);

    private OWLEditorKit owlEditorKit;


    public MissingImportHandlerUI(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    public URI getPhysicalURI(IRI ontologyIRI) {

        int ret = JOptionPane.showConfirmDialog(null,
                                                "<html><body>The system couldn't locate the ontology:<br><font color=\"blue\">" + ontologyIRI.toString() + "</font><br><br>" +

                                                        "Would you like to attempt to resolve the missing import?</body></html>",
                                                "Resolve missing import?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        if (ret != JOptionPane.YES_OPTION) {
            return null;
        }
        UIHelper helper = new UIHelper(owlEditorKit);
        File file = helper.chooseOWLFile("Please select an ontology file");
        if (file == null) {
            return ontologyIRI.toURI();
        }
        // Add a mapping from the ontology to the file.  If the user wants the ontology
        // to be editable, then they should have the option to copy the file into the
        // base folder.
//        owlEditorKit.getModelManager().add(ontologyURI, file.toURI());
        return file.toURI();

        //"<font color=\"gray\">Cause: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")</font><br><br>"
    }
}

