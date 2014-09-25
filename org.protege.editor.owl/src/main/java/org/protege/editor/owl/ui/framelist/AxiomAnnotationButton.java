package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;

import java.awt.*;
import java.awt.event.ActionListener;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 */
public class AxiomAnnotationButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);

    private static final String ANNOTATE_STRING = "@";

    private boolean annotationPresent = false;


    public AxiomAnnotationButton(ActionListener actionListener) {
        super("Annotations", ROLL_OVER_COLOR, actionListener);
    }

    @Override
    public Color getBackground() {
        if (annotationPresent) {
            return Color.ORANGE;
        }
        else {
            return super.getBackground();
        }
    }

    public void paintButtonContent(Graphics2D g) {

        int w = getBounds().width;
        int h = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;

        Font font = g.getFont().deriveFont(Font.BOLD, OWLRendererPreferences.getInstance().getFontSize());
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics(font);
        final Rectangle stringBounds = fontMetrics.getStringBounds(ANNOTATE_STRING, g).getBounds();
        int baseline = fontMetrics.getLeading() + fontMetrics.getAscent();
        g.drawString(ANNOTATE_STRING, x + w / 2 - stringBounds.width / 2, y + (h - stringBounds.height) / 2 + baseline );

//        if (annotationPresent) {
//            g.drawOval(x + 2, y + 2, w - 4, h - 4);
//        }

        g.setFont(font);
    }


    public void setAnnotationPresent(boolean annotationPresent) {
        this.annotationPresent = annotationPresent;
    }
}
