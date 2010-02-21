package org.protege.editor.owl.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JTextArea;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 29, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SpeciesValidationReportView extends AbstractOWLViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 702819594671937895L;

    public static final String ID = "org.protege.editor.owl.SpeciesValidationReport";

    private JTextArea textArea;


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialiseOWLView() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        add(ComponentFactory.createScrollPane(textArea));
        textArea.append("Species validation:\n\n");
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 10.0f));
    }


    public void append(String text) {
        textArea.append(text);
    }


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public void disposeOWLView() {
    }
}
