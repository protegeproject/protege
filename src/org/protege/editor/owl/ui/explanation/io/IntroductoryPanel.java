package org.protege.editor.owl.ui.explanation.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;

public class IntroductoryPanel extends JPanel {
	private static final long serialVersionUID = 2168617534333064174L;
	public static final Logger LOGGER = Logger.getLogger(IntroductoryPanel.class);
	private InconsistentOntologyPlugin selectedPlugin;

	public IntroductoryPanel(OWLEditorKit owlEditorKit, InconsistentOntologyPlugin lastSelectedPlugin) throws IOException {
		setLayout(new BorderLayout());
		add(createCenterPanel(owlEditorKit, lastSelectedPlugin));
	}
	
    private JPanel createCenterPanel(OWLEditorKit owlEditorKit, InconsistentOntologyPlugin lastSelectedPlugin) throws IOException {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        JTextPane tp = new JTextPane();
        URL help = IntroductoryPanel.class.getResource("/InconsistentOntologyHelp.html");
        tp.setPage(help);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tp);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        center.add(scrollPane);
        
        Box optBox = new Box(BoxLayout.Y_AXIS);
        optBox.setAlignmentX(0.0f);
        optBox.setBorder(ComponentFactory.createTitledBorder("Explanation Plugins"));
        ButtonGroup group = new ButtonGroup();
		InconsistentOntologyPluginLoader loader= new InconsistentOntologyPluginLoader(owlEditorKit);
		boolean selectedOne = false;
		for (final InconsistentOntologyPlugin plugin : loader.getPlugins()) {
			JRadioButton button = new JRadioButton(plugin.getName());
			if (!selectedOne) {
				button.setSelected(true);
				selectedOne = true;
			}
			if (lastSelectedPlugin != null && plugin.getName().equals(lastSelectedPlugin.getName())) {
				button.setSelected(true);
			}
			group.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedPlugin = plugin;
				}
			});
			optBox.add(button);
		}
		center.add(optBox);
        return center;
    }

    public InconsistentOntologyPlugin getSelectedPlugin() {
		return selectedPlugin;
	}
}
