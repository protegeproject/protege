package org.protege.editor.owl.ui.view.ontology;

import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public abstract class AbstractOntologyRenderingViewComponent extends AbstractActiveOntologyViewComponent {
    private static final long serialVersionUID = 496671619048384054L;
    private JTextArea textArea;


    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        add(new JScrollPane(textArea));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        updateView(getOWLModelManager().getActiveOntology());
    }


    protected void disposeOntologyView() {

    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        StringWriter w = new StringWriter();
        renderOntology(activeOntology, new BufferedWriter(w));
        textArea.setText(w.getBuffer().toString());
    }


    protected abstract void renderOntology(OWLOntology ontology, Writer writer) throws Exception;
}
