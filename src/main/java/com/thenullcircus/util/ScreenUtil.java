package com.thenullcircus.util;

import java.awt.Dimension;
import java.awt.Toolkit;
public class ScreenUtil {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private ScreenUtil() {}
    public static int getScreenWidth() {
        return screenSize.width;
    }
    public static int getScreenHeight() {
        return screenSize.height;
    }
}
