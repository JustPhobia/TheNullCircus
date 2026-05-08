package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;

class RoundedLabel extends JLabel {
    private final int cornerRadius;

    public RoundedLabel(String text, int radius) {
        super(text);
        this.cornerRadius = radius;
        setOpaque(false); // must be false so our custom background doesn't get clipped
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); // create a copy so we don't affect other paint ops
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose(); // always dispose the Graphics2D copy to free resources
        super.paintComponent(g); // paints the label text on top of our background
    }
}
