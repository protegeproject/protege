package org.protege.editor.owl.ui.editor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests that a language tag survives while the editor is locked to a datatype taken from an annotation property's
 * range.  See <a href="https://github.com/protegeproject/protege/issues/1111">issue 1111</a>.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class OWLConstantEditor_TestCase {

    private static final String LEXICAL_VALUE = "hello";

    private static final String LANG = "en";

    @Mock
    private OWLEditorKit editorKit;

    @Mock
    private OWLModelManager modelManager;

    private OWLDataFactory dataFactory;

    private OWLDatatype xsdString;

    private OWLConstantEditor editor;

    @Before
    public void setUp() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
        xsdString = OWL2Datatype.XSD_STRING.getDatatype(dataFactory);
        when(editorKit.getModelManager()).thenReturn(modelManager);
        when(editorKit.getOWLModelManager()).thenReturn(modelManager);
        when(modelManager.getOWLDataFactory()).thenReturn(dataFactory);
        when(modelManager.getOWLOntologyManager()).thenReturn(ontologyManager);
        when(modelManager.getActiveOntologies()).thenReturn(Collections.emptySet());
        when(modelManager.getOWLObjectComparator()).thenReturn(Comparator.<OWLObject>naturalOrder());
        when(modelManager.getRendering(any(OWLObject.class))).thenAnswer(inv -> inv.getArgument(0).toString());
        editor = new OWLConstantEditor(editorKit, false);
        // The editor's own datatype renderer reads renderer preferences, which need a running OSGi framework
        findComponent(editor, JComboBox.class).ifPresent(c -> c.setRenderer(new DefaultListCellRenderer()));
    }

    @Test
    public void shouldKeepLangTagOfExistingValueWhenOverridingDatatypeIsSet() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        editor.setOverridingDatatype(xsdString);
        assertThat(editor.getEditedObject(), is(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG)));
    }

    @Test
    public void shouldLoadTaggedValueWhileOverridingDatatypeIsSet() {
        editor.setOverridingDatatype(xsdString);
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        assertThat(editor.getEditedObject(), is(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG)));
    }

    @Test
    public void shouldAcceptTypedLangTagWhileOverridingDatatypeIsSet() {
        editor.setOverridingDatatype(xsdString);
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, xsdString));
        getLangTagTextField().setText(LANG);
        assertThat(editor.getEditedObject(), is(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG)));
    }

    @Test
    public void shouldReportLangTagThatConflictsWithOverridingDatatype() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        editor.setOverridingDatatype(xsdString);
        assertThat(editor.getErrorMessage(), containsString("language tag is not allowed"));
    }

    @Test
    public void shouldNotReportLangTagWithoutOverridingDatatype() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        assertThat(editor.getErrorMessage(), is(""));
    }

    @Test
    public void shouldNotReportUntaggedValueWithOverridingDatatype() {
        editor.setOverridingDatatype(xsdString);
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, xsdString));
        assertThat(editor.getErrorMessage(), is(""));
    }

    @Test
    public void shouldShowLangTaggedDatatypeForLoadedTaggedValue() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        assertThat(getSelectedDatatype(), is(getLangTaggedDatatype()));
    }

    @Test
    public void shouldReplaceChosenDatatypeWithLangTaggedDatatypeWhenTagIsTyped() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, xsdString));
        getLangTagTextField().setText(LANG);
        assertThat(getSelectedDatatype(), is(getLangTaggedDatatype()));
    }

    @Test
    public void shouldRestoreParsedDatatypeWhenTagIsRemoved() {
        editor.setEditedObject(dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG));
        getLangTagTextField().setText("");
        assertThat(getSelectedDatatype(), is(xsdString));
        assertThat(editor.getEditedObject(), is(dataFactory.getOWLLiteral(LEXICAL_VALUE, xsdString)));
    }

    /**
     * The datatype of a language-tagged literal, as the OWL API represents it (rdf:PlainLiteral in OWL API 4).
     */
    private OWLDatatype getLangTaggedDatatype() {
        return dataFactory.getOWLLiteral(LEXICAL_VALUE, LANG).getDatatype();
    }

    private Object getSelectedDatatype() {
        Optional<JComboBox> field = findComponent(editor, JComboBox.class);
        assertThat("Datatype field present", field.isPresent(), is(true));
        return field.get().getSelectedItem();
    }

    /**
     * The language tag field is the only single-line text field in the editor; the lexical value is a text area.
     */
    private JTextField getLangTagTextField() {
        Optional<JTextField> field = findComponent(editor, JTextField.class);
        assertThat("Language tag text field present", field.isPresent(), is(true));
        return field.get();
    }

    private static <C extends Component> Optional<C> findComponent(Container container, Class<C> type) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                return Optional.of(type.cast(component));
            }
            if (component instanceof Container) {
                Optional<C> found = findComponent((Container) component, type);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }
}
