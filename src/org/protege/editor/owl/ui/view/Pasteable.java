package org.protege.editor.owl.ui.view;

import java.util.List;

import javax.swing.event.ChangeListener;

import org.protege.editor.owl.ui.action.ActionTarget;
import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface Pasteable extends ActionTarget {

    boolean canPaste(List<OWLObject> objects);


    void pasteObjects(List<OWLObject> objects);


    void addChangeListener(ChangeListener changeListener);


    void removeChangeListener(ChangeListener changeListener);
}
