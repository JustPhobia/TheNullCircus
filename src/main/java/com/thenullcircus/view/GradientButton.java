package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;

public class GradientButton extends JButton {

    private final Color startColor;
    private final Color endColor;

    public GradientButton(String label, Color startColor, Color endColor) {
        super(label);
        this.startColor = startColor;
        this.endColor   = endColor;
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(
                getWidth(), 0, startColor,   // top right
                0,          0, endColor      // top left
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2d.dispose();
        super.paintComponent(g);
    }
}