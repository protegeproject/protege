package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

import java.text.FieldPosition;
import java.text.NumberFormat;

/*
* Copyright (C) 2007, University of Manchester
*
*
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
