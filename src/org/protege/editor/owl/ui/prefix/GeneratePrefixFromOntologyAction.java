package org.protege.editor.owl.ui.prefix;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GeneratePrefixFromOntologyAction extends AbstractAction {

    private static final Logger logger = Logger.getLogger(GeneratePrefixFromOntologyAction.class);


    private OWLEditorKit owlEditorKit;

    private PrefixMapperTable table;


    public GeneratePrefixFromOntologyAction(OWLEditorKit owlEditorKit, PrefixMapperTable table) {
        super("Generate from ontology URI", OWLIcons.getIcon("prefix.generate.png"));
        putValue(AbstractAction.SHORT_DESCRIPTION, "Generate prefix mappings from ontology URIs...");
        this.owlEditorKit = owlEditorKit;
        this.table = table;
    }


    public void actionPerformed(ActionEvent e) {
        UIHelper uiHelper = new UIHelper(owlEditorKit);
        Set<OWLOntology> ontologies = uiHelper.pickOWLOntologies();
        for (OWLOntology ont : ontologies) {
            String uriString = ont.getURI().toString();
            String prefix;
            if (uriString.endsWith("/")) {
                String sub = uriString.substring(0, uriString.length() - 1);
                prefix = sub.substring(sub.lastIndexOf("/") + 1, sub.length());
            }
            else {
                prefix = uriString.substring(uriString.lastIndexOf('/') + 1, uriString.length());
            }
            if (prefix.endsWith(".owl")) {
                prefix = prefix.substring(0, prefix.length() - 4);
            }
            prefix = prefix.toLowerCase();
            if (!uriString.endsWith("#") && !uriString.endsWith("/")) {
                uriString = uriString + "#";
            }
            int index = table.getPrefixMapperTableModel().addMapping(prefix, uriString);
            table.getSelectionModel().setSelectionInterval(index, index);
        }
    }
}
