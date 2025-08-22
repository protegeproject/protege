package org.protege.editor.owl.ui.axiom;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitIRIShortFormProvider;
import org.protege.editor.owl.model.OWLEditorKitOntologyShortFormProvider;
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider;
import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.protege.editor.owl.ui.renderer.context.OWLObjectRenderingContext;
import org.protege.editor.owl.ui.renderer.styledstring.OWLObjectStyledStringRenderer;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.protege.editor.owl.ui.renderer.styledstring.StyledStringPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

import javax.swing.*;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 3, 2008<br><br>
 */
public class AxiomAnnotationPanel extends JComponent {

    private static final int PREF_WIDTH = 500;

    private AxiomAnnotationsList annotationsComponent;

    private final OWLEditorKit editorKit;

    private final StyledStringPanel axiomPanel = new StyledStringPanel();

    public AxiomAnnotationPanel(OWLEditorKit eKit) {
        this.editorKit = checkNotNull(eKit);
        setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(PREF_WIDTH, 300));

        annotationsComponent = new AxiomAnnotationsList(eKit);

        axiomPanel.setPreferredSize(new Dimension(PREF_WIDTH, 50));
        add(axiomPanel, BorderLayout.NORTH);
        add(new JScrollPane(annotationsComponent), BorderLayout.CENTER);

        setVisible(true);
    }

    private StyledString getAxiomRendering(OWLAxiom axiom) {
        OWLEditorKitShortFormProvider sfp = new OWLEditorKitShortFormProvider(editorKit);
        OWLEditorKitOntologyShortFormProvider ontologySfp = new OWLEditorKitOntologyShortFormProvider(editorKit);
        OWLEditorKitIRIShortFormProvider iriSfp = new OWLEditorKitIRIShortFormProvider(editorKit, new SimpleIRIShortFormProvider());
        OWLObjectRenderingContext renderingContext = new OWLObjectRenderingContext(sfp, iriSfp, ontologySfp);
        OWLObjectStyledStringRenderer renderer = new OWLObjectStyledStringRenderer(renderingContext);
        return renderer.getRendering(axiom);
    }


    public void setAxiomInstance(OWLAxiomInstance axiomInstance) {
        axiomPanel.setStyledString(StyledString.EMPTY_STYLED_STRING);
        if (axiomInstance != null) {
            StyledString axiomRendering = getAxiomRendering(axiomInstance.getAxiom());
            axiomPanel.setStyledString(axiomRendering);
            annotationsComponent.setRootObject(axiomInstance);
        }
        else {
            annotationsComponent.setRootObject(null);
        }
    }


    public OWLAxiomInstance getAxiom() {
        return annotationsComponent.getRoot();
    }


    public void dispose() {
        annotationsComponent.dispose();
    }
}
