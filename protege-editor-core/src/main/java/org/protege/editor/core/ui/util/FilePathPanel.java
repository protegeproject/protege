package org.protege.editor.core.ui.util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FilePathPanel extends JPanel {


    private String fileChooserText;

    private Set<String> extensions;

    private List<ChangeListener> listeners;

    private JTextField textField;

    private boolean selectFolders = false;

    private Action browseAction;

    public static final int OPEN_DIALOG_MODE = 0;

    public static final int SAVE_DIALOG_MODE = 1;

    private int dialogMode = OPEN_DIALOG_MODE;


    public FilePathPanel(String fileChooserText, Set<String> extensions) {
        this.fileChooserText = fileChooserText;
        this.extensions = new HashSet<>(extensions);
        listeners = new ArrayList<>();
        setLayout(new BorderLayout(3, 3));
        textField = ComponentFactory.createTextField();
        add(textField, BorderLayout.NORTH);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                fireChange();
            }


            public void removeUpdate(DocumentEvent e) {
                fireChange();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        });
        add(new JButton(browseAction = new AbstractAction("Browse...") {
            /**
             * 
             */
            private static final long serialVersionUID = -392303565634614444L;

            public void actionPerformed(ActionEvent e) {
                if (selectFolders) {
                    browseForFolder();
                }
                else {
                    browseForFile();
                }
            }
        }), BorderLayout.EAST);
    }


    public int getDialogMode() {
        return dialogMode;
    }


    public void setDialogMode(int dialogMode) {
        this.dialogMode = dialogMode;
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        browseAction.setEnabled(enabled);
    }


    public void setPath(String path) {
        textField.setText(path);
    }


    public void setSelectsFolders(boolean selectFolders) {
        this.selectFolders = selectFolders;
    }


    public void browseForFile() {
        Window f = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
        File file = dialogMode == OPEN_DIALOG_MODE ? UIUtil.openFile(f, fileChooserText, "OWL File", extensions) : 
                                                     UIUtil.saveFile(new JFrame(),fileChooserText, "OWL File", extensions);
        if (file != null) {
            textField.setText(file.toString());
        }
    }


    public void browseForFolder() {
        File f = UIUtil.chooseFolder(this, "Select a folder");
        if (f != null) {
            textField.setText(f.toString());
        }
    }


    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }


    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }


    protected void fireChange() {
        for (ChangeListener listener : new ArrayList<>(listeners)) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }


    public File getFile() {
        return new File(textField.getText());
    }


    public void requestFocus() {
        // Pass on
        textField.requestFocus();
    }
}
