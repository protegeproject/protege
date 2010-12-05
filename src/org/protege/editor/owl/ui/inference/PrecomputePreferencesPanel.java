package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.ui.inference.PrecomputePreferencesTableModel.Column;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.reasoner.InferenceType;

public class PrecomputePreferencesPanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = -8812068573828834020L;
    private Set<InferenceType>             required;
    private Set<InferenceType>             disallowed;
    private Map<InferenceType, JCheckBox> selectedInferences = new EnumMap<InferenceType, JCheckBox>(InferenceType.class);
    private boolean applied = false;
    
    public void initialise() throws Exception {
    	readPreferences();
        setLayout(new BorderLayout());
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    public void dispose() throws Exception {
    }

    private void readPreferences() {
    	ReasonerPreferences preferences = getOWLModelManager().getOWLReasonerManager().getReasonerPreferences();
    	required = EnumSet.noneOf(InferenceType.class);
    	required.addAll(preferences.getRequired());
    	disallowed = EnumSet.noneOf(InferenceType.class);
    	disallowed.addAll(preferences.getDisallowed());
    }
    
    @Override
    public void applyChanges() {
    	ReasonerPreferences preferences = getOWLModelManager().getOWLReasonerManager().getReasonerPreferences();
        for (InferenceType type : InferenceType.values()) {
        	preferences.setRequired(type, required.contains(type));
        	preferences.setDisallowed(type, disallowed.contains(type));
        }
        preferences.save();
    }
    
    
    private JPanel createCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        
        JComponent help = buildHelp("/PrecomputePreferencesHelp.txt");
        if (help != null) {
        	center.add(help);
        }
        
        center.add(Box.createRigidArea(new Dimension(0,10)));
        
        Box resetButtonContainer = new Box(BoxLayout.X_AXIS);
        resetButtonContainer.add(Box.createHorizontalGlue());
        JButton reset = new JButton("Reset to Default");
        reset.setAlignmentX(CENTER_ALIGNMENT);
        resetButtonContainer.add(reset);
        resetButtonContainer.add(Box.createHorizontalGlue());
        center.add(resetButtonContainer);

        center.add(Box.createRigidArea(new Dimension(0,10)));

        JComponent tableContainer = new JPanel(new BorderLayout());
        final PrecomputePreferencesTableModel tableModel = new PrecomputePreferencesTableModel(required, disallowed);
        JTable table = new JTable(tableModel);
        tableContainer.add(table.getTableHeader(), BorderLayout.PAGE_START);
        tableContainer.add(table, BorderLayout.CENTER);
        double preferredWidth = 0;
        for (InferenceType type  : InferenceType.values()) {
        	double width = new JLabel(getInferenceTypeName(type)).getPreferredSize().getWidth();
        	if (width > preferredWidth) preferredWidth = width;
        }
        table.getColumnModel().getColumn(Column.INFERENCE_TYPE.ordinal()).setPreferredWidth((int) preferredWidth);
        table.getColumnModel().getColumn(Column.REQUIRED.ordinal()).setPreferredWidth((int) new JLabel(Column.REQUIRED.getColumnName()).getPreferredSize().getWidth());
        table.getColumnModel().getColumn(Column.DISALLOWED.ordinal()).setPreferredWidth((int) new JLabel(Column.DISALLOWED.getColumnName()).getPreferredSize().getWidth());
        reset.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		disallowed.clear();
        		required.clear();
        		tableModel.fireTableDataChanged();
        	}
        });
        
        center.add(tableContainer);

        return center;
    }
    
    /* package */ static JComponent buildHelp(String resource) {
        Box help = new Box(BoxLayout.Y_AXIS);
        help.setBorder(ComponentFactory.createTitledBorder("Description"));
        help.setAlignmentX(0.0f);
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(PrecomputePreferencesPanel.class.getResourceAsStream(resource)));
        	String line;
        	while ((line = reader.readLine()) != null) {
        		help.add(new JLabel(line));
        	}
        	return help;
        }
        catch (Throwable t) {
        	ProtegeApplication.getErrorLog().logError(t);
        	return null;
        }
    }
    
    
    public Set<InferenceType> getPreCompute() {
        Set<InferenceType> preCompute = EnumSet.noneOf(InferenceType.class);
        for (Entry<InferenceType, JCheckBox> entry : selectedInferences.entrySet()) {
            JCheckBox check = entry.getValue();
            if (check.isSelected()) {
                preCompute.add(entry.getKey());
            }
        }
        return preCompute;
    }
    
    public static String getInferenceTypeName(InferenceType type) {
    	return type.toString();
    }
    
}
