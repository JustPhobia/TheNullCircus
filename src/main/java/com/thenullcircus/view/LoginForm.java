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

    public LoginForm(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);


    }

    public void styleComponents(){
        mainPanel.setBackground(Theme.BG_DEEP);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        //this is for the title
        welcomeText.setFont(Theme.FONT_TITLE);
    }
}
