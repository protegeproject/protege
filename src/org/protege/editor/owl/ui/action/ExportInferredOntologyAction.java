package org.protege.editor.owl.ui.action;

import org.apache.commons.lang.NotImplementedException;

import java.awt.event.ActionEvent;
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
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyAction extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        throw new NotImplementedException();
        // @@TODO v3 port
//        try {
//            ExportInferredOntologyWizard wizard = new ExportInferredOntologyWizard(getOWLEditorKit());
//            int ret = wizard.showModalDialog();
//            if (ret != Wizard.FINISH_RETURN_CODE) {
//                return;
//            }
//            List<InferredAxiomGenerator<? extends OWLAxiom>> inferredAxiomGenerator = wizard.getInferredAxiomGenerators();
//
//            OWLOntologyManager tempMan = OWLManager.createOWLOntologyManager();
//            InferredOntologyGenerator gen = new InferredOntologyGenerator(getOWLModelManager().getReasoner(),
//                                                                          inferredAxiomGenerator);
//            OWLOntology ont = tempMan.createOntology(wizard.getOntologyURI());
//
//            gen.fillOntology(tempMan, ont);
//
//            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
//            if (wizard.isIncludeAnnotations()) {
//                for (OWLOntology o : getOWLModelManager().getReasoner().getLoadedOntologies()) {
//                    for (OWLAnnotationAxiom ax : o.getAnnotationAxioms()) {
//                        if (!(ax instanceof OWLOntologyAnnotationAxiom)) {
//                            changes.add(new AddAxiom(ont, ax));
//                        }
//                        else {
//                            OWLOntologyAnnotationAxiom newAx = getOWLDataFactory().getOWLOntologyAnnotationAxiom(ont,
//                                                                                                                 ((OWLOntologyAnnotationAxiom) ax).getAnnotation());
//                            changes.add(new AddAxiom(ont, newAx));
//                        }
//                    }
//                }
//            }
//            if (wizard.isIncludeAssertedLogicalAxioms()) {
//                for (OWLOntology o : getOWLModelManager().getReasoner().getLoadedOntologies()) {
//                    for (OWLLogicalAxiom ax : o.getLogicalAxioms()) {
//                        changes.add(new AddAxiom(ont, ax));
//                    }
//                }
//            }
//            tempMan.applyChanges(changes);
//
//            tempMan.saveOntology(ont, wizard.getFormat(), wizard.getPhysicalURI());
//
//            JOptionPane.showMessageDialog(getWorkspace(),
//                                          "The inferred axioms have been exported as an ontology to \n" + wizard.getPhysicalURI(),
//                                          "Export finished",
//                                          JOptionPane.INFORMATION_MESSAGE);
//        }
//        catch (OWLOntologyCreationException e1) {
//            JOptionPane.showMessageDialog(getWorkspace(),
//                                          "Could not create ontology:\n" + e1.getMessage(),
//                                          "Error",
//                                          JOptionPane.ERROR_MESSAGE);
//        }
//        catch (InferredAxiomGeneratorException e1) {
//            JOptionPane.showMessageDialog(getWorkspace(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//        catch (OWLOntologyChangeException e1) {
//            ProtegeApplication.getErrorLog().logError(e1);
//        }
//        catch (OWLReasonerException e1) {
//            JOptionPane.showMessageDialog(getWorkspace(),
//                                          "The selected reasoner does not support the following queries: " + e1.getMessage(),
//                                          "Error",
//                                          JOptionPane.ERROR_MESSAGE);
//        }
//        catch (OWLOntologyStorageException e1) {
//            ProtegeApplication.getErrorLog().logError(e1);
//        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
