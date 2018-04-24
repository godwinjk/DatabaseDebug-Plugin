package com.godwin.debug.common;

import java.awt.*;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ViewUtil {
    public static void setCursor(int cursor, Component... components) {
        for (Component component : components) {
            component.setCursor(Cursor.getPredefinedCursor(cursor));
        }
    }
}
