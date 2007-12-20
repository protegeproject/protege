package org.protege.editor.owl.ui.framelist;

import org.semanticweb.owl.debugging.DebuggerDescriptionGenerator;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.protege.editor.owl.ui.explanation.ExplanationGeneratorPanel;
import org.protege.editor.owl.OWLEditorKit;
import com.clarkparsia.explanation.SatisfiabilityConverter;
import com.clarkparsia.explanation.BlackBoxExplanation;
import com.clarkparsia.explanation.HSTExplanationGenerator;
import com.clarkparsia.explanation.ExplanationGenerator;

import javax.swing.*;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class OWLFrameListExplanationHandler implements ExplanationHandler {

    private OWLEditorKit editorKit;


    public OWLFrameListExplanationHandler(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }


    public void handleExplain(OWLAxiom ax) {
        DebuggerDescriptionGenerator gen = new DebuggerDescriptionGenerator(editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory());
        ax.accept(gen);
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        SatisfiabilityConverter satCon = new SatisfiabilityConverter(editorKit.getOWLModelManager().getOWLDataFactory());
        final OWLDescription desc = satCon.convert(ax);

        BlackBoxExplanation bbexp = new BlackBoxExplanation(man);
        OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
        bbexp.setOntology(ontology);
        OWLReasoner reasoner = editorKit.getOWLModelManager().getReasoner();
        bbexp.setReasoner(reasoner);
        bbexp.setReasonerFactory(editorKit.getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory());


        HSTExplanationGenerator hstGen = new HSTExplanationGenerator(bbexp);

        final ExplanationGenerator debugger = hstGen;

        ExplanationGeneratorPanel genPan = new ExplanationGeneratorPanel(ax, editorKit);
        hstGen.setProgressMonitor(genPan.getProgressMonitor());
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, editorKit.getWorkspace()));
        dlg.setContentPane(genPan);

        Runnable r = new Runnable() {
            public void run() {
                debugger.getExplanations(desc);
            }
        };

        Thread t = new Thread(r);
        t.start();
        dlg.pack();
        dlg.setVisible(true);
    }
}
