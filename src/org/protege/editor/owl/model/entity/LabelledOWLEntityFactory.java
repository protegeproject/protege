package org.protege.editor.owl.model.entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LabelledOWLEntityFactory implements OWLEntityFactory {

    private OWLModelManager owlModelManager;


    public LabelledOWLEntityFactory(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    private OWLDataFactory getOWLDataFactory() {
        return owlModelManager.getOWLDataFactory();
    }


    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) {
        OWLClass cls = getOWLDataFactory().getOWLClass(createURI(shortName, "Class", baseURI));
        return new OWLEntityCreationSet<OWLClass>(cls, getChanges(cls, shortName));
    }


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) {
        OWLObjectProperty property = getOWLDataFactory().getOWLObjectProperty(createURI(shortName,
                                                                                        "ObjectProperty",
                                                                                        baseURI));
        return new OWLEntityCreationSet<OWLObjectProperty>(property, getChanges(property, shortName));
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) {
        OWLDataProperty property = getOWLDataFactory().getOWLDataProperty(createURI(shortName,
                                                                                    "DataProperty",
                                                                                    baseURI));
        return new OWLEntityCreationSet<OWLDataProperty>(property, getChanges(property, shortName));
    }


    public OWLEntityCreationSet<OWLIndividual> createOWLIndividual(String shortName, URI baseURI) {
        OWLIndividual property = getOWLDataFactory().getOWLIndividual(createURI(shortName, "Individual", baseURI));
        return new OWLEntityCreationSet<OWLIndividual>(property, getChanges(property, shortName));
    }


    protected List<OWLOntologyChange> getChanges(OWLEntity owlEntity, String shortName) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        // Add the label
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLConstant con = df.getOWLUntypedConstant(shortName);
        OWLAnnotation anno = df.getOWLConstantAnnotation(OWLRDFVocabulary.RDFS_LABEL.getURI(), con);
        OWLAxiom ax = df.getOWLEntityAnnotationAxiom(owlEntity, anno);
        changes.add(new AddAxiom(owlModelManager.getActiveOntology(), ax));
        return changes;
    }


    protected static URI createURI(String shortName, String prefix, URI baseURI) {
        String base = baseURI.toString();
        if (!base.endsWith("#") && !base.endsWith("/")) {
            base += "#";
        }
        return URI.create(base + prefix + System.nanoTime());
    }
}
