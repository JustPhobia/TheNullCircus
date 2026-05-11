package com.thenullcircus.view;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        logger.info("Starting NullCircus Application...");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            logger.info("Look and Feel set to CrossPlatform.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize UI theme.", e);
        }

        SwingUtilities.invokeLater(() -> {
            logger.info("Creating MainWindow on Event Dispatch Thread...");
            MainWindow window = new MainWindow();
            window.setVisible(true);
            logger.info("Application window is now visible.");
        });
    }
}