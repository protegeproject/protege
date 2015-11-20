package org.protege.editor.owl.ui.inference;

import org.semanticweb.owlapi.reasoner.InferenceType;

import javax.swing.table.AbstractTableModel;
import java.util.Set;

public class PrecomputePreferencesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3237338845911865353L;
    private Set<InferenceType>             required;
    private Set<InferenceType>             disallowed;
	
	public PrecomputePreferencesTableModel(Set<InferenceType> required, Set<InferenceType> disallowed) {
		this.required = required;
		this.disallowed = disallowed;
	}
	
	public enum Column {
		INFERENCE_TYPE("Inference Type"), REQUIRED("Required"), DISALLOWED("Disallowed");
		
		private String columnName;
		
		private Column(String columnName) {
			this.columnName = columnName;
		}
		
		public String getColumnName() {
			return columnName;
		}
	}

	public int getRowCount() {
		return InferenceType.values().length;
	}

	public int getColumnCount() {
		return Column.values().length;
	}
	
	@Override
	public String getColumnName(int column) {
		return Column.values()[column].getColumnName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Column column = Column.values()[columnIndex];
		switch (column) {
		case INFERENCE_TYPE:
			return String.class;
		case REQUIRED:
		case DISALLOWED:
			return Boolean.class;
		default:
			throw new UnsupportedOperationException("Programmer Error: missed a case");		
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Column column = Column.values()[columnIndex];
		switch (column) {
		case INFERENCE_TYPE:
			return false;
		case REQUIRED:
		case DISALLOWED:
			return true;
		default:
			throw new UnsupportedOperationException("Programmer Error: missed a case");
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		InferenceType type = InferenceType.values()[rowIndex];
		Column column = Column.values()[columnIndex];
		switch (column) {
		case INFERENCE_TYPE:
			return PrecomputePreferencesPanel.getInferenceTypeName(type);
		case REQUIRED:
			return required.contains(type);
		case DISALLOWED:
			return disallowed.contains(type);
		default:
			throw new UnsupportedOperationException("Programmer Error: missed a case");
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Boolean v = (Boolean) aValue;
		InferenceType type = InferenceType.values()[rowIndex];
		Column column = Column.values()[columnIndex];
		switch (column) {
		case REQUIRED:
			setRequired(type, v);
			if (v) {
				setDisallowed(type, !v);
			}
			break;
		case DISALLOWED:
			setDisallowed(type, v);
			if (v) {
				setRequired(type, !v);
			}
			break;
		default:
			throw new UnsupportedOperationException("Programmer Error: shouldn't happen");
		}
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	private void setRequired(InferenceType type, boolean v) {
		if (v) {
			required.add(type);
		}
		else {
			required.remove(type);
		}
	}
	
	private void setDisallowed(InferenceType type, boolean v) {
		if (v) {
			disallowed.add(type);
		}
		else {
			disallowed.remove(type);
		}
	}

}
