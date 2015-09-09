package org.protege.editor.owl.ui.ontology.wizard.move.byprofile;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWLProfile;

import javax.swing.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 3, 2008<br><br>
 */
public class ProfileSelectorPanel extends MoveAxiomsKitConfigurationPanel {

    private MoveAxiomsByProfileKit kit;

    private JRadioButton owlDLButton;

    private JRadioButton elPPButton;

    private JRadioButton owl2Button;


    public ProfileSelectorPanel(MoveAxiomsByProfileKit kit) {
        this.kit = kit;
    }


    public void initialise() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(owlDLButton = new JRadioButton("OWL DL", true));
        add(owl2Button = new JRadioButton("OWL 2"));
        add(elPPButton = new JRadioButton("EL++"));

        ButtonGroup bg = new ButtonGroup();
        bg.add(owl2Button);
        bg.add(owlDLButton);
        bg.add(elPPButton);
    }


    public void dispose() {
        // do nothing
    }


    public String getID() {
        return getClass().getName();
    }


    public String getTitle() {
        return "Select profile";
    }


    public String getInstructions() {
        return "Select the OWL profile that you wish to extract.";
    }


    public void update() {
        // do nothing
    }


    public void commit() {
        kit.setProfile(getSelectedProfile());
    }


    private OWLProfile getSelectedProfile() {
        if (owl2Button.isSelected()){
            return new OWL2Profile();
        }
        else if (owlDLButton.isSelected()){
            return new OWL2DLProfile();
        }
        else if (elPPButton.isSelected()){
            return new OWL2ELProfile();
        }
        return null;
    }
}
