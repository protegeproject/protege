package org.protege.editor.owl.ui.renderer.context;

import java.util.Collections;
import java.util.List;

import org.protege.editor.owl.ui.renderer.layout.Link;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class NullLinkFactory implements LinkFactory {

    public List<Link> getLinks(OWLObject object) {
        return Collections.emptyList();
    }
}
