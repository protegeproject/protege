package org.protege.editor.core.ui.preferences.node;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
public class PreferenceNodeGroupPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -6992474303814825147L;
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
