package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.OWLEntity;

import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractRenameEntityAction extends ProtegeOWLAction {

    private OWLSelectionModelListener listener;


    final public void initialise() throws Exception {
        setEnabled(isSuitable());
        listener = new OWLSelectionModelListener() {

            public void selectionChanged() {
            }

//            public void selectedClassChanged() {
//                updateState(getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
//            }
//
//            public void selectedDataPropertyChanged() {
//                updateState(getOWLWorkspace().getOWLSelectionModel().getLastSelectedDataProperty());
//            }
//
//            public void selectedObjectPropertyChanged() {
//                updateState(getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty());
//            }
//
//            public void selectedIndividualChanged() {
//                updateState(getOWLWorkspace().getOWLSelectionModel().getLastSelectedIndividual());
//            }
        };
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
    }


    private void updateState(OWLEntity entity) {
        if (entity == null) {
            setEnabled(false);
        }
        setEnabled(isRenamePossible());
    }


    final public void dispose() {
        if (listener != null) {
            getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        }
    }


    public void actionPerformed(ActionEvent e) {
        System.out.println("NOT IMPLEMENTED!");
//        try {
////            UIHelper uiHelper = new UIHelper(getOWLEditorKit());
////            URI uri = uiHelper.getURIForActiveOntology("Rename entity", "Please enter a name",
////                                                       getOWLModelManager().getOWLEntityRenderer().render(getEntity()));
//            URI uri = RenameEntityPanel.showDialog(getOWLEditorKit(), getEntity());
//            if (uri == null) {
//                return;
//            }
//            RenameEntity renameEntity = new RenameEntity(getOWLModelManager().getOntologies(), getEntity(), uri);
//            getOWLModelManager().applyChanges(renameEntity.getChanges());
//            // Select the new entity
//            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(renameEntity.getRenamedEntity());
//        } catch (Exception ex) {
//            Logger.getLogger(getClass()).error(ex);
//        }
    }


    protected abstract OWLEntity getEntity();


    private boolean isRenamePossible() {
//        OWLEntity selEntity = getEntity();
//        if(selEntity == null) {
//            return false;
//        }
//        try {
//            for(OWLOntology ont : selEntity.getOntologies()) {
//                if(!getOWLModelManager().isMutable(ont)) {
//                    return false;
//                }
//            }
//        } catch (OWLException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return isSuitable();
        return false;
    }


    protected abstract boolean isSuitable();
}
