package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyDescriptionFrame extends AbstractOWLFrame<OWLObjectProperty> {

    public OWLObjectPropertyDescriptionFrame(OWLEditorKit editorKit) {
        super(editorKit.getOWLModelManager().getOWLOntologyManager());
        addSection(new OWLObjectPropertyDomainFrameSection(editorKit, this));
        addSection(new OWLObjectPropertyRangeFrameSection(editorKit, this));

        addSection(new OWLEquivalentObjectPropertiesAxiomFrameSection(editorKit, this));
        addSection(new OWLSubObjectPropertyAxiomSuperPropertyFrameSection(editorKit, this));
        addSection(new OWLInverseObjectPropertiesAxiomFrameSection(editorKit, this));


        addSection(new OWLDisjointObjectPropertiesFrameSection(editorKit, this));
        addSection(new OWLPropertyChainAxiomFrameSection(editorKit, this));
    }
}
