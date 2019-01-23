package org.protege.editor.core.ui.util;

import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import org.protege.editor.core.ui.laf.CheckBoxMenuItemIcon;
import org.protege.editor.core.ui.laf.ProtegeScrollBarUI;
import org.protege.editor.core.ui.laf.RadioButtonMenuItemIcon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;


/**
 *  @author Ray Fergerson
 *  
 */
public class ProtegePlasticTheme extends ExperienceBlue {
	
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Border lineBorder = BorderFactory.createLineBorder(getSecondary1(), 1, true);
        Border controlBorder = BorderFactory.createCompoundBorder(lineBorder,
                                                                  BorderFactory.createEmptyBorder(2, 5, 2, 5)
        );
        Color scrollBarThumbColor = new Color(200, 200, 200);
        Object[] uiDefaults = {
        		"Tree.expandedIcon", Icons.getIcon("hierarchy.expanded.gif"), 
        		"Tree.collapsedIcon", Icons.getIcon("hierarchy.collapsed.gif"),
                "Button.is3DEnabled", false,
                "Button.background", new Color(255, 255, 255),
                "Button.border", controlBorder,
                "CheckBoxMenuItem.checkIcon", new CheckBoxMenuItemIcon(),
                "RadioButtonMenuItem.checkIcon", new RadioButtonMenuItemIcon(),
                "ComboBox.background", new Color(255, 255, 255),
                "ComboBox.border", lineBorder,
                "ComboBox.arrowButtonBorder", BorderFactory.createEmptyBorder(),
                "Toolbar.background", new Color(255, 255, 255),
                "Separator.ui", "javax.swing.plaf.basic.BasicSeparatorUI",
                "Separator.background", getSecondary3(),
                "Menu.borderPainted", false,
                "MenuItem.disabledForeground", new Color(180, 180, 180),
                "ScrollPane.background", Color.WHITE,
                "ScrollBarUI", ProtegeScrollBarUI.class.getName(),
                "scrollbar", getSecondary3(),
                "Scrollbar.maxBumpsWidth", 0,
                "ScrollBar.squareButtons", false,
                "ScrollBar.border", BorderFactory.createEmptyBorder(),
                "ScrollBar.highlight", scrollBarThumbColor,
                "ScrollBar.foreground", Color.WHITE,
                "ScrollBar.darkShadow", scrollBarThumbColor,
                "ScrollBar.shadow", scrollBarThumbColor,
                "ScrollBar.thumb", scrollBarThumbColor,
                "ScrollBar.thumbDarkShadow", scrollBarThumbColor,
                "ScrollBar.thumbHighlight", scrollBarThumbColor,
                "ScrollBar.thumbShadow", scrollBarThumbColor,
                "ScrollBar.trackForeground", Color.WHITE,
                "ScrollBar.trackHighlight", Color.WHITE,
                "ScrollBar.trackHighlightForeground", Color.WHITE,
                "ScrollBar.width", 12,
                "ScrollBar.track", Color.WHITE,
                "ToolTip.background", new Color(250, 250, 250),
                "ToolTip.border", lineBorder,
                "TitledBorder.titleColor", getMenuForeground()
        };
        table.putDefaults(uiDefaults);
        table.forEach((key, val) -> {
            System.out.println(key + " ---> " + val);
        });
    }

    // Enabled control lines
    protected ColorUIResource getSecondary1() {
        return new ColorUIResource(200, 200, 200);
    }

    // Disabled control lines
    protected ColorUIResource getSecondary2() {
        return new ColorUIResource(220, 220, 220);
    }

    protected ColorUIResource getSecondary3() {
        return new ColorUIResource(244, 244, 244);
    }

    /**
     * Returns the menu background color. This
     * returns the value of {@code getSecondary3()}.
     *
     * @return the menu background color
     */
    @Override
    public ColorUIResource getMenuBackground() {
        return new ColorUIResource(237, 237, 237);
    }

    @Override
    public ColorUIResource getMenuItemBackground() {
        return getMenuBackground();
    }

    @Override
    public FontUIResource getTitleTextFont() {
        return new FontUIResource("Dialog", Font.BOLD, 14);
    }

    @Override
    public FontUIResource getControlTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 13);
    }

    @Override
    public FontUIResource getMenuTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 13);
    }

    @Override
    public FontUIResource getSubTextFont() {
        return new FontUIResource("Dialog", Font.BOLD, 12);
    }

    @Override
    public FontUIResource getSystemTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getUserTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getWindowTitleFont() {
        return new FontUIResource("Dialog", Font.BOLD, 12);
    }
}