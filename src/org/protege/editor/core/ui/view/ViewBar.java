package org.protege.editor.core.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A component that comprises a <code>ViewBanner</code>
 * and a toolbar that holds actions.
 */
public class ViewBar extends JPanel {

    private ViewBanner viewBanner;

    private JToolBar toolBar;


    public ViewBar(String bannerText, Color bannerColor) {
        setLayout(new BorderLayout(2, 2));
        viewBanner = new ViewBanner(bannerText, bannerColor);
        add(viewBanner, BorderLayout.NORTH);
        toolBar = new JToolBar();
        add(toolBar, BorderLayout.SOUTH);
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        viewBanner.setEnabled(enabled);
    }


    public ViewBanner getViewBanner() {
        return viewBanner;
    }


    public void addAction(Action action) {
        JButton button = toolBar.add(action);
        button.setRequestFocusEnabled(false);
    }


    public void addSeparator() {
        toolBar.addSeparator(new Dimension(6, 6));
    }
}
