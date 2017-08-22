package org.protege.editor.owl.ui.breadcrumb;

import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.function.Consumer;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public interface BreadcrumbView {

    void setParentArrowVisible(boolean visible);

    void setViewClickedHandler(@Nonnull Consumer<OWLObject> objectConsumer);

    JComponent asJComponent();
}
