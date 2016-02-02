package org.protege.editor.owl.ui.renderer.layout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/11/2011
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class ActionSpan {

    private List<Action> actions;

    private String paragraphText;

    private List<LinkSpan> linkSpans = new ArrayList<>();

    public ActionSpan(List<Action> actions) {
        this.actions = actions;
        StringBuilder sb = new StringBuilder();
        for(Iterator<Action> it = actions.iterator(); it.hasNext(); ) {
            Action action = it.next();
            sb.append("[");
            int linkStart = sb.length();
            sb.append(action.getValue(Action.NAME));
            int linkEnd = sb.length();
            LinkSpan linkSpan = new LinkSpan(new ActionLink(action), new Span(linkStart, linkEnd));
            linkSpans.add(linkSpan);
            sb.append("]");
            if(it.hasNext()) {
                sb.append("  ");
            }
        }
        paragraphText = sb.toString();
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public List<LinkSpan> getLinkSpans() {
        return linkSpans;
    }

    public Paragraph createParagraph() {
        return new Paragraph(paragraphText, linkSpans);
    }
}
