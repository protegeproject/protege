package org.protege.editor.core.ui.preferences.node;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferenceNodeGroup {

    private String label;


    private List<PreferenceNode> nodes;


    public PreferenceNodeGroup(String label) {
        this.label = label;
        nodes = new ArrayList<PreferenceNode>();
    }


    public String getLabel() {
        return label;
    }


    public void addNode(PreferenceNode node) {
        nodes.add(node);
    }


    public List<PreferenceNode> getNodes() {
        return nodes;
    }
}
