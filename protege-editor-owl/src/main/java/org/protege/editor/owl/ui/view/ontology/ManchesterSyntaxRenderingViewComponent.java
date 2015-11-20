package org.protege.editor.owl.ui.view.ontology;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxRenderer;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.Writer;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 13-Aug-2007<br><br>
 */
public class ManchesterSyntaxRenderingViewComponent extends AbstractOntologyRenderingViewComponent {
    private static final long serialVersionUID = 5547044902285761296L;

    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        ManchesterOWLSyntaxRenderer ren = new ManchesterOWLSyntaxRenderer();
        ren.render(ontology, writer);
        writer.flush();
    }
}
