package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.prefix.PrefixedNameRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.util.stream.Collectors.joining;
import static javax.swing.SwingConstants.CENTER;
import static org.protege.editor.owl.ui.renderer.RenderingEscapeUtils.RenderingEscapeSetting.UNESCAPED_RENDERING;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31 Aug 2017
 */
public class ProtegeTreeNodeRenderer implements TreeCellRenderer {

    private static final Color UNSAT_ENTITY_COLOR = Color.RED;

    private static final Color DEPRECATED_CLASS_COLOR = Color.GRAY;

    private static final String EQUIV_SEPARATOR = " \u2261 ";

    private Color getDefaultTextColor(@Nullable JTree tree) {
        Color textColor = UIManager.getColor("Tree.textForeground");
        if (textColor == null) {
            textColor = UIManager.getColor("Tree.foreground");
        }
        if (textColor == null && tree != null) {
            textColor = tree.getForeground();
        }
        if (textColor == null) {
            textColor = UIManager.getColor("textText");
        }
        if (textColor == null) {
            textColor = UIManager.getColor("controlText");
        }
        if (textColor == null) {
            textColor = Color.WHITE;
        }
        return textColor;
    }

    private Color getSelectionTextColor() {
        Color selectionColor = UIManager.getColor("Tree.selectionForeground");
        if (selectionColor == null) {
            selectionColor = UIManager.getColor("Tree.textForeground");
        }
        if (selectionColor == null) {
            selectionColor = UIManager.getColor("textHighlightText");
        }
        if (selectionColor == null) {
            selectionColor = Color.WHITE;
        }
        return selectionColor;
    }



    @Nonnull
    private final OWLEditorKit editorKit;

    @Nonnull
    private final ActiveOntologyVisitor activeOntologyVisitor;

    private final DefaultTreeCellRendererEx delegateTreeCellRenderer = new DefaultTreeCellRendererEx();

    private final IconDecorator icon = new IconDecorator();

    private Font plainFont = new Font("sans-serif", Font.PLAIN, 12);

    private Font boldFont = plainFont.deriveFont(Font.BOLD);

    private final PrefixedNameRenderer prefixedNameRenderer = PrefixedNameRenderer.builder()
            .withOwlPrefixes()
            .withWellKnownPrefixes()
            .build();


    public ProtegeTreeNodeRenderer(@Nonnull OWLEditorKit editorKit) {
        this.editorKit = checkNotNull(editorKit);
        this.activeOntologyVisitor = new ActiveOntologyVisitor(editorKit.getOWLModelManager());
    }

