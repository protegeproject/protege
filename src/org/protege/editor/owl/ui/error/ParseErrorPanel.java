package org.protege.editor.owl.ui.error;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.error.ErrorExplainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 11, 2008<br><br>
 */
public class ParseErrorPanel<O extends Throwable> extends ErrorPanel<O>{

    private static final Logger logger = Logger.getLogger(ParseErrorPanel.class);

    private SourcePanel source;

    private JSplitPane splitter;


    public ParseErrorPanel(final ErrorExplainer.ErrorExplanation<O> oErrorExplanation, URI loc, SourcePanel sourcePanel) {
        super(oErrorExplanation, loc);
        if (sourcePanel != null){
            this.source = sourcePanel;

            removeComponentFromParent(getTabs());
            splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getTabs(), null);
            splitter.setBorder(new EmptyBorder(0, 0, 0, 0));
            add(splitter, BorderLayout.CENTER);

            addComponentListener(new ComponentAdapter(){
                public void componentShown(ComponentEvent event) {
                    if (oErrorExplanation instanceof ErrorExplainer.ParseErrorExplanation){
                        ErrorExplainer.ParseErrorExplanation expl = (ErrorExplainer.ParseErrorExplanation) oErrorExplanation;
                        source.highlight(expl.getLine(), expl.getColumn());
                    }

                    removeComponentFromParent(source);
                    splitter.setBottomComponent(source);
                    splitter.setDividerLocation(0.5);
                    splitter.repaint();
                }
            });
        }
    }


    protected static void removeComponentFromParent(JComponent component) {
        if (component.getParent() != null){
            component.getParent().remove(component);
        }
    }

}

