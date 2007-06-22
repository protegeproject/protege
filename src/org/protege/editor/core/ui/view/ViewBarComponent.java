package org.protege.editor.core.ui.view;

import javax.swing.*;
import java.awt.*;
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
