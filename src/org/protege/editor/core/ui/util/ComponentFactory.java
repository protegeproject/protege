package org.protege.editor.core.ui.util;


import org.eclipse.core.runtime.IExtension;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
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
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ComponentFactory {

    public static JComponent createExceptionComponent(String message, Throwable exception, IExtension extension) {
        return new ComponentExceptionPanel(message, exception, extension);
    }


    public static JScrollPane createScrollPane(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }


    public static JToolBar createViewToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        return toolBar;
    }


    public static JTextField createTextField() {
        return new JTextField();
    }


    /**
     * Creates a JEditorPane suitable for showing HTML content
     * @param hyperlinkListener an optional hyperlink listener
     * @return
     */
    public static JEditorPane createHTMLPane(HyperlinkListener hyperlinkListener) {
        JEditorPane editorPane = new JEditorPane(new HTMLEditorKit().getContentType(), "");

        // set the font to the same as a normal label
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                          "font-size: " + font.getSize() + "pt; }";
        ((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule(bodyRule);

        editorPane.setEditable(false);
        editorPane.setOpaque(false);

        if (hyperlinkListener != null){
            editorPane.addHyperlinkListener(hyperlinkListener);
        }

        return editorPane;
    }


    public static Border createTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                                                                   title),
                                                  BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }
}
