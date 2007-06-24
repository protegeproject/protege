package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public abstract class AbstractOntologyRenderingViewComponent extends AbstractActiveOntologyViewComponent {

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
        System.out.println("Written");
        textArea.setText(w.getBuffer().toString());
    }


    protected abstract void renderOntology(OWLOntology ontology, Writer writer) throws Exception;
}
