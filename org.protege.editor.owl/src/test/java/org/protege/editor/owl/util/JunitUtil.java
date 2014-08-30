package org.protege.editor.owl.util;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class JunitUtil {
    private static Logger log = Logger.getLogger(JunitUtil.class);
    
    public static void enableDebug() {
        log.setLevel(Level.DEBUG);
    }

    public static void printOntology(OWLOntologyManager manager, OWLOntology ontology) {
        if (!log.isDebugEnabled()) {
            return;
        }
        try {
            StringWriter writer = new StringWriter();
            OWLFunctionalSyntaxRenderer renderer = new OWLFunctionalSyntaxRenderer();
            renderer.render(ontology, writer);
            log.debug(writer.toString());
        }
        catch (OWLRendererException ore) {
            log.debug("exceptions ignored while logging", ore);
        }
    }
}
