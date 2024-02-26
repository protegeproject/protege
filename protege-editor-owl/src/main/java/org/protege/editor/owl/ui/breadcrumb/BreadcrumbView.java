package org.protege.editor.owl.ui.breadcrumb;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

import org.semanticweb.owlapi.model.OWLObject;

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
