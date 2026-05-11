package com.thenullcircus.view;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class MainWindow extends JFrame {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private NavPanel navPanel;
    private JPanel contentWrapper;

    public static final String LOGIN_PANEL = "LOGIN";
    public static final String REGISTRATION_PANEL = "REGISTER";
    public static final String MAIN_FEED_PANEL = "MAIN_FEED";
    public static final String DASHBOARD_PANEL = "DASHBOARD";
    public static final String SETTINGS_PANEL = "SETTINGS";
    public static final String POST_CREATION_PANEL = "POST_CREATION";

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
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(new LoginForm(this), LOGIN_PANEL);
        cardPanel.add(new RegisterForm(this), REGISTRATION_PANEL);
        cardPanel.add(new DashboardPanel(this), DASHBOARD_PANEL);
        cardPanel.add(new MainFeedPanel(this), MAIN_FEED_PANEL);
        cardPanel.add(new PostCreationPanel(this), POST_CREATION_PANEL);
        cardPanel.add(new SettingsPanel(this), SETTINGS_PANEL);

        navPanel = new NavPanel(this);
        navPanel.setVisible(false);

        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.add(navPanel, BorderLayout.WEST);
        contentWrapper.add(cardPanel, BorderLayout.CENTER);
        setContentPane(contentWrapper);
        cardLayout.show(cardPanel, LOGIN_PANEL);
    }

    public void navigateTo(String panelName) {
        logger.info("[NAV_ROUTER] Switching active view to: " + panelName);
        cardLayout.show(cardPanel, panelName);
        SwingUtilities.invokeLater(() -> {
            for (Component c : cardPanel.getComponents()) {
                if (c.isVisible() && c instanceof BasePanel panel) {
                    logger.fine("[NAV_LIFECYCLE] Triggering onVisible() for panel: " + panelName);
                    panel.onVisible();
                }
            }
        });
    }

    public void showNav(boolean visible) {
        logger.info("[UI_STATE] Side navigation visibility toggled to: " + visible);
        navPanel.setVisible(visible);
        contentWrapper.revalidate();
        contentWrapper.repaint();
    }

    public void refreshCurrentPanel() {
        logger.info("[LIFECYCLE] Manual refresh requested for the current visible panel.");
        SwingUtilities.invokeLater(() -> {
            boolean found = false;
            for (Component c : cardPanel.getComponents()) {
                if (c.isVisible() && c instanceof BasePanel panel) {
                    logger.fine("[LIFECYCLE] Refreshing content for: " + panel.getClass().getSimpleName());
                    panel.onVisible();
                    found = true;
                }
            }
            if (!found) {
                logger.warning("[LIFECYCLE] Refresh requested but no active BasePanel was identified.");
            }
        });
    }
}