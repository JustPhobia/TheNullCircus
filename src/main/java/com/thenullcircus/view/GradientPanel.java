package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private final Color startColor;
    private final Color endColor;

    public GradientPanel(Color startColor, Color endColor) {
        super();
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false);
    }
    public GradientPanel(Color startColor, Color endColor, LayoutManager layout) {
        super(layout);
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(
                0, 0, startColor,
                0, getHeight(), endColor
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

    }
}
