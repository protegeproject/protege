package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

import java.text.FieldPosition;
import java.text.NumberFormat;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 28, 2008<br><br>
 */
public abstract class AbstractIDGenerator implements AutoIDGenerator {

    private static final String START_MACRO = "\\[";
    private static final String END_MACRO = "\\]";


    public String getNextID(Class<? extends OWLEntity> type) throws AutoIDException {
        return getPrefix(type) + pad(getRawID(type), getDigitLength()) + getSuffix(type);
    }


    protected abstract long getRawID(Class<? extends OWLEntity> type) throws AutoIDException;


    protected String getPrefix(Class<? extends OWLEntity> type){
        String prefix = EntityCreationPreferences.getPrefix();
        return preprocess(prefix, type);
    }


    protected String getSuffix(Class<? extends OWLEntity> type){
        String suffix = EntityCreationPreferences.getSuffix();
        return preprocess(suffix, type);
    }


    protected String preprocess(String s, Class<? extends OWLEntity> type) {
        s = s.replaceAll(START_MACRO + "user" + END_MACRO, System.getProperty("user.name", "no_one"));
        s = s.replaceAll(START_MACRO + "type" + END_MACRO, type.getSimpleName());
        return s;
    }


    protected int getDigitLength(){
        return EntityCreationPreferences.getAutoIDDigitCount();
    }

    
    private String pad(long l, int padding) {
        StringBuffer sb = new StringBuffer();
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(0);
        format.setMinimumIntegerDigits(padding);
        format.setMaximumIntegerDigits(padding);
        format.setGroupingUsed(false);
        format.format(l, sb, new FieldPosition(0));
        return sb.toString();
    }
}
