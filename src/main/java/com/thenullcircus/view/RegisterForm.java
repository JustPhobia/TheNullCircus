package com.thenullcircus.view;

import com.thenullcircus.controller.client.RegisterController;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class RegisterForm extends BasePanel {

    // UI Components
    private final JPanel mainPanel;
    private final JPanel formCard;
    private final JLabel titleText;
    private final JLabel subtitleText;
    private final JLabel nameTitle;
    private final JLabel surnameTitle;
    private final JLabel emailTitle;
    private final JLabel genderTitle;
    private final JLabel usernameTitle;
    private final JLabel passwordTitle;
    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField emailField;
    private final JComboBox<String> genderDropdown;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton registerButton;
    private final JLabel errorLabel;
    private final JLabel loginLink;

    // Controller
    private RegisterController registerController;

    public RegisterForm(MainWindow mainWindow) {
        super(mainWindow);

        // Initialise all components
        mainPanel = new JPanel(new GridBagLayout());
        formCard = new JPanel(new GridBagLayout());
        titleText = new JLabel("Join The Circus");
        subtitleText = new JLabel("Create your account");
        nameTitle = new JLabel("First Name");
        surnameTitle = new JLabel("Surname");
        emailTitle = new JLabel("Email");
        genderTitle = new JLabel("Gender");
        usernameTitle = new JLabel("Username");
        passwordTitle = new JLabel("Password");
        nameField = new JTextField();
        surnameField = new JTextField();
        emailField = new JTextField();
        // Dropdown options must match your Gender enum exactly
        genderDropdown = new JComboBox<>(new String[]{"MALE", "FEMALE", "NON_BINARY", "OTHER"});
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        registerButton = new JButton("Register");
        errorLabel = new JLabel();
        loginLink = new JLabel("<html>Already have an account? Login here</html>");

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        styleComponents();
        initController();
    }

    private void styleComponents() {
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 0, 6, 0);

        formCard.removeAll();

        // Add all components to the card in order
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0;
        formCard.add(titleText, gbc);
        gbc.gridy = 1;
        formCard.add(subtitleText, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        formCard.add(nameTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 3;
        formCard.add(nameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 4;
        formCard.add(surnameTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 5;
        formCard.add(surnameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 6;
        formCard.add(emailTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 7;
        formCard.add(emailField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 8;
        formCard.add(genderTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 9;
        formCard.add(genderDropdown, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 10;
        formCard.add(usernameTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 11;
        formCard.add(usernameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 12;
        formCard.add(passwordTitle, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 13;
        formCard.add(passwordField, gbc);

        gbc.gridy = 14;
        formCard.add(registerButton, gbc);
        gbc.gridy = 15;
        formCard.add(errorLabel, gbc);
        gbc.gridy = 16;
        formCard.add(loginLink, gbc);

        // Style titles
        titleText.setFont(Theme.FONT_TITLE);
        titleText.setForeground(Theme.ACCENT_YELLOW);
        subtitleText.setFont(Theme.FONT_SUBTITLE);
        subtitleText.setForeground(Theme.TEXT_SUBTITLE);

        // Style labels
        nameTitle.setFont(Theme.FONT_LABEL);
        nameTitle.setForeground(Theme.TEXT_LABEL);
        surnameTitle.setFont(Theme.FONT_LABEL);
        surnameTitle.setForeground(Theme.TEXT_LABEL);
        emailTitle.setFont(Theme.FONT_LABEL);
        emailTitle.setForeground(Theme.TEXT_LABEL);
        genderTitle.setFont(Theme.FONT_LABEL);
        genderTitle.setForeground(Theme.TEXT_LABEL);
        usernameTitle.setFont(Theme.FONT_LABEL);
        usernameTitle.setForeground(Theme.TEXT_LABEL);
        passwordTitle.setFont(Theme.FONT_LABEL);
        passwordTitle.setForeground(Theme.TEXT_LABEL);

        // Style input fields
        Dimension inputSize = new Dimension(280, 42);

        nameField.setPreferredSize(inputSize);
        nameField.setBackground(Theme.BG_INPUT);
        nameField.setForeground(Theme.TEXT_PRIMARY);
        nameField.setCaretColor(Theme.ACCENT_PINK);
        nameField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        nameField.setFont(Theme.FONT_BODY);

        surnameField.setPreferredSize(inputSize);
        surnameField.setBackground(Theme.BG_INPUT);
        surnameField.setForeground(Theme.TEXT_PRIMARY);
        surnameField.setCaretColor(Theme.ACCENT_PINK);
        surnameField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        surnameField.setFont(Theme.FONT_BODY);

        emailField.setPreferredSize(inputSize);
        emailField.setBackground(Theme.BG_INPUT);
        emailField.setForeground(Theme.TEXT_PRIMARY);
        emailField.setCaretColor(Theme.ACCENT_PINK);
        emailField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        emailField.setFont(Theme.FONT_BODY);

        genderDropdown.setPreferredSize(inputSize);
        genderDropdown.setBackground(Theme.BG_INPUT);
        genderDropdown.setForeground(Theme.TEXT_PRIMARY);
        genderDropdown.setFont(Theme.FONT_BODY);

        usernameField.setPreferredSize(inputSize);
        usernameField.setBackground(Theme.BG_INPUT);
        usernameField.setForeground(Theme.TEXT_PRIMARY);
        usernameField.setCaretColor(Theme.ACCENT_PINK);
        usernameField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        usernameField.setFont(Theme.FONT_BODY);

        passwordField.setPreferredSize(inputSize);
        passwordField.setBackground(Theme.BG_INPUT);
        passwordField.setForeground(Theme.TEXT_PRIMARY);
        passwordField.setCaretColor(Theme.ACCENT_PINK);
        passwordField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        passwordField.setFont(Theme.FONT_BODY);

        // Style button
        Dimension btnSize = new Dimension(280, 46);
        registerButton.setPreferredSize(btnSize);
        registerButton.setBackground(Theme.ACCENT_PINK);
        registerButton.setForeground(Theme.BG_DEEP);
        registerButton.setFont(Theme.FONT_BUTTON);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setOpaque(true);

        // Style error label
        errorLabel.setFont(Theme.FONT_ERROR);
        errorLabel.setForeground(Theme.ERROR);
        errorLabel.setText("");

        // Style login link
        loginLink.setFont(Theme.FONT_BODY);
        loginLink.setForeground(Theme.TEXT_SUBTITLE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add formCard to mainPanel
        mainPanel.removeAll();
        mainPanel.add(formCard);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void initController() {
        this.registerController = new RegisterController();

        // Wire the register button
        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();
            String gender = (String) genderDropdown.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty()
                    || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            registerButton.setEnabled(false);
            errorLabel.setText("Registering...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return registerController.register(name, username, surname, email, gender, password);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            navigateTo(MainWindow.LOGIN_PANEL);
                        } else {
                            errorLabel.setText("Failed to register. Username and password may exist already");
                            registerButton.setEnabled(true);
                        }
                    } catch (ExecutionException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }.execute();
        });

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateTo(MainWindow.LOGIN_PANEL);
            }
        });
    }
}

