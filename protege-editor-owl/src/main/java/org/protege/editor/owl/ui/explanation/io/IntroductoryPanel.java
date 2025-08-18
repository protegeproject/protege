package org.protege.editor.owl.ui.explanation.io;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class IntroductoryPanel extends JPanel {
	private static final long serialVersionUID = 2168617534333064174L;
	private InconsistentOntologyPlugin selectedPlugin;

	public IntroductoryPanel(OWLEditorKit owlEditorKit, InconsistentOntologyPlugin lastSelectedPlugin) throws IOException {
		setLayout(new BorderLayout());
		add(createCenterPanel(owlEditorKit, lastSelectedPlugin));
	}
	
    private JPanel createCenterPanel(OWLEditorKit owlEditorKit, InconsistentOntologyPlugin lastSelectedPlugin) throws IOException {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        JTextPane tp = new JTextPane();
        tp.setEditable(false);
        URL help = getClass().getResource("/InconsistentOntologyHelp.html");
        tp.setPage(help);
		Font font = UIManager.getFont("TextArea.font");
		if (font != null) {
			String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
			((HTMLDocument) tp.getDocument()).getStyleSheet().addRule(bodyRule);
		}
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tp);
        scrollPane.setPreferredSize(new Dimension(480, 300));
        center.add(scrollPane);
        
		InconsistentOntologyPluginLoader loader= new InconsistentOntologyPluginLoader(owlEditorKit);
		Set<InconsistentOntologyPlugin> plugins = loader.getPlugins();
		if (plugins.size() > 1) {
			Box optBox = new Box(BoxLayout.Y_AXIS);
			optBox.setAlignmentX(0.0f);
			optBox.setBorder(ComponentFactory.createTitledBorder("Explanation Plugins"));
			ButtonGroup group = new ButtonGroup();

			boolean selectedOne = false;
			for (final InconsistentOntologyPlugin plugin : plugins) {
				JRadioButton button = new JRadioButton(plugin.getName());
				if (!selectedOne) {
					button.setSelected(true);
					selectedOne = true;
					selectedPlugin = plugin;
				}
				if (lastSelectedPlugin != null && plugin.getName().equals(lastSelectedPlugin.getName())) {
					button.setSelected(true);
					selectedPlugin = plugin;
				}
				group.add(button);
				button.addActionListener(e -> {
                    selectedPlugin = plugin;
                });
				optBox.add(button);
			}
			center.add(optBox);
		}
		else {
			selectedPlugin = plugins.iterator().next();
		}
        return center;
    }

    public InconsistentOntologyPlugin getSelectedPlugin() {
		return selectedPlugin;
	}
}
