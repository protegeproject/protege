package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLSubClassAxiom;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 03-Feb-2007<br><br>
 *
 * @deprecated now use <code>CreateNewChildAction</code>
 */
public class CreateSubClassAction extends SelectedOWLClassAction {

    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialiseAction() throws Exception {

    }


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public void dispose() {
        super.dispose();
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        OWLClass newClass = set.getOWLEntity();
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        OWLSubClassAxiom ax = getOWLDataFactory().getOWLSubClassAxiom(newClass, selectedClass);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    }
}
