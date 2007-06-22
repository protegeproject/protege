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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileURIPanel extends JPanel {

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
        listeners = new ArrayList<ChangeListener>();
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
        JFrame parent = null;//(JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        if (parent == null) {
            parent = new JFrame();
        }
        File f = UIUtil.openFile(parent, "Select an ontology file", fileExtensions);
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
        for (ChangeListener listener : new ArrayList<ChangeListener>(listeners)) {
            listener.stateChanged(new ChangeEvent(this));
        }
//        try {
//                File file = new File(textField.getText());
//                System.out.println("File: " + file);
//                System.out.println("Exists: " + file.exists());
//                if(file.exists() == false) {
//                    URI uri = getURI();
//                    System.out.println("Is URI: " + uri);
//                    System.out.println("Abs: " + uri.isAbsolute());
//                    System.out.println("Op: " + uri.isOpaque());
//
//                }
//        } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
    }


    public URI getURI() throws URISyntaxException {
        return new URI(textField.getText());
    }


    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new FileURIPanel(new HashSet<String>(Arrays.asList("owl"))));
        f.setVisible(true);
        f.pack();
    }
}
