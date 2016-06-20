package org.protege.editor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 Jun 16
 */
public class Fonts {

    private static final Logger logger = LoggerFactory.getLogger(Fonts.class);

    /**
     * Scales the font size in the UI based on a default font size of 12.
     * @param fontSize The font size.  The scaling factor will be computed from this using a default font size
     *                 of 12.  Thus, if fontSize is set to 12 then the scaling factor is 1.0.
     */
    public static void updateUIDefaultsFontSize(int fontSize) {
        UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        double scale = fontSize / 12.0;
        logger.info("Font scale factor: {}", scale);
        lookAndFeelDefaults.keySet().stream()
                .filter(k -> k.toString().toLowerCase().endsWith("font"))
                .forEach(k -> {
                    Font font = (Font) lookAndFeelDefaults.get(k);
                    if (font != null) {
                        Font scaledFont = font.deriveFont((float) scale * font.getSize());
                        // Place the scaled font in the developer defaults then we don't clobber the L&F defaults.
                        UIManager.put(k, scaledFont);
                        logger.info("Set {} to {}", k, scaledFont.getSize());
                    }
                });
    }
}
