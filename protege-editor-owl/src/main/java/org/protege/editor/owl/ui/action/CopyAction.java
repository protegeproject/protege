package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.transfer.TransferableOWLObject;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.ViewClipboard;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CopyAction extends ProtegeOWLAction {

    private final PropertyChangeListener propertyChangeListener = event -> {
        if ("focusOwner".equals(event.getPropertyName())) {
            updateState();
        }
    };

    private final ChangeListener changeListener = event -> {
        getCopyable().ifPresent(c -> setEnabled(c.canCopy()));
    };

    private Optional<Copyable> currentCopyable = Optional.empty();

    @Override
    public void initialise() throws Exception {
        FocusManager.getCurrentManager().addPropertyChangeListener(propertyChangeListener);
        updateState();
    }

    @Override
    public void dispose() throws Exception {
        FocusManager.getCurrentManager().removePropertyChangeListener(propertyChangeListener);
    }

    private void updateState() {
        currentCopyable.ifPresent(c -> c.removeChangeListener(changeListener));
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
        if (getCopyable().isPresent()) {
            currentCopyable = getCopyable();
            currentCopyable.ifPresent(c -> {
                c.addChangeListener(changeListener);
                setEnabled(c.canCopy());
            });
        }
        else if (focusOwner instanceof JTextComponent) {
            setEnabled(true);
        }
        else if (focusOwner instanceof JTable) {
            setEnabled(true);
        }
        else if (focusOwner instanceof JList) {
            setEnabled(true);
        }
        else {
            setEnabled(false);
        }

    }

    private Optional<Copyable> getCopyable() {
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
        if (focusOwner instanceof Copyable) {
            return Optional.of((Copyable) focusOwner);
        }
        else {
            return Optional.ofNullable((Copyable) SwingUtilities.getAncestorOfClass(Copyable.class, focusOwner));
        }
    }

    public void actionPerformed(ActionEvent e) {
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
        if (getCopyable().isPresent()) {
            getCopyable().ifPresent(this::handleCopy);
        }
        else if (focusOwner instanceof JTextComponent) {
            handleCopy((JTextComponent) focusOwner);
        }
        else if (focusOwner instanceof JTable) {
            handleCopy((JTable) focusOwner);
        }
        else if (focusOwner instanceof JList) {
            handleCopy((JList) focusOwner);
        }


        // Actually, we could put text on to the system clipboard
        // OWLObject should be serializable!!!
    }

    private void handleCopy(JTextComponent textComponent) {
        textComponent.copy();
    }

    private void handleCopy(JTable table) {
        StringBuilder sb = new StringBuilder();
        for (int row : table.getSelectedRows()) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component renderer = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, col);
                String stringValue;
                if (renderer instanceof JLabel) {
                    stringValue = ((JLabel) renderer).getText();
                }
                else {
                    stringValue = value.toString();
                }
                sb.append(stringValue);
                sb.append("\t");
            }
            sb.append("\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString().trim()), null);
    }

    @SuppressWarnings("unchecked")
    private void handleCopy(JList list) {
        StringBuilder sb = new StringBuilder();
        for (int i : list.getSelectedIndices()) {
            Object value = list.getModel().getElementAt(i);
            String stringValue;
            if (value instanceof OWLObject) {
                stringValue = getOWLModelManager().getRendering((OWLObject) value);
            }
            else {
                stringValue = value.toString();
            }
            sb.append(stringValue);
            sb.append("\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString().trim()), null);
    }

    private void handleCopy(Copyable copyable) {
        List<OWLObject> objects = copyable.getObjectsToCopy();
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
    }

//
//    private static final Logger logger = LoggerFactory.getLogger(CopyAction.class);
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
