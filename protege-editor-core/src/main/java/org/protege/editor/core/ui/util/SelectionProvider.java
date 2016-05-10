package org.protege.editor.core.ui.util;

import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface SelectionProvider {

    public List getSelection();


    // Add listener
    public void addSelectionProviderListener(SelectionProviderListener listener);


    // Remove listener
    public void removeSelectionProviderListener(SelectionProviderListener listener);


}
