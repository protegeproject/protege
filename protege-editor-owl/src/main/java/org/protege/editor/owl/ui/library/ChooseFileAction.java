package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XmlBaseContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;

/**
 * Created by vblagodarov on 09-08-17.
 */
public class ChooseFileAction extends AbstractAction {
    private JComponent parent;
    private XmlBaseContext original;
    private JTextField physicalLocation;

    public ChooseFileAction(JComponent parent, XmlBaseContext original, JTextField physicalLocation) {
        this.parent = parent;
        this.original = original;
        this.physicalLocation = physicalLocation;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        URI base = CatalogUtilities.resolveXmlBase(original);
        File directory;
        if (base != null && (directory = new File(base)).isDirectory()) {
            fileChooser.setCurrentDirectory(directory);
        }
        int retValue = fileChooser.showOpenDialog(parent);
        if (retValue == JFileChooser.APPROVE_OPTION) {
            physicalLocation.setText(fileChooser.getSelectedFile().toURI().toString());
        }
    }
}
