package org.protege.editor.owl.ui.view;

import java.util.List;

import javax.swing.event.ChangeListener;

import org.protege.editor.owl.ui.action.ActionTarget;
import org.semanticweb.owl.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public interface Cuttable extends ActionTarget {

    boolean canCut();


    List<OWLObject> cutObjects();


    void addChangeListener(ChangeListener changeListener);


    void removeChangeListener(ChangeListener changeListener);
}
