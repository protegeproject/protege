package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.cls.OWLClassAssertionAxiomMembersSection;
import org.protege.editor.owl.ui.frame.cls.OWLDisjointClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLDisjointUnionAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection;
import org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyRangeFrameSection;
import org.protege.editor.owl.ui.frame.dataproperty.OWLDisjointDataPropertiesFrameSection;
import org.protege.editor.owl.ui.frame.dataproperty.OWLEquivalentDataPropertiesFrameSection;
import org.protege.editor.owl.ui.frame.dataproperty.OWLSubDataPropertyAxiomSuperPropertyFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLDataPropertyAssertionAxiomFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLDifferentIndividualsAxiomFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLNegativeDataPropertyAssertionFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLNegativeObjectPropertyAssertionFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLObjectPropertyAssertionAxiomFrameSection;
import org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLDisjointObjectPropertiesFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLEquivalentObjectPropertiesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyRangeFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLPropertyChainAxiomFrameSection;
import org.protege.editor.owl.ui.frame.objectproperty.OWLSubObjectPropertyAxiomSuperPropertyFrameSection;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 11-Jul-2007<br><br>
 */
public class OWLEntityFrame extends AbstractOWLFrame implements OWLEntityVisitor {

    private OWLEditorKit editorKit;


    private List<OWLFrameSection> owlOntologyFrameSections;

    private List<OWLFrameSection> owlClassSections;

    private List<OWLFrameSection> owlObjectPropertySections;

    private List<OWLFrameSection> owlDataPropertySections;

    private List<OWLFrameSection> owlAnnotationPropertySections;

    private List<OWLFrameSection> owlIndividualSections;

    private OWLModelManagerListener owlModelManagerListener = new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if(event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
                    refill();
                    setRootObject(editorKit.getModelManager().getActiveOntology());
                }
            }
        };


    @SuppressWarnings("rawtypes")
	public OWLEntityFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
        this.editorKit = editorKit;

        owlOntologyFrameSections = new ArrayList<>();
        owlOntologyFrameSections.add(new InferredAxiomsFrameSection(editorKit, this));


        owlClassSections = new ArrayList<>();

        owlClassSections.add(new OWLAnnotationFrameSection(editorKit, this));
        owlClassSections.add(new OWLEquivalentClassesAxiomFrameSection(editorKit, this));
        owlClassSections.add(new OWLSubClassAxiomFrameSection(editorKit, this));
        owlClassSections.add(new OWLClassAssertionAxiomMembersSection(editorKit, this));
        owlClassSections.add(new OWLDisjointClassesAxiomFrameSection(editorKit, this));
        owlClassSections.add(new OWLDisjointUnionAxiomFrameSection(editorKit, this));

        owlObjectPropertySections = new ArrayList<>();

        owlObjectPropertySections.add(new OWLAnnotationFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLObjectPropertyDomainFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLObjectPropertyRangeFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLEquivalentObjectPropertiesAxiomFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLSubObjectPropertyAxiomSuperPropertyFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLDisjointObjectPropertiesFrameSection(editorKit, this));
        owlObjectPropertySections.add(new OWLPropertyChainAxiomFrameSection(editorKit, this));


        owlDataPropertySections = new ArrayList<>();

        owlDataPropertySections.add(new OWLAnnotationFrameSection(editorKit, this));
        owlDataPropertySections.add(new OWLDataPropertyDomainFrameSection(editorKit, this));
        owlDataPropertySections.add(new OWLDataPropertyRangeFrameSection(editorKit, this));
        owlDataPropertySections.add(new OWLEquivalentDataPropertiesFrameSection(editorKit, this));
        owlDataPropertySections.add(new OWLSubDataPropertyAxiomSuperPropertyFrameSection(editorKit, this));
        owlDataPropertySections.add(new OWLDisjointDataPropertiesFrameSection(editorKit, this));


        owlAnnotationPropertySections = new ArrayList<>();



        owlIndividualSections = new ArrayList<>();

        owlIndividualSections.add(new OWLAnnotationFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLClassAssertionAxiomTypeFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLObjectPropertyAssertionAxiomFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLNegativeObjectPropertyAssertionFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLDataPropertyAssertionAxiomFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLNegativeDataPropertyAssertionFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLSameIndividualsAxiomFrameSection(editorKit, this));
        owlIndividualSections.add(new OWLDifferentIndividualsAxiomFrameSection(editorKit, this));

        setRootObject(editorKit.getModelManager().getOWLDataFactory().getOWLThing());

        editorKit.getModelManager().addListener(owlModelManagerListener);
    }


    public void setRootObject(OWLObject rootObject) {
        clearSections();
        if (rootObject != null) {
            if(rootObject instanceof OWLOntology) {
                setupSections(owlOntologyFrameSections);
            }
            else {
                ((OWLEntity) rootObject).accept(this);
            }

        }
        super.setRootObject(rootObject);
    }


    public void visit(OWLClass owlClass) {
        setupSections(owlClassSections);
    }


    public void visit(OWLObjectProperty owlObjectProperty) {
        setupSections(owlObjectPropertySections);
    }


    public void visit(OWLDataProperty owlDataProperty) {
        setupSections(owlDataPropertySections);
    }


    public void visit(OWLAnnotationProperty owlAnnotationProperty) {
        setupSections(owlAnnotationPropertySections);
    }


    public void visit(OWLDatatype owlDatatype) {
    }


    public void visit(OWLNamedIndividual owlIndividual) {
        setupSections(owlIndividualSections);
    }

    
    private void setupSections(List<OWLFrameSection> sections) {
        for(OWLFrameSection sec : sections) {
            addSection(sec);
        }
    }


    public void dispose() {
        super.dispose();
        editorKit.getModelManager().removeListener(owlModelManagerListener);
        disposeOfSections(owlClassSections);
        disposeOfSections(owlObjectPropertySections);
        disposeOfSections(owlDataPropertySections);
        disposeOfSections(owlAnnotationPropertySections);
        disposeOfSections(owlIndividualSections);
    }


    private void disposeOfSections(List<OWLFrameSection> sections) {
        for (OWLFrameSection sec : sections) {
            sec.dispose();
        }
    }
}
