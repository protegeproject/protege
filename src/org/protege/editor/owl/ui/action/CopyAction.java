package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owl.model.OWLObject;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
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
public class CopyAction extends FocusedComponentAction<Copyable> {

    protected boolean canPerform() {
        return getCurrentTarget().canCopy();
    }


    protected Class<Copyable> initialiseAction() {
        return Copyable.class;
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLObject> objects = getCurrentTarget().getObjectsToCopy();
        if (objects.isEmpty()) {
            // Shouldn't really happen, but just in case
            return;
        }
        // Push the objects on to the clip board
        ViewClipboard clipboard = ViewClipboard.getInstance();
        clipboard.getClipboard().setContents(new TransferableOWLObject(getOWLModelManager(), objects), null);

        new TransferableOWLObject(getOWLModelManager(), objects);

        StringBuilder buffer = new StringBuilder();
        for (OWLObject owlObject : objects) {
            buffer.append(getOWLModelManager().getRendering(owlObject));
            buffer.append("\n");
        }
        StringSelection stringSelection = new StringSelection(buffer.toString().trim());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        // Actually, we could put text on to the system clipboard
        // OWLObject should be serializable!!!
    }

//
//    private static final Logger logger = Logger.getLogger(CopyAction.class);
//
//    private Copyable currentCopyable;
//
//    private PropertyChangeListener listener;
//
//    private ChangeListener changeListener;
//
//
//    public void actionPerformed(ActionEvent e) {
//        List<OWLObject> objects = currentCopyable.getObjectsToCopy();
//        if (objects.isEmpty()) {
//            // Shouldn't really happen, but just in case
//            return;
//        }
//        // Push the objects on to the clip board
//        ViewClipboard clipboard = ViewClipboard.getInstance();
//        clipboard.getClipboard().setContents(new TransferableOWLObject(getOWLModelManager(), objects), null);
//
//        new TransferableOWLObject(getOWLModelManager(), objects);
//
//        StringBuilder buffer = new StringBuilder();
//        for (OWLObject owlObject : objects) {
//            buffer.append(getOWLModelManager().getOWLObjectRenderer().render(owlObject,
//                                                                             getOWLModelManager().getOWLEntityRenderer()));
//            buffer.append("\n");
//        }
//        StringSelection stringSelection = new StringSelection(buffer.toString().trim());
//        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
//        // Actually, we could put text on to the system clipboard
//        // OWLObject should be serializable!!!
//    }
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
//        Copyable copyable = getCopyable();
//        if (currentCopyable != null) {
//            detatchListener();
//        }
//        currentCopyable = copyable;
//        if (currentCopyable != null) {
//            attatchListeners();
//            setEnabled(currentCopyable.canCopy());
//        }
//        else {
//            setEnabled(false);
//        }
//    }
//
//
//    private static Copyable getCopyable() {
//        Component c = FocusManager.getCurrentManager().getFocusOwner();
//        if (c instanceof Copyable) {
//            return (Copyable) c;
//        }
//        if (c == null) {
//            return null;
//        }
//        return (Copyable) SwingUtilities.getAncestorOfClass(Copyable.class, c);
//    }
//
//
//    private void attatchListeners() {
//        currentCopyable.addChangeListener(changeListener);
//    }
//
//
//    private void detatchListener() {
//        currentCopyable.removeChangeListener(changeListener);
//    }
//
//
//    public void dispose() {
//        FocusManager.getCurrentManager().removePropertyChangeListener(listener);
//    }
}
