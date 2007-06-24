package org.protege.editor.owl.ui.framelist;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public class OWLOntologyListMenu extends JPopupMenu {

    private static final Logger logger = Logger.getLogger(OWLOntologyListMenu.class);

    private OWLEditorKit owlEditorKit;

    private OntologySelectedHandler handler;

    private Set<OWLOntology> ontologies;


    public OWLOntologyListMenu(String title, OWLEditorKit owlEditorKit, Set<OWLOntology> ontologies,
                               OntologySelectedHandler handler) {
        this.owlEditorKit = owlEditorKit;
        this.handler = handler;
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
                                                     BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }


    public void show(Component invoker, int x, int y) {
        for (final OWLOntology ont : ontologies) {
            String rendering = ont.getURI().toString();
            if (owlEditorKit.getModelManager().getActiveOntology().equals(ont)) {
                rendering += " (Active)";
            }
            add(new AbstractAction(rendering, OWLIcons.getIcon("ontology.png")) {
                public void actionPerformed(ActionEvent e) {
                    handler.ontologySelected(ont);
                }
            });
        }
        super.show(invoker, x, y);
    }


    public interface OntologySelectedHandler {

        void ontologySelected(OWLOntology ontology);
    }
}
