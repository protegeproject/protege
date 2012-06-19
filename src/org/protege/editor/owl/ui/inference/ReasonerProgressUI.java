package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 10-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerProgressUI implements ReasonerProgressMonitor, Disposable, Resettable {
	public static final long CLOSE_PROGRESS_TIMEOUT = 1000;

    private OWLEditorKit owlEditorKit;

    private JLabel label;
    
    private JLabel taskLabel;

    private JProgressBar progressBar;

    private JDialog window;

    private Action cancelledAction;
    
    private InterruptedReasonerListener reasonerListener;



    public ReasonerProgressUI(final OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        progressBar = new JProgressBar();
        panel.add(progressBar, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label = new JLabel("Classifying...");
        panel.add(label, BorderLayout.NORTH);
        taskLabel = new JLabel();
        panel.add(taskLabel, BorderLayout.NORTH);

        window = new JDialog((Frame) (SwingUtilities.getAncestorOfClass(Frame.class, owlEditorKit.getWorkspace())),
                             "Reasoner progress",
                             true);
        cancelledAction = new AbstractAction("Cancel") {
			private static final long serialVersionUID = 2843243451134187772L;

			public void actionPerformed(ActionEvent e) {
                setCancelled();
            }
        };
        JButton cancelledButton = new JButton(cancelledAction);

        window.setLocation(400, 400);
        JPanel holderPanel = new JPanel(new BorderLayout(7, 7));
        holderPanel.add(panel, BorderLayout.NORTH);
        holderPanel.add(cancelledButton, BorderLayout.EAST);
        holderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(holderPanel, BorderLayout.NORTH);
        window.pack();
        Dimension windowSize = window.getSize();
        window.setSize(400, windowSize.height);
        window.setResizable(false);
        
        reasonerListener = new InterruptedReasonerListener(owlEditorKit) {
			
			@Override
			protected void onInterrupt() {
				window.setVisible(false);
			}
		};
        owlEditorKit.getOWLModelManager().addListener(reasonerListener);
    }
    
    public void setCancelled() {
    	owlEditorKit.getOWLModelManager().getOWLReasonerManager().killCurrentClassification();
    }

    public void reasonerTaskBusy() {
    	progressBar.setIndeterminate(true);
    }




    public void reasonerTaskProgressChanged(int value, int max) {
    	progressBar.setIndeterminate(false);
    	progressBar.setMaximum(max);
    	progressBar.setValue(value);
    }




    public void reasonerTaskStarted(String taskName) {
    	progressBar.setIndeterminate(false);
    	progressBar.setValue(0);
    	showWindow();
    	taskLabel.setText(taskName);
    }




    public void reasonerTaskStopped() {
    	window.setVisible(false);
    	taskLabel.setText("");
    }



    private void showWindow() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			if (!window.isVisible()) {
    				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    				window.setLocation(screenSize.width / 2 - window.getWidth() / 2,
    						screenSize.height / 2 - window.getHeight() / 2);
    				window.setVisible(true);
    			}
    		}
    	});
    }

    public void reset() {
    	SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
		    	window.setVisible(false);				
			}
		});
    	reasonerListener.reset();
    }

    @Override
    public void dispose() throws Exception {
    	owlEditorKit.getOWLModelManager().removeListener(reasonerListener);
    }


}
