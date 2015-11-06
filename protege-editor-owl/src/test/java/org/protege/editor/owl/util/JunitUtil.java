package org.protege.editor.owl.util;

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer;
import org.slf4j.Logger;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

public class JunitUtil {

    private static Logger log = LoggerFactory.getLogger(JunitUtil.class);
    
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
