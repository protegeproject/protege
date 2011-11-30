package org.protege.editor.owl.ui.renderer.layout;

import java.awt.print.Book;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 28/11/2011
 * <p>
 *     A PageCacheKey describes objects that pages represent and also the visual
 *     state of the page - whether it is selected or not, and whether it has the focus or not.  This allows pages
 *     representing particular objects and states to be represented and retrived from caches.
 * </p>
 */
public class PageCacheKey {

    private Object objectKey;

    private Boolean isSelected = Boolean.FALSE;

    private Boolean hasFocus = Boolean.FALSE;

    public PageCacheKey(Object objectKey, Boolean selected, Boolean hasFocus) {
        this.objectKey = objectKey;
        isSelected = selected;
        this.hasFocus = hasFocus;
    }

    public Object getObjectKey() {
        return objectKey;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public Boolean getHasFocus() {
        return hasFocus;
    }

    @Override
    public int hashCode() {
        return objectKey.hashCode() + isSelected.hashCode() + hasFocus.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof PageCacheKey)) {
            return false;
        }
        PageCacheKey other = (PageCacheKey) obj;
        return other.objectKey.equals(this.objectKey) && other.isSelected.equals(this.isSelected) && other.hasFocus.equals(this.hasFocus);
    }
}
