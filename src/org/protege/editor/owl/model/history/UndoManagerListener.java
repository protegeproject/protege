package org.protege.editor.owl.model.history;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface UndoManagerListener {

    /**
     * Called when the state of the undo manager has changed.
     * This usually indicates that the availability of undo or
     * redo has changed.
     *
     * @param source
     */
    public void stateChanged(HistoryManager source);
}
