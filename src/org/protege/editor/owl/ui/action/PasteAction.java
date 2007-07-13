package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.view.Pasteable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLRuntimeException;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PasteAction extends FocusedComponentAction<Pasteable> {

    protected boolean canPerform() {
        return getCurrentTarget().canPaste(getObjectsOnClipboard());
    }


    protected Class<Pasteable> initialiseAction() {
        return Pasteable.class;
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLObject> objects = getObjectsOnClipboard();
        if (!objects.isEmpty()) {
            getCurrentTarget().pasteObjects(objects);
        }
    }

//    private static final Logger logger = Logger.getLogger(PasteAction.class);
//
//
//    private Pasteable currentPasteable;
//
//    private PropertyChangeListener listener;
//
//    private ChangeListener changeListener;
//
//
//    public void actionPerformed(ActionEvent e) {
//        List<OWLObject> objects = getObjectsOnClipboard();
//        if (!objects.isEmpty()) {
//            currentPasteable.pasteObjects(objects);
//        }
//    }
//


    //
    private static List<OWLObject> getObjectsOnClipboard() {
        try {
            Transferable transferable = ViewClipboard.getInstance().getClipboard().getContents(null);
            if (transferable == null) {
                return Collections.emptyList();
            }
            if (transferable.isDataFlavorSupported(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR)) {
                return (List<OWLObject>) transferable.getTransferData(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR);
            }
        }
        catch (UnsupportedFlavorException e) {
            throw new OWLRuntimeException(e);
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
        return Collections.emptyList();
    }
//
//
//    public void initialise() throws Exception {
//        FocusManager.getCurrentManager().addPropertyChangeListener(listener = new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("focusOwner")) {
//                    update();
//                }
//            }
//        });
//        changeListener = new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                update();
//            }
//        };
//        update();
//    }
//
//
//    private void update() {
//        Component c = FocusManager.getCurrentManager().getFocusOwner();
//        // By default text components are pasteable
//        if (c instanceof TextComponent) {
//            setEnabled(true);
//            return;
//        }
//
//        Pasteable pasteable = getPasteable();
//        if (currentPasteable != null) {
//            detatchListener();
//        }
//        currentPasteable = pasteable;
//        if (currentPasteable != null) {
//            attatchListeners();
//            if (getObjectsOnClipboard().isEmpty()) {
//                setEnabled(false);
//            }
//            else {
//                setEnabled(currentPasteable.canPaste(getObjectsOnClipboard()));
//            }
//        }
//        else {
//            setEnabled(false);
//        }
//    }
//
//
//    private static Pasteable getPasteable() {
//        Component c = FocusManager.getCurrentManager().getFocusOwner();
//        if (c instanceof Pasteable) {
//            return (Pasteable) c;
//        }
//        if (c == null) {
//            return null;
//        }
//        return (Pasteable) SwingUtilities.getAncestorOfClass(Pasteable.class, c);
//    }
//
//
//    private void attatchListeners() {
//        currentPasteable.addChangeListener(changeListener);
//    }
//
//
//    private void detatchListener() {
//        currentPasteable.removeChangeListener(changeListener);
//    }
//
//
//    public void dispose() {
//        FocusManager.getCurrentManager().removePropertyChangeListener(listener);
//    }
}
