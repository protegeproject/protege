package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


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

    private URI defaultURI;

    private String defaultLang;


    public LabelledOWLEntityFactory(OWLModelManager owlModelManager, URI defaultURI, String defaultLang) {
        this.owlModelManager = owlModelManager;
        this.defaultURI = defaultURI;
        this.defaultLang = defaultLang;
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
        String lang = getLabelLanguage();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLConstant con;
        if (lang == null){
            con = df.getOWLUntypedConstant(shortName);
        }
        else{
            con = df.getOWLUntypedConstant(shortName, lang);
        }
        OWLAnnotation anno = df.getOWLConstantAnnotation(getLabelURI(), con);
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


    public URI getLabelURI() {
        return defaultURI;
    }

    public String getLabelLanguage() {
        return defaultLang;
    }
}
