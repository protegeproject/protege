package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;
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
public class ReasonerProgressUI implements ReasonerProgressMonitor {
	private static final Logger log = Logger.getLogger(ReasonerProgressUI.class);
	public static final long CLOSE_PROGRESS_TIMEOUT = 1000;

    private OWLEditorKit owlEditorKit;

    private JLabel label;
    
    private JLabel taskLabel;

    private JProgressBar progressBar;

    private JDialog window;

    private boolean cancelled = false;

    private Action cancelledAction;

    private static final int CANCEL_TIMEOUT_MS = 5000;

    private Timer cancelTimeout;
    
    private boolean running = false;


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

        cancelTimeout = new Timer(CANCEL_TIMEOUT_MS, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                owlEditorKit.getOWLModelManager().getOWLReasonerManager().killCurrentClassification();
            }
        });
        cancelTimeout.setRepeats(false);
    }
    
    public void setCancelled() {
    	synchronized (this) {
    		cancelled = true;
    		running = false;
    	}
    	hideWindow();
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
    	synchronized (this) {
    		running = true;
    	}
    	progressBar.setIndeterminate(false);
    	progressBar.setValue(0);
    	showWindow();
    	taskLabel.setText(taskName);
    }




    public void reasonerTaskStopped() {
    	synchronized (this) {
    		running = false;
    	}
    	hideWindow();
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


    private void hideWindow() {
    	new Thread() {  // ToDo probably can be written better with an executor and rescheduling
    		@Override
    		public void run() {
    			try {
					Thread.sleep(CLOSE_PROGRESS_TIMEOUT);
				} catch (InterruptedException e) {
					log.error("ow, ow, ow.  Don't prod me with that stick", e);
				}
    			boolean localRunning;
    			final boolean localCancelled;
    			synchronized (ReasonerProgressUI.this) {
    				localRunning = running;
    				localCancelled = cancelled;
    			}
    			if (!localRunning) {
    				SwingUtilities.invokeLater(new Runnable() {
    					public void run() {
    						if (localCancelled) {
    							JOptionPane.showMessageDialog(window,
    									null,
    									"Reasoning Task Cancelled",
    									JOptionPane.INFORMATION_MESSAGE);
    						}
    						window.setVisible(false);
    					}
    				});
    			}
    		}
    	}.start();
    }





}
