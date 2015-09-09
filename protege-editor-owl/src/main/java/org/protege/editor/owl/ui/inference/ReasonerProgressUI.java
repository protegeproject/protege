package org.protege.editor.owl.ui.inference;

import java.awt.*;
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
import javax.swing.WindowConstants;

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

    public static final int PADDING = 5;

    public static final String DEFAULT_MESSAGE = "Classifying...";

    private OWLEditorKit owlEditorKit;

    private JLabel taskLabel;

    private JProgressBar progressBar;

    private JDialog window;

    private Action cancelledAction;

    private boolean taskIsRunning = false;


    public ReasonerProgressUI(final OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;        
        progressBar = new JProgressBar();        
    }
    
	public void initWindow() {
		if (window != null)
			return;		
		JPanel panel = new JPanel(new BorderLayout(PADDING, PADDING));
        panel.add(progressBar, BorderLayout.SOUTH);
        taskLabel = new JLabel(DEFAULT_MESSAGE);
        panel.add(taskLabel, BorderLayout.NORTH);
		Frame parent = (Frame) (SwingUtilities.getAncestorOfClass(Frame.class,
				owlEditorKit.getWorkspace()));
		window = new JDialog(parent, "Reasoner progress", true);
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 
		cancelledAction = new AbstractAction("Cancel") {
			private static final long serialVersionUID = 3688085823398242640L;
			public void actionPerformed(ActionEvent e) {
				setCancelled();
			}
		};
		JButton cancelledButton = new JButton(cancelledAction);
		JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonHolder.add(cancelledButton);

		JPanel holderPanel = new JPanel(new BorderLayout(PADDING, PADDING));
		holderPanel.add(panel, BorderLayout.NORTH);
		holderPanel.add(buttonHolder, BorderLayout.SOUTH);

		holderPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		window.getContentPane().setLayout(new BorderLayout());
		window.getContentPane().add(holderPanel, BorderLayout.NORTH);
		window.pack();
		Dimension windowSize = window.getPreferredSize();
		window.setSize(400, windowSize.height);
		window.setResizable(false);		
	}
    
	public void setCancelled() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initWindow();
				taskLabel
						.setText("Cancelled.  Waiting for reasoner to terminate...");
				cancelledAction.setEnabled(false);
			}
		});
		owlEditorKit.getOWLModelManager().getOWLReasonerManager()
				.killCurrentClassification();
	}

	public void reasonerTaskBusy() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setIndeterminate(true);
			}
		});
	}

	public void reasonerTaskProgressChanged(final int value, final int max) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setIndeterminate(false);
				progressBar.setMaximum(max);
				progressBar.setValue(value);
			}
		});
	}

	public void reasonerTaskStarted(String taskName) {
		if (taskIsRunning)
			return;
		taskIsRunning = true;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setIndeterminate(false);
				progressBar.setValue(0);
			}
		});
		showWindow(taskName);
	}


    public void reasonerTaskStopped() {
    	if (!taskIsRunning)
    		return;
    	taskIsRunning = false;    	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (taskIsRunning)
					return;				
				initWindow();
				if (!window.isVisible())
					return;
				taskLabel.setText("");
				window.setVisible(false);
			}
		});		
    }


    private void showWindow(final String message) {    	
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			if (!taskIsRunning)
    				return;    			
    			initWindow();    			
    			taskLabel.setText(message);
				if (window.isVisible())
					return;				
				cancelledAction.setEnabled(true);
				window.setLocationRelativeTo(window.getOwner());
				window.setVisible(true);
    		}
    	});
    }

	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initWindow();
				window.dispose();
			}
		});
	}

    public void dispose() throws Exception {
    	reset();
    }

}
