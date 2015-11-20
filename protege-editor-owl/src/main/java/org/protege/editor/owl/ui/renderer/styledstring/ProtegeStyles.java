package org.protege.editor.owl.ui.renderer.styledstring;

import org.protege.editor.owl.ui.renderer.OWLAnnotationCellRenderer2;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/09/2012
 */
public class ProtegeStyles {

    private static final ProtegeStyles instance = new ProtegeStyles();

    private static final Style STRIKE_THROUGH_STYLE = new Style(StrikeThroughAttribute.getSingle());

    private static final Style UNSATISFIABLE_CLASS_STYLE = new Style(ForegroundAttribute.get(Color.RED));

//    private static Color restrictionColor = new Color(178, 0, 178);
//    private static Color logicalOpColor = new Color(0, 178, 178);
//    private static Color axiomColor = new Color(10, 94, 168);
//    private static Color typeColor = new Color(178, 178, 178);

    private static final Style AXIOM_KEYWORD_STYLE = new Style(ForegroundAttribute.get(new Color(10, 94, 168)), FontWeightAttribute.getBoldFontWeight());

    private static final Style FRAME_KEYWORD_STYLE = new Style(ForegroundAttribute.get(new Color(178, 178, 178)), FontWeightAttribute.getBoldFontWeight());

    private static final Style CLASS_EXPRESSION_QUANTIFIER_STYLE = new Style(ForegroundAttribute.get(new Color(178, 0, 178)), FontWeightAttribute.getBoldFontWeight());

    private static final Style CLASS_EXPRESSION_CONNECTIVE_STYLE = new Style(ForegroundAttribute.get(new Color(0, 178, 178)), FontWeightAttribute.getBoldFontWeight());

    private static final Style HIGHLIGHT_STYLE = new Style(BackgroundAttribute.get(Color.YELLOW), ForegroundAttribute.get(Color.BLACK), FontWeightAttribute.getBoldFontWeight());

    private static final Style BLANK_STYLE = new Style();

    private static final Style ANNOTATION_PROPERTY_STYLE = new Style(ForegroundAttribute.get(OWLAnnotationCellRenderer2.ANNOTATION_PROPERTY_FOREGROUND));

    private static final Style ANNOTATION_LANG_STYLE = new Style(Color.GRAY);

    public static ProtegeStyles getStyles() {
        return instance;
    }

    public Style getDeprecatedEntityStyle() {
        return STRIKE_THROUGH_STYLE;
    }

    public Style getUnsatisfiableClassStyle() {
        return UNSATISFIABLE_CLASS_STYLE;
    }

    public Style getAnnotationPropertyStyle() {
        return ANNOTATION_PROPERTY_STYLE;
    }

    public Style getAnnotationLangStyle() {
        return ANNOTATION_LANG_STYLE;
    }


    public Style getKeywordStyle(ManchesterOWLSyntax keyword) {
        if (keyword.isAxiomKeyword()) {
            return AXIOM_KEYWORD_STYLE;
        }
        if (keyword.isFrameKeyword()) {
            return FRAME_KEYWORD_STYLE;
        }
        if (keyword.isClassExpressionQuantiferKeyword()) {
            return CLASS_EXPRESSION_QUANTIFIER_STYLE;
        }
        if (keyword.isClassExpressionConnectiveKeyword()) {
            return CLASS_EXPRESSION_CONNECTIVE_STYLE;
        }
        return BLANK_STYLE;
    }

    public static Style getHighlightStyle() {
        return HIGHLIGHT_STYLE;
    }
}
