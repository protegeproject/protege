package org.protege.editor.owl.model.history;

import java.util.List;

import org.semanticweb.owl.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface HistoryManager {

    public void logChanges(List<? extends OWLOntologyChange> changes);


    public boolean canUndo();


    public void undo();


    public boolean canRedo();


    public void redo();


    public void addUndoManagerListener(UndoManagerListener listener);


    public void removeUndoManagerListener(UndoManagerListener listener);

//    public boolean isEntityChanged(OWLEntity entity);

//    public Set<OWLEntity> getChangedEntities();
}
