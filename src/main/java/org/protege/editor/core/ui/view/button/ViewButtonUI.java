package org.protege.editor.core.ui.view.button;

import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 24, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewButtonUI extends BasicButtonUI {

    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }


    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        g.translate(1, 1);
        paintIcon(g, b, b.getBounds());
    }
}
