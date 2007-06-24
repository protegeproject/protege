package org.protege.editor.owl.ui.find;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.model.find.EntityFinderPreferences;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FinderPreferencesPanel extends OWLPreferencesPanel {

    private JRadioButton simpleSearchButton;

    private JRadioButton regularExpressionSearchButton;

    private JSpinner delaySpinner;


    public void applyChanges() {
        EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
        prefs.setSearchDelay(((Double) delaySpinner.getModel().getValue()).intValue());
        prefs.setUseRegularExpressions(regularExpressionSearchButton.isSelected());
    }


    public void initialise() throws Exception {
        setLayout(new PreferencesPanelLayoutManager(this));
        EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
        SpinnerNumberModel model = new SpinnerNumberModel(prefs.getSearchDelay(), 0, 10000, 50);
        delaySpinner = new JSpinner(model);
        add(delaySpinner, "Search delay (ms)");
        delaySpinner.setToolTipText("1000 = 1 second");
        simpleSearchButton = new JRadioButton("Simple search (using simple wildcards *)",
                                              !prefs.isUseRegularExpressions());
        regularExpressionSearchButton = new JRadioButton("Full regular expression search",
                                                         prefs.isUseRegularExpressions());
        ButtonGroup bg = new ButtonGroup();
        bg.add(simpleSearchButton);
        bg.add(regularExpressionSearchButton);
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.add(simpleSearchButton, BorderLayout.NORTH);
        panel.add(regularExpressionSearchButton, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search type"),
                                                           BorderFactory.createEmptyBorder(7, 7, 7, 7)));
        add(panel, null);
    }


    public void dispose() {
    }
}
