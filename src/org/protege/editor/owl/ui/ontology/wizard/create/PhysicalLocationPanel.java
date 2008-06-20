package org.protege.editor.owl.ui.ontology.wizard.create;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.ProtegeOWL;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PhysicalLocationPanel extends AbstractWizardPanel {

    public static final String ID = PhysicalLocationPanel.class.getName();

    public static final String DEFAULT_LOCAL_BASE_KEY = "DEFAULT_LOCAL_BASE_KEY";

    public static final String RECENT_LOCATIONS_KEY = "RECENT_LOCATIONS_KEY";

    private JTextField locationField;

    private File locationBase;

    private JList recentLocations;

    private ListSelectionListener listSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setBaseFromRecentLocationList();
            }
        }
    };


    public PhysicalLocationPanel(EditorKit editorKit) {
        super(ID, "Physical Location", editorKit);
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeOWL.ID);
        String path = prefs.getString(DEFAULT_LOCAL_BASE_KEY,
                                      new File(new File(System.getProperty("user.home")), "ontologies").toString());
        locationBase = new File(path);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout(10, 10));
        setInstructions("Please specify the file path that points to the location where your ontology will be saved to." + " (Click on a location in the \'recent locations\' list to automatically select that location).");
        JPanel locationPanel = new JPanel(new BorderLayout(3, 3));
        locationField = new JTextField();
        JPanel locationFieldPanel = new JPanel(new BorderLayout(3, 3));
        locationFieldPanel.add(locationField, BorderLayout.NORTH);
        locationFieldPanel.add(new JButton(new AbstractAction("Browse...") {
            public void actionPerformed(ActionEvent e) {
                browseForLocation();
            }
        }), BorderLayout.EAST);
        locationPanel.add(locationFieldPanel);
        JButton button = new JButton(new AbstractAction("Default base...") {
            public void actionPerformed(ActionEvent e) {
                setDefaultLocalBase();
            }
        });
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        holder.add(button);
        parent.add(holder, BorderLayout.SOUTH);
        parent.add(locationPanel, BorderLayout.NORTH);

        recentLocations = new JList(new DefaultListModel());
        JPanel recentLocationsPanel = new JPanel(new BorderLayout(3, 3));
        recentLocationsPanel.add(new JLabel("RecentLocations"), BorderLayout.NORTH);
        recentLocationsPanel.add(ComponentFactory.createScrollPane(recentLocations));
        parent.add(recentLocationsPanel);
        loadRecentLocations();
    }


    private void setBaseFromRecentLocationList() {
        File file = (File) recentLocations.getSelectedValue();
        if (file != null) {
            locationBase = file;
            locationField.setText(new File(file, getOntologyLocalName()).toString());
        }
    }


    private void browseForLocation() {
        Set<String> exts = new HashSet<String>();
        exts.add(".owl");
        exts.add(".rdf");
        File file = UIUtil.saveFile(new JFrame(), "Select a file", exts, getOntologyLocalName());
        if (file != null) {
            locationField.setText(file.toString());
        }
    }


    private void updateLocationField() {
        String name = getOntologyLocalName();
        // The suggested location is the base plus
        // the name of the ontology (without the .owl extension) as
        // a folder.
        // Strip any extension
        String foldername = name;
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) {
            foldername = name.substring(0, dotIndex);
        }

        File f = new File(locationBase, foldername);
        f = new File(f, name);
        try {
            locationField.setText(f.getCanonicalPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getOntologyLocalName() {
        Wizard wizard = getWizard();
        OntologyURIPanel uriPanel = (OntologyURIPanel) wizard.getModel().getPanel(OntologyURIPanel.ID);
        String uriString = "";
        if (uriPanel != null) {
            uriString = uriPanel.getURI().toString();
        }
        int lastSlashIndex = uriString.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return uriString;
        }
        return uriString.substring(lastSlashIndex + 1, uriString.length());
    }


    public void displayingPanel() {
        updateLocationField();
        recentLocations.addListSelectionListener(listSelectionListener);
        locationField.requestFocus();
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        // remove listener otherwise a selection event is called when the component is hidden
        recentLocations.removeListSelectionListener(listSelectionListener);
    }


    public void loadRecentLocations() {
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeOWL.ID);
        List<String> list = new ArrayList<String>();
        list = prefs.getStringList(RECENT_LOCATIONS_KEY, list);
        DefaultListModel model = new DefaultListModel();
        for (String s : list) {
            File file = new File(URI.create(s));
            if (file.exists()) {
                model.add(0, file);
            }
        }
        recentLocations.setModel(model);
        prefs.putStringList(RECENT_LOCATIONS_KEY, list);
    }


    public void storeRecentLocations() {
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeOWL.ID);
        List<String> list = new ArrayList<String>();
        DefaultListModel model = ((DefaultListModel) recentLocations.getModel());
        // Add in current file
        if (getLocationURI() != null) {
            File file = new File(getLocationURI()).getParentFile();
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).equals(file)) {
                    model.remove(i);
                    break;
                }
            }
            model.add(0, file);
        }

        for (Object o : model.toArray()) {
            File file = (File) o;
            URI uri = file.toURI();
            list.add(uri.toString());
        }
        prefs.putStringList(RECENT_LOCATIONS_KEY, list);
    }


    protected void setDefaultLocalBase() {
        File folder = UIUtil.chooseFolder(this, "Please select a folder");
        if (folder != null) {
            locationBase = folder;
            Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeOWL.ID);
            prefs.putString(DEFAULT_LOCAL_BASE_KEY, locationBase.toString());
            updateLocationField();
        }
    }


    public URI getLocationURI() {
        File f = new File(locationField.getText());
        return f.toURI();
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    public Object getBackPanelDescriptor() {
        return OntologyURIPanel.ID;
    }
}
