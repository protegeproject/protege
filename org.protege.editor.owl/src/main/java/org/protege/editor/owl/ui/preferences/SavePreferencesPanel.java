package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.XMLWriterPrefs;

import javax.swing.*;
import java.awt.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 9, 2009<br><br>
 */
public class SavePreferencesPanel extends OWLPreferencesPanel {

    private static final int VERTICAL_SPACE = 20;

    private JCheckBox useXMLEntitiesCheckBox;

    private JCheckBox writeTemporaryFilesCheckBox;


    public void initialise() throws Exception {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        writeTemporaryFilesCheckBox = new JCheckBox("Write temporary files when saving");
        writeTemporaryFilesCheckBox.setSelected(getFilePrefs().getBoolean(UIUtil.ENABLE_TEMP_DIRECTORIES_KEY, true));

        JComponent xmlWriterPanel = createPane("XML Writer", BoxLayout.PAGE_AXIS);
        useXMLEntitiesCheckBox = new JCheckBox("Use XML Entities");
        useXMLEntitiesCheckBox.setSelected(XMLWriterPrefs.getInstance().isUseEntities());
        xmlWriterPanel.add(useXMLEntitiesCheckBox);

        add(writeTemporaryFilesCheckBox);
        add(Box.createVerticalStrut(VERTICAL_SPACE));
        add(xmlWriterPanel);
    }


    public void applyChanges() {
        XMLWriterPrefs.getInstance().setUseEntities(useXMLEntitiesCheckBox.isSelected());
        getFilePrefs().putBoolean(UIUtil.ENABLE_TEMP_DIRECTORIES_KEY, writeTemporaryFilesCheckBox.isSelected());
    }


    public void dispose() throws Exception {
    }


    private JComponent createPane(String title, int orientation) {
        JComponent c = new Box(orientation){
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
            }
        };
        c.setAlignmentX(0.0f);
        if (title != null){
            c.setBorder(ComponentFactory.createTitledBorder(title));
        }
        return c;
    }


    private Preferences getFilePrefs() {
        return PreferencesManager.getInstance().getApplicationPreferences(UIUtil.FILE_PREFERENCES_KEY);
    }
}
