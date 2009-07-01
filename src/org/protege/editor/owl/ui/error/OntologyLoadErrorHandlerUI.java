package org.protege.editor.owl.ui.error;

import de.uulm.ecs.ai.owl.krssparser.KRSS2OWLParser;
import de.uulm.ecs.ai.owl.krssparser.KRSS2OntologyFormat;
import edu.unika.aifb.rdf.api.syntax.RDFParserException;
import org.coode.owl.owlxmlparser.OWLXMLParser;
import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParser;
import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParserException;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyParser;
import org.coode.owlapi.obo.parser.OBOOntologyFormat;
import org.coode.owlapi.obo.parser.OBOParser;
import org.coode.owlapi.obo.parser.OBOParserException;
import org.coode.owlapi.rdfxml.parser.RDFXMLParser;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.protege.editor.core.ui.error.ErrorExplainer;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.io.SyntaxGuesser;
import org.protege.editor.owl.model.util.URIUtilities;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import uk.ac.manchester.cs.owl.owlapi.turtle.parser.TurtleParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    private String lastSelectedParser = null;

    private ErrorExplainer errorExplainer;


    public OntologyLoadErrorHandlerUI(OWLEditorKit owlEditorKit) {
        eKit = owlEditorKit;
    }


    public <T extends Throwable> void handleErrorLoadingOntology(OWLOntologyID ontologyID, URI loc, T e) throws Throwable {

        if (e instanceof UnparsableOntologyException){
            errorExplainer = createErrorExplainer();
            ParseErrorsPanel errorPanel = new ParseErrorsPanel((UnparsableOntologyException)e, loc);
            JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                            "Load Error: " + ontologyID,
                                            errorPanel,
                                            JOptionPane.ERROR_MESSAGE,
                                            JOptionPane.DEFAULT_OPTION,
                                            null);
            lastSelectedParser = errorPanel.tabs.getTitleAt(errorPanel.tabs.getSelectedIndex());
        }
        else{
            ErrorExplainer.ErrorExplanation explanation = new ErrorExplainer().getErrorExplanation(e, true);
            ErrorPanel<T> errorPanel = new ErrorPanel<T>(explanation, loc);
            JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                            "Load Error: " + ontologyID,
                                            errorPanel,
                                            JOptionPane.ERROR_MESSAGE,
                                            JOptionPane.DEFAULT_OPTION,
                                            null);
        }
    }

    
    private ErrorExplainer createErrorExplainer() {
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

        return errorFilter;
    }


    public class ParseErrorsPanel extends JPanel{

        private JTabbedPane tabs;

        private Map<Class<? extends OWLOntologyFormat>, String> format2ParserMap = new HashMap<Class<? extends OWLOntologyFormat>, String>();

        public ParseErrorsPanel(UnparsableOntologyException e, final URI loc) {
            setLayout(new BorderLayout(12, 12));

            // hack as there is no way to get from an ontology format to a parser
            // this must be updated if the supported parsers are changed
            format2ParserMap.put(RDFXMLOntologyFormat.class, RDFXMLParser.class.getSimpleName());
            format2ParserMap.put(OWLXMLOntologyFormat.class, OWLXMLParser.class.getSimpleName());
            format2ParserMap.put(ManchesterOWLSyntaxOntologyFormat.class, ManchesterOWLSyntaxOntologyParser.class.getSimpleName());
            format2ParserMap.put(TurtleOntologyFormat.class, TurtleParser.class.getSimpleName());
            format2ParserMap.put(OWLFunctionalSyntaxOntologyFormat.class, OWLFunctionalSyntaxParser.class.getSimpleName());
            format2ParserMap.put(KRSS2OntologyFormat.class, KRSS2OWLParser.class.getSimpleName());
            format2ParserMap.put(OBOOntologyFormat.class, OBOParser.class.getSimpleName());


            tabs = new JTabbedPane();
            tabs.setPreferredSize(new Dimension(700, 500));

            SourcePanel sourcePanel = null;
            try {
                URL physicalLoc = loc.toURL();
                sourcePanel = new SourcePanel(physicalLoc);
                sourcePanel.setBorder(new EmptyBorder(0, 7, 0, 7));
            }
            catch (MalformedURLException e1) {
                throw new RuntimeException(e1);
            }


            final java.util.List<OWLParser> parsers = new ArrayList<OWLParser>(e.getExceptions().keySet());
// sort
//            Collections.sort(parsers, new Comparator<OWLParser>(){
//                public int compare(OWLParser p1, OWLParser p2) {
//                    return p1.getClass().getSimpleName().compareTo(p2.getClass().getSimpleName());
//                }
//            });

            for (OWLParser parser : parsers){
                Throwable parseError = e.getExceptions().get(parser);
                ErrorExplainer.ErrorExplanation explanation = errorExplainer.getErrorExplanation(parseError, true);
                final ErrorPanel errorPanel = new ParseErrorPanel(explanation, loc, sourcePanel);
                tabs.addTab(parser.getClass().getSimpleName(), errorPanel);
            }

            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    InputStream is = null;
                    try {
                        is = URIUtilities.getInputStream(loc);
                        SyntaxGuesser syntaxGuesser = new SyntaxGuesser();
                        OWLOntologyFormat syntax = syntaxGuesser.getSyntax(is);
                        if (syntax != null){
                            String parserName = format2ParserMap.get(syntax.getClass());
                            if (parserName != null){
                                lastSelectedParser = parserName;
                            }

                            for (int i=0; i<tabs.getTabCount(); i++){
                                if (lastSelectedParser.equals(tabs.getTitleAt(i))){
                                    tabs.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    }
                    catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                    finally{
                        if (is != null){
                            try {
                                is.close();
                            }
                            catch (IOException e1) {
                                throw new RuntimeException(e1);
                            }
                        }
                    }
                }
            });

            add(new JLabel("<html>Could not parse the ontology found at: " + loc +
                           "<p>The following parsers were tried:</html>"), BorderLayout.NORTH);

            add(tabs, BorderLayout.CENTER);
        }
    }
}
