package org.protege.editor.core.ui.about;


import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.osgi.framework.Bundle;
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

    private List<Bundle> bundles;

    public enum Columns {
        NAME("Name/ID"), VERSION("Version"), QUALIFIER("Qualifier");

        private String name;

        private Columns(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public PluginInfoTableModel() {
        bundles = Arrays.stream(ProtegeApplication.getContext().getBundles())
                .filter(ProtegeApplication::isPlugin)
                .sorted(comparing(
                        b -> (String) b.getHeaders().get(Constants.BUNDLE_NAME),
                        nullsLast(CASE_INSENSITIVE_ORDER)))
                .collect(toList());
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
                String name = (String) bundle.getHeaders().get(Constants.BUNDLE_NAME);
                if (name == null) {
                    name = bundle.getSymbolicName();
                }
                return name;
            case VERSION:
                return v == null ? "" : "" + v.getMajor() + "." + v.getMinor() + "." + v.getMicro();
            case QUALIFIER:
                return v.getQualifier();
            default:
                throw new RuntimeException("Programmer error - missed a case");
        }
    }


    public String getColumnName(int column) {
        return Columns.values()[column].getName();
    }
}