    private void setupFonts() {
        Font sysFont = OWLRendererPreferences.getInstance().getFont();
        if(!plainFont.equals(sysFont)) {
            plainFont = sysFont;
            boldFont = plainFont.deriveFont(Font.BOLD);
        }
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        setupFonts();
        boolean consistent = true;
        boolean deprecated = false;
        boolean satisfiable = true;
        boolean active = false;
        String rendering = "";
        icon.clearIcon();
        icon.clearRelationship();
        icon.setRelationshipsDisplayed(false);
        if(value instanceof OWLObjectTreeNode) {
            OWLObjectTreeNode<? extends OWLObject> node = (OWLObjectTreeNode<? extends OWLObject>) value;
            OWLObject object = node.getOWLObject();
            if (object != null) {
                consistent = isConsistent();
                if(consistent) {
                    satisfiable = isSatisfiable(object);
                }
                rendering = getNodeStringRendering(node);
                String equivsRendering = node.getEquivalentObjects().stream()
                        .map(this::getOwlObjectDisabmiguatedRendering)
                        .map(this::prefixWithEquivalentSymbol)
                        .collect(joining());
                rendering += equivsRendering;
                deprecated = isDeprecated(object);
                active = isActive(object);
                Icon entityIcon = editorKit.getOWLWorkspace().getOWLIconProvider().getIcon(object);
                icon.setIcon(entityIcon);
                node.getRelationship().ifPresent(icon::setRelationship);
                boolean displayRelationships = shouldDisplayRelationships(tree);
                icon.setRelationshipsDisplayed(displayRelationships && !object.isTopEntity());
            }
        }
        delegateTreeCellRenderer.setDeprecated(deprecated);
        Color defaultTextColor = getDefaultTextColor(tree);
        Color selectionTextColor = getSelectionTextColor();
        if(!consistent || !satisfiable) {
            delegateTreeCellRenderer.setTextNonSelectionColor(UNSAT_ENTITY_COLOR);
            delegateTreeCellRenderer.setTextSelectionColor(UNSAT_ENTITY_COLOR);
        }
        else if(deprecated) {
            delegateTreeCellRenderer.setTextNonSelectionColor(DEPRECATED_CLASS_COLOR);
            delegateTreeCellRenderer.setTextSelectionColor(DEPRECATED_CLASS_COLOR);
        }
        else {
            delegateTreeCellRenderer.setTextNonSelectionColor(defaultTextColor);
            delegateTreeCellRenderer.setTextSelectionColor(selectionTextColor);
        }
        JLabel renderingComponent = (JLabel) delegateTreeCellRenderer.getTreeCellRendererComponent(tree,
                                                                                                   rendering,
                                                                                                   selected,
                                                                                                   expanded,
                                                                                                   leaf,
                                                                                                   row,
                                                                                                   hasFocus);
        if(active) {
            renderingComponent.setFont(boldFont);
        }
        else {
            renderingComponent.setFont(plainFont);
        }
        icon.setDeprecated(deprecated);
        icon.rebuild();
        renderingComponent.setIcon(icon);
        renderingComponent.setVerticalTextPosition(CENTER);
        renderingComponent.setVerticalAlignment(CENTER);

        return renderingComponent;
    }

    private String prefixWithEquivalentSymbol(String equivRendering) {
        return EQUIV_SEPARATOR + equivRendering;
    }

    private String getNodeStringRendering(OWLObjectTreeNode<?> node) {
        OWLObject object = node.getOWLObject();
        return getOwlObjectDisabmiguatedRendering(object);
    }

    private String getOwlObjectDisabmiguatedRendering(OWLObject object) {
        return editorKit.getOWLModelManager().getDisabmiguatedRendering(object, UNESCAPED_RENDERING);
    }

    private static boolean shouldDisplayRelationships(@Nonnull JTree tree) {
        return tree instanceof OWLModelManagerTree
                && !((OWLModelManagerTree<?>) tree).getProvider().getDisplayedRelationships().isEmpty();
    }


    private boolean isDeprecated(@Nonnull OWLObject owlObject) {
        return editorKit.getOWLModelManager().isDeprecated(owlObject);
    }

    private boolean isConsistent() {
        return editorKit.getOWLModelManager()
                .getOWLReasonerManager()
                .getCurrentReasoner()
                .isConsistent();
    }

    private boolean isSatisfiable(@Nonnull OWLObject owlObject) {
        return !(owlObject instanceof OWLClass) || editorKit.getOWLModelManager()
                                                            .getOWLReasonerManager()
                                                            .getCurrentReasoner()
                                                            .isSatisfiable((OWLClass) owlObject);
    }

    /**
     * Determines if the specified object is "active", meaning that it is defined in the active ontology.
     * @return true if the specified object is active otherwise false.  A value of true will only ever
     * be returned for OWLEntity object.s
     */
    private boolean isActive(@Nonnull OWLObject owlObject) {
        return owlObject.accept(activeOntologyVisitor);
    }


    /**
     * An implementation of a visitor that can be used to determine if an object should be highlighted as being
     * in the active ontology.
     */
    private static final class ActiveOntologyVisitor extends OWLObjectVisitorExAdapter<Boolean> {

        @Nonnull
        private final OWLModelManager modelManager;

        public ActiveOntologyVisitor(@Nonnull OWLModelManager modelManager) {
            super(false);
            this.modelManager = modelManager;
        }

        @Override
        public Boolean visit(OWLClass ce) {
            return !modelManager.getActiveOntology().getAxioms(ce, Imports.EXCLUDED).isEmpty()
                    || !modelManager.getActiveOntology().getAnnotationAssertionAxioms(ce.getIRI()).isEmpty();
        }

