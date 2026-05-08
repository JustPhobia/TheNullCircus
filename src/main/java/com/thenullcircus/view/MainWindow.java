package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private NavPanel navPanel;
    private JPanel contentWrapper;

    public static final String LOGIN_PANEL         = "LOGIN";
    public static final String REGISTRATION_PANEL  = "REGISTER";
    public static final String MAIN_FEED_PANEL     = "MAIN_FEED";
    public static final String POST_CREATION_PANEL = "POST_CREATION";
    public static final String DASHBOARD_PANEL     = "DASHBOARD";
    public static final String SETTINGS_PANEL      = "SETTINGS";
    public static final String MODERATION_PANEL    = "MODERATION";

    public MainWindow() {
        initFrame();
        initCards();
    }

    public void initFrame() {
        setTitle("NullCircus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void initCards() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);

        cardPanel.add(new LoginForm(this),      LOGIN_PANEL);
        cardPanel.add(new RegisterForm(this),   REGISTRATION_PANEL);
        cardPanel.add(new DashboardPanel(this), DASHBOARD_PANEL);
        cardPanel.add(new MainFeedPanel(this), MAIN_FEED_PANEL);
        cardPanel.add(new PostCreationPanel(this), POST_CREATION_PANEL);


        navPanel = new NavPanel(this);  // ← initialise BEFORE adding to contentWrapper
        navPanel.setVisible(false);     // ← hidden until login

        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.add(navPanel,  BorderLayout.WEST);
        contentWrapper.add(cardPanel, BorderLayout.CENTER);

        setContentPane(contentWrapper);

        cardLayout.show(cardPanel, LOGIN_PANEL);
    }

    public void showNav(boolean visible) {
        if (visible) {
            contentWrapper.remove(navPanel);
            navPanel = new NavPanel(this);
            contentWrapper.add(navPanel, BorderLayout.WEST);
        }
        navPanel.setVisible(visible);
        contentWrapper.revalidate();
        contentWrapper.repaint();
    }

    public void navigateTo(String panelName) {
        cardLayout.show(cardPanel, panelName);

        SwingUtilities.invokeLater(() -> {
            for (Component c : cardPanel.getComponents()) {
                if (c.isVisible() && c instanceof BasePanel panel) {
                    panel.onVisible();
                }
            }
        });
    }
}