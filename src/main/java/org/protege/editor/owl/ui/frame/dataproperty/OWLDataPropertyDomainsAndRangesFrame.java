package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyDomainsAndRangesFrame extends AbstractOWLFrame<OWLDataProperty> {

    public OWLDataPropertyDomainsAndRangesFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new OWLDataPropertyDomainFrameSection(owlEditorKit, this));
        addSection(new OWLDataPropertyRangeFrameSection(owlEditorKit, this));
    }
}
