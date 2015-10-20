package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.transfer.OWLObjectDataFlavor;
import org.protege.editor.owl.ui.view.Pasteable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


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

//    private static final Logger logger = LoggerFactory.getLogger(PasteAction.class);
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
