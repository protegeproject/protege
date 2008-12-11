package org.protege.editor.owl.ui.error;

import org.protege.editor.core.ui.error.ErrorExplainer;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLParser;
import org.semanticweb.owl.io.UnparsableOntologyException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
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


    public <T extends Throwable> void handleErrorLoadingOntology(URI ont, URI loc, T e) throws Throwable {

        JComponent errorPanel;

        if (e instanceof UnparsableOntologyException){
            errorPanel = new ParseErrorPanel((UnparsableOntologyException)e, loc);
        }
        else{
            ErrorExplainer.ErrorExplanation explanation = new ErrorExplainer().getErrorExplanation(e, true);
            errorPanel = new ErrorPanel<T>(explanation, loc);
        }

        JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                        "Load Error: " + ont,
                                        errorPanel,
                                        JOptionPane.ERROR_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION,
                                        null);
    }


    public class ErrorPanel<O extends Throwable> extends JPanel{

        private JTextComponent errorConsole;

        private JSplitPane splitPane;

        private JComponent errorMessageComponent;

        private JButton stackTraceButton;

        private boolean hidden = true;


        public ErrorPanel(final ErrorExplainer.ErrorExplanation<O> explanation, URI loc) {

            setLayout(new BorderLayout(12, 12));
            setBorder(new EmptyBorder(7, 7, 7, 7));

            JEditorPane errorMessagePanel = new JEditorPane();
            errorMessagePanel.setEditable(false);
            errorMessagePanel.setText(explanation.getMessage());
            errorMessagePanel.setBorder(new EmptyBorder(7, 7, 7, 7));
            JScrollPane messageScroller = new JScrollPane(errorMessagePanel);

            errorMessageComponent = new JPanel(new BorderLayout(7, 7));
            errorMessageComponent.add(messageScroller, BorderLayout.CENTER);

            Box expandPanel = new Box(BoxLayout.LINE_AXIS);
            expandPanel.add(Box.createHorizontalGlue());
            stackTraceButton = new JButton("Show stacktrace");
            stackTraceButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent event) {
                    if (hidden){
                        showStackTrace(explanation.getCause());
                    }
                    else{
                        hideStackTrace();
                    }
                }
            });
            expandPanel.add(stackTraceButton);

            add(errorMessageComponent, BorderLayout.CENTER);
            add(expandPanel, BorderLayout.SOUTH);
        }


        private void hideStackTrace(){
            hidden = true;
            remove(splitPane);
            add(errorMessageComponent, BorderLayout.CENTER);
            stackTraceButton.setText("Show stacktrace");
            repaint();
        }

        private void showStackTrace(Throwable exception){
            hidden = false;
            remove(errorMessageComponent);

            if (errorConsole == null){
                errorConsole = new JTextArea(20, 60);
                errorConsole.setBorder(new EmptyBorder(7, 7, 7, 7));

                JComponent stackTraceComponent = new JPanel(new BorderLayout(7, 7));
                stackTraceComponent.add(new JLabel("Stack Trace:"), BorderLayout.NORTH);
                stackTraceComponent.add(new JScrollPane(errorConsole), BorderLayout.CENTER);

                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                           errorMessageComponent,
                                           stackTraceComponent);

                splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));

                setErrorMessage(exception);
            }
            else{
                splitPane.setTopComponent(errorMessageComponent);
            }

            add(splitPane, BorderLayout.CENTER);

            splitPane.setDividerLocation(0.3);
            
            stackTraceButton.setText("Hide stacktrace");

            repaint();
        }


        protected void setErrorMessage(Throwable exception) {

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


    public class ParseErrorPanel extends JPanel{

        public ParseErrorPanel(UnparsableOntologyException e, URI loc) {
            setLayout(new BorderLayout(12, 12));

            ErrorExplainer errorFilter = new ErrorExplainer();

            JTabbedPane tabs = new JTabbedPane();
            tabs.setPreferredSize(new Dimension(700, 500));
            for (OWLParser parser : e.getExceptions().keySet()){
                Throwable parseError = e.getExceptions().get(parser);
                ErrorExplainer.ErrorExplanation explanation = errorFilter.getErrorExplanation(parseError, true);
                tabs.addTab(parser.getClass().getSimpleName(), new ErrorPanel(explanation, loc));
            }

            add(new JLabel("<html>Could not parse the ontology found at: " + loc +
                           "<p>The following parsers were tried:</html>"), BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
        }
    }
}
