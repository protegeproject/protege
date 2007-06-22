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
public class PreferenceNodeGroupPanel extends JPanel {

    private List<PreferenceNodeGroup> nodes;


    public PreferenceNodeGroupPanel(List<PreferenceNodeGroup> nodes) {
        this.nodes = nodes;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        add(box);
        for (PreferenceNodeGroup group : nodes) {
            box.add(new PreferenceNodePanel(group));
        }
    }


    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
        List<PreferenceNodeGroup> nodes = new ArrayList<PreferenceNodeGroup>();
        PreferenceNodeGroup group = new PreferenceNodeGroup("Class description rendering");
        group.addNode(new BooleanPreferenceNode("Show hyperlinks"));
        group.addNode(new BooleanPreferenceNode("Highlight key words"));
        group.addNode(new BooleanPreferenceNode("Show tooltips"));
        group.addNode(new BooleanPreferenceNode("Hightlight active ontology descriptions"));
        nodes.add(group);

        PreferenceNodeGroup g2 = new PreferenceNodeGroup("Change");
        g2.addNode(new BooleanPreferenceNode("Show changes classes in blue"));

        StringPreferenceNode n = new StringPreferenceNode("User name");
        n.setValue("Matthew Horridge");
        g2.addNode(n);

        nodes.add(g2);

        JOptionPane pane = new JOptionPane(new PreferenceNodeGroupPanel(nodes),
                                           JOptionPane.PLAIN_MESSAGE,
                                           JOptionPane.OK_CANCEL_OPTION);


        JDialog dlg = pane.createDialog(null, "Test");
        dlg.setVisible(true);
    }
}
