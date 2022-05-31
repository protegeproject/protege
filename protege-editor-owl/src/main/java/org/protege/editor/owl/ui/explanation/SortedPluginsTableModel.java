package org.protege.editor.owl.ui.explanation;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class SortedPluginsTableModel extends AbstractTableModel {

	private List<String> pluginIds;
	private List<String> disabledIds;
	private Map<String, String> nameMap;

	private String[] columnNames = new String[] { "#", "Enabled", "Plugin" };
	private Class<?>[] columnClasses = new Class<?>[] { Integer.class, Boolean.class, String.class };

	public SortedPluginsTableModel(Map<String, String> nameMap) {
		this.nameMap = nameMap;
	}

	public List<String> getPluginIds() {
		return pluginIds;
	}

	public List<String> getDisabledIds() {
		return disabledIds;
	}

	public void setPluginIds(List<String> pluginIds) {
		this.pluginIds = pluginIds;
		fireTableDataChanged();
	}

	public void setDisabledIds(List<String> disabledIds) {
		this.disabledIds = disabledIds;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return pluginIds.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String pluginId = pluginIds.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return rowIndex + 1;
		case 1:
			return !disabledIds.contains(pluginId);
		case 2:
			return nameMap.get(pluginId);
		default:
			throw new IllegalArgumentException("Invalid column index: " + columnIndex);
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		assert (columnIndex == 1);
		boolean enabled = (Boolean) aValue;
		String pluginId = pluginIds.get(rowIndex);
		if (enabled) {
			disabledIds.remove(pluginId);
		} else {
			if (!disabledIds.contains(pluginId)) {
				disabledIds.add(pluginId);
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void swap(int rowIndex1, int rowIndex2) {
		String pluginId1 = pluginIds.get(rowIndex1);
		String pluginId2 = pluginIds.get(rowIndex2);
		pluginIds.set(rowIndex1, pluginId2);
		pluginIds.set(rowIndex2, pluginId1);
		fireTableRowsUpdated(rowIndex1, rowIndex1);
		fireTableRowsUpdated(rowIndex2, rowIndex2);
	}

}