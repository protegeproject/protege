package org.protege.editor.core.ui.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A component that comprises a <code>ViewBanner</code>
 * and a toolbar that holds actions.
 */
public class ViewBar extends JPanel {

    private final ViewBanner viewBanner;

    private final JToolBar toolBar;

    private final List<ViewMode> viewModes = new ArrayList<>();

    private final JComboBox<ViewMode> viewModeComboBox;

    private final List<ViewModeChangedHandler> viewModeChangedHandlers = new ArrayList<>();

    public ViewBar(String bannerText, Color bannerColor) {
        setLayout(new BorderLayout(2, 2));
        viewBanner = new ViewBanner(bannerText, bannerColor);
        add(viewBanner, BorderLayout.NORTH);
        JPanel southPanel = new JPanel(new BorderLayout(7, 7));
        toolBar = new JToolBar();
        southPanel.add(toolBar, BorderLayout.WEST);
        add(southPanel, BorderLayout.SOUTH);
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        viewModeComboBox = new JComboBox<>();
        viewModeComboBox.addActionListener(e -> fireViewModeChanged());
        viewModeComboBox.setVisible(false);
        southPanel.add(viewModeComboBox, BorderLayout.EAST);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        viewBanner.setEnabled(enabled);
    }


    public ViewBanner getViewBanner() {
        return viewBanner;
    }


    public void addAction(Action action) {
        JButton button = toolBar.add(action);
        button.setRequestFocusEnabled(false);
    }


    public void addSeparator() {
        toolBar.addSeparator(new Dimension(6, 6));
    }

    public void addMode(ViewMode viewMode) {
        viewModes.add(viewMode);
        viewModeComboBox.setModel(new DefaultComboBoxModel<>(viewModes.toArray(new ViewMode [viewModes.size()])));
        viewModeComboBox.setVisible(!viewModes.isEmpty());
    }

    public Optional<ViewMode> getViewMode() {
        if(viewModes.isEmpty()) {
            return Optional.empty();
        }
        int selIndex = viewModeComboBox.getSelectedIndex();
        if(selIndex == -1) {
            return Optional.empty();
        }
        return Optional.of(viewModeComboBox.getItemAt(selIndex));
    }

    public void setViewMode(ViewMode viewMode) {
        viewModeComboBox.setSelectedItem(viewMode);
    }

    public void addViewModeChangedHandler(ViewModeChangedHandler handler) {
        viewModeChangedHandlers.add(handler);
    }

    private void fireViewModeChanged() {
        viewModeChangedHandlers.stream()
                .forEach(h -> h.handleViewModeChanged(getViewMode()));
    }
}
