package org.protege.editor.owl.ui.lang;

import org.protege.editor.owl.model.lang.LangCode;
import org.protege.editor.owl.model.lang.LangCodeRegistry;
import org.protege.editor.owl.ui.util.SuggestField;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-05-07
 */
public class LangTagEditor extends JComponent {

    private static final String PLACEHOLDER_TEXT = "Language Tag";

    private final LangCodeRegistry registry;

    private final SuggestField<LangCode> suggestField;

    public LangTagEditor(LangCodeRegistry registry) {
        this.registry = registry;
        this.suggestField = new SuggestField<>(PLACEHOLDER_TEXT);
        setLayout(new BorderLayout());
        add(suggestField, BorderLayout.NORTH);
        this.suggestField.setSuggestOracle(this::getLangCodesMatching);
        this.suggestField.setSuggestionToString(LangCode::getLangCode);
        this.suggestField.setRenderer(new LangCodeRenderer());
    }

    private Stream<LangCode> getLangCodesMatching(@Nonnull String query) {
        return registry
                .getLangCodes()
                .stream()
                .filter(lc -> matches(query, lc));
    }

    private static boolean matches(String enteredText, LangCode lc) {
        Pattern pattern = Pattern.compile(String.format("^.*\\b%s.*$", Pattern.quote(enteredText)), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(lc.getLangCode() + " " + lc.getDescription());
        return matcher.find();
    }

    @Nonnull
    public Optional<LangCode> getLangCode() {
        return registry.getLangCode(suggestField.getText().trim().toLowerCase());
    }

    public void setLangCode(@Nonnull LangCode langCode) {
        suggestField.setText(langCode.getLangCode());
    }

    public void clear() {
        suggestField.clear();
    }

    public void setChangeListener(ChangeListener changeListener) {
        suggestField.setChangeListener(changeListener);
    }
}
