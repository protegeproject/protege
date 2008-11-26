package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import uk.ac.manchester.cs.owl.mansyntaxrenderer.ManchesterOWLSyntaxFrameRenderer;

import javax.swing.*;
import java.awt.*;
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

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 26, 2008<br><br>
 */
public class ManchesterSyntaxFrameView extends AbstractOWLSelectionViewComponent {

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
                renderOWLEntity(owlEntity, new BufferedWriter(w));
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
        ManchesterOWLSyntaxFrameRenderer ren = new ManchesterOWLSyntaxFrameRenderer(getOWLModelManager().getOWLOntologyManager(),
                                                                                    getOWLModelManager().getActiveOntology(),
                                                                                    writer);
        if (entity.isOWLClass()){
            ren.write(entity.asOWLClass());
        }
        else if (entity.isOWLObjectProperty()){
            ren.write(entity.asOWLObjectProperty());
        }
        else if (entity.isOWLDataProperty()){
            ren.write(entity.asOWLDataProperty());
        }
        else if (entity.isOWLIndividual()){
            ren.write(entity.asOWLIndividual());
        }
        writer.flush();
    }


    public void disposeView() {
        // do nothing
    }
}
