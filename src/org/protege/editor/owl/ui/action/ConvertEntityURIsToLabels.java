package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.refactor.ontology.ConvertEntityURIsToIdentifierPattern;
import org.protege.editor.owl.model.refactor.ontology.OntologyTargetResolver;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ConvertEntityURIsToLabels extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        ConvertEntityURIsToIdentifierPattern converter = new ConvertEntityURIsToIdentifierPattern(getOWLModelManager(),
                                                                                                  getOWLModelManager().getOntologies());
        
        converter.setOntologyResolver(new OntologyTargetResolver(){
            public OWLOntology resolve(OWLEntity entity, Set<OWLOntology> ontologies) {
                return handleResolveTarget(entity, ontologies);
            }
        });

        converter.performConversion();

        converter.dispose();
    }


    private OWLOntology handleResolveTarget(OWLEntity entity, Set<OWLOntology> ontologies) {
        OWLOntologySelectorPanel ontPanel = new OWLOntologySelectorPanel(getOWLEditorKit(), ontologies);
        ontPanel.setSelection(ontologies.iterator().next());
        ontPanel.setMultipleSelectionEnabled(false);
        int ret = new UIHelper(getOWLEditorKit()).showDialog("Select an ontology in which to add a label for " +
                                                             getOWLModelManager().getRendering(entity),
                                                             ontPanel);
        if (ret == JOptionPane.OK_OPTION) {
            return ontPanel.getSelectedOntology();
        }
        else {
            return null;
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
