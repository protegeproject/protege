package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLPropertyChainChecker;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
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
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyChainEditor extends AbstractOWLFrameSectionRowObjectEditor<List<OWLObjectPropertyExpression>> {

    private static final Logger logger = Logger.getLogger(OWLObjectPropertyChainEditor.class);

    private JLabel impliesLabel;

    private OWLEditorKit owlEditorKit;

    private JPanel panel;

    protected ExpressionEditor<List<OWLObjectPropertyExpression>> editor;


    public OWLObjectPropertyChainEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        editor = new ExpressionEditor<List<OWLObjectPropertyExpression>>(owlEditorKit,
                                                                         new OWLPropertyChainChecker(owlEditorKit.getOWLModelManager()));
        editor.setPreferredSize(editor.getPreferredSize());
        impliesLabel = new JLabel();
        panel = new JPanel(new BorderLayout(7, 7));
        panel.add(editor);
        panel.add(impliesLabel, BorderLayout.EAST);
    }


    public List<OWLObjectPropertyExpression> getEditedObject() {
        try {
            return editor.createObject();
        }
        catch (OWLException e) {
            logger.error(e);
        }
        return null;
    }


    public void clear() {
        editor.setText("");
    }


    public void setAxiom(OWLObjectPropertyChainSubPropertyAxiom ax) {
        String rendering = "";
        for (Iterator<OWLObjectPropertyExpression> it = ax.getPropertyChain().iterator(); it.hasNext();) {
            rendering += owlEditorKit.getOWLModelManager().getRendering(it.next());
            if (it.hasNext()) {
                rendering += " o ";
            }
        }
        editor.setText(rendering);
        setSuperProperty(ax.getSuperProperty());
    }


    public void setSuperProperty(OWLObjectPropertyExpression prop) {
        String rendering = owlEditorKit.getOWLModelManager().getRendering(prop);
        impliesLabel.setText(" \u279E " + rendering);
    }


    public JComponent getEditorComponent() {
        return panel;
    }


    public void dispose() {
    }
}
