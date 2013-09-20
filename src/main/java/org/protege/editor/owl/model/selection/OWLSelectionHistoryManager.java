package org.protege.editor.owl.model.selection;

import javax.swing.event.ChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLSelectionHistoryManager {

    public boolean canGoBack();


    public void goBack();


    public boolean canGoForward();


    public void goForward();


    public void dispose();


    public void addChangeListener(ChangeListener changeListener);


    public void removeChangeListener(ChangeListener changeListener);
}
