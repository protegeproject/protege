package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.conf.valueset.LabelledValue;
import org.protege.editor.owl.model.conf.valueset.ValueSetComponent;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
public class DeprecationCodePage extends AbstractOWLWizardPanel {

    public static final String ID = "DeprecationCodePage";

    private final DeprecateEntityWizardState wizardState;

    private final ValueSetComponent component = new ValueSetComponent();

    public DeprecationCodePage(@Nonnull OWLEditorKit owlEditorKit,
                               @Nonnull DeprecateEntityWizardState wizardState) {
        super(ID, "Deprecation code", owlEditorKit);
        setInstructions("<b>Please select a reason for the deprecation</b>");
        this.wizardState = checkNotNull(wizardState);
        JPanel contentHolder = new JPanel(new BorderLayout());
        contentHolder.add(component);
        setContent(contentHolder);
    }

    @Override
    public void aboutToDisplayPanel() {
        wizardState.getDeprecationProfile().ifPresent(profile -> {
            profile.getDeprecationCode().ifPresent(code -> {
                List<LabelledValue> values = code.getValueSet().getLabelledValues(getOWLModelManager());
                component.setValues(values);
                for(int i = 0; i < values.size(); i++) {
                    LabelledValue v = values.get(i);
                    Optional<OWLAnnotationValue> annoValue = v.toOWLAnnotationValue(getOwlDataFactory());
                    if(annoValue.equals(wizardState.getDeprecationCode())) {
                        component.setSelectedIndex(i);
                        break;
                    }
                }

            });
        });
    }

    private OWLDataFactory getOwlDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }

    @Override
    public void aboutToHidePanel() {
        wizardState.setDeprecationCode(null);
        component.getSelectedValue()
                 .flatMap(v -> v.toOWLAnnotationValue(getOwlDataFactory()))
                 .ifPresent(wizardState::setDeprecationCode);
    }

    @Nullable
    @Override
    public Object getBackPanelDescriptor() {
        return DeprecationReasonPage.ID;
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return DeprecationReplacementEntityPage.ID;
    }
}
