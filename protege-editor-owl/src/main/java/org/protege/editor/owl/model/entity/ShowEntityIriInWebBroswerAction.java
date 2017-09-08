package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class ShowEntityIriInWebBroswerAction extends SelectedOWLEntityAction {

    private static final Logger logger = LoggerFactory.getLogger(ShowEntityIriInWebBroswerAction.class);

    public ShowEntityIriInWebBroswerAction() {
        putValue(NAME, "Show in Web browser");
    }

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        try {
            Desktop.getDesktop().browse(selectedEntity.getIRI().toURI());
        } catch (IOException e) {
            logger.error("An error occurred while attempting to open the selected entity IRI in a Web browser", e);
        }
    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
