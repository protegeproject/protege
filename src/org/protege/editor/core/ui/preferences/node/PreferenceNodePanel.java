package org.protege.editor.core.ui.preferences.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


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
