package org.protege.editor.owl.ui.explanation.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.AxiomListFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.owlapi.inconsistent.Phase01;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public class ProtegeHeuristics implements InconsistentOntologyPluginInstance {
	private JFrame explanations;
	private OWLEditorKit owlEditorKit;
	private Phase01 phase01;

	public void initialise() throws Exception {

	}

	public void dispose() throws Exception {

	}

	public void setup(OWLEditorKit editorKit) {
		owlEditorKit = editorKit;
	}

	public void explain(OWLOntology ontology) {
		try {
			OWLModelManager p4Manager = owlEditorKit.getOWLModelManager();
		    explanations = new JFrame("Protege Explanation Heuristics");
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			phase01 = new Phase01();
			phase01.phase01(p4Manager.getActiveOntology(), 
					        p4Manager.getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory());
			mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
			explanations.getContentPane().add(mainPanel);
			explanations.pack();
			explanations.setVisible(true);
		}
		catch (Exception e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
	}

	private JComponent createCenterPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(0.0f);
		panel.add(createTitledEntityList("Inconsistent Classes", phase01.getInconsistentClasses().toArray()));
		panel.add(createTitledEntityList("Overconstrained Individuals", phase01.getInconsistentIndividuals().toArray()));
		return panel;
	}
	
	private JComponent createTitledEntityList(String title, Object[] objects) {
		Box optBox = new Box(BoxLayout.Y_AXIS);
		optBox.setAlignmentX(0.0f);
		optBox.setBorder(ComponentFactory.createTitledBorder(title));
		final JList entitiesList = new JList(objects);
		entitiesList.setCellRenderer(new OWLCellRenderer(owlEditorKit));
		entitiesList.setPreferredSize(new Dimension(300, 400));
		entitiesList.addListSelectionListener(new SynchronizeSelectionListener(entitiesList));
		entitiesList.addMouseListener(new ExplainEntityMouseAdapter(entitiesList));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(entitiesList);
		JPanel inner = new JPanel();
		inner.add(scrollPane);
		optBox.add(inner);
		return optBox;
	}

    private class SynchronizeSelectionListener implements ListSelectionListener {
    	private JList entitiesList;
    	
    	public SynchronizeSelectionListener(JList entitiesList) {
    		this.entitiesList = entitiesList;
    	}
    	
        public void valueChanged(ListSelectionEvent e) {
            Object o = entitiesList.getSelectedValue();
            if (o instanceof OWLEntity) {
                owlEditorKit.getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) o);
            }
        }
    }

    private class ExplainEntityMouseAdapter extends MouseAdapter {
    	private JList entitiesList;
    	
    	public ExplainEntityMouseAdapter(JList entitiesList) {
    		this.entitiesList = entitiesList;
    	}
        public void mouseClicked(MouseEvent event) {
            Object o = entitiesList.getSelectedValue();
            if (event.getClickCount() == 2) {
                Set<OWLAxiom> axioms = null;
                if (o instanceof OWLIndividual) {
                    axioms = phase01.explain((OWLIndividual) o);
                }
                else if (o instanceof OWLClass) {
                    axioms = phase01.explain((OWLClass) o);
                }
                else {
                    return;
                }
                AxiomListFrame frame = new AxiomListFrame(owlEditorKit);
                frame.setRootObject(axioms);
                final OWLFrameList<Set<OWLAxiom>> frameList = new OWLFrameList<Set<OWLAxiom>>(owlEditorKit, frame);
                frameList.setPreferredSize(new Dimension(800, 600));
                frameList.refreshComponent();
                String title = null;
                String rendering = owlEditorKit.getOWLModelManager().getRendering((OWLEntity) o);
                if (o instanceof OWLIndividual) {
                	title = "How the individual " + rendering + " is overconstrained";
                }
                else {
                	title = "Why the class " + rendering + " is inconsistent";
                }
                JOptionPane.showMessageDialog(explanations, frameList, title, JOptionPane.PLAIN_MESSAGE);
                frameList.dispose();
            }
        }
    }
}
