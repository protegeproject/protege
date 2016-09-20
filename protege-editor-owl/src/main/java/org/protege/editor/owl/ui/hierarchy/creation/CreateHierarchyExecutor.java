package org.protege.editor.owl.ui.hierarchy.creation;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.tabbed.*;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16 Sep 16
 */
public class CreateHierarchyExecutor<E extends OWLEntity> {

    private static final Logger logger = LoggerFactory.getLogger(CreateHierarchyExecutor.class);

    private final OWLEditorKit editorKit;

    private final EntityType<E> entityType;

    private final Optional<Recommendation> makeSiblingsDisjointRecommendation;

    private final CreateHierarchyChangeGenerator<E> hierarchyChangeGenerator;

    private final MakeSiblingsDisjointChangeGenerator<E> makeSiblingsDisjointChangeGenerator;

    public CreateHierarchyExecutor(@Nonnull OWLEditorKit editorKit,
                                   @Nonnull EntityType<E> entityType,
                                   @Nonnull Optional<Recommendation> makeSiblingsDisjointRecommendation,
                                   @Nonnull CreateHierarchyChangeGenerator<E> hierarchyChangeGenerator,
                                   @Nonnull MakeSiblingsDisjointChangeGenerator<E> makeSiblingsDisjointChangeGenerator) {
        this.editorKit = checkNotNull(editorKit);
        this.entityType = checkNotNull(entityType);
        this.hierarchyChangeGenerator = checkNotNull(hierarchyChangeGenerator);
        this.makeSiblingsDisjointChangeGenerator = checkNotNull(makeSiblingsDisjointChangeGenerator);
        this.makeSiblingsDisjointRecommendation = checkNotNull(makeSiblingsDisjointRecommendation);
    }

    public void showWizardAndCreateHierarchy() {
        CreateSubHierarchyWizard wizard = new CreateSubHierarchyWizard(
                editorKit,
                entityType,
                makeSiblingsDisjointRecommendation);
        if(wizard.showModalDialog() != Wizard.FINISH_RETURN_CODE) {
            return;
        }
        try {
            createHierarchy(wizard);
        } catch (IOException ex) {
            logger.error("An error occurred whilst parsing the tab-indented hierarchy", ex);
        }
    }

    private void createHierarchy(CreateSubHierarchyWizard wizard) throws IOException {
        List<OWLOntologyChange> changes = new ArrayList<>();

        String termPrefix = wizard.getPrefix();
        String termSuffix = wizard.getSuffix();
        String tabIndentedHierarchy = wizard.getHierarchy();

        TabIndentedHierarchyParser parser = new TabIndentedHierarchyParser(4, termPrefix, termSuffix);
        List<Edge> edges = parser.parse(new StringReader(tabIndentedHierarchy));

        ImmutableSetMultimap<E, E> parent2ChildMap = hierarchyChangeGenerator.generateHierarchy(edges, changes);

        if(wizard.isMakeSiblingsDisjoint().orElse(false)) {
            List<OWLOntologyChange> disjointSibsChanges = makeSiblingsDisjointChangeGenerator.generateChanges(parent2ChildMap);
            changes.addAll(disjointSibsChanges);
        }

        final OWLModelManager modelManager = editorKit.getOWLModelManager();
        modelManager.applyChanges(changes);
    }
}
