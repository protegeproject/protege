package org.protege.editor.owl.ui.renderer;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.protege.editor.core.ModelManagerEvent;
import org.protege.editor.core.ModelManagerListener;
import org.protege.editor.core.ModelManagerEvent.EventType;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.NamespaceUtil;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

  /*
   * Workaround for owlapi feature request 2896097.  Remove this fix when 
   * the simple rule renderer and parser is implemented.  Svn at time of 
   * commit is approximately 16831.  Class should be removed.
   */

public class SWRLRuleRenderer {
    public static final String SWRL_PREFIX = "swrl";
    public static final String SWRLB_PREFIX = "swrlb";
    
    private OWLModelManager            manager;
    private ShortFormProvider          entityRenderer;
    private SimpleIRIShortFormProvider iriRenderer;
    private Map<String, SWRLVariable>  renderedVariableMap;
    private NamespaceUtil              namespaceUtil;
    
    public SWRLRuleRenderer(OWLModelManager manager) {
        this.manager = manager;
        setNamespace();
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
            
            public void visit(SWRLBuiltInAtom node) {
                write(renderSWRLBuiltIn(node.getPredicate()));
                write("(");
                for (Iterator<SWRLDArgument> it = node.getArguments().iterator(); it.hasNext();) {
                    it.next().accept(this);
                    if (it.hasNext()) {
                        write(", ");
                    }
                }
                write(")");
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
    
    private String renderSWRLBuiltIn(IRI builtIn) {
        String[] split = namespaceUtil.split(builtIn.toString(), new String[2]);
        if (split[0] != null && !split[0].equals("") && split[1] != null && !split[1].equals("")) {
            String prefix = namespaceUtil.getNamespace2PrefixMap().get(split[0]);
            if (prefix == null) {
                ;
            }
            else if (prefix.equals("")) {
                return split[1]; 
            }
            else {
                return prefix + ":" + split[1];
            }
        }
        return builtIn.toQuotedString();
    }
    
    /*
     * TODO: Does the active ontology ever change on this guy?  Perhaps he should have a listener.
     * But he has two callers and one of them is not disposable.
     */
    private void setNamespace() {
        namespaceUtil = new NamespaceUtil();
        OWLOntology ontology = manager.getActiveOntology();
        OWLOntologyFormat format = manager.getOWLOntologyManager().getOntologyFormat(ontology);
        if (format instanceof PrefixOWLOntologyFormat) {
            PrefixOWLOntologyFormat prefixedFormat = (PrefixOWLOntologyFormat) format;
            for (Entry<String, String> entry : prefixedFormat.getPrefixName2PrefixMap().entrySet()) {
                String namespace = entry.getKey();
                String prefix = entry.getValue();
                namespaceUtil.setPrefix(namespace, prefix);
            }
        }
        addMapping(Namespaces.SWRL.toString(), SWRL_PREFIX);
        addMapping(Namespaces.SWRLB.toString(), SWRLB_PREFIX);
    }
    
    private void addMapping(String namespace, String prefix) {
        if (namespaceUtil.getNamespace2PrefixMap().get(namespace) == null && 
                !namespaceUtil.getNamespace2PrefixMap().values().contains(prefix)) {
            namespaceUtil.setPrefix(namespace, prefix);
        }
    }
    
}
