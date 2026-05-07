package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public static final String LOGIN_PANEL = "LOGIN";
    public static final String REGISTRATION_PANEL = "REGISTER";
    public static final String MAIN_FEED_PANEL = "MAIN_FEED";
    public static final String POST_CREATION_PANEL = "POST_CREATION";
    public static final String DASHBOARD_PANEL = "DASHBOARD";
    public static final String SETTINGS_PANEL = "SETTINGS";
    public static final String MODERATION_PANEL = "MODERATION";

    public MainWindow() {
        initFrame();
        initCards();
    }

    public void initFrame() {
        setTitle("My App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setLocationRelativeTo(null); //this will center our frame
    }

    public void initCards(){
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        //we will add our pages here
        cardPanel.add(new LoginForm(this), LOGIN_PANEL);
        cardPanel.add(new MainFeedPanel(this), MAIN_FEED_PANEL);

        setContentPane(cardPanel);
        //pack(); supposedly makes window shrink to fit its contents

        cardLayout.show(cardPanel, MAIN_FEED_PANEL);
    }

    public void navigateTo(String panelName){
        cardLayout.show(cardPanel, panelName);
    }


}
