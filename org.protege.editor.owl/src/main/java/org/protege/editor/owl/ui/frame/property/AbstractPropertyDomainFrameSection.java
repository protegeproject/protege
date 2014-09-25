package org.protege.editor.owl.ui.frame.property;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 16, 2008<br><br>
 */
public abstract class AbstractPropertyDomainFrameSection<P extends OWLProperty, A extends OWLPropertyDomainAxiom>  extends AbstractOWLFrameSection<P, A, OWLClassExpression> {

    public static final String LABEL = "Domains (intersection)";

    Set<OWLClassExpression> addedDomains = new HashSet<OWLClassExpression>();


    public AbstractPropertyDomainFrameSection(OWLEditorKit editorKit, OWLFrame<P> frame) {
        super(editorKit, LABEL, "Domain", frame);
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        AxiomType type;
        if (getRootObject() instanceof OWLObjectProperty){
            type = AxiomType.OBJECT_PROPERTY_DOMAIN;
        }
        else{
            type = AxiomType.DATA_PROPERTY_DOMAIN;
        }
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, type);        
    }


    public final boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    public final boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression desc = (OWLClassExpression) obj;
                OWLAxiom ax = createAxiom(desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    protected abstract AbstractPropertyDomainFrameSectionRow<P, A> createFrameSectionRow(A domainAxiom, OWLOntology ontology);


    protected abstract Set<A> getAxioms(OWLOntology ontology);


    protected abstract NodeSet<OWLClass> getInferredDomains();


    protected void clear() {
        addedDomains.clear();
    }


    protected final void refill(OWLOntology ontology) {
        for (A ax : getAxioms(ontology)) {
            addRow(createFrameSectionRow(ax, ontology));
            addedDomains.add(ax.getDomain());
        }
    }


    protected void refillInferred() {
        for (Node<OWLClass> domains : getInferredDomains()) {
            for (OWLClassExpression domain : domains){
                if (!addedDomains.contains(domain)) {
                    addInferredRowIfNontrivial(createFrameSectionRow(createAxiom(domain), null));
                    addedDomains.add(domain);
                }
            }
        }
    }


    public Comparator<OWLFrameSectionRow<P, A, OWLClassExpression>> getRowComparator() {
        return null;
    }
}
