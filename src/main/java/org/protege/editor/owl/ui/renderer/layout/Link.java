package org.protege.editor.owl.ui.renderer.layout;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/11/2011
 * <p>
 *     Represents some kind of hyper link on a page.  SubClasses of this class define specific kinds of links and their
 *     behaviour when clicked.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public abstract class Link {

    private String text;


    public Link(String linkLabel) {
        this.text = linkLabel;
    }

    public String getText() {
        return text;
    }

    public abstract void activate(Component component, MouseEvent event);

    public abstract boolean isRollOverLink();


    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Link)) {
            return false;
        }
        Link other = (Link) obj;
        return text.equals(other.text);
    }



}
