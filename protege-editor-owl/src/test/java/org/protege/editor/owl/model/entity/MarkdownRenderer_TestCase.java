package org.protege.editor.owl.model.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Sep 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class MarkdownRenderer_TestCase {


    @Mock
    private OWLModelManager modelManager;

    @Mock
    private OWLEntity entity;

    @Mock
    private IRI iri;

    private String iriString = "The IRI String";

    private String displayName = "The display name";

    private MarkdownRenderer markdownRenderer;

    @Before
    public void setUp() throws Exception {
        markdownRenderer = new MarkdownRenderer(modelManager);
        when(entity.getIRI()).thenReturn(iri);
        when(iri.toString()).thenReturn(iriString);
    }

    @Test
    public void shouldRenderMarkdown() {
        when(modelManager.getRendering(entity)).thenReturn(displayName);
        String rendering = markdownRenderer.renderMarkdown(entity);
        assertThat(rendering, is("[" + displayName + "](" + iriString + ")"));
    }

    @Test
    public void shouldRenderWithoutQuotes() {
        when(modelManager.getRendering(entity)).thenReturn("'" + displayName + "'");
        String rendering = markdownRenderer.renderMarkdown(entity);
        assertThat(rendering, is("[" + displayName + "](" + iriString + ")"));
    }


}
