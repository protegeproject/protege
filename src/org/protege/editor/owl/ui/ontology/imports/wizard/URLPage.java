package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.core.ui.util.URLPanel;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class URLPage extends AbstractImportSourcePage {

    public static final String ID = "URLPage";

    public URLPanel urlPanel;


    public URLPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from URL", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the URL that points to the file that contains the " + "ontology.  (Please note that this should be the physical URL, rather than the " + "ontology URI)");
        parent.setLayout(new BorderLayout());
        parent.add(urlPanel = new URLPanel());
        urlPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                URL url = urlPanel.getURL();
                if (url == null) {
                    getWizard().setNextFinishButtonEnabled(false);
                    return;
                }
                try {
                    URI uri = url.toURI();

                    getWizard().setNextFinishButtonEnabled(uri.isAbsolute() && uri.getScheme() != null && uri.getPath() != null);
                }
                catch (URISyntaxException e1) {
                    getWizard().setNextFinishButtonEnabled(false);
                    return;
                }
            }
        });
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        urlPanel.requestFocus();
    }


    public ImportVerifier getImportVerifier() {
        return new URLImportFileVerifier(urlPanel.getURL());
    }
}
