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

    private OWLEditorKit owlEditorKit;

    private JLabel label;

    private JProgressBar progressBar;

    private JDialog window;

    private boolean cancelled;

    private Action cancelledAction;

    private String currentClass;

    private static final int CANCEL_TIMEOUT_MS = 5000;

    private Timer cancelTimeout;
    
    private long progressEndMarker;


    public ReasonerProgressUI(final OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        progressBar = new JProgressBar();
        panel.add(progressBar, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label = new JLabel("Classifying...");
        panel.add(label, BorderLayout.NORTH);

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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void reasonerTaskBusy() {
        throw new UnsupportedOperationException("Not implemented yet");
    }




    public void reasonerTaskProgressChanged(int value, int max) {
        throw new UnsupportedOperationException("Not implemented yet");
    }




    public void reasonerTaskStarted(String taskName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }




    public void reasonerTaskStopped() {
        throw new UnsupportedOperationException("Not implemented yet");
    }



    private void showWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                window.setLocation(screenSize.width / 2 - window.getWidth() / 2,
                                   screenSize.height / 2 - window.getHeight() / 2);
                window.setVisible(true);
            }
        });
    }


    private void hideWindow() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (cancelled && currentClass != null) {
                    JOptionPane.showMessageDialog(window,
                                                  "Cancelled while classifying " + currentClass,
                                                  "Cancelled classification",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
                window.setVisible(false);
            }
        });
    }





}
