package org.protege.editor.core.update;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.protege.editor.core.ui.util.LinkLabel;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginPanel extends JPanel {



    private final JLabel authorLabel = new JLabel("");

    private LinkLabel licenseLabel;

    private final JEditorPane readmePane = new JEditorPane();

    private JScrollPane readmeScroller;

    private Map<PluginInfo, ContentMimePair> updateInfoReadmeMap = new HashMap<>();

    private PluginTable pluginTable;

    private JCheckBox alwaysShow;


    public PluginPanel(List<PluginInfo> pluginInfoList) {
        setPreferredSize(new Dimension(600, 600));
        setLayout(new BorderLayout(2, 2));

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setBorder(new EmptyBorder(6, 6, 6, 6));
        sp.setResizeWeight(0.5);


        pluginTable = new PluginTable(pluginInfoList);
        pluginTable.addListSelectionListener(e -> {
            PluginInfo info = pluginTable.getCurrentUpdateInfo();
            updateInfoPanel(info);
        });

        sp.setTopComponent(new JScrollPane(pluginTable));
        sp.setBottomComponent(createInfoBox());

        add(sp, BorderLayout.CENTER);

        alwaysShow = new JCheckBox("Always check for updates on startup.", PluginManager.getInstance().isAutoUpdateEnabled());
        alwaysShow.addActionListener(event -> {
            PluginManager.getInstance().setAutoUpdateEnabled(alwaysShow.isSelected());
        });
        alwaysShow.setAlignmentX(0.0f);
        add(alwaysShow, BorderLayout.SOUTH);
    }


    private Component createInfoBox() {
        Box infoBox = new Box(BoxLayout.PAGE_AXIS);
        infoBox.add(Box.createRigidArea(new Dimension(0, 7)));
        infoBox.add(createAuthorPanel());
        infoBox.add(Box.createRigidArea(new Dimension(0, 7)));
        infoBox.add(createLicensePanel());
        infoBox.add(Box.createRigidArea(new Dimension(0, 7)));
        infoBox.add(createDocPanel());
        return infoBox;
    }


    private Component createAuthorPanel() {
        Box box = new Box(BoxLayout.LINE_AXIS);
        final JLabel label = new JLabel("Author: ");
        final Font font = label.getFont().deriveFont(Font.BOLD);
        label.setFont(font);
        label.setAlignmentX(0.0f);
        box.add(label);
        authorLabel.setFont(font);

        box.add(authorLabel);
        box.add(Box.createHorizontalGlue());
        return box;
    }


    private Component createLicensePanel() {
        Box box = new Box(BoxLayout.LINE_AXIS);
        final JLabel label = new JLabel("License: ");
        final Font font = label.getFont().deriveFont(Font.BOLD);
        label.setFont(font);
        label.setAlignmentX(0.0f);
        box.add(label);
        licenseLabel = new LinkLabel("", event -> {
            try {
                URL url = new URL(licenseLabel.getText());
                NativeBrowserLauncher.openURL(url.toString());
            }
            catch (MalformedURLException e) {
                // do nothing
            }
        });
        licenseLabel.setFont(font);

        box.add(licenseLabel);
        box.add(Box.createHorizontalGlue());
        return box;
    }


    private Component createDocPanel() {
        createTextPanel();
        readmeScroller = new JScrollPane(readmePane,
                                         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        readmeScroller.setOpaque(false);

        return readmeScroller;
    }


    private void createTextPanel() {
        JEditorPane pane = new JEditorPane();
        pane.setBorder(new EmptyBorder(12, 12, 12, 12));
        pane.setPreferredSize(new Dimension(300, 200));
        pane.setEditable(false);
        pane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED &&
                event.getURL() != null) {
                NativeBrowserLauncher.openURL(event.getURL().toString());
            }
        });
    }


    private void updateInfoPanel(final PluginInfo info) {
        updateDocPanel(new ContentMimePair("", ""), readmePane, readmeScroller);
        if (info != null){
            authorLabel.setText(info.getAuthor().orElse(""));
            licenseLabel.setText(info.getLicense().orElse(""));

            ContentMimePair readme = updateInfoReadmeMap.get(info);
            if (readme != null){
                updateDocPanel(readme, readmePane, readmeScroller);
            }
            else{
                if (info.getReadmeURI().isPresent()) {
                    Runnable loadContent = () -> {
                        final ContentMimePair readme1 = getContent(info.getReadmeURI().get());
                        updateInfoReadmeMap.put(info, readme1);
                        SwingUtilities.invokeLater(() -> {
                            updateDocPanel(readme1, readmePane, readmeScroller);
                        });
                    };
                    Thread t = new Thread(loadContent, "Load plugin info");
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.start();
                }
            }
        }
        else{
            authorLabel.setText("");
            licenseLabel.setText("");
        }
    }


    private ContentMimePair getContent(URL url) {
        if (url == null) {
            return new ContentMimePair("No info provided", "text/plain");
        }
        else {
            try {
                String mimeType = url.openConnection().getContentType();
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(url.openStream())));
                String line;
                StringBuilder readmeBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    readmeBuilder.append(line);
                    readmeBuilder.append("\n");
                }
                if (readmeBuilder.toString().contains("<html")){
                    mimeType = "text/html"; // some servers give the incorrect type
                }
                reader.close();
                return new ContentMimePair(readmeBuilder.toString(), mimeType);
            }
            catch (IOException e) {
                return new ContentMimePair("Problem obtaining details: " + e.getMessage(), "text/plain");
            }
        }
    }


    private void updateDocPanel(ContentMimePair content, JEditorPane textEditor, final JScrollPane scroller) {
        textEditor.setContentType(content.mimeType);
        textEditor.setText(content.content);
        SwingUtilities.invokeLater(() -> {
            scroller.getViewport().setViewPosition(new Point(0, 0));
            Document document = readmePane.getDocument();
            if(document instanceof HTMLDocument) {
                HTMLDocument htmlDocument = (HTMLDocument) document;
                StyleSheet styleSheet = htmlDocument.getStyleSheet();
                styleSheet.addRule("body {" +
                        "font-family: Helvetica Neue, Helvetica, Arial, Sans-Serif; " +
                        "font-weight: 300; " +
                        "font-size: 10px; " +
                        "color: #808080; " +
                        "padding-left: 10px; " +
                        "padding-right: 10px; " +
                        "padding-top: 2px; " +
                        "}");
            }
        });
    }


    public List<PluginInfo> getPluginsToInstall() {
        return pluginTable.getSelectedUpdateInfo();
    }


    public static List<PluginInfo> showDialog(List<PluginInfo> pluginInfoList, Component parent) {
        PluginPanel panel = new PluginPanel(pluginInfoList);
        final String installOption = "Install";
        final String notNowOption = "Not now";
        Object [] options = new String []{installOption, notNowOption};
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                options,
                options[0]);
        JDialog dlg = optionPane.createDialog(parent, "Automatic Update");
        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.setVisible(true);
        if(installOption.equals(optionPane.getValue())) {
            return panel.getPluginsToInstall();
        }
        else {
            return Collections.emptyList();
        }
    }


    class ContentMimePair{
        public String content;
        public String mimeType;

        ContentMimePair(String content, String mimeType) {
            this.content = content;
            this.mimeType = mimeType;
        }
    }
}
