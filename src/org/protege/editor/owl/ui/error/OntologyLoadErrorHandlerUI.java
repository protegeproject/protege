package org.protege.editor.owl.ui.error;

import org.protege.editor.core.ui.error.ErrorExplainer;
import org.protege.editor.core.ui.error.SendErrorReportHandler;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLParser;
import org.semanticweb.owl.io.UnparsableOntologyException;

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

    private ErrorExplainer errorFilter;

    private ErrorExplainer.ErrorExplanationFactory<UnparsableOntologyException> myErrorExplanationFactory =
            new ErrorExplainer.ErrorExplanationFactory<UnparsableOntologyException>(){
                public <T extends UnparsableOntologyException> ErrorExplainer.ErrorExplanation<T> createExplanation(T e) {
                    // TODO should report the location from which we tried to load
                    String message = "<html>Could not parse the ontology." +
                                     "<p>The following parsers were tried:</html>";
                    return new ErrorExplainer.ErrorExplanation<T>(e, message);
                }
            };


    public OntologyLoadErrorHandlerUI(OWLEditorKit owlEditorKit) {
        eKit = owlEditorKit;
        errorFilter = new ErrorExplainer();
        errorFilter.addExplanationFactory(UnparsableOntologyException.class, myErrorExplanationFactory);
    }


    public <T extends Throwable> void handleErrorLoadingOntology(URI ont, URI loc, T e) throws Throwable {
        ErrorExplainer.ErrorExplanation explanation = errorFilter.getErrorExplanation(e, true);

        ErrorPanel errorPanel;

        if (e instanceof UnparsableOntologyException){
            errorPanel = new ParseErrorPanel((ErrorExplainer.ErrorExplanation<UnparsableOntologyException>)explanation, loc);
        }
        else{
            errorPanel = new ErrorPanel<T>(explanation, loc);
        }

        JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                        "Load Error: " + ont,
                                        errorPanel,
                                        JOptionPane.ERROR_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION,
                                        null);
    }


    private SendErrorReportHandler getErrorReportHandler() {
        return null;
    }


    public class ErrorPanel<O extends Throwable> extends JPanel{

        private JTextComponent errorConsole;

        private JComponent clientPanel;

        private URI loc;

        private ErrorExplainer.ErrorExplanation<O> explanation;

        private String message;


        public ErrorPanel(ErrorExplainer.ErrorExplanation<O> explanation, URI loc) {
            this.explanation = explanation;
            this.loc = loc;
            this.message = explanation.getMessage();

            initialise(explanation, loc);

            updateErrorMessage();
        }


        protected void initialise(ErrorExplainer.ErrorExplanation<O> explanation, URI loc){
            setLayout(new BorderLayout(6, 6));

            JComponent messageComponent = new JLabel(getMessage());

            add(messageComponent, BorderLayout.NORTH);

            errorConsole = new JTextArea(20, 60);

            clientPanel = new JPanel(new BorderLayout(6, 6));
            clientPanel.add(new JScrollPane(errorConsole), BorderLayout.CENTER);

            add(clientPanel, BorderLayout.CENTER);
        }


        protected JComponent getClientPanel(){
            return clientPanel;
        }


        public Throwable getThrowable() {
            return explanation.getCause();
        }


        protected String getMessage() {
            return message;//"<html>Could not load the ontology found at <b>" + getLoc() + "</b>.</html>";
        }


        protected URI getLoc() {
            return loc;
        }


        protected void updateErrorMessage() {

            Throwable exception = getThrowable();

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


    public class ParseErrorPanel extends ErrorPanel<UnparsableOntologyException>{

        private JComboBox formatPanel;

        private Map<OWLParser, Throwable> exceptionMap;


        public ParseErrorPanel(ErrorExplainer.ErrorExplanation<UnparsableOntologyException> e, URI loc) {
            super(e, loc);
        }


        protected void initialise(ErrorExplainer.ErrorExplanation<UnparsableOntologyException> e, URI loc){
            super.initialise(e, loc);

            exceptionMap = e.getCause().getExceptions();
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

            getClientPanel().add(formatPanel, BorderLayout.NORTH);
        }


        public Throwable getThrowable() {
            OWLParser parser = (OWLParser)formatPanel.getSelectedItem();
            return exceptionMap.get(parser);
        }
    }
}
