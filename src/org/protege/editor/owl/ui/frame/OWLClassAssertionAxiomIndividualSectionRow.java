package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListRemoveTypeButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomIndividualSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> {

    public OWLClassAssertionAxiomIndividualSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClass, OWLClassAssertionAxiom, OWLIndividual> section,
                                                      OWLOntology ontology, OWLClass rootObject,
                                                      OWLClassAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLIndividual> getObjectEditor() {
        return null;
    }


    protected OWLClassAssertionAxiom createAxiom(OWLIndividual editedObject) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(editedObject, getRoot());
    }


    public boolean isFixedHeight() {
        return true;
    }
    
    @Override
    public boolean isDeleteable() {
        return false;
    }
    
    @Override
    public List<MListButton> getAdditionalButtons() {
        List<MListButton> buttons = new ArrayList<MListButton>();
        if (isEditable()) {
            
            buttons.add(new MListRemoveTypeButton(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    OWLModelManager manager = getOWLEditorKit().getModelManager();
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    OWLIndividual individual = getAxiom().getIndividual();
                    
                    manager.applyChange(new RemoveAxiom(getOntology(), getAxiom()));
                    
                    Set<OWLClassAssertionAxiom> typeAxioms = getOntology().getClassAssertionAxioms(individual);
                    if (typeAxioms == null || typeAxioms.isEmpty()) { // should actually check the import tree?
                        OWLClassAssertionAxiom declaration = factory.getOWLClassAssertionAxiom(individual, factory.getOWLThing());
                        manager.applyChange(new AddAxiom(getOntology(), declaration));
                    }
                }
                
            }));
        }
        return buttons;
    }

    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getIndividual());
    }
}
