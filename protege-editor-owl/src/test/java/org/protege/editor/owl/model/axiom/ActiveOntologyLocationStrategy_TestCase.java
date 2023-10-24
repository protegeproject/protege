package org.protege.editor.owl.model.axiom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.protege.editor.owl.model.HasActiveOntology;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class ActiveOntologyLocationStrategy_TestCase {

    @Mock
    private OWLAxiom axiom;

    @Mock
    private HasActiveOntology hasActiveOntology;

    @Mock
    private OWLOntology ontology;

    @Before
    public void setUp() {
        when(hasActiveOntology.getActiveOntology()).thenReturn(ontology);
    }

    @Test
    public void shouldReturnActiveOntology() {
        ActiveOntologyLocationStrategy strategy = new ActiveOntologyLocationStrategy();
        OWLOntology ont = strategy.getFreshAxiomLocation(axiom, hasActiveOntology);
        assertThat(ont, is(equalTo(ontology)));
    }
}
