package org.protege.editor.owl.ui.action;

import org.semanticweb.owl.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyPanel extends JPanel {

    private Map<JCheckBox, InferredAxiomGenerator> map;

    private Box checkBoxBox;


    public ExportInferredOntologyPanel() {
        map = new HashMap<JCheckBox, InferredAxiomGenerator>();
        checkBoxBox = new Box(BoxLayout.Y_AXIS);
        setLayout(new BorderLayout(7, 7));
        add(checkBoxBox);
        addCheckBox(new InferredSubClassAxiomGenerator());
        addCheckBox(new InferredEquivalentClassAxiomGenerator());
        addCheckBox(new InferredSubObjectPropertyAxiomGenerator());
        addCheckBox(new InferredSubDataPropertyAxiomGenerator());
        addCheckBox(new InferredEquivalentObjectPropertyAxiomGenerator());
        addCheckBox(new InferredEquivalentDataPropertiesAxiomGenerator());
        addCheckBox(new InferredObjectPropertyCharacteristicAxiomGenerator());
        addCheckBox(new InferredDataPropertyCharacteristicAxiomGenerator());
        addCheckBox(new InferredInverseObjectPropertiesAxiomGenerator());
        addCheckBox(new InferredClassAssertionAxiomGenerator());
    }


    private void addCheckBox(InferredAxiomGenerator gen) {
        JCheckBox box = new JCheckBox(gen.getLabel(), true);
        checkBoxBox.add(box);
        checkBoxBox.add(Box.createVerticalStrut(4));
        map.put(box, gen);
    }


    public List<InferredAxiomGenerator> getInferredAxiomGenerators() {
        List<InferredAxiomGenerator> result = new ArrayList<InferredAxiomGenerator>();
        for (JCheckBox checkBox : map.keySet()) {
            if (checkBox.isSelected()) {
                result.add(map.get(checkBox));
            }
        }
        return result;
    }


    public static ExportInferredOntologyPanel showDialog() {
        ExportInferredOntologyPanel panel = new ExportInferredOntologyPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        int ret = JOptionPane.showConfirmDialog(null,
                                                panel,
                                                "Export inferred ontology",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
            return panel;
        }
        else {
            return null;
        }
    }


    public static void main(String[] args) {

        ExportInferredOntologyPanel.showDialog();
    }
}
