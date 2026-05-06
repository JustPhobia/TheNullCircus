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

        formCard.setBackground(Theme.BG_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE
                )
        ));

        // Build GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Add components to formCard, not mainPanel
        formCard.removeAll();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0; formCard.add(welcomeText, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1; formCard.add(usernameTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 2; formCard.add(usernameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 3; formCard.add(passwordTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 4; formCard.add(passwordField, gbc);
        gbc.gridy = 5; formCard.add(loginButton, gbc);
        gbc.gridy = 6; formCard.add(errorLabel, gbc);

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

        // Add formCard to mainPanel
        mainPanel.removeAll();
        mainPanel.add(formCard);

        mainPanel.revalidate();
        mainPanel.repaint();
    }
}