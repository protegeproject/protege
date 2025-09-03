package org.protege.editor.core.ui.util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileURIPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -9037381161423211140L;

    private Action browseAction = new AbstractAction("Browse...") {

        public void actionPerformed(ActionEvent e) {
            handleBrowse();
        }
    };

    private JTextField textField;

    private ArrayList<ChangeListener> listeners;

    private Set<String> fileExtensions;


    public FileURIPanel(Set<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        setLayout(new BorderLayout(7, 7));
        JPanel holder = new JPanel(new BorderLayout(7, 7));
        holder.add(textField = new JTextField(30), BorderLayout.NORTH);
        holder.add(new JButton(browseAction), BorderLayout.EAST);
        add(holder, BorderLayout.NORTH);
        listeners = new ArrayList<>();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }


            public void insertUpdate(DocumentEvent e) {
                fireStateChanged();
            }


            public void removeUpdate(DocumentEvent e) {
                fireStateChanged();
            }
        });
    }


    private void handleBrowse() {
        JFrame parent = new JFrame();
        File f = UIUtil.openFile(parent, "Select an ontology file", "OWL File", fileExtensions);
        if (f != null) {
            textField.setText(f.getAbsolutePath());
        }
    }


    public void setURI(URI uri) {
        textField.setText(uri.toString());
    }


    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }


    protected void fireStateChanged() {
        for (ChangeListener listener : new ArrayList<>(listeners)) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }


    public URI getURI() throws URISyntaxException {
        return new URI(textField.getText());
    }
}
