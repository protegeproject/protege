package org.protege.editor.owl.ui.error;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.error.ErrorExplainer;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFParserException;
import org.semanticweb.owlapi.util.PriorityCollection;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 28, 2008<br><br>
 */
public class OntologyLoadErrorHandlerUI implements OntologyLoadErrorHandler {
	protected static final Logger LOGGER = Logger.getLogger(OntologyLoadErrorHandlerUI.class);
    private enum OPTIONS {
        OK("OK"), RELOAD("Reload");

        private String name;
        private OPTIONS(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
    
    private OWLEditorKit eKit;

    private String lastSelectedParser = null;

    private ErrorExplainer errorExplainer;

    private boolean reload = false;

    public OntologyLoadErrorHandlerUI(OWLEditorKit owlEditorKit) {
        eKit = owlEditorKit;
    }

    public boolean getReloadFlag() {
        return reload;
    }

    public void setReloadFlag(boolean reload) {
        this.reload = reload;
    }


    public <T extends Throwable> void handleErrorLoadingOntology(OWLOntologyID ontologyID, URI loc, T e) throws Throwable {
        int retVal;
        Object[] options = OPTIONS.values();
        
        if (e instanceof UnparsableOntologyException) {
            errorExplainer = createErrorExplainer();
            ParseErrorsPanel errorPanel = new ParseErrorsPanel((UnparsableOntologyException)e, loc);
            retVal = JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                                     "Load Error: "  + ontologyID,
                                                     errorPanel,
                                                     JOptionPane.ERROR_MESSAGE,
                                                     JOptionPane.DEFAULT_OPTION,
                                                     null,
                                                     options,
                                                     OPTIONS.OK);
            lastSelectedParser = errorPanel.tabs.getTitleAt(errorPanel.tabs.getSelectedIndex());
        }
        else{
            ErrorExplainer.ErrorExplanation explanation = new ErrorExplainer().getErrorExplanation(e, true);
            ErrorPanel<T> errorPanel = new ErrorPanel<T>(explanation, loc);
            LOGGER.warn(explanation.getMessage(), explanation.getCause());
            retVal = JOptionPaneEx.showConfirmDialog(eKit.getWorkspace(),
                                                     "Load Error: " + ontologyID,
                                                     errorPanel,
                                                     JOptionPane.ERROR_MESSAGE,
                                                     JOptionPane.DEFAULT_OPTION,
                                                     null,
                                                     options,
                                                     OPTIONS.OK);
        }
        if (retVal == OPTIONS.RELOAD.ordinal()) {
            setReloadFlag(true);
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
        errorFilter.addExplanationFactory(OWLParserException.class, owlParserExceptionExplanationFac);

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

        private Map<Class<? extends OWLDocumentFormat>, String> format2ParserMap = new HashMap<Class<? extends OWLDocumentFormat>, String>();

        public ParseErrorsPanel(UnparsableOntologyException e, final URI loc) {
            setLayout(new BorderLayout(12, 12));

            PriorityCollection<OWLParserFactory> ontologyParsers = eKit
                    .getOWLModelManager().getOWLOntologyManager()
                    .getOntologyParsers();
            for (OWLParserFactory p : ontologyParsers) {
                format2ParserMap.put(
                        p.getSupportedFormat().get().getClass(), 
                        p.get().getClass().getSimpleName());
            }

            tabs = new JTabbedPane();
            tabs.setPreferredSize(new Dimension(700, 500));


            final java.util.List<OWLParser> parsers = new ArrayList<OWLParser>(e.getExceptions().keySet());

            for (OWLParser parser : parsers){
                Throwable parseError = e.getExceptions().get(parser);
                ErrorExplainer.ErrorExplanation<? extends Throwable> explanation = errorExplainer.getErrorExplanation(parseError, true);
                final ErrorPanel<? extends Throwable> errorPanel = new ParseErrorPanel(explanation, loc);
                tabs.addTab(parser.getClass().getSimpleName(), errorPanel);
            }

            add(new JLabel("<html>Could not parse the ontology found at: " + loc +
                           "<p>The following parsers were tried:</html>"), BorderLayout.NORTH);

            add(tabs, BorderLayout.CENTER);
        }
    }
}
