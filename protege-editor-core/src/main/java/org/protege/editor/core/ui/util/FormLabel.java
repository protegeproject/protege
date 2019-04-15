package org.protege.editor.core.ui.util;

import org.protege.editor.core.Fonts;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-15
 */
public class FormLabel extends JLabel {

    private static final Font FONT = Fonts.getMediumDialogFont().deriveFont(Font.BOLD, 14f);

    public FormLabel(String text) {
        super(text);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        setFont(FONT);
    }
}
