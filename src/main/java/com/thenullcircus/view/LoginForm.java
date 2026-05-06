package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;
import util.Theme;

public class LoginForm extends BasePanel {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel welcomeText;
    private JLabel usernameTitle;
    private JLabel passwordTitle;
    private JPanel formCard;

    public LoginForm(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        styleComponents();
    }

    public void styleComponents() {
        mainPanel.setBackground(Theme.BG_DEEP);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                Theme.PADDING_LARGE, Theme.PADDING_LARGE
        ));
        //setting the background for the card behind the form
        formCard.setBackground(Theme.BG_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE
                )
        ));




        // Build GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(6, 0, 6, 0);

        //this code centres all the fields
        mainPanel.removeAll();

// Centered components
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0; mainPanel.add(welcomeText, gbc);

// Left-aligned labels and their fields
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1; mainPanel.add(usernameTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 2; mainPanel.add(usernameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 3; mainPanel.add(passwordTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 4; mainPanel.add(passwordField, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 5; mainPanel.add(loginButton, gbc);
        gbc.gridy = 6; mainPanel.add(errorLabel, gbc);

        // Title
        welcomeText.setFont(Theme.FONT_TITLE);
        welcomeText.setForeground(Theme.ACCENT_YELLOW);

        // Labels
        usernameTitle.setFont(Theme.FONT_LABEL);
        usernameTitle.setForeground(Theme.TEXT_LABEL);
        passwordTitle.setFont(Theme.FONT_LABEL);
        passwordTitle.setForeground(Theme.TEXT_LABEL);

        // Input fields
        Dimension inputSize = new Dimension(280, 42);

        usernameField.setPreferredSize(inputSize);
        usernameField.setMinimumSize(inputSize);
        usernameField.setMaximumSize(inputSize);
        usernameField.setBackground(Theme.BG_INPUT);
        usernameField.setForeground(Theme.TEXT_PRIMARY);
        usernameField.setCaretColor(Theme.ACCENT_PINK);
        usernameField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        usernameField.setFont(Theme.FONT_BODY);

        passwordField.setPreferredSize(inputSize);
        passwordField.setMinimumSize(inputSize);
        passwordField.setMaximumSize(inputSize);
        passwordField.setBackground(Theme.BG_INPUT);
        passwordField.setForeground(Theme.TEXT_PRIMARY);
        passwordField.setCaretColor(Theme.ACCENT_PINK);
        passwordField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        passwordField.setFont(Theme.FONT_BODY);

        // Button
        Dimension btnSize = new Dimension(280, 46);

        loginButton.setPreferredSize(btnSize);
        loginButton.setMinimumSize(btnSize);
        loginButton.setMaximumSize(btnSize);
        loginButton.setBackground(Theme.ACCENT_PINK);
        loginButton.setForeground(Theme.BG_DEEP);
        loginButton.setFont(Theme.FONT_BUTTON);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);

        // Error label
        errorLabel.setFont(Theme.FONT_ERROR);
        errorLabel.setForeground(Theme.ERROR);

        mainPanel.revalidate();
        mainPanel.repaint();
    }
}