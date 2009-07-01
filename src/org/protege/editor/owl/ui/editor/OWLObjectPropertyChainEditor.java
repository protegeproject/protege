package org.protege.editor.owl.ui.editor;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 *
 * Should probably just be of type OWLSubPropertyChainOfAxiom
 */
public class OWLObjectPropertyChainEditor extends AbstractOWLObjectEditor<List<OWLObjectPropertyExpression>>
        implements VerifiedInputEditor {

    private static final Logger logger = Logger.getLogger(OWLObjectPropertyChainEditor.class);

    private JLabel impliesLabel;

    private OWLEditorKit owlEditorKit;

    private JPanel panel;

    protected ExpressionEditor<List<OWLObjectPropertyExpression>> editor;

    public OWLObjectPropertyChainEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        final OWLExpressionChecker<List<OWLObjectPropertyExpression>> checker = owlEditorKit.getModelManager().getOWLExpressionCheckerFactory().getPropertyChainChecker();
        editor = new ExpressionEditor<List<OWLObjectPropertyExpression>>(owlEditorKit, checker);
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


    public boolean setEditedObject(List<OWLObjectPropertyExpression> objectPropertyList) {
        editor.setExpressionObject(objectPropertyList);
        return true;
    }


    public void setAxiom(OWLSubPropertyChainOfAxiom ax) {
        String rendering = "";
        for (Iterator<OWLObjectPropertyExpression> it = ax.getPropertyChain().iterator(); it.hasNext();) {
            rendering += owlEditorKit.getModelManager().getRendering(it.next());
            if (it.hasNext()) {
                rendering += " o ";
            }
        }
        editor.setText(rendering);
        setSuperProperty(ax.getSuperProperty());
    }


    public void setSuperProperty(OWLObjectPropertyExpression prop) {
        String rendering = owlEditorKit.getModelManager().getRendering(prop);
        impliesLabel.setText(" \u279E " + rendering);
    }


    public String getEditorTypeName() {
        return "List of Object Properties";
    }


    public boolean canEdit(Object object) {
        return checkList(object, OWLObjectPropertyExpression.class);
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
