package org.protege.editor.owl.ui.error;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLParser;
import org.semanticweb.owl.io.UnparsableOntologyException;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;
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
 * Date: Aug 28, 2008<br><br>
 */
public class OntologyLoadErrorHandlerUI implements OntologyLoadErrorHandler {

    private OWLEditorKit eKit;


    public OntologyLoadErrorHandlerUI(OWLEditorKit owlEditorKit) {
        eKit = owlEditorKit;
    }


    public void handleErrorLoadingOntology(URI ont, OWLOntologyCreationException e) throws OWLOntologyCreationException {
        if (e instanceof UnparsableOntologyException){
            UnparsableOntologyException parseException = ((UnparsableOntologyException)e);
            LoadErrorPanel errorPanel = new LoadErrorPanel(parseException);

            JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                            "Load Error: " + ont,
                                            errorPanel,
                                            JOptionPane.ERROR_MESSAGE,
                                            JOptionPane.DEFAULT_OPTION,
                                            null);
        }
        else{
            throw e; // as we don't handle the error
        }
    }


    public class LoadErrorPanel extends JPanel{

        private JComboBox formatPanel;

        private Map<OWLParser, Exception> exceptionMap;

        private JTextComponent errorConsole;


        public LoadErrorPanel(UnparsableOntologyException e) {
            setLayout(new BorderLayout(6, 6));
            
            JComponent messageComponent = new JLabel("Could not load the ontology. The following parsers were tried:");

            add(messageComponent, BorderLayout.NORTH);

            exceptionMap = e.getExceptions();
            formatPanel = new JComboBox();
            for (OWLParser parser : exceptionMap.keySet()){
                formatPanel.addItem(parser);
            }
            formatPanel.setRenderer(new DefaultListCellRenderer(){
                public Component getListCellRendererComponent(JList jList, Object val, int i, boolean b, boolean b1) {
                    return super.getListCellRendererComponent(jList, val.getClass().getSimpleName(), i, b, b1);
                }
            });
            formatPanel.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent event) {
                    updateErrorMessage();
                }
            });

            errorConsole = new JTextArea(20, 60);

            JComponent exceptionPanel = new JPanel(new BorderLayout(6, 6));
            exceptionPanel.add(formatPanel, BorderLayout.NORTH);
            exceptionPanel.add(new JScrollPane(errorConsole), BorderLayout.CENTER);

            add(exceptionPanel, BorderLayout.CENTER);

            updateErrorMessage();
        }

        private void updateErrorMessage() {
            OWLParser parser = (OWLParser)formatPanel.getSelectedItem();
            Throwable exception = exceptionMap.get(parser);

            final StringWriter stringWriter = new StringWriter();
            final PrintWriter writer = new PrintWriter(stringWriter);

            writer.write(exception.getMessage());
            writer.write("\n\n\nFull Stack Trace\n-----------------------------------------------------------------------------------------\n\n");
            exception.printStackTrace(writer);
            writer.flush();
            errorConsole.setText(stringWriter.toString());
            errorConsole.setCaretPosition(0);
        }

    }
}
