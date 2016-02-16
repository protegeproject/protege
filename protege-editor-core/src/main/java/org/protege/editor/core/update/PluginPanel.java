package org.protege.editor.core.update;


import com.google.common.collect.ListMultimap;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.LinkLabel;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginPanel extends JPanel {



    private JLabel authorLabel;

    private JLabel licenseLabel;

    private JEditorPane readmePane;

    private JScrollPane readmeScroller;

    private Map<PluginInfo, ContentMimePair> updateInfoReadmeMap = new HashMap<>();

    private Set<PluginTable> tables = new HashSet<>();

    private JCheckBox alwaysShow;


    public PluginPanel(ListMultimap<String, PluginInfo> downloadsProviders) {
        setLayout(new BorderLayout(2, 2));


        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setBorder(new EmptyBorder(6, 6, 6, 6));
        sp.setResizeWeight(0.5);

        final JTabbedPane tabbedPane = new JTabbedPane();

        for (String label : downloadsProviders.keySet()){
            final PluginTable table = new PluginTable(downloadsProviders.get(label));
            tables.add(table);
            tabbedPane.addTab(label, table);

            table.addListSelectionListener(e -> {
                PluginInfo info = table.getCurrentUpdateInfo();
                updateInfoPanel(info);
            });
        }
        tabbedPane.setSelectedIndex(0);

        sp.setLeftComponent(tabbedPane);
        sp.setRightComponent(createInfoBox());

        add(sp, BorderLayout.CENTER);

        tabbedPane.addChangeListener(event -> {
            PluginTable table = (PluginTable)tabbedPane.getSelectedComponent();
            updateInfoPanel(table.getCurrentUpdateInfo());
        });

        alwaysShow = new JCheckBox("Always check for updates on startup.", PluginManager.getInstance().isAutoUpdateEnabled());
        alwaysShow.addActionListener(event -> {
            PluginManager.getInstance().setAutoUpdateEnabled(alwaysShow.isSelected());
        });
        alwaysShow.setAlignmentX(0.0f);
        add(alwaysShow, BorderLayout.SOUTH);
    }


    private Component createInfoBox() {
        Box infoBox = new Box(BoxLayout.PAGE_AXIS);
        infoBox.setBorder(ComponentFactory.createTitledBorder("Plugin info"));
        infoBox.add(Box.createRigidArea(new Dimension(0, 10)));
        infoBox.add(createAuthorPanel());
        infoBox.add(Box.createRigidArea(new Dimension(0, 10)));
        infoBox.add(createLicensePanel());
        infoBox.add(Box.createRigidArea(new Dimension(0, 20)));
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
        authorLabel = new JLabel("");
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
        readmePane = createTextPanel();
        readmeScroller = new JScrollPane(readmePane,
                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        readmeScroller.setOpaque(false);

        return readmeScroller;
    }


    private JEditorPane createTextPanel() {
        JEditorPane pane = new JEditorPane();
        pane.setBorder(new EmptyBorder(12, 12, 12, 12));
        pane.setPreferredSize(new Dimension(300, 200));
        pane.setEditable(false);
        pane.setFont(new Font("SansSerif", Font.PLAIN, 9));
        pane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED &&
                event.getURL() != null) {
                NativeBrowserLauncher.openURL(event.getURL().toString());
            }
        });
        return pane;
    }


    private void updateInfoPanel(final PluginInfo info) {
        updateDocPanel(new ContentMimePair("", ""), readmePane, readmeScroller);
        if (info != null){
            authorLabel.setText(info.getAuthor());
            licenseLabel.setText(info.getLicense());

            ContentMimePair readme = updateInfoReadmeMap.get(info);
            if (readme != null){
                updateDocPanel(readme, readmePane, readmeScroller);
            }
            else{
                Runnable loadContent = () -> {
                    final ContentMimePair readme1 = getContent(info.getReadmeURI());
                    updateInfoReadmeMap.put(info, readme1);
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run() {
                            updateDocPanel(readme1, readmePane, readmeScroller);
                        }
                    });
                };
                Thread t = new Thread(loadContent, "Load plugin info");
                t.setPriority(Thread.MIN_PRIORITY);
                t.start();
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
        });
    }


    public List<PluginInfo> getPluginsToInstall() {
        List<PluginInfo> plugins = new ArrayList<>();
        for (PluginTable table : tables){
            plugins.addAll(table.getSelectedUpdateInfo());
        }
        return plugins;
    }


    public static List<PluginInfo> showDialog(ListMultimap<String, PluginInfo> plugins, Component parent) {
        PluginPanel panel = new PluginPanel(plugins);
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
