package org.protege.editor.core.ui.view;

import org.coode.mdock.NodePanel;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;

import javax.swing.*;
import javax.swing.FocusManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.GridBagConstraints.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 05/02/2012
 */
public class CaptureScreenshotAction extends ProtegeAction {

    public static final String PERMANENT_FOCUS_OWNER_PROPERTY_NAME = "permanentFocusOwner";

    private Component currentView;

    private final PropertyChangeListener listener = this::handleFocusManagerPropertyChange;

    public static final CaptureTypePanel captureTypePanel = new CaptureTypePanel();

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        takeScreenCaptureOfCurrentView();

    }


    private void takeScreenCaptureOfCurrentView() {
        if (currentView == null) {
            return;
        }
        Component currentFocusOwner = currentView;
        WorkspaceFrame frame = ProtegeManager.getInstance().getFrame(getWorkspace());
        int ret = JOptionPane.showConfirmDialog(frame, captureTypePanel, "Capture type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        CaptureType captureType = captureTypePanel.getSelectedCaptureType();
        Component component = captureType.getComponentToCapture(currentFocusOwner);
        Dimension size = component.getSize();
        double scaleFactor = captureTypePanel.getScaleFactor();
        final BufferedImage bufferedImage = new BufferedImage(
                (int) (size.width * scaleFactor),
                (int) (size.height * scaleFactor),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.scale(scaleFactor, scaleFactor);

        component.print(g);


        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(DataFlavor.imageFlavor);
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return bufferedImage;
            }
        }, null);
    }

    private void handleFocusManagerPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PERMANENT_FOCUS_OWNER_PROPERTY_NAME)) {
            handlePermanentFocusOwnerChange();
        }
    }

    private void handlePermanentFocusOwnerChange() {
        updateState();
    }

    private void updateState() {
        FocusManager fm = FocusManager.getCurrentManager();
        Component permanentFocusOwner = fm.getPermanentFocusOwner();
        updateStateForPermanentFocusOwner(permanentFocusOwner);
    }

    private void updateStateForPermanentFocusOwner(Component component) {
        currentView = component;
        setEnabled(currentView != null);
    }

    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to initialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialise() throws Exception {
        FocusManager fm = FocusManager.getCurrentManager();
        fm.addPropertyChangeListener(listener);
        updateState();
    }

    public void dispose() throws Exception {
        FocusManager fm = FocusManager.getCurrentManager();
        fm.removePropertyChangeListener(listener);
    }

    public static enum CaptureType {

        VIEW_HOLDER("View and outer component"),

        VIEW("View"),

        VIEW_VIEWPORT("View visible content"),

        VIEW_CONTENT("View complete content");

        private String name;

        private CaptureType(String name) {
            this.name = name;
        }


        public Component getComponentToCapture(Component component) {
            switch (this) {
                case VIEW_HOLDER:
                    Component viewHolder = null;
                    if (component instanceof View) {
                        viewHolder = component;
                    }
                    else {
                        viewHolder = SwingUtilities.getAncestorOfClass(View.class, component);
                    }
                    if (viewHolder == null) {
                        return null;
                    }
                    while (true) {
                        Container parent = viewHolder.getParent();
                        if (!(parent instanceof NodePanel)) {
                            viewHolder = parent;
                        }
                        else {
                            break;
                        }
                    }
                    return viewHolder;

                case VIEW:
                    Component view = null;
                    if (component instanceof View) {
                        view = component;
                    }
                    else {
                        view = SwingUtilities.getAncestorOfClass(View.class, component);
                    }
                    if (view == null) {
                        return null;
                    }
                    return view.getParent();
                case VIEW_VIEWPORT:
                    if (component instanceof JViewport) {
                        return component;
                    }
                    return SwingUtilities.getAncestorOfClass(JViewport.class, component);

                case VIEW_CONTENT:
                    Component contentParent = component;
                    while (true) {
                        Container parent = contentParent.getParent();
                        if (parent instanceof JViewport) {
                            break;
                        }
                        if (parent instanceof ViewHolder) {
                            break;
                        }
                        if (parent == null) {
                            break;
                        }
                        contentParent = parent;
                    }
                    return contentParent;

                default:
                    return component;
            }
        }
    }


    private static class CaptureTypePanel extends JPanel {

        private static CaptureType lastCaptureType = CaptureType.VIEW_HOLDER;

        private final JSpinner scaleFactorSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 10, 1.0));

        private List<JRadioButton> captureTypeButtons = new ArrayList<>();

        //        private final JTextField pathField;

        private CaptureTypePanel() {
            setLayout(new GridBagLayout());
            add(new JLabel("Capture type:"), getGBG(0, 0, 0.0, BASELINE_TRAILING, NONE));

            ButtonGroup bg = new ButtonGroup();
            int currentRow = 0;
            int buttonX = 1;
            for (CaptureType type : CaptureType.values()) {
                JRadioButton typeButton = new JRadioButton(type.name);
                captureTypeButtons.add(typeButton);
                if (type.equals(lastCaptureType)) {
                    typeButton.setSelected(true);
                }
                bg.add(typeButton);
                add(typeButton, getGBG(buttonX, currentRow, 0.0, BASELINE_LEADING, NONE));
                currentRow++;
            }
            add(new JSeparator(), new GridBagConstraints(0, currentRow, 3, 1, 100.0, 0.0, CENTER, HORIZONTAL, new Insets(5, 2, 5, 2), 0, 0));
            currentRow++;
            add(new JLabel("Scale factor"),
                    new GridBagConstraints(0, currentRow, 1, 1, 0, 0, BASELINE_TRAILING, NONE, new Insets(0, 0, 0, 0), 0, 0));
            add(scaleFactorSpinner,
                    new GridBagConstraints(1, currentRow, 2, 1, 0, 0, BASELINE_LEADING, NONE, new Insets(0, 0, 0, 0), 0, 0));

        }

        public CaptureType getSelectedCaptureType() {
            int index = 0;

            for (JRadioButton radioButton : captureTypeButtons) {
                if (radioButton.isSelected()) {
                    lastCaptureType = CaptureType.values()[index];
                    return lastCaptureType;
                }
                index++;
            }
            return null;
        }

        public double getScaleFactor() {
            return (double) scaleFactorSpinner.getValue();
        }

        private static GridBagConstraints getGBG(int x, int y, double wX, int anchor, int fill) {
            return new GridBagConstraints(x, y, 1, 1, wX, 0.0, anchor, fill, new Insets(0, 0, 2, 2), 0, 0);
        }
    }


}
