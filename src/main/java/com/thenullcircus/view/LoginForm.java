package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

import com.thenullcircus.controller.client.LoginController;
import com.thenullcircus.util.Theme;

public class LoginForm extends BasePanel {
    private final JPanel mainPanel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel errorLabel;
    private final JLabel welcomeText;
    private final JLabel usernameTitle;
    private final JLabel passwordTitle;
    private final JPanel formCard;
    private final JLabel loginText;
    private final JLabel registerLink;

    private LoginController loginController;

    private void initController(){
        this.loginController = new LoginController();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if(username.isEmpty() || password.isEmpty()){
                errorLabel.setText("Please fill out all fields");
                return;
            }

            loginButton.setEnabled(false);
            errorLabel.setText("Logging in...");

            new SwingWorker<Boolean, Void>(){

                @Override
                protected Boolean doInBackground() throws Exception {
                    return loginController.login(username, password);
                }

                @Override
                protected void done() {
                    try{
                        boolean success = get();

                        if(success){
                            navigateTo(MainWindow.MAIN_FEED_PANEL);
                        }else{
                            errorLabel.setText("Invalid username or password");
                            loginButton.setEnabled(true);
                        }
                    } catch (Exception ex) {
                        errorLabel.setText("Could not reach the server");
                        loginButton.setEnabled(true);
                    }
                }
            }.execute();
        });

        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                navigateTo(MainWindow.REGISTRATION_PANEL);
            }
        });
    }

    public LoginForm(MainWindow mainWindow) {
        super(mainWindow);

        // initialize all components the .form file was responsible for
        mainPanel = new JPanel(new GridBagLayout());
        formCard = new JPanel(new GridBagLayout());
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new GradientButton(
                "Login",
                Theme.GRADIENT_RED_START,
                Theme.GRADIENT_YELLOW_END
        );
        errorLabel = new JLabel();
        welcomeText = new JLabel("Welcome Back");
        loginText = new JLabel("Login Below");
        usernameTitle = new JLabel("Username");
        passwordTitle = new JLabel("Password");
        registerLink = new JLabel();

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        styleComponents();
        initController();
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
        gbc.gridy = 1; formCard.add(loginText, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2; formCard.add(usernameTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 3; formCard.add(usernameField, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 4; formCard.add(passwordTitle, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 5; formCard.add(passwordField, gbc);
        gbc.gridy = 6; formCard.add(loginButton, gbc);
        gbc.gridy = 7; formCard.add(errorLabel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 8; formCard.add(registerLink, gbc);

        // Title
        welcomeText.setFont(Theme.FONT_TITLE);
        welcomeText.setForeground(Theme.ACCENT_YELLOW);
        loginText.setFont(Theme.FONT_SUBTITLE);
        loginText.setForeground(Theme.TEXT_SUBTITLE);

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
//        loginButton.setForeground(Color.WHITE);
//        loginButton.setFont(Theme.FONT_BUTTON);
//        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        loginButton.setBorderPainted(false);
//        loginButton.setFocusPainted(false);
//        loginButton.setOpaque(true);
        loginButton.setForeground(Theme.TEXT_PRIMARY);
        loginButton.setFont(Theme.FONT_BUTTON);
        loginButton.addActionListener(e -> {
            mainWindow.showNav(true);
            navigateTo(MainWindow.MAIN_FEED_PANEL);
        });


        // Error label
        errorLabel.setFont(Theme.FONT_ERROR);
        errorLabel.setForeground(Theme.ERROR);
        errorLabel.setText("");

        //register Link
        registerLink.setText("<html>Don't have an account? Register here</html>");
        registerLink.setFont(Theme.FONT_BODY);
        registerLink.setForeground(Theme.TEXT_SUBTITLE);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add formCard to mainPanel
        mainPanel.removeAll();
        mainPanel.add(formCard);

        mainPanel.revalidate();
        mainPanel.repaint();
    }
}