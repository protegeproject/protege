package org.protege.editor.owl.ui.breadcrumb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.protege.editor.core.util.ClickHandler;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.renderer.RenderingEscapeUtils;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public class BreadcrumbTrailViewImpl extends JPanel implements BreadcrumbTrailView {

    private final OWLWorkspace workspace;

    private ClickHandler<Breadcrumb> breadcrumbClickHandler = value -> {};

    public BreadcrumbTrailViewImpl(@Nonnull OWLWorkspace workspace) {
        this.workspace = checkNotNull(workspace);
        setLayout(new BreadcrumbTrailLayout());
    }

    @Override
    public void setBreadcrumbClickedHandler(@Nonnull ClickHandler<Breadcrumb> clickedHandler) {
        this.breadcrumbClickHandler = checkNotNull(clickedHandler);
    }

    @Override
    public void setDisplayedBreadcrumbTrail(@Nonnull List<Breadcrumb> path) {
        this.removeAll();
        if(path.isEmpty()) {
            this.validate();
            this.repaint();
            return;
        }
        OWLObject firstBreadcrumb = path.get(0).getObject();
        // Don't display obvious top entities as this justs wastes valuable horizontal space
        int skip = firstBreadcrumb.isTopEntity() ? 1 : 0;
        path.stream()
            .skip(skip)
            .map(breadcrumb -> {
                String rendering = workspace.getOWLModelManager().getRendering(breadcrumb.getObject());
                String displayText = stripSingleQuotes(rendering);
                BreadcrumbView breadcrumbView = new BreadcrumbViewImpl(breadcrumb.getObject(), displayText);
                breadcrumbView.setViewClickedHandler(owlObject -> breadcrumbClickHandler.handleClicked(breadcrumb));
                return breadcrumbView;
            })
            .forEach(v -> this.add(v.asJComponent()));
        this.validate();
        this.repaint();
    }

    @Nonnull
    private static String stripSingleQuotes(@Nonnull String rendering) {
        return RenderingEscapeUtils.unescape(rendering);
    }

    @Override
    public void clearDisplayedBreadcrumbTrail() {
        this.removeAll();
        this.repaint();
    }

    @Nonnull
    @Override
    public JComponent asJComponent() {
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }


    private static class BreadcrumbTrailLayout implements LayoutManager2 {

        private static final int SPACING = 0;

        private List<Component> components = new ArrayList<>();

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
            components.add(comp);
        }

        @Override
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(2000, 20);
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0;
        }

        @Override
        public void invalidateLayout(Container target) {

        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
            components.add(comp);
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            components.remove(comp);
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(1500, 22);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(20, 22);
        }

        @Override
        public void layoutContainer(Container parent) {
            if(components.isEmpty()) {
                return;
            }
            // Distribute the width over the components
            int prefTotalWidth = 0;
            int totalAvailableSpaceToTrim = 0;
            for(Iterator<Component> it = components.iterator(); it.hasNext(); ) {
                Component component = it.next();
                Dimension prefSize = component.getPreferredSize();
                prefTotalWidth += prefSize.width;
                if (it.hasNext()) {
                    totalAvailableSpaceToTrim += (prefSize.width);
                    prefTotalWidth += SPACING;
                }
            }

            int x = 0;
            if(prefTotalWidth > parent.getWidth()) {
                // Need to shrink some components because there is not enough room for all of the
                // components.  Don't shrink the last component, just the rest of them.
                double spaceToTrim = prefTotalWidth - parent.getWidth();
                double percentageToTrim = spaceToTrim / totalAvailableSpaceToTrim;
                // The last component gets its preferred size
                // Shrink the other components by a percentage of their preferred width
                for(Iterator<Component> it = components.iterator(); it.hasNext(); ) {
                    Component component = it.next();
                    Dimension prefSize = component.getPreferredSize();
                    int componentWidth;
                    if (it.hasNext()) {
                        componentWidth = (int)((1 - percentageToTrim) * prefSize.width);
                    }
                    else {
                        componentWidth = prefSize.width;
                    }
                    component.setBounds(x, 0, componentWidth, prefSize.height);
                    x = x + SPACING + componentWidth;
                }
            }
            else {
                // Everything gets its preferred size
                for(Component component : components) {
                    Dimension prefSize = component.getPreferredSize();
                    component.setBounds(x, 0, prefSize.width, prefSize.height);
                    x = x + SPACING + prefSize.width;
                }
            }
        }
    }

}