        @Override
        public Boolean visit(OWLDataProperty property) {
            return !modelManager.getActiveOntology().getAxioms(property, Imports.EXCLUDED).isEmpty()
                    || !modelManager.getActiveOntology().getAnnotationAssertionAxioms(property.getIRI()).isEmpty();
        }

        @Override
        public Boolean visit(OWLObjectProperty property) {
            return !modelManager.getActiveOntology().getAxioms(property, Imports.EXCLUDED).isEmpty()
                    || !modelManager.getActiveOntology().getAnnotationAssertionAxioms(property.getIRI()).isEmpty();
        }

        @Override
        public Boolean visit(OWLNamedIndividual individual) {
            return !modelManager.getActiveOntology().getAxioms(individual, Imports.EXCLUDED).isEmpty()
                    || !modelManager.getActiveOntology().getAnnotationAssertionAxioms(individual.getIRI()).isEmpty();
        }

        @Override
        public Boolean visit(OWLAnnotationProperty property) {
            return !modelManager.getActiveOntology().getAxioms(property, Imports.EXCLUDED).isEmpty()
                    || !modelManager.getActiveOntology().getAnnotationAssertionAxioms(property.getIRI()).isEmpty();
        }
    }


    /**
     * Extends the {@link DefaultTreeCellRenderer} so that a strikeout line is painted through it
     * if the deprecated flag is set.
     */
    private static class DefaultTreeCellRendererEx extends DefaultTreeCellRenderer {

        private boolean deprecated = false;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(deprecated) {
                int textOffset = getIcon().getIconWidth() + getIconTextGap();
                int y = (int) (getHeight() * 3.0 / 5.0);
                g.drawLine(textOffset, y, getWidth(), y);
            }
        }

        public void setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
        }
    }


    /**
     * A decorator that paints an icon and can decorate it with an arrow to indicate the displayed relationship
     */
    private static class IconDecorator implements Icon {

        private static final BasicStroke RELATIONSHIP_STROKE = new BasicStroke(1.5f);

        private static final Color UNSPECIFIED_RELATIONSHIP_COLOR = new Color(190, 190, 190);

        @Nullable
        private Icon icon = null;

        private int width = 0;

        private int height = 0;

        private boolean deprecated = false;

        private boolean relationshipsDisplayed = false;

        private Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);

        @Nullable
        private Object relationship;

        public void setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
        }

        public void clearIcon() {
            this.icon = null;
        }

        public void setIcon(@Nonnull Icon icon) {
            this.icon = icon;
        }

        public void clearRelationship() {
            this.relationship = null;
        }

        public void setRelationship(Object relationship) {
            this.relationship = relationship;
        }

        public void setRelationshipsDisplayed(boolean relationshipsDisplayed) {
            this.relationshipsDisplayed = relationshipsDisplayed;
        }

        public void rebuild() {
            width = 0;
            height = 0;
            if (icon != null) {
                height = icon.getIconHeight();
                width = icon.getIconWidth();
            }
            if (relationshipsDisplayed) {
                width += 18;
            }
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (icon == null) {
                return;
            }

            int xOffset = 1 + x;
            Graphics2D g2 = (Graphics2D) g;
            Composite comp = g2.getComposite();
            if(deprecated) {
                g2.setComposite(alpha);
            }

            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            if (relationshipsDisplayed) {
                g2.setStroke(RELATIONSHIP_STROKE);
                if (relationship != null) {
                    g.setColor(OWLSystemColors.getOWLObjectPropertyColor());
                }
                else {
                    g.setColor(UNSPECIFIED_RELATIONSHIP_COLOR);
                }
                // Paint a left pointing arrow
                int lineY = y + height / 2;
                g.drawLine(xOffset + 5, lineY, xOffset + 14, lineY);
                g.drawLine(xOffset + 4, lineY, xOffset + 6, lineY - 2);
                g.drawLine(xOffset + 4, lineY, xOffset + 6, lineY + 2);
                xOffset += 16;
            }
            if (icon != null) {
                icon.paintIcon(c, g, xOffset, y);
            }
            g2.setComposite(comp);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
