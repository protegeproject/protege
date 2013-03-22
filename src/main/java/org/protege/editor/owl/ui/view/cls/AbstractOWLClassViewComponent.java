package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An <code>OWLView</code> that displays information about classes
 */
public abstract class AbstractOWLClassViewComponent extends AbstractOWLSelectionViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -3168286621261106535L;


    final public void initialiseView() throws Exception {
        initialiseClassView();
    }


    public abstract void initialiseClassView() throws Exception;


    final protected OWLEntity updateView() {
        OWLClass cls = updateView(getSelectedOWLClass());
        if (cls != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return cls;
    }


    /**
     * This method is called to request that the view is updated with
     * the specified class.
     * @param selectedClass The class that the view should be updated with.  Note
     *                      that this may be <code>null</code>, which indicates that the view should
     *                      be cleared
     * @return The actual class that the view is displaying after it has been updated
     *         (may be <code>null</code>)
     */
    protected abstract OWLClass updateView(OWLClass selectedClass);


    /**
     * A convenience method.
     * Gets the selected <code>OWLClass</code> from the main
     * <code>OWLSelectionModel</code>.  (This is not the class
     * that the view is displaying)
     * @return The selected <code>OWLClass</code>, or
     *         <code>null</code> if no class is selected.
     */
    protected OWLClass getSelectedOWLClass() {
        return getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }


    protected boolean isOWLClassView() {
        return true;
    }
}
