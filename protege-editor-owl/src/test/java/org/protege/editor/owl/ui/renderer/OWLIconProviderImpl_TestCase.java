package org.protege.editor.owl.ui.renderer;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests icon selection for supported and unsupported OWL objects.
 */
public class OWLIconProviderImpl_TestCase {

    private final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();

    private OWLIconProviderImpl iconProvider;

    private OWLClass cls;

    @Before
    public void setUp() {
        iconProvider = new OWLIconProviderImpl(definedClass -> false);
        cls = dataFactory.getOWLClass(IRI.create("http://example.org/A"));
    }

    @Test
    public void shouldProvideIconForHandledObject() {
        assertThat(iconProvider.getIcon(cls), is(notNullValue()));
    }

    @Test
    public void shouldReturnNullForUnhandledObject() {
        assertThat(iconProvider.getIcon(dataFactory.getOWLLiteral("value")), is(nullValue()));
    }
}
