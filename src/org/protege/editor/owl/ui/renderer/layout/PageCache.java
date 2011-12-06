package org.protege.editor.owl.ui.renderer.layout;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/11/2011
 * <p>
 *     A cache for pages.  The cache is limited to some size, with the default size being given by the
 *     {@link #DEFAULT_CACHE_SIZE} field value.
 * </p>
 * <p>
 *     Pages are cached by {@PageCacheKey}s, which describe some object represented by the page, and whether or not
 *     the page is rendered as selected and with or without focus.
 * </p>
 */
public class PageCache {

    public static final int DEFAULT_CACHE_SIZE = 200;


    private int cacheSize = DEFAULT_CACHE_SIZE;

    private Map<PageCacheKey, Page> map = new LinkedHashMap<PageCacheKey, Page>();

    public PageCache() {
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Page getPage(PageCacheKey key) {
        return map.get(key);
    }

    public void put(PageCacheKey key, Page page) {
        if (map.size() == cacheSize) {
            Iterator<PageCacheKey> iterator = map.keySet().iterator();
            iterator.next();
            iterator.remove();
        }
        map.put(key, page);
    }
}
