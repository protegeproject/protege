package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class OWLObjectRenderingContext {

    final private DeprecatedObjectChecker deprecatedObjectChecker;

    final private ClassSatisfiabilityChecker classSatisfiabilityChecker;

    final private ObjectPropertySatisfiabilityChecker objectPropertySatisfiabilityChecker;

    final private DataPropertySatisfiabilityChecker dataPropertySatisfiabilityChecker;

    final private LinkFactory linkFactory;

    final private ShortFormProvider shortFormProvider;

    final private OntologyIRIShortFormProvider ontologyIRIShortFormProvider;


    public OWLObjectRenderingContext(DeprecatedObjectChecker deprecatedObjectChecker, ClassSatisfiabilityChecker classSatisfiabilityChecker, ObjectPropertySatisfiabilityChecker objectPropertySatisfiabilityChecker, DataPropertySatisfiabilityChecker dataPropertySatisfiabilityChecker, LinkFactory linkFactory, ShortFormProvider shortFormProvider, OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this.deprecatedObjectChecker = deprecatedObjectChecker;
        this.classSatisfiabilityChecker = classSatisfiabilityChecker;
        this.objectPropertySatisfiabilityChecker = objectPropertySatisfiabilityChecker;
        this.dataPropertySatisfiabilityChecker = dataPropertySatisfiabilityChecker;
        this.linkFactory = linkFactory;
        this.shortFormProvider = shortFormProvider;
        this.ontologyIRIShortFormProvider = ontologyIRIShortFormProvider;
    }

    public OWLObjectRenderingContext(ShortFormProvider shortFormProvider, OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this(new NullDeprecatedObjectChecker(), new NullClassSatisfiabilityChecker(), new NullObjectPropertySatisfiabilityChecker(), new NullDataPropertySatisfiabilityChecker(), new NullLinkFactory(), shortFormProvider, ontologyIRIShortFormProvider);
    }


    public OWLObjectRenderingContext() {
        deprecatedObjectChecker = new NullDeprecatedObjectChecker();
        classSatisfiabilityChecker = new NullClassSatisfiabilityChecker();
        objectPropertySatisfiabilityChecker = new NullObjectPropertySatisfiabilityChecker();
        dataPropertySatisfiabilityChecker = new NullDataPropertySatisfiabilityChecker();
        linkFactory = new NullLinkFactory();
        shortFormProvider = new SimpleShortFormProvider();
        ontologyIRIShortFormProvider = new OntologyIRIShortFormProvider();
    }

    public ShortFormProvider getShortFormProvider() {
        return shortFormProvider;
    }

    public OntologyIRIShortFormProvider getOntologyIRIShortFormProvider() {
        return ontologyIRIShortFormProvider;
    }

    public ClassSatisfiabilityChecker getClassSatisfiabilityChecker() {
        return classSatisfiabilityChecker;
    }


    public OWLObjectRenderingContext setClassSatisfiabilityChecker(ClassSatisfiabilityChecker classSatisfiabilityChecker) {
        return new OWLObjectRenderingContext(this.deprecatedObjectChecker, classSatisfiabilityChecker, this.objectPropertySatisfiabilityChecker, this.dataPropertySatisfiabilityChecker, this.linkFactory, this.shortFormProvider, this.ontologyIRIShortFormProvider);
    }


    public ObjectPropertySatisfiabilityChecker getObjectPropertySatisfiabilityChecker() {
        return objectPropertySatisfiabilityChecker;
    }


    public DataPropertySatisfiabilityChecker getDataPropertySatisfiabilityChecker() {
        return dataPropertySatisfiabilityChecker;
    }

    public DeprecatedObjectChecker getDeprecatedObjectChecker() {
        return deprecatedObjectChecker;
    }

    public LinkFactory getLinkFactory() {
        return linkFactory;
    }
}
