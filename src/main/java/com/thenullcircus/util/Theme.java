package com.thenullcircus.util;

import java.awt.*;

public class Theme {

    // --- Background Colors ---
    public static final Color BG_DEEP        = new Color(26, 10, 46);    // outer dark purple
    public static final Color BG_CARD        = new Color(18, 8, 34);     // card surface
    public static final Color BG_INPUT       = new Color(30, 15, 56);    // input fields
    public static final Color BG_HOVER       = new Color(255, 233, 78);  // button hover yellow

    // --- Accent Colors ---
    public static final Color ACCENT_PINK    = new Color(255, 60, 172);  // hot pink — primary
    public static final Color ACCENT_YELLOW  = new Color(255, 233, 78);  // circus yellow
    public static final Color ACCENT_CYAN    = new Color(0, 229, 255);   // electric cyan
    public static final Color ACCENT_PURPLE  = new Color(176, 106, 255); // soft purple

    // --- Text Colors ---
    public static final Color TEXT_PRIMARY   = new Color(240, 230, 255); // near white purple
    public static final Color TEXT_LABEL     = new Color(255, 60, 172);  // pink labels
    public static final Color TEXT_MUTED     = new Color(90, 58, 122);   // muted purple
    public static final Color TEXT_SUBTITLE  = new Color(176, 133, 216); // light purple

    // --- Semantic Colors ---
    public static final Color ERROR          = new Color(255, 107, 107);
    public static final Color SUCCESS        = new Color(255, 233, 78);

    // --- Border Colors ---
    public static final Color BORDER_DEFAULT = new Color(74, 42, 110);   // dark purple border
    public static final Color BORDER_FOCUS   = new Color(255, 60, 172);  // pink on focus

    // --- Fonts ---
    public static final Font FONT_TITLE      = new Font("Serif", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE   = new Font("Serif", Font.BOLD, 18);
    public static final Font FONT_LABEL      = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_BODY       = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BUTTON     = new Font("Serif", Font.BOLD, 16);
    public static final Font FONT_ERROR      = new Font("SansSerif", Font.PLAIN, 12);

    // --- Spacing ---
    public static final int PADDING_SMALL    = 8;
    public static final int PADDING_MEDIUM   = 16;
    public static final int PADDING_LARGE    = 40;

    // --- Sizes ---
    public static final Dimension INPUT_SIZE  = new Dimension(300, 42);
    public static final Dimension BUTTON_SIZE = new Dimension(300, 46);

    private Theme() {}
}