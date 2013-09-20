package org.protege.editor.core.ui.view;

import org.coode.mdock.NodePanel;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    private final PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            handleFocusManagerPropertyChange(evt);
        }
    };

    public static final CaptureTypePanel captureTypePanel = new CaptureTypePanel();

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        takeScreenCaptureOfCurrentView();

    }


    private void takeScreenCaptureOfCurrentView() {
//        try {
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
            final BufferedImage bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.getGraphics();
            component.paintAll(g);


            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new Transferable() {
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] {DataFlavor.imageFlavor};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(DataFlavor.imageFlavor);
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    return bufferedImage;
                }
            }, null);
//            File file = captureTypePanel.getFile();
//            File tempFile = File.createTempFile("Screen-Capture", "png");
//            ImageIO.write(bufferedImage, "png", tempFile);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

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
                        if(parent instanceof JViewport) {
                            break;
                        }
                        if(parent instanceof ViewHolder) {
                            break;
                        }
                        if(parent == null) {
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

        private List<JRadioButton> captureTypeButtons = new ArrayList<JRadioButton>();

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
//            add(new JSeparator(), new GridBagConstraints(0, currentRow, 3, 1, 100.0, 0.0, CENTER, HORIZONTAL, new Insets(5, 2, 5, 2), 0, 0));
//            currentRow++;
//            add(new JLabel("Save to:"), getGBG(0, currentRow, 0.0, BASELINE_TRAILING, NONE));
//            pathField = new JTextField(50);
//            add(pathField, getGBG(1, currentRow, 100.0, BASELINE, HORIZONTAL));
//            add(new JButton(new AbstractAction("Browse...") {
//                public void actionPerformed(ActionEvent e) {
//                    Component parent = SwingUtilities.getAncestorOfClass(Window.class, getParent());
//                    File file = UIUtil.saveFile(parent, "Save as", "Save screen capture to", Collections.<String>emptySet(), "screenshot.png");
//                    if (file != null) {
//                        pathField.setText(file.getAbsolutePath());
//                    }
//                }
//            }), getGBG(2, currentRow, 0.0, BASELINE_LEADING, NONE));
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

//        public File getFile() {
//            return new File(pathField.getText().trim());
//        }

        private static GridBagConstraints getGBG(int x, int y, double wX, int anchor, int fill) {
            return new GridBagConstraints(x, y, 1, 1, wX, 0.0, anchor, fill, new Insets(0, 0, 2, 2), 0, 0);
        }
    }




}
