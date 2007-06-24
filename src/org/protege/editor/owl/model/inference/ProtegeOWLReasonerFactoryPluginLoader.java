package org.protege.editor.owl.model.inference;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.owl.model.OWLModelManager;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeOWLReasonerFactoryPluginLoader extends AbstractPluginLoader<ProtegeOWLReasonerFactoryPlugin> {

    private OWLModelManager owlModelManager;


    public ProtegeOWLReasonerFactoryPluginLoader(OWLModelManager owlModelManager) {
        super("org.protege.editor.owl", ProtegeOWLReasonerFactoryPlugin.REASONER_PLUGIN_TYPE_ID);
        this.owlModelManager = owlModelManager;
    }


    /**
     * This method needs to be overriden to create an instance
     * of the desired plugin, based on the plugin <code>Extension</code>
     * @param extension The <code>Extension</code> that describes the
     *                  Java Plugin Framework extension.
     * @return A plugin object (typically some sort of wrapper around
     *         the extension)
     */
    protected ProtegeOWLReasonerFactoryPlugin createInstance(IExtension extension) {
        return new ProtegeOWLReasonerFactoryPluginJPFImpl(owlModelManager, extension);
    }


    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    protected PluginExtensionMatcher getExtensionMatcher() {
        return new PluginExtensionMatcher() {
            /**
             * Determines whether the specified <code>Extension</code>
             * constitutes a "match" or not.
             * @param extension The <code>Extension</code> to test.
             * @return <code>true</code> if the <code>Extension</code> matches
             *         or <code>false</code> if the <code>Extension</code> doesn't match.
             */
            public boolean matches(IExtension extension) {
                return true;
            }
        };
    }
}
