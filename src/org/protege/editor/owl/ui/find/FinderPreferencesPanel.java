package org.protege.editor.owl.ui.find;

import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.model.find.EntityFinderPreferences;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import java.awt.*;
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
