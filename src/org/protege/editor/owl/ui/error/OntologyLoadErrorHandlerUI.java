package org.protege.editor.owl.ui.error;

import edu.unika.aifb.rdf.api.syntax.RDFParserException;
import org.coode.obo.parser.OBOParserException;
import org.coode.owl.functionalparser.OWLFunctionalSyntaxParserException;
import org.protege.editor.core.ui.error.ErrorExplainer;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.io.OWLParser;
import org.semanticweb.owl.io.OWLParserException;
import org.semanticweb.owl.io.UnparsableOntologyException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
            errorPanel = new ParseErrorsPanel((UnparsableOntologyException)e, loc);
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


    public class ParseErrorsPanel extends JPanel{

        public ParseErrorsPanel(UnparsableOntologyException e, URI loc) {
            setLayout(new BorderLayout(12, 12));

            ErrorExplainer errorFilter = new ErrorExplainer();
            // RDF/XML syntax
            errorFilter.addExplanationFactory(RDFParserException.class, new ErrorExplainer.ErrorExplanationFactory<RDFParserException>(){
                public <T extends RDFParserException> ErrorExplainer.ErrorExplanation<T> createExplanation(T throwable) {
                    return new ErrorExplainer.ParseErrorExplanation(throwable, throwable.getMessage(), throwable.getLineNumber(), throwable.getColumnNumber());
                }
            });
            // Will catch functional syntax/OBO but only use it for the specified exceptions (below)
            ErrorExplainer.ErrorExplanationFactory<OWLParserException> owlParserExceptionExplanationFac = new ErrorExplainer.ErrorExplanationFactory<OWLParserException>(){
                public <T extends OWLParserException> ErrorExplainer.ErrorExplanation<T> createExplanation(T throwable) {
                    return new ErrorExplainer.ParseErrorExplanation(throwable, throwable.getMessage(), throwable.getLineNumber(), 0);
                }
            };
            errorFilter.addExplanationFactory(OWLFunctionalSyntaxParserException.class, owlParserExceptionExplanationFac);
            errorFilter.addExplanationFactory(OBOParserException.class, owlParserExceptionExplanationFac);

            // Manchester Syntax
            errorFilter.addExplanationFactory(ParserException.class, new ErrorExplainer.ErrorExplanationFactory<ParserException>(){
                public <T extends ParserException> ErrorExplainer.ErrorExplanation<T> createExplanation(T throwable) {
                    return new ErrorExplainer.ParseErrorExplanation(throwable, throwable.getMessage(), throwable.getLineNumber(), throwable.getColumnNumber());
                }
            });

            SourcePanel sourcePanel = null;
            try {
                URL physicalLoc = loc.toURL();
                sourcePanel = new SourcePanel(physicalLoc);
                sourcePanel.setBorder(new EmptyBorder(0, 7, 0, 7));
            }
            catch (MalformedURLException e1) {
                throw new RuntimeException(e1);
            }

            JTabbedPane tabs = new JTabbedPane();
            tabs.setPreferredSize(new Dimension(700, 500));
            for (OWLParser parser : e.getExceptions().keySet()){
                Throwable parseError = e.getExceptions().get(parser);
                ErrorExplainer.ErrorExplanation explanation = errorFilter.getErrorExplanation(parseError, true);
                final ErrorPanel errorPanel = new ParseErrorPanel(explanation, loc, sourcePanel);
                tabs.addTab(parser.getClass().getSimpleName(), errorPanel);
            }

            add(new JLabel("<html>Could not parse the ontology found at: " + loc +
                           "<p>The following parsers were tried:</html>"), BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
        }
    }
}
