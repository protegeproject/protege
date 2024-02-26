package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class OWLObjectRenderingContext {

    private final DeprecatedObjectChecker deprecatedObjectChecker;

    private final ClassSatisfiabilityChecker classSatisfiabilityChecker;

    private final ObjectPropertySatisfiabilityChecker objectPropertySatisfiabilityChecker;

    private final DataPropertySatisfiabilityChecker dataPropertySatisfiabilityChecker;

    private final LinkFactory linkFactory;

    private final ShortFormProvider shortFormProvider;

    private final IRIShortFormProvider iriShortFormProvider;

    final private OntologyIRIShortFormProvider ontologyIRIShortFormProvider;


    public OWLObjectRenderingContext(DeprecatedObjectChecker deprecatedObjectChecker,
                                     ClassSatisfiabilityChecker classSatisfiabilityChecker,
                                     ObjectPropertySatisfiabilityChecker objectPropertySatisfiabilityChecker,
                                     DataPropertySatisfiabilityChecker dataPropertySatisfiabilityChecker,
                                     LinkFactory linkFactory,
                                     ShortFormProvider shortFormProvider,
                                     OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this.deprecatedObjectChecker = deprecatedObjectChecker;
        this.classSatisfiabilityChecker = classSatisfiabilityChecker;
        this.objectPropertySatisfiabilityChecker = objectPropertySatisfiabilityChecker;
        this.dataPropertySatisfiabilityChecker = dataPropertySatisfiabilityChecker;
        this.linkFactory = linkFactory;
        this.shortFormProvider = shortFormProvider;
        this.ontologyIRIShortFormProvider = ontologyIRIShortFormProvider;
        this.iriShortFormProvider = new SimpleIRIShortFormProvider();
    }

    public OWLObjectRenderingContext(DeprecatedObjectChecker deprecatedObjectChecker,
                                     ClassSatisfiabilityChecker classSatisfiabilityChecker,
                                     ObjectPropertySatisfiabilityChecker objectPropertySatisfiabilityChecker,
                                     DataPropertySatisfiabilityChecker dataPropertySatisfiabilityChecker,
                                     LinkFactory linkFactory,
                                     ShortFormProvider shortFormProvider,
                                     IRIShortFormProvider iriShortFormProvider,
                                     OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this.deprecatedObjectChecker = deprecatedObjectChecker;
        this.classSatisfiabilityChecker = classSatisfiabilityChecker;
        this.objectPropertySatisfiabilityChecker = objectPropertySatisfiabilityChecker;
        this.dataPropertySatisfiabilityChecker = dataPropertySatisfiabilityChecker;
        this.linkFactory = linkFactory;
        this.shortFormProvider = shortFormProvider;
        this.iriShortFormProvider = iriShortFormProvider;
        this.ontologyIRIShortFormProvider = ontologyIRIShortFormProvider;
    }

    public OWLObjectRenderingContext(ShortFormProvider shortFormProvider,
                                     OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this(new NullDeprecatedObjectChecker(),
             new NullClassSatisfiabilityChecker(),
             new NullObjectPropertySatisfiabilityChecker(),
             new NullDataPropertySatisfiabilityChecker(),
             new NullLinkFactory(),
             shortFormProvider,
             ontologyIRIShortFormProvider);
    }

    public OWLObjectRenderingContext(ShortFormProvider shortFormProvider,
                                     IRIShortFormProvider iriShortFormProvider,
                                     OntologyIRIShortFormProvider ontologyIRIShortFormProvider) {
        this(new NullDeprecatedObjectChecker(),
             new NullClassSatisfiabilityChecker(),
             new NullObjectPropertySatisfiabilityChecker(),
             new NullDataPropertySatisfiabilityChecker(),
             new NullLinkFactory(),
             shortFormProvider,
             iriShortFormProvider,
             ontologyIRIShortFormProvider);
    }

    public OWLObjectRenderingContext() {
        deprecatedObjectChecker = new NullDeprecatedObjectChecker();
        classSatisfiabilityChecker = new NullClassSatisfiabilityChecker();
        objectPropertySatisfiabilityChecker = new NullObjectPropertySatisfiabilityChecker();
        dataPropertySatisfiabilityChecker = new NullDataPropertySatisfiabilityChecker();
        linkFactory = new NullLinkFactory();
        shortFormProvider = new SimpleShortFormProvider();
        ontologyIRIShortFormProvider = new OntologyIRIShortFormProvider();
        iriShortFormProvider = new SimpleIRIShortFormProvider();
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
        return new OWLObjectRenderingContext(this.deprecatedObjectChecker,
                                             classSatisfiabilityChecker,
                                             this.objectPropertySatisfiabilityChecker,
                                             this.dataPropertySatisfiabilityChecker,
                                             this.linkFactory,
                                             this.shortFormProvider,
                                             this.ontologyIRIShortFormProvider);
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

    public IRIShortFormProvider getIriShortFormProvider() {
        return iriShortFormProvider;
    }
}
