package org.protege.editor.core.ui.about;




import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.protege.editor.core.ProtegeApplication;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoTableModel extends AbstractTableModel {

    private List<Bundle> bundles = new ArrayList<Bundle>();
    
    public enum Columns  {
        NAME("Name/ID"), VERSION("Version"), BUILD("Build");
        
        private String name;
        
        private Columns(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }


    public PluginInfoTableModel() {
        BundleContext context = ProtegeApplication.getContext();
        for (Bundle b : context.getBundles()) {
            bundles.add(b);
        }
    }


    public int getRowCount() {
        return bundles.size();
    }


    public int getColumnCount() {
        return Columns.values().length;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        Bundle bundle = bundles.get(rowIndex);
        String version_name = (String) bundle.getHeaders().get(Constants.BUNDLE_VERSION);
        Version v = null;
        if (version_name != null) {
             v = new Version(version_name);
        }
        switch (Columns.values()[columnIndex]) {
        case NAME:
            return bundle.getSymbolicName();
        case VERSION:
            return v == null ? "" : "" + v.getMajor() + "." + v.getMinor();
        case BUILD:
            return "" + v.getMicro();
        default:
            throw new RuntimeException("Programmer error - missed a case");
        }
    }


    public String getColumnName(int column) {
        return Columns.values()[column].getName();
    }
}
