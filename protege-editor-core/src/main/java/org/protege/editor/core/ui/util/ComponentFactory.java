package org.protege.editor.core.ui.util;


/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.eclipse.core.runtime.IExtension;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>

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
        return BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
                                                  BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }
}
