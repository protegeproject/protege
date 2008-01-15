package org.protege.editor.core.ui.list;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public abstract class MListButton {

    private String name;

    private Color rollOverColor;

    private ActionListener actionListener;

    private Rectangle bounds;

    private Object rowObject;


    protected MListButton(String name, Color rollOverColor, ActionListener actionListener) {
        this.name = name;
        this.rollOverColor = rollOverColor;
        this.actionListener = actionListener;
        if(actionListener == null) {
            actionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                }
            };
        }
    }

    protected MListButton(String name, Color rollOverColor) {
        this(name, rollOverColor, null);
    }


    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }


    public String getName() {
        return name;
    }


    public Object getRowObject() {
        return rowObject;
    }


    public void setRowObject(Object rowObject) {
        this.rowObject = rowObject;
    }


    public Color getRollOverColor() {
        return rollOverColor;
    }


    public ActionListener getActionListener() {
        return actionListener;
    }


    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public Color getBackground() {
        return Color.LIGHT_GRAY;
    }

    /**
     * Paints the button content. For convenience, the graphics origin will be
     * the top left corner of the button
     * @param g The graphics which should be used for rendering
     *          the content
     */
    public abstract void paintButtonContent(Graphics2D g);
}
