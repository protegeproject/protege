package org.protege.editor.owl.model.history;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class HistoryManagerImpl implements HistoryManager {

    private static Logger logger = Logger.getLogger(HistoryManager.class);

    private OWLModelManager owlModelManager;

    //private Map<OWLEntity, Set<OWLOntologyChange>> changeMap;

    //private ChangedEntityExtractor changedEntityExtractor;


    private boolean inUndoMode;

    /**
     * Holds a list of sets of changes that can be undone.
     * These are a list of "forward" changes - in other words
     * if the list contain an "add superclass" history, then the
     * required undo history is a "remove superclass" history.
     */
    private Stack<List<OWLOntologyChange>> undoStack;

    /**
     * Holds a list of sets of changes that can be redone. These
     * are changes that result from an undo operation.
     * These are a list of "forward" changes rather that the
     * "undo changes".
     */
    private Stack<List<OWLOntologyChange>> redoStack;

    private List<UndoManagerListener> listeners;

    JList viewList;


    public HistoryManagerImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        undoStack = new Stack<List<OWLOntologyChange>>();
        redoStack = new Stack<List<OWLOntologyChange>>();
        listeners = new ArrayList<UndoManagerListener>();
        inUndoMode = false;
        //changeMap = new HashMap<OWLEntity, Set<OWLOntologyChange>>();
        //changedEntityExtractor = new ChangedEntityExtractor();
    }


    public boolean canRedo() {
        return redoStack.size() > 0;
    }


    public boolean canUndo() {
        return undoStack.size() > 0;
    }


    public void logChanges(List<? extends OWLOntologyChange> changes) {
        // If we aren't in the undo mode, then push the changes onto the undo stack
        if (!inUndoMode) {
            // Clear the redo stack, because we can
            // no longer redo
            redoStack.clear();

            // Push the changes onto the stack
            undoStack.push(new ArrayList<OWLOntologyChange>(changes));

//            // Store the changes against the entity
//            storeChanges(changes);

        }
        else {
            // In undo mode, so handleSave changes for redo.
            // Push the changes onto the redo stack.  Since these will
            // be undo changes, we need to get hold of the reverse changes
            // (The stacks, both undo and redo, should always hold the forward
            // changes).
            List<OWLOntologyChange> redoChanges = new ArrayList<OWLOntologyChange>();
            for (OWLOntologyChange change : changes) {
                ReverseChangeGenerator gen = new ReverseChangeGenerator();
                change.accept(gen);
                // Reverse the order
                redoChanges.add(0, gen.getReverseChange());
            }
            redoStack.push(new ArrayList<OWLOntologyChange>(redoChanges));
        }
        fireStateChanged();
    }

//    private void storeChanges(List<OWLOntologyChange> changes) {
//
//        try {
//            for (OWLOntologyChange change : changes) {
//                change.accept(changedEntityExtractor);
//                for (OWLEntity entity : changedEntityExtractor.getChangedEntities()) {
//                    getChanges(entity).add(change);
//                }
//            }
//        } catch (OWLException e) {
//            logger.error(e);
//        }
//    }

//    private void removeChanges(List<OWLOntologyChange> changes) {
//        try {
//            for (OWLOntologyChange change : changes) {
//                change.accept(changedEntityExtractor);
//                for (OWLEntity entity : changedEntityExtractor.getChangedEntities()) {
//                    Set<OWLOntologyChange> storedChanges = getChanges(entity);
//                    storedChanges.remove(change);
//                    if (storedChanges.isEmpty()) {
//                        changeMap.remove(entity);
//                    }
//                }
//            }
//        } catch (OWLException e) {
//            logger.error(e);
//        }
//    }

//    public Set<OWLOntologyChange> getChanges(OWLEntity entity) {
//        Set<OWLOntologyChange> changes = changeMap.get(entity);
//        if (changes == null) {
//            changes = new HashSet<OWLOntologyChange>();
//            changeMap.put(entity, changes);
//        }
//        return changes;
//    }


    public void redo() {
        if (canRedo()) {
            try {
                List<OWLOntologyChange> redoChanges = redoStack.pop();
                owlModelManager.applyChanges(redoChanges);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public void undo() {
        if (canUndo()) {
            try {
                inUndoMode = true;
                // Attempt to undo the changes
                List<OWLOntologyChange> changes = undoStack.pop();
                List<OWLOntologyChange> undoChanges = new ArrayList<OWLOntologyChange>();
                for (OWLOntologyChange change : changes) {
                    ReverseChangeGenerator gen = new ReverseChangeGenerator();
                    change.accept(gen);
                    // Reverse the order
                    undoChanges.add(0, gen.getReverseChange());
                }
                // Apply the changes
                owlModelManager.applyChanges(undoChanges);
//                // Remove changes from log
//                removeChanges(changes);
            }
            catch (Exception e) {
                logger.error(e);
            }
            finally {
                // No longer in undo mode
                inUndoMode = false;
            }
        }
    }


    public void addUndoManagerListener(UndoManagerListener listener) {
        listeners.add(listener);
    }


    public void removeUndoManagerListener(UndoManagerListener listener) {
        listeners.remove(listener);
    }


    public List<List<OWLOntologyChange>> getLoggedChanges() {
        List<List<OWLOntologyChange>> copyOfLog = new ArrayList<List<OWLOntologyChange>>();
        for (List<OWLOntologyChange> changes : undoStack){
            copyOfLog.add(new ArrayList<OWLOntologyChange>(changes));
        }
        return copyOfLog;
    }


    public void fireStateChanged() {
        for (UndoManagerListener listener : new ArrayList<UndoManagerListener>(listeners)) {
            listener.stateChanged(this);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Change logging stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

//    public boolean isEntityChanged(OWLEntity entity) {
//        throw new OWLException("Not implemented");
//        //return changeMap.containsKey(entity);
//    }
//
//    public Set<OWLEntity> getChangedEntities() {
//        return changeMap.keySet();
//    }

}
