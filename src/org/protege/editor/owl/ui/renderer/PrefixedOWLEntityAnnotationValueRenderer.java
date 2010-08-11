package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.prefix.PrefixMapper;
import org.protege.editor.owl.ui.prefix.MergedPrefixMapperManager;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixedOWLEntityAnnotationValueRenderer extends OWLEntityAnnotationValueRenderer {
	private MergedPrefixMapperManager prefixManager;
    
    public void initialise() {
    	prefixManager = new MergedPrefixMapperManager(getOWLModelManager());
    }
    
    @Override
    public void ontologiesChanged() {
    	prefixManager = new MergedPrefixMapperManager(getOWLModelManager());
    }
    
    public String render(OWLEntity entity) {
        String shortForm = getProvider().getShortForm(entity);
        if (OWLRendererPreferences.getInstance().isRenderPrefixes()){
            final String uriStr = entity.getIRI().toString();

            PrefixMapper mapper = prefixManager.getMapper();
            for (String base : mapper.getValues()){
                if (uriStr.startsWith(base)){
                    return escape(mapper.getPrefix(base) + ":" + shortForm);
                }
            }
        }
        return escape(shortForm);
    }
}