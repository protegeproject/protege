package org.protege.editor.owl.ui.renderer;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

  /*
   * Workaround for owlapi feature request 2896097.  Remove this fix when 
   * the simple rule renderer and parser is implemented.  Svn at time of 
   * commit is approximately 16831.  Class should be removed.
   */

public class SWRLRuleRenderer {
    private OWLModelManager            manager;
    private ShortFormProvider          entityRenderer;
    private SimpleIRIShortFormProvider iriRenderer;
    private Map<String, SWRLVariable>  renderedVariableMap;
    
    public SWRLRuleRenderer(OWLModelManager manager) {
        this.manager = manager;
        entityRenderer = new ShortFormProvider(){
            public String getShortForm(OWLEntity owlEntity) {
                return SWRLRuleRenderer.this.manager.getRendering(owlEntity);
            }

            public void dispose() {
                // do nothing
            }
        };
        iriRenderer = new SimpleIRIShortFormProvider();
        renderedVariableMap = new HashMap<String, SWRLVariable>();
    }
    
    public  String render(SWRLRule rule) {
        StringWriter writer = new StringWriter();
        ManchesterOWLSyntaxObjectRenderer renderer = new ManchesterOWLSyntaxObjectRenderer(writer, entityRenderer) {
            public void visit(SWRLVariable node) {
                write("?");
                write(getVariableName(node));
            }
        };
        renderer.visit(rule);
        return writer.toString();
    }
    
    private String getVariableName(SWRLVariable node) {
        String shortForm = iriRenderer.getShortForm(node.getIRI());
        String rendering = shortForm;
        int i = 0;
        while (true) {
            if (renderedVariableMap.get(rendering) == null) {
                renderedVariableMap.put(rendering, node);
                return rendering;
            }
            else if (renderedVariableMap.get(rendering).equals(node)) {
                return rendering;
            }
            else {
                rendering = shortForm + (++i);
            }
        }
    }
    
}
