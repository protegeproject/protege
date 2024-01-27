package org.protege.editor.owl.model.history;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class HistoryManagerImpl implements HistoryManager {
    
    private enum ChangeType {
        UNDOING, REDOING, NORMAL
    }

    private ChangeType typeOfChangeInProgress = ChangeType.NORMAL;

    private static final Logger logger = LoggerFactory.getLogger(HistoryManager.class);

    private final OWLOntologyManager manager;


    /**
     * Holds a list of sets of changes that can be undone.
     * These are a list of "forward" changes - in other words
     * if the list contain an "add superclass" history, then the
     * required undo history is a "remove superclass" history.
     */
    private final Stack<List<OWLOntologyChange>> undoStack = new Stack<>();

    /**
     * Holds a list of sets of changes that can be redone. These
     * are changes that result from an undo operation.
     * These are a list of "forward" changes rather that the
     * "undo changes".
     */
    private final Stack<List<OWLOntologyChange>> redoStack = new Stack<>();

    private final List<UndoManagerListener> listeners = new ArrayList<>();


    public HistoryManagerImpl(OWLModelManager owlModelManager) {
        this(owlModelManager.getOWLOntologyManager());

    }
    
    public HistoryManagerImpl(OWLOntologyManager manager) {
        this.manager = manager;
        typeOfChangeInProgress = ChangeType.NORMAL;
    }


    public boolean canRedo() {
        return redoStack.size() > 0;
    }


    public boolean canUndo() {
        return undoStack.size() > 0;
    }


    public void logChanges(@Nonnull List<? extends OWLOntologyChange> changes) {
        switch (typeOfChangeInProgress) {
        case NORMAL:
            // Clear the redo stack, because we can
            // no longer redo
            redoStack.clear();
            // no break;
        case REDOING:
            // Push the changes onto the stack
            undoStack.push(new ArrayList<>(changes));
            break;
        case UNDOING:
            // In undo mode, so handleSave changes for redo.
            // Push the changes onto the redo stack.  Since these will
            // be undo changes, we need to get hold of the reverse changes
            // (The stacks, both undo and redo, should always hold the forward
            // changes).

            redoStack.push(reverseChanges(changes));
            break;
        }
        fireStateChanged();
    }


    public void redo() {
        if (canRedo()) {
            try {
                typeOfChangeInProgress = ChangeType.REDOING;
                List<OWLOntologyChange> redoChanges = redoStack.pop();
                manager.applyChanges(redoChanges);
            }
            catch (Exception e) {
                logger.error("An error occurred whilst redoing the last set of undone changes.", e);
            }
            finally {
                typeOfChangeInProgress = ChangeType.NORMAL;
            }
        }
    }


    public void undo() {
        if (canUndo()) {
            try {
                typeOfChangeInProgress = ChangeType.UNDOING;
                // Attempt to undo the changes
                List<OWLOntologyChange> changes = undoStack.pop();

                // Apply the changes
                manager.applyChanges(reverseChanges(changes));
//                // Remove changes from log
//                removeChanges(changes);
            }
            catch (Exception e) {
                logger.error("An error occurred whilst attempting to undo the last set of changes.", e);
            }
            finally {
                // No longer in undo mode
                typeOfChangeInProgress = ChangeType.NORMAL;
            }
        }
    }

    @Override
    public void clear() {
        redoStack.clear();
        undoStack.clear();
        fireStateChanged();
    }

    public void addUndoManagerListener(@Nonnull UndoManagerListener listener) {
        listeners.add(checkNotNull(listener));
    }


    public void removeUndoManagerListener(@Nonnull UndoManagerListener listener) {
        listeners.remove(listener);
    }

    @Nonnull
    public List<List<OWLOntologyChange>> getLoggedChanges() {
        List<List<OWLOntologyChange>> copyOfLog = new ArrayList<>();
        for (List<OWLOntologyChange> changes : undoStack){
            copyOfLog.add(new ArrayList<>(changes));
        }
        return copyOfLog;
    }


    public void fireStateChanged() {
        for (UndoManagerListener listener : new ArrayList<>(listeners)) {
            listener.stateChanged(this);
        }
    }
    
    private List<OWLOntologyChange> reverseChanges(List<? extends OWLOntologyChange> changes) {
        List<OWLOntologyChange> reversedChanges = new ArrayList<>();
        for (OWLOntologyChange change : changes) {
            ReverseChangeGenerator gen = new ReverseChangeGenerator();
            change.accept(gen);
            // Reverse the order
            reversedChanges.add(0, gen.getReverseChange());
        }
        return reversedChanges;
    }
}
