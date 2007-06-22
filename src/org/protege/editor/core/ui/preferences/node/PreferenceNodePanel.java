package org.protege.editor.core.ui.preferences.node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferenceNodePanel extends JPanel {

    private List<JLabel> labels;


    public PreferenceNodePanel(PreferenceNodeGroup group) {
        labels = new ArrayList<JLabel>();
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(group.getLabel()),
                                                     BorderFactory.createEmptyBorder(10, 30, 20, 10)));
        List<PreferenceNode> prefNodes = group.getNodes();
        Box box = new Box(BoxLayout.Y_AXIS);
        for (PreferenceNode node : prefNodes) {
            JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JComponent component = node.getComponent();
            String labelText = "";
            if (component instanceof JCheckBox == false) {
                labelText = node.getLabel();
            }
            holder.add(createComponentLabel(labelText));
            holder.add(component);
            box.add(holder);
        }
        setLayout(new BorderLayout());
        add(box, BorderLayout.NORTH);

        Dimension dim = new Dimension();
        for (JLabel label : labels) {
            if (label.getPreferredSize().width > dim.width) {
                dim = label.getPreferredSize();
            }
        }
        for (JLabel label : labels) {
            label.setPreferredSize(dim);
        }
    }


    protected JLabel createComponentLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
        labels.add(label);
        label.setHorizontalAlignment(JLabel.RIGHT);
        return label;
    }
}
