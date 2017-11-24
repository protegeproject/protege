package org.protege.editor.core.log;

import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * A {@link StyledEditorKit} using which one can set line wrapping of the text
 * 
 * @author Yevgeny Kazakov
 */
public class LogEditorKit extends StyledEditorKit implements ViewFactory {

	private static final long serialVersionUID = -6193302665953492671L;
	private boolean lineWrap_ = false;

	public boolean isLineWrap() {
		return lineWrap_;

	}

	public void setLineWrap(boolean wrap) {
		this.lineWrap_ = wrap;
	}

	private class LogParagraphView extends ParagraphView {
		public LogParagraphView(Element elem) {
			super(elem);
		}

		@Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {

			if (lineWrap_)
				return super.calculateMinorAxisRequirements(axis, r);

			SizeRequirements req = super.calculateMinorAxisRequirements(axis,
					r);
			req.minimum = req.preferred;
			return req;
		}

		@Override
		public int getFlowSpan(int index) {

			if (lineWrap_)
				return super.getFlowSpan(index);

			return Integer.MAX_VALUE;
		}
	}

	@Override
	public ViewFactory getViewFactory() {
		return this;
	}

	@Override
	public View create(Element elem) {
		String kind = elem.getName();
		if (kind != null)
			if (kind.equals(AbstractDocument.ContentElementName)) {
				return new LabelView(elem);
			} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
				return new LogParagraphView(elem);
			} else if (kind.equals(AbstractDocument.SectionElementName)) {
				return new BoxView(elem, View.Y_AXIS);
			} else if (kind.equals(StyleConstants.ComponentElementName)) {
				return new ComponentView(elem);
			} else if (kind.equals(StyleConstants.IconElementName)) {
				return new IconView(elem);
			}
		return new LabelView(elem);
	}
}