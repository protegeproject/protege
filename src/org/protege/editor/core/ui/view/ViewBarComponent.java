package org.protege.editor.core.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A component that holds a view component and a view
 * bar above the component.  The viewbar has a banner
 * an contains a toolbar.
 */
public class ViewBarComponent extends JPanel {

    private ViewBar viewBar;


    public ViewBarComponent(String bannerText, Color bannerColor, JComponent component) {
        setLayout(new BorderLayout(3, 3));
        viewBar = new ViewBar(bannerText, bannerColor);
        add(viewBar, BorderLayout.NORTH);
        add(component);
        BorderFactory.createEmptyBorder(2, 2, 2, 2);
    }


    public ViewBar getViewBar() {
        return viewBar;
    }


    public void addAction(Action action) {
        viewBar.addAction(action);
    }
}
