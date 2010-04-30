package org.protege.editor.core.plugin;

import org.eclipse.core.runtime.IExtension;

public class OrPluginExtensionMatcher implements PluginExtensionMatcher {
    private PluginExtensionMatcher[] extensionMatchers;
    
    public OrPluginExtensionMatcher(PluginExtensionMatcher... extensionMatchers) {
        this.extensionMatchers = extensionMatchers;
    }

    @Override
    public boolean matches(IExtension extension) {
        for (PluginExtensionMatcher extensionMatcher : extensionMatchers) {
            if (extensionMatcher.matches(extension)) {
                return true;
            }
        }
        return false;
    }

}
