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


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyChainEditor extends AbstractOWLFrameSectionRowObjectEditor<List<OWLObjectPropertyExpression>>
        implements VerifiedInputEditor {

    private static final Logger logger = Logger.getLogger(OWLObjectPropertyChainEditor.class);

    private JLabel impliesLabel;

    private OWLEditorKit owlEditorKit;

    private JPanel panel;

    protected ExpressionEditor<List<OWLObjectPropertyExpression>> editor;

    public OWLObjectPropertyChainEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        editor = new ExpressionEditor<List<OWLObjectPropertyExpression>>(owlEditorKit,
                                                                         new OWLPropertyChainChecker(owlEditorKit.getOWLModelManager()));
        Dimension prefSize = editor.getPreferredSize();
        editor.setPreferredSize(new Dimension(350, prefSize.height));
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

    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.addStatusChangedListener(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.removeStatusChangedListener(listener);
    }
}
