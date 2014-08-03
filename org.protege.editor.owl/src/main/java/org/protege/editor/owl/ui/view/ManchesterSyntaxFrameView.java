package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxFrameRenderer;

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


    @Override
    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));

        scroller = new JScrollPane(textArea);
        add(scroller, BorderLayout.CENTER);

        updateView();
    }

    @Override
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
                    @Override
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


    @Override
    public void disposeView() {
        // do nothing
    }


    class OWLEntityFrameRendererAdapter implements OWLEntityVisitor {

        private ManchesterOWLSyntaxFrameRenderer ren;

        OWLEntityFrameRendererAdapter(OWLModelManager mngr, Writer writer) {
            ren = new ManchesterOWLSyntaxFrameRenderer(
                    mngr.getActiveOntology(),
                                                       writer,
                                                       mngr.getOWLEntityRenderer());
        }


        @Override
        public void visit(OWLClass owlClass) {
            ren.write(owlClass);
        }


        @Override
        public void visit(OWLObjectProperty owlObjectProperty) {
            ren.write(owlObjectProperty);
        }


        @Override
        public void visit(OWLDataProperty owlDataProperty) {
            ren.write(owlDataProperty);
        }


        @Override
        public void visit(OWLNamedIndividual owlNamedIndividual) {
            ren.write(owlNamedIndividual);
        }


        @Override
        public void visit(OWLDatatype owlDatatype) {
            ren.write(owlDatatype);
        }


        @Override
        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            ren.write(owlAnnotationProperty);
        }
    }
}
