package org.protege.editor.owl.ui.renderer.styledstring;

import java.text.AttributedCharacterIterator;

import javax.swing.text.StyleConstants;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/09/2012
 */
public abstract class StyleAttribute {

    public abstract String getCSSPropertyName();

    public abstract String getCSSValue();

    public abstract AttributedCharacterIterator.Attribute getAttributedStringAttribute();

    public abstract Object getAttributesStringValue();


    public abstract StyleConstants getTextAttribute();

    public abstract Object getTextValue();

}
