package org.protege.editor.owl.ui.inference;

import javax.swing.JTextField;

import org.slf4j.Logger;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGReasonerPreferencesPanel extends OWLPreferencesPanel {


    private JTextField reasonerURLField;


    public void applyChanges() {
//        try {
//            URL url = new URL(reasonerURLField.getText());
//            URL curURL = DIGReasonerPreferences.getInstance().getReasonerURL();
//            if(!curURL.equals(url)) {
//                DIGReasonerPreferences.getInstance().setReasonerURL(url);
//            }
//        } catch (MalformedURLException e) {
//            logger.warn("Malformed reasoner URL: " + e.getMessage());
//        }
    }


    public void initialise() throws Exception {
        //DIGReasonerPreferences prefs = DIGReasonerPreferences.getInstance();
//        URL url = prefs.getReasonerURL();
//        reasonerURLField = new JTextField();
//        setLayout(new BorderLayout());
//        JPanel holder = new JPanel(new BorderLayout(3, 3));
//        holder.add(new JLabel("Reasoner URL"), BorderLayout.NORTH);
//        holder.add(reasonerURLField, BorderLayout.SOUTH);
//        add(holder, BorderLayout.NORTH);
//        reasonerURLField.setText(url.toString());
    }


    public void dispose() {
    }
}
