package com.godwin.debug.ui;

import com.godwin.debug.model.DApplication;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Godwin on 5/9/2018 10:13 AM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class CellRenderer extends JLabel implements javax.swing.ListCellRenderer<DApplication> {
    @Override
    public Component getListCellRendererComponent(JList list, DApplication value, int index, boolean isSelected, boolean cellHasFocus) {
        ImageIcon imageIcon = value.getIcon();
        if (null == imageIcon) {
            imageIcon = new ImageIcon(getClass().getResource("/images/android.png"));
        }
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  //
        setIcon(imageIcon);

        //<html><FONT COLOR=RED>Red</FONT> and <FONT COLOR=BLUE>Blue</FONT> Text</html>
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><p>");
        builder.append("<font size=\"5\"><b>")
                .append(value.getAppName())
                .append("-v")
                .append(value.getVersion())
                .append("</p></b></font>");
        builder.append("<p><font size=\"4\">")
                .append("</br>")
                .append(value.getPackageName())
                .append("</font>");
        builder.append("</p></body></html>");

        setText(builder.toString());
        return this;
    }
}
