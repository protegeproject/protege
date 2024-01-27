package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class LiteralLexicalValueReplacer {

    private static final Pattern WHOLE_STRING_PATTERN = Pattern.compile("^.*$");

    @Nonnull
    private final OWLDataFactory dataFactory;

    public LiteralLexicalValueReplacer(@Nonnull OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    @Nonnull
    public OWLLiteral replaceLexicalValue(@Nonnull OWLLiteral literal,
                                          @Nonnull Pattern pattern,
                                          @Nonnull String replacement) {
        checkNotNull(literal);
        checkNotNull(replacement);
        String currentLexicalValue = literal.getLiteral();
        String replacementLexicalValue = pattern.matcher(currentLexicalValue).replaceAll(replacement);
        if (literal.hasLang()) {
            return dataFactory.getOWLLiteral(
                    replacementLexicalValue,
                    literal.getLang());
        }
        else {
            return dataFactory.getOWLLiteral(
                    replacementLexicalValue,
                    literal.getDatatype());
        }

    }

    @Nonnull
    public OWLLiteral replaceLexicalValue(@Nonnull OWLLiteral literal,
                                          @Nonnull String replacement) {
        return replaceLexicalValue(literal, WHOLE_STRING_PATTERN, replacement);
    }

}
