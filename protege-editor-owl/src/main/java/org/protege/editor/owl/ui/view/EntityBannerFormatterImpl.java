package org.protege.editor.owl.ui.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.RenderingEscapeUtils;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 7 Sep 16
 */
public class EntityBannerFormatterImpl implements EntityBannerFormatter {

    private static final Pattern OBO_STYLE_ID_PATTERN = Pattern.compile("/(\\w+)_(\\d+)$");

    @Nonnull
    @Override
    public String formatBanner(@Nonnull OWLEntity entity, @Nonnull OWLEditorKit editorKit) {
        String rendering = editorKit.getOWLModelManager().getRendering(entity);
        String unescapedRendering = RenderingEscapeUtils.unescape(rendering);
        String iri = entity.getIRI().toString();
        String oboStyleId = "";
        Matcher oboIdMatcher = OBO_STYLE_ID_PATTERN.matcher(iri);
        if(oboIdMatcher.find()) {
            oboStyleId = " \u2014 " + oboIdMatcher.group(1) + ":" + oboIdMatcher.group(2);
        }
        return String.format(
                "<html><body><nobr>%s <span style='color: #808080;'>%s \u2014 %s</span><nobr></body></html>",
                unescapedRendering,
                oboStyleId,
                iri);
    }
}
