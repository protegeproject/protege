package org.protege.editor.owl.model.history;

import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface HistoryManager {

    /**
     * Logs the specified list of changes so that they can be undone at a later point in time.
     * @param changes The changes to be logged.
     */
    void logChanges(@Nonnull List<? extends OWLOntologyChange> changes);

    /**
     * Determines whether or not it is possible to perform an undo operation.
     * @return {@code true} if it is possible to perform an undo operation, otherwise {@code false}.
     */
    boolean canUndo();


    /**
     * Performs an undo operation, undoing the last set of changes.  If it is not possible to perform an undo
     * then nothing will happen.
     */
    void undo();


    /**
     * Determines whether or not it is possible to perform a redo operation.
     * @return {@code true} if it is possible to perform a redo operation, otherwise {@code false}.
     */
    boolean canRedo();

    /**
     * Performs a redo operation, redoing the last set of undone changes.  If it is not possible to perfom a redo
     * then nothing will happen.
     */
    void redo();

    /**
     * Clears the history.  Warning: Clearing the history will prevent the user from undoing any of their previous
     * changes.
     */
    void clear();

    /**
     * Gets a list of the lists of changes that are currently in the undo stack.
     * @return The changes logged in the undo stack.
     */
    @Nonnull
    List<List<OWLOntologyChange>> getLoggedChanges();

    /**
     * Adds a listener to this manager.
     * @param listener The listener.
     */
    void addUndoManagerListener(@Nonnull UndoManagerListener listener);

    /**
     * Removes a previously added listener
     * @param listener The listener.
     */
    void removeUndoManagerListener(@Nonnull UndoManagerListener listener);

}
