package org.protege.editor.owl.ui.explanation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class ExplanationPreferencesGeneralPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -3354987384223578780L;

	private JRadioButton buttonLast, buttonFirst;
	private SortedPluginsTableModel tableModel;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		addDefaultExplanationServiceComponent(panel);
		addInstalledExplanationServicesComponent(panel);
		loadFrom(ExplanationPreferences.create().load());
	}

	@Override
	public void dispose() throws Exception {
		getOWLEditorKit().getModelManager().getExplanationManager().reload();
	}

	@Override
	public void applyChanges() {
		ExplanationPreferences prefs = ExplanationPreferences.create();
		saveTo(prefs);
		prefs.save();
	}

	private void loadFrom(ExplanationPreferences prefs) {
		if (prefs.useLastExplanationService) {
			buttonLast.setSelected(true);
		} else {
			buttonFirst.setSelected(true);
		}
		tableModel.setPluginIds(prefs.explanationServicesList);
		tableModel.setDisabledIds(prefs.disabledExplanationServices);
	}

	private void saveTo(ExplanationPreferences prefs) {
		prefs.useLastExplanationService = buttonLast.isSelected();
		prefs.explanationServicesList = tableModel.getPluginIds();
		prefs.disabledExplanationServices = tableModel.getDisabledIds();
	}

	private void addDefaultExplanationServiceComponent(PreferencesLayoutPanel panel) {
		panel.addGroup("Default explanation service");
		buttonLast = new JRadioButton("Most recently used explanation service");
		buttonLast.setToolTipText(
				"Always use the most recently used explanation service, if it can provide an explanation for the chosen axiom");
		buttonFirst = new JRadioButton("First available explanation service from the list below");
		buttonFirst.setToolTipText(
				"Always use the first explanation service from the list below that can provide an explanation for the chosen axiom");
		ButtonGroup group = new ButtonGroup();
		group.add(buttonLast);
		group.add(buttonFirst);
		panel.addGroupComponent(buttonLast);
		panel.addGroupComponent(buttonFirst);
	}

	private void addInstalledExplanationServicesComponent(PreferencesLayoutPanel panel) {
		panel.addGroup("Installed explanation services");
		Collection<ExplanationService> services = getOWLEditorKit().getOWLModelManager().getExplanationManager()
				.getExplainers();
		Map<String, String> nameMap = new HashMap<>();
		for (ExplanationService service : services) {
			nameMap.put(service.getPluginId(), service.getName());
		}
		tableModel = new SortedPluginsTableModel(nameMap);
		JTable pluginTable = new JTable(tableModel);
		pluginTable.setToolTipText(
				"Plugins that provide explanation facilities. You can disable and enable plugins and change their order using the buttons below.");
		pluginTable.setRowSelectionAllowed(true);
		pluginTable.setColumnSelectionAllowed(false);
		pluginTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pluginTable.getColumnModel().getColumn(0).setMaxWidth(20);
		pluginTable.getColumnModel().getColumn(1).setMaxWidth(50);
		pluginTable.getColumnModel().getColumn(2).setMinWidth(300);
		pluginTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane pluginTableScrollPane = new JScrollPane(pluginTable);
		pluginTableScrollPane.setPreferredSize(new Dimension(400, 100));
		panel.addGroupComponent(pluginTableScrollPane);
		addUpDownButtons(panel, pluginTable);
	}

	private void addUpDownButtons(PreferencesLayoutPanel panel, JTable pluginTable) {
		JButton buttonUp = new JButton("↑ Move up");
		buttonUp.setToolTipText("Move the selected explanation service towards the top of the list");
		buttonUp.addActionListener(e -> {
			int rowIndex = pluginTable.getSelectedRow();
			if (rowIndex > 0) {
				tableModel.swap(rowIndex - 1, rowIndex);
			}
			pluginTable.setRowSelectionInterval(rowIndex - 1, rowIndex - 1);
		});

		JButton buttonDown = new JButton("↓ Move down︎");
		buttonDown.setToolTipText("Move the selected explanation service towards the bottom of the list");
		buttonDown.addActionListener(e -> {
			int rowIndex = pluginTable.getSelectedRow();
			if (rowIndex < pluginTable.getRowCount() - 1) {
				tableModel.swap(rowIndex, rowIndex + 1);
			}
			pluginTable.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
		});

		JPanel buttonsUpDown = new JPanel();
		buttonsUpDown.add(buttonUp);
		buttonsUpDown.add(buttonDown);
		panel.addGroupComponent(buttonsUpDown);

		pluginTable.getSelectionModel().addListSelectionListener(e -> {
			int rowIndex = pluginTable.getSelectedRow();
			if (rowIndex == -1) {
				buttonUp.setEnabled(false);
				buttonDown.setEnabled(false);
			} else {
				buttonUp.setEnabled(rowIndex > 0);
				buttonDown.setEnabled(rowIndex < pluginTable.getRowCount() - 1);
			}
		});
	}

}
