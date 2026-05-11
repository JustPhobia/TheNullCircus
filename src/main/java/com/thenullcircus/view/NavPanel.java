package com.thenullcircus.view;

import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;

public class NavPanel extends JPanel {

    private final MainWindow mainWindow;

    private JButton settingsButton;
    private JButton feedButton;
    private JButton dashboardButton;
    private JButton newPostButton;      // Clowns only
    private JButton logoutButton;
    private JButton refreshButton;

    public NavPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_CARD);
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, Theme.ACCENT_PINK),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_MEDIUM,
                        Theme.PADDING_LARGE, Theme.PADDING_MEDIUM)
        ));
        buildNav();
    }

    private void buildNav() {
        // Initialise components
        JLabel appTitle = new JLabel("NullCircus");
        feedButton       = createNavButton("Feed");
        dashboardButton  = createNavButton("Dashboard");
        newPostButton    = createNavButton("New Post");
        logoutButton     = createNavButton("Logout");
        settingsButton = createNavButton("Settings ⚙");
        refreshButton = createNavButton("⟳ Refresh");

        // Style title separately
        appTitle.setFont(Theme.FONT_TITLE);
        appTitle.setForeground(Theme.ACCENT_YELLOW);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

//        // Style logout differently — muted to distinguish it
//        logoutButton.setBackground(Theme.BG_DEEP);
//        logoutButton.setForeground(Theme.TEXT_MUTED);

        // layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;

        int row = 0;

        // App title at top
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, Theme.PADDING_LARGE, 0);
        add(appTitle, gbc);

        // Reset insets for buttons
        gbc.insets = new Insets(4, 0, 4, 0);

        // Everyone sees Feed and Dashboard
        gbc.gridy = row++; add(feedButton, gbc);
        gbc.gridy = row++; add(dashboardButton, gbc);
        gbc.gridy = row++; add(refreshButton, gbc);

        // Clowns only
        if (Session.isClown()) {
            gbc.gridy = row++; add(newPostButton, gbc);
        }

        // Spacer
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createVerticalGlue(), gbc);

        // Push settings and logout to the bottom
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createVerticalGlue(), gbc);

        // Settings — everyone sees this
        gbc.gridy = row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(Theme.PADDING_MEDIUM, 0, 0, 0);
        add(settingsButton, gbc);

        // Logout at the bottom
        gbc.gridy = row;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(Theme.PADDING_MEDIUM, 0, 0, 0);
        add(logoutButton, gbc);

        wireListeners();
    }

    // button creation

    private JButton createNavButton(String label) {
        JButton button = new JButton(label);
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BG_CARD);
        button.setForeground(Theme.TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 46));
        button.setMaximumSize(new Dimension(160, 46));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.BG_HOVER);
                button.setForeground(Theme.BG_DEEP);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.BG_CARD);
                button.setForeground(Theme.TEXT_PRIMARY);
            }
        });

        return button;
    }

    // listeners for buttons

    private void wireListeners() {
        feedButton.addActionListener(e ->
                mainWindow.navigateTo(MainWindow.MAIN_FEED_PANEL));

        dashboardButton.addActionListener(e ->
                mainWindow.navigateTo(MainWindow.DASHBOARD_PANEL));

        if (Session.isClown()) {
            newPostButton.addActionListener(e ->
                    mainWindow.navigateTo(MainWindow.POST_CREATION_PANEL));
        }


        settingsButton.addActionListener(e -> {
            mainWindow.navigateTo(MainWindow.SETTINGS_PANEL);
        });

        logoutButton.addActionListener(e -> {
            Session.logout();
            mainWindow.showNav(false);
            mainWindow.navigateTo(MainWindow.LOGIN_PANEL);
        });

        refreshButton.addActionListener(e ->
                mainWindow.refreshCurrentPanel());

    }
}