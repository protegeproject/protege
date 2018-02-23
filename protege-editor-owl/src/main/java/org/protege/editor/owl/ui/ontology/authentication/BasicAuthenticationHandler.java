package org.protege.editor.owl.ui.ontology.authentication;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.apache.commons.codec.binary.Base64;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLOntologyID;

public interface BasicAuthenticationHandler {

<T extends Throwable> String handleBasicAuthenticationOntology(OWLOntologyID ontologyID, URI loc, T e) throws Throwable;

}
