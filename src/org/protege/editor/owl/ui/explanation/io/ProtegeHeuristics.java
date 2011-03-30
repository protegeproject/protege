package org.protege.editor.owl.ui.explanation.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class ProtegeHeuristics implements InconsistentOntologyPluginInstance {
	private JFrame explanations;
	private OWLEditorKit owlEditorKit;
	private Phase01 phase01;
	private boolean successfulExplanation = false;

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {

	}

	public void setup(OWLEditorKit editorKit) {
		owlEditorKit = editorKit;
	}

	public void explain(OWLOntology ontology) {
		successfulExplanation = false;
		final String cancelOption = "Cancel";
		phase01 = new Phase01();
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setVisible(true);
		JOptionPane pane = new JOptionPane(bar, JOptionPane.PLAIN_MESSAGE, JOptionPane.CANCEL_OPTION, null, new Object[] { cancelOption });
		final JDialog dialog = pane.createDialog(owlEditorKit.getOWLWorkspace(), "Explanation in progress");
		new Thread(new Runnable() {
			public void run() {
				try {
					OWLModelManager p4Manager = owlEditorKit.getOWLModelManager();
					successfulExplanation = phase01.run(p4Manager.getActiveOntology(), 
														p4Manager.getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory());
				}
				catch (OWLOntologyCreationException oce) {
					ProtegeApplication.getErrorLog().logError(oce);
				}
				finally {
					dialog.dispose();
				}
				if (successfulExplanation) {
					createGui();
				}
			}
		}, "Protege Explanation Thread").start();
		dialog.setVisible(true);
		if (cancelOption.equals(pane.getValue())) {
			phase01.cancel();
		}
	}
	
	private void createGui() {
		try {
			explanations = new JFrame("Protege Explanation Heuristics");
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(createTopPanel(), BorderLayout.NORTH);
			mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
			explanations.getContentPane().add(mainPanel);
			explanations.pack();
			explanations.setVisible(true);
		}
		catch (IOException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
	}

	private JComponent createTopPanel() throws IOException {
        JTextPane tp = new JTextPane();
        tp.setEditable(false);
        URL help = ProtegeHeuristics.class.getResource("/InconsistentOntologyExplanationHelp.html");
        tp.setPage(help);
        tp.setPreferredSize(new Dimension(600, 100));
        return tp;
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
		entitiesList.addListSelectionListener(new SynchronizeSelectionListener(entitiesList));
		entitiesList.addMouseListener(new ExplainEntityMouseAdapter(entitiesList));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(entitiesList);
		optBox.add(scrollPane);
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
