package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxFrameRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 26, 2008<br><br>
 */
public class ManchesterSyntaxFrameView extends AbstractOWLSelectionViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -7745031898151962692L;

    private JTextArea textArea;

    private JScrollPane scroller;


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));

        scroller = new JScrollPane(textArea);
        add(scroller, BorderLayout.CENTER);

        updateView();
    }

    protected OWLObject updateView() {
        final OWLEntity owlEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (owlEntity != null){
            StringWriter w = new StringWriter();
            try {
                final BufferedWriter bw = new BufferedWriter(w);
                renderOWLEntity(owlEntity, bw);
                bw.close();
                textArea.setText(w.getBuffer().toString());

                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        scroller.getViewport().setViewPosition(new Point(0, 0));
                    }
                });
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return owlEntity;
    }



    private void renderOWLEntity(OWLEntity entity, Writer writer) throws Exception {
        OWLEntityFrameRendererAdapter adapter = new OWLEntityFrameRendererAdapter(getOWLModelManager(), writer);
        entity.accept(adapter);
    }


    public void disposeView() {
        // do nothing
    }


    class OWLEntityFrameRendererAdapter implements OWLEntityVisitor {

        private ManchesterOWLSyntaxFrameRenderer ren;

        OWLEntityFrameRendererAdapter(OWLModelManager mngr, Writer writer) {
            ren = new ManchesterOWLSyntaxFrameRenderer(mngr.getOWLOntologyManager(),
                                                       mngr.getActiveOntology(),
                                                       writer,
                                                       mngr.getOWLEntityRenderer());
        }


        public void visit(OWLClass owlClass) {
            ren.write(owlClass);
        }


        public void visit(OWLObjectProperty owlObjectProperty) {
            ren.write(owlObjectProperty);
        }


        public void visit(OWLDataProperty owlDataProperty) {
            ren.write(owlDataProperty);
        }


        public void visit(OWLNamedIndividual owlNamedIndividual) {
            ren.write(owlNamedIndividual);
        }


        public void visit(OWLDatatype owlDatatype) {
            ren.write(owlDatatype);
        }


        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            ren.write(owlAnnotationProperty);
        }
    }
}
