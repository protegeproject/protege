package org.protege.editor.owl.ui.hierarchy.creation;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabIndentedHierachyTextPane extends JTextArea {

    public TabIndentedHierachyTextPane() {
        setTabSize(4);
        setFont(new Font("monospaced", Font.PLAIN, 14));
    }
}
