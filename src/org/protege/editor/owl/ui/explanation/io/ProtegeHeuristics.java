package org.protege.editor.owl.ui.explanation.io;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.AxiomListFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
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
		explanations = new JFrame("Prot\u00E9g\u00E9 Explanation Heuristics");
		JTabbedPane tabs = new JTabbedPane();
		String[] inconsistentIndividualsExplanation = {"Double click on the individuals listed below to see an explanation of an inconsistency.",
				"Each individual in the list below corresponds to a reason why the ontology is inconsistent"
		};
		tabs.add("Inconsistent Individuals", createEntityListPanel(inconsistentIndividualsExplanation,
																   phase01.getInconsistentIndividuals()));
		String[] inconsistentClassesExplanation = {"Double click on the classes listed below to see an explanation of an inconsistency.",
				"These classes can be proved inconsistent but this may not be a reason why the ontology is inconsistent."
		};
		tabs.add("Inconsistent Classes", createEntityListPanel(inconsistentClassesExplanation,
															   phase01.getInconsistentClasses()));
		String[] hotSpotExplanation = {"The axioms listed below are likely to be involved in an explanation of an inconsistency.",
				"In particular, if the ontology contains no individuals then any explanation of why the ontology is ",
				"inconsistent must include one of the axioms listed below."
		};
		tabs.add("Hot spots", createHotspotList(hotSpotExplanation));
		explanations.setPreferredSize(new Dimension(900, 600));
		explanations.getContentPane().add(tabs);
		explanations.pack();
		explanations.setVisible(true);
	}
	
	private <X> JComponent createEntityListPanel(String[] explanations, Set<X> entities) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		
		panel.add(buildExplanationsPanel(explanations), BorderLayout.NORTH);
		
		int tall = 12000;
		
		JPanel frameListPanel = new JPanel();
		frameListPanel.setLayout(new BoxLayout(frameListPanel, BoxLayout.Y_AXIS));
		JLabel frameListLabel = new JLabel();
		frameListPanel.add(frameListLabel);
		AxiomListFrame frame = new AxiomListFrame(owlEditorKit);
        final OWLFrameList<Set<OWLAxiom>> frameList = new OWLFrameList<Set<OWLAxiom>>(owlEditorKit, frame);
        frameList.setPreferredSize(new Dimension(700, tall));
        frameList.setMaximumSize(new Dimension(tall, tall));
        frameList.refreshComponent();
        frameListPanel.add(frameList);
        
        JPanel leftToRight = new JPanel();
        leftToRight.setLayout(new BoxLayout(leftToRight, BoxLayout.X_AXIS));
		
        JComponent individualsList = createTitledEntityList(entities.toArray(), frameList, frameListLabel);
        individualsList.setPreferredSize(new Dimension(300, tall));
        
        leftToRight.add(individualsList);
        leftToRight.add(frameListPanel);
        
        panel.add(leftToRight, BorderLayout.CENTER);
        
        return panel;
	}
	
	private JComponent createTitledEntityList(Object[] objects, OWLFrameList<Set<OWLAxiom>> frameList, JLabel frameListLabel) {
		final JList entitiesList = new JList(objects);
		entitiesList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = -8027034437907937901L;

			@Override
			public Component getListCellRendererComponent(JList list,
														  Object value, int index, boolean isSelected,
														  boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(entitiesList, value, index, isSelected, cellHasFocus);
				if (value instanceof OWLEntity) {
					label.setText(owlEditorKit.getOWLModelManager().getRendering((OWLEntity) value));
				}
				return label;
			}
		});
		entitiesList.addListSelectionListener(new SynchronizeSelectionListener(entitiesList));
		entitiesList.addMouseListener(new ExplainEntityMouseAdapter(entitiesList, frameList, frameListLabel));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(entitiesList);
		return scrollPane;
	}
	
	private JComponent createHotspotList(String[] explanations) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(buildExplanationsPanel(explanations), BorderLayout.NORTH);
		int tall = 12000;
		
		AxiomListFrame frame = new AxiomListFrame(owlEditorKit);
        final OWLFrameList<Set<OWLAxiom>> frameList = new OWLFrameList<Set<OWLAxiom>>(owlEditorKit, frame);
        frameList.setPreferredSize(new Dimension(700, tall));
        frameList.setMaximumSize(new Dimension(tall, tall));
        frameList.refreshComponent();
        frameList.setRootObject(phase01.getHotspots().getAxioms());
        
        panel.add(frameList, BorderLayout.CENTER);
        
        return panel;
	}
	
	private JComponent buildExplanationsPanel(String[] explanations) {
		JPanel explanationPanel = new JPanel();
		explanationPanel.setLayout(new BoxLayout(explanationPanel, BoxLayout.Y_AXIS));
		for (String explanation : explanations) {
			JLabel label = new JLabel(explanation);
			label.setAlignmentY(0f);
			explanationPanel.add(label);
		}
		return explanationPanel;
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
    	private OWLFrameList<Set<OWLAxiom>> frameList;
    	private JLabel frameListLabel;
    	
    	public ExplainEntityMouseAdapter(JList entitiesList, OWLFrameList<Set<OWLAxiom>> frameList, JLabel frameListLabel) {
    		this.entitiesList = entitiesList;
    		this.frameList = frameList;
    		this.frameListLabel = frameListLabel;
    	}
    	
        public void mouseClicked(MouseEvent event) {
            Object o = entitiesList.getSelectedValue();
            if (event.getClickCount() == 2) {
                Set<OWLAxiom> axioms = null;
                if (o instanceof OWLIndividual) {
                	OWLIndividual i = (OWLIndividual) o;
                    axioms = phase01.explain(i);
                    String rendering = owlEditorKit.getOWLModelManager().getRendering(i);
                    frameListLabel.setText("Inconsistency derived from axioms about " + rendering);
                }
                else if (o instanceof OWLClass) {
                	OWLClass c = (OWLClass) o;
                    axioms = phase01.explain(c);
                    String rendering = owlEditorKit.getOWLModelManager().getRendering(c);
                    frameListLabel.setText("Why " + rendering + " is inconsistent.");
                }
                else {
                    return;
                }
                frameList.setRootObject(axioms);
                frameList.refreshComponent();
            }
        }
    }
}
