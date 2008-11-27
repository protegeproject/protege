package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLSubClassAxiomFrameSection extends AbstractOWLFrameSection<OWLClass, OWLSubClassAxiom, OWLDescription> {

    private static final String LABEL = "Superclasses";

    private Set<OWLDescription> added = new HashSet<OWLDescription>();


    public OWLSubClassAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, "Superclass", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLSubClassAxiom ax : ontology.getSubClassAxiomsForLHS(getRootObject())) {
            addRow(new OWLSubClassAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            added.add(ax.getSuperClass());
        }
    }


    protected void refillInferred() {
        try {
            if (getOWLModelManager().getReasoner().isSatisfiable(getRootObject())) {
                for (Set<OWLClass> descs : getOWLModelManager().getReasoner().getSuperClasses(getRootObject())) {
                    for (OWLClass desc : descs) {
                        if (!added.contains(desc)) {
                            addRow(new OWLSubClassAxiomFrameSectionRow(getOWLEditorKit(),
                                                                       this,
                                                                       null,
                                                                       getRootObject(),
                                                                       getOWLModelManager().getOWLDataFactory().getOWLSubClassAxiom(
                                                                               getRootObject(),
                                                                               desc)));
                            added.add(desc);
                        }
                    }
                }
            }
        }
        catch (OWLReasonerException e) {
            throw new RuntimeException(e);
        }
    }


    protected OWLSubClassAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLSubClassAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return new OWLClassDescriptionEditor(getOWLEditorKit(), null);
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLDescription)) {
                return false;
            }
        }
        return true;
    }


    private OWLObjectProperty prop;


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLDescription) {
                OWLDescription desc;
                if (prop != null) {
                    desc = getOWLDataFactory().getOWLObjectSomeRestriction(prop, (OWLDescription) obj);
                }
                else {
                    desc = (OWLDescription) obj;
                }
                OWLAxiom ax = getOWLDataFactory().getOWLSubClassAxiom(getRootObject(), desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else if (obj instanceof OWLObjectProperty) {
                // Prime
                prop = (OWLObjectProperty) obj;
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    public void visit(OWLSubClassAxiom axiom) {
        if (axiom.getSubClass().equals(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLClass, OWLSubClassAxiom, OWLDescription>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLClass, OWLSubClassAxiom, OWLDescription>>() {


            public int compare(OWLFrameSectionRow<OWLClass, OWLSubClassAxiom, OWLDescription> o1,
                               OWLFrameSectionRow<OWLClass, OWLSubClassAxiom, OWLDescription> o2) {
                if (o1.isInferred()) {
                    if (!o2.isInferred()) {
                        return 1;
                    }
                }
                else {
                    if (o2.isInferred()) {
                        return -1;
                    }
                }
//                int val = o1.getAxiom().getSuperClass().compareTo(o2.getAxiom().getSuperClass());
                int val = getOWLModelManager().getOWLObjectComparator().compare(o1.getAxiom(), o2.getAxiom());

                if(val == 0) {
                    return o1.getOntology().getURI().compareTo(o2.getOntology().getURI());
                }
                else {
                    return val;
                }

            }
        };
    }
}
