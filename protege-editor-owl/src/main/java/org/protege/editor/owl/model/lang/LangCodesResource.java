package org.protege.editor.owl.model.lang;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-15
 */
public class LangCodesResource {

    private static final Logger logger = LoggerFactory.getLogger(LangCodesResource.class);

    private static final String LANG_CODE_COL = "langCode";

    private static final String DESCRIPTION_COL = "description";

    private static final String LANGUAGE_CODES_CSV_PATH = "/language-codes.csv";

    private static ImmutableList<LangCode> cachedLangCodes;

    static {
        try {
            cachedLangCodes = loadLangCodes();
        } catch(IOException e) {
            logger.error("Could not load lang codes: {}", e.getMessage(), e);
            cachedLangCodes = ImmutableList.of();
        }
    }

    public static ImmutableList<LangCode> getLangCodes() {
        return cachedLangCodes;
    }

    private static ImmutableList<LangCode> loadLangCodes() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .addColumn(LANG_CODE_COL)
                .addColumn(DESCRIPTION_COL)
                .build();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        URL langCodesUrl = LangCodeRegistry.class.getResource(LANGUAGE_CODES_CSV_PATH);
        MappingIterator<LangCode> iterator = mapper.readerFor(LangCode.class).with(schema).readValues(langCodesUrl);
        ImmutableList.Builder<LangCode> langCodeListBuilder = ImmutableList.builder();
        while(iterator.hasNext()) {
            langCodeListBuilder.add(iterator.next());
        }
        ImmutableList<LangCode> langCodeList = langCodeListBuilder.build();
        ImmutableMap.Builder<String, LangCode> builder = ImmutableMap.builder();
        langCodeList.forEach(lc -> builder.put(lc.getLangCode().toLowerCase(), lc));
        return langCodeList;
    }
}
