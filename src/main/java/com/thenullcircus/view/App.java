package com.thenullcircus.view;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class
App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            System.out.println("There was an error while trying to load the UI...");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });

    }
}
