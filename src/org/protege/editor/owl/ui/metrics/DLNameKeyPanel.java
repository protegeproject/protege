package org.protege.editor.owl.ui.metrics;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.OWLIcons;

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
 * Date: 03-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLNameKeyPanel extends JPanel {

    public DLNameKeyPanel() {
        List<NameObject> box = new ArrayList<NameObject>();
        addExplanation(OWLIcons.getIcon("AL.png"),
                       "Attributive language.  This is the base language which allows:" + "<ul><li>Atomic negation (negation of concepts that do not appear on the left hand side of axioms)</li>" + "<li>Concept intersection</li>" + "<li>Universal restrictions</li>" + "<li>Limited existential quatification (restrictions that only have fillers " + "of Thing)</li></ul>",
                       box);

//        addExplanation(OWLIcons.getIcon("F.png"), "Attributive language", box);
        addExplanation(OWLIcons.getIcon("FLM.png"),
                       "A sub-langauge of AL, which is obtained by disallowing atomic negation",
                       box);

        addExplanation(OWLIcons.getIcon("FLO.png"),
                       "A sub-language of FLo, which is obtained by disallowing limited existential quantification",
                       box);
        addExplanation(OWLIcons.getIcon("C.png"), "Complex concept negation", box);
        addExplanation(OWLIcons.getIcon("S.png"), "An abbreviation for AL and C with transitive properties", box);
        addExplanation(OWLIcons.getIcon("H.png"), "Role hierarchy (subproperties - rdfs:subPropertyOf)", box);
        addExplanation(OWLIcons.getIcon("O.png"),
                       "Nominals. (Enumerated classes or object value restrictions - owl:oneOf, owl:hasValue)",
                       box);
        addExplanation(OWLIcons.getIcon("I.png"), "Inverse properties", box);
        addExplanation(OWLIcons.getIcon("N.png"),
                       "Cardinality restrictions (owl:Cardinality, owl:minCardianlity, owl:maxCardinality)",
                       box);
        addExplanation(OWLIcons.getIcon("Q.png"), "Qualified cardinality restrictions (available in OWL 1.1)", box);
        addExplanation(OWLIcons.getIcon("F.png"), "Functional properties", box);
        addExplanation(OWLIcons.getIcon("E.png"),
                       "Full existential quantification (Existential restrictions that have fillers other that owl:Thing)",
                       box);
        addExplanation(OWLIcons.getIcon("U.png"), "Concept union", box);
        addExplanation(OWLIcons.getIcon("Datatype.png"), "Use of datatype properties, data values or datatypes", box);

        setLayout(new BorderLayout());
        JList l = new JList(box.toArray());
        l.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                                           value,
                                                                           index,
                                                                           isSelected,
                                                                           cellHasFocus);
                NameObject nameObject = (NameObject) value;
                label.setIcon(nameObject.getIcon());
                label.setText(nameObject.getDesc());
                label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 12));
                label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,
                                                                                                   0,
                                                                                                   1,
                                                                                                   0,
                                                                                                   Color.LIGHT_GRAY),
                                                                   BorderFactory.createEmptyBorder(5, 2, 5, 2)));
                return label;
            }
        });
        l.setBackground(Color.WHITE);
        JScrollPane sp = ComponentFactory.createScrollPane(l);
        add(sp, BorderLayout.CENTER);
    }


    private class NameObject {

        private Icon icon;

        private String desc;


        public NameObject(final Icon icon, String desc) {
            this.icon = new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    icon.paintIcon(c, g, x, y);
                }


                public int getIconWidth() {
                    return 80;
                }


                public int getIconHeight() {
                    return icon.getIconHeight();
                }
            };
            this.desc = "<html><body>" + desc + "</body></html>";
        }


        public Icon getIcon() {
            return icon;
        }


        public String getDesc() {
            return desc;
        }
    }


    private void addExplanation(Icon icon, String description, List<NameObject> list) {
        list.add(new NameObject(icon, description));
    }


    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setContentPane(new DLNameKeyPanel());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
