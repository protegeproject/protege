package org.protege.editor.owl.ui.breadcrumb;

import org.protege.editor.core.Fonts;
import org.protege.editor.core.ui.util.ProtegeColorProvider;
import org.protege.editor.core.ui.util.ThemeManager;
import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public class BreadcrumbViewImpl extends JPanel implements BreadcrumbView, ThemeManager.ThemeChangeListener {

    private static final int PARENT_ARROW_BOX_WIDTH = 9;

    @Nonnull
    private final OWLObject object;

    @Nonnull
    private final String rendering;

    private boolean parentArrowVisible = true;

    private Consumer<OWLObject> clickHandler = owlObject -> {};

    public BreadcrumbViewImpl(@Nonnull OWLObject object,
                              @Nonnull String rendering) {
        this.object = object;
        this.rendering = rendering;
        setFont(Fonts.getMediumDialogFont());
        setupTooltip(object, rendering);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Register for theme changes
        ThemeManager.addThemeChangeListener(this);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                clickHandler.accept(object);
            }
        });
    }

    @Override
    public void setViewClickedHandler(@Nonnull Consumer<OWLObject> objectConsumer) {
        this.clickHandler = checkNotNull(objectConsumer);
    }

    private void setupTooltip(@Nonnull OWLObject object, @Nonnull String rendering) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<span style=\"font-weight: bold;\">");
        sb.append(rendering);
        sb.append("</span>");
        if(object instanceof OWLEntity) {
            OWLEntity entity = (OWLEntity) object;
            OboUtilities.getOboIdFromIri(entity.getIRI()).ifPresent(id -> {
                sb.append("<br>");
                sb.append(id);
            });
            sb.append("<br>");
            sb.append("<span style=\"color: #a0a0a0;\">");
            sb.append(entity.getIRI().toString());
            sb.append("</span>");
        }
        sb.append("</body></html>");
        setToolTipText(sb.toString());

    }

    @Override
    public void setParentArrowVisible(boolean visible) {
        this.parentArrowVisible = visible;
    }

    @Override
    public JComponent asJComponent() {
        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        Rectangle2D renderingBounds = fm.getStringBounds(rendering, getGraphics());
        int prefWidth = 2 + (int) renderingBounds.getWidth();
        if(parentArrowVisible) {
            prefWidth += PARENT_ARROW_BOX_WIDTH;
        }
        int prefTextHeight = fm.getMaxAscent() + fm.getMaxDescent() + fm.getLeading();
        return new Dimension(prefWidth, prefTextHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Get theme-aware colors
        Color arrowColor = getArrowColor();
        Color textColor = getTextColor();
        
        int x = 0;
        if(parentArrowVisible) {
            g2.setColor(arrowColor);
            g2.drawLine(x + 1, 0, x + 5, getHeight() / 2);
            g2.drawLine(x + 1, getHeight(), x + 5, getHeight() / 2);
            x += PARENT_ARROW_BOX_WIDTH;
        }
        g2.setColor(textColor);
        g.drawString(rendering, x, getFontMetrics(getFont()).getAscent());
        int width = getWidth();
        if(width < getPreferredSize().width) {
            int fadeLength = 12;
            int fadeX = width - fadeLength;
            Color bgColor = getBackground();
            Color transparentBgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0);
            GradientPaint paint = new GradientPaint(fadeX, 0, transparentBgColor,
                                                    fadeX + fadeLength, 0, bgColor);
            g2.setPaint(paint);
            g2.fillRect(fadeX, 0, fadeLength, getHeight());
        }
    }
    
    /**
     * Gets the arrow color for the current theme.
     */
    private Color getArrowColor() {
        if (ThemeManager.isDarkTheme()) {
            return new Color(210, 210, 210);
        } else {
            return new Color(150, 150, 150);
        }
    }

    /**
     * Gets the text color for the current theme.
     */
    private Color getTextColor() {
        return ProtegeColorProvider.getTextColor();
    }
    
    @Override
    public void onThemeChanged(String newTheme, String oldTheme) {
        // Repaint to use new theme colors
        repaint();
    }
}
