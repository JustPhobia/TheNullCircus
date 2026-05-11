package com.thenullcircus.view;

import com.thenullcircus.controller.client.RegisterController;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class RegisterForm extends BasePanel {

    // UI Components
    private final GradientPanel mainPanel;
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
        mainPanel = new GradientPanel(Theme.GRADIENT_PURPLE_START, Theme.GRADIENT_PINK_END, new GridBagLayout());
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
        registerButton = new GradientButton( "Register",
                Theme.GRADIENT_RED_START,
                Theme.GRADIENT_YELLOW_END);
        errorLabel = new JLabel();
        loginLink = new JLabel("<html>Already have an account? Login here</html>");

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        styleComponents();
        initController();
    }

    private void styleComponents() {

        formCard.setBackground(Theme.BG_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(6, 8, 6, 8);

        formCard.removeAll();

        // ── Title row — spans both columns ───────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formCard.add(titleText, gbc);

        gbc.gridy = 1;
        formCard.add(subtitleText, gbc);

        // Reset to single column
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // ── Row 1 labels — First Name | Surname ──────────────────────────────
        gbc.gridy = 2; gbc.gridx = 0; formCard.add(nameTitle, gbc);
        gbc.gridy = 2; gbc.gridx = 1; formCard.add(surnameTitle, gbc);

        // ── Row 1 fields ─────────────────────────────────────────────────────
        gbc.gridy = 3; gbc.gridx = 0; formCard.add(nameField, gbc);
        gbc.gridy = 3; gbc.gridx = 1; formCard.add(surnameField, gbc);

        // ── Row 2 labels — Email | Gender ────────────────────────────────────
        gbc.gridy = 4; gbc.gridx = 0; formCard.add(emailTitle, gbc);
        gbc.gridy = 4; gbc.gridx = 1; formCard.add(genderTitle, gbc);

        // ── Row 2 fields ─────────────────────────────────────────────────────
        gbc.gridy = 5; gbc.gridx = 0; formCard.add(emailField, gbc);
        gbc.gridy = 5; gbc.gridx = 1; formCard.add(genderDropdown, gbc);

        // ── Row 3 labels — Username | Password ───────────────────────────────
        gbc.gridy = 6; gbc.gridx = 0; formCard.add(usernameTitle, gbc);
        gbc.gridy = 6; gbc.gridx = 1; formCard.add(passwordTitle, gbc);

        // ── Row 3 fields ─────────────────────────────────────────────────────
        gbc.gridy = 7; gbc.gridx = 0; formCard.add(usernameField, gbc);
        gbc.gridy = 7; gbc.gridx = 1; formCard.add(passwordField, gbc);

        // ── Register button — spans both columns ─────────────────────────────
        gbc.gridy = 8; gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formCard.add(registerButton, gbc);

        // ── Error label — spans both columns ─────────────────────────────────
        gbc.gridy = 9;
        formCard.add(errorLabel, gbc);

        // ── Login link — spans both columns ──────────────────────────────────
        gbc.gridy = 10;
        formCard.add(loginLink, gbc);

        // ── Styling ───────────────────────────────────────────────────────────

        titleText.setFont(Theme.FONT_TITLE);
        titleText.setForeground(Theme.ACCENT_YELLOW);
        subtitleText.setFont(Theme.FONT_SUBTITLE);
        subtitleText.setForeground(Theme.TEXT_SUBTITLE);

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

        Dimension inputSize = new Dimension(280, 42);

        nameField.setPreferredSize(inputSize);
        nameField.setBackground(Theme.BG_INPUT);
        nameField.setForeground(Theme.TEXT_PRIMARY);
        nameField.setCaretColor(Theme.ACCENT_PINK);
        nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT),
                BorderFactory.createEmptyBorder(0, 6,0, 0)));
        nameField.setFont(Theme.FONT_BODY);

        surnameField.setPreferredSize(inputSize);
        surnameField.setBackground(Theme.BG_INPUT);
        surnameField.setForeground(Theme.TEXT_PRIMARY);
        surnameField.setCaretColor(Theme.ACCENT_PINK);
        surnameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT),
                BorderFactory.createEmptyBorder(0, 6,0, 0)));
        surnameField.setFont(Theme.FONT_BODY);

        emailField.setPreferredSize(inputSize);
        emailField.setBackground(Theme.BG_INPUT);
        emailField.setForeground(Theme.TEXT_PRIMARY);
        emailField.setCaretColor(Theme.ACCENT_PINK);
        emailField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT),
                BorderFactory.createEmptyBorder(0, 6,0, 0)));
        emailField.setFont(Theme.FONT_BODY);

        genderDropdown.setPreferredSize(inputSize);
        genderDropdown.setBackground(Theme.BG_INPUT);
        genderDropdown.setForeground(Theme.TEXT_PRIMARY);
        genderDropdown.setFont(Theme.FONT_BODY);

// ── Force Swing's own renderer so OS styling doesn't interfere ────────
        genderDropdown.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▾");
                arrow.setBackground(Theme.BG_INPUT);
                arrow.setForeground(Theme.ACCENT_PINK);
                arrow.setFont(Theme.FONT_BODY);
                arrow.setBorderPainted(false);
                arrow.setFocusPainted(false);
                arrow.setContentAreaFilled(false);
                arrow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return arrow;
            }

            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                comboBox.setBackground(Theme.BG_INPUT);
                comboBox.setForeground(Theme.TEXT_PRIMARY);
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Fill the background manually before painting the text
                g.setColor(Theme.BG_INPUT);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                super.paintCurrentValue(g, bounds, hasFocus);
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Fill background here too — this is what shows when not focused
                g.setColor(Theme.BG_INPUT);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        genderDropdown.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));

// ── Style the dropdown list ───────────────────────────────────────────
        genderDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? Theme.ACCENT_PINK : Theme.BG_INPUT);
                label.setForeground(isSelected ? Theme.BG_DEEP : Theme.TEXT_PRIMARY);
                label.setFont(Theme.FONT_BODY);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return label;
            }
        });

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

        //button

        Dimension btnSize = new Dimension(280, 42);

        registerButton.setPreferredSize(btnSize);
        registerButton.setMinimumSize(btnSize);
        registerButton.setMaximumSize(btnSize);
        registerButton.setForeground(Theme.TEXT_PRIMARY);
        registerButton.setFont(Theme.FONT_BUTTON);

        errorLabel.setFont(Theme.FONT_ERROR);
        errorLabel.setForeground(Theme.ERROR);
        errorLabel.setText("");

        loginLink.setFont(Theme.FONT_BODY);
        loginLink.setForeground(Theme.TEXT_SUBTITLE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add formCard to mainPanel
        mainPanel.removeAll();
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx   = 0;
        cardGbc.gridy   = 0;
        cardGbc.fill    = GridBagConstraints.NONE;
        cardGbc.weightx = 1.0;
        cardGbc.insets  = new Insets(6, 8, 0, 8);
        mainPanel.add(formCard, cardGbc);
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
                    return registerController.register(name, surname, email, gender, username, password);
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

