package com.thenullcircus.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;

public class MainFeedPanel
        extends BasePanel {

    // ── Feed ──────────────────────────────────────────────────────────────────
    private JPanel feedContainer;
    private JScrollPane scrollPane;

    // ── Bottom nav ────────────────────────────────────────────────────────────
    private JButton feedNavButton;
    private JButton dashboardNavButton;
    private JButton newPostNavButton;       // Clowns only
    private JButton moderationNavButton;    // Ringleaders only

    public MainFeedPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        buildUI();
        loadMockPosts();

    }

    // ── Build UI ──────────────────────────────────────────────────────────────

    private void buildUI() {
        add(buildBanner(),  BorderLayout.NORTH);
        add(buildFeed(),    BorderLayout.CENTER);
        add(buildBottomNav(), BorderLayout.SOUTH);
    }

    // ── Banner ────────────────────────────────────────────────────────────────

    private JPanel buildBanner() {
        JPanel banner = new JPanel();
        banner.setBackground(Theme.ACCENT_YELLOW);
        banner.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_SMALL, Theme.PADDING_MEDIUM,
                Theme.PADDING_SMALL, Theme.PADDING_MEDIUM
        ));
        JLabel bannerLabel = new JLabel("⭐ Joke of the Day — Coming Soon");
        bannerLabel.setFont(Theme.FONT_SUBTITLE);
        bannerLabel.setForeground(Theme.BG_DEEP);
        banner.add(bannerLabel);
        return banner;
    }

    // ── Feed ──────────────────────────────────────────────────────────────────

    private JScrollPane buildFeed() {
        feedContainer = new JPanel();
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBackground(Theme.BG_DEEP);
        feedContainer.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM,
                Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM
        ));

        // Clown-only New Joke button at top of feed
        if (Session.isClown()) {
            JButton newJokeButton = new JButton("New Joke");
            newJokeButton.setFont(Theme.FONT_BUTTON);
            newJokeButton.setBackground(Theme.ACCENT_PINK);
            newJokeButton.setForeground(Theme.BG_DEEP);
            newJokeButton.setBorderPainted(false);
            newJokeButton.setFocusPainted(false);
            newJokeButton.setOpaque(true);
            newJokeButton.setMaximumSize(new Dimension(160, 42));
            newJokeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            newJokeButton.addActionListener(e ->
                    navigateTo(MainWindow.POST_CREATION_PANEL));

            feedContainer.add(newJokeButton);
            feedContainer.add(Box.createVerticalStrut(Theme.PADDING_MEDIUM));
        }

        scrollPane = new JScrollPane(feedContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BG_DEEP);
        return scrollPane;
    }

    // ── Bottom nav ────────────────────────────────────────────────────────────

    private JPanel buildBottomNav() {
        int cols = 2 + (Session.isClown() ? 1 : 0) + (Session.isRingLeader() ? 1 : 0);

        JPanel nav = new JPanel(new GridLayout(1, cols));
        nav.setBackground(Theme.BG_CARD);
        nav.setBorder(BorderFactory.createMatteBorder(
                2, 0, 0, 0, Theme.ACCENT_PINK
        ));

        feedNavButton      = createNavButton("🏠", "Feed");
        dashboardNavButton = createNavButton("📊", "Dashboard");

        nav.add(feedNavButton);
        nav.add(dashboardNavButton);

        if (Session.isClown()) {
            newPostNavButton = createNavButton("＋", "New Post");
            nav.add(newPostNavButton);
        }

        if (Session.isRingLeader()) {
            moderationNavButton = createNavButton("🛡", "Moderation");
            nav.add(moderationNavButton);
        }

        wireNavListeners();
        setActiveNav(feedNavButton);

        return nav;
    }

    private JButton createNavButton(String icon, String label) {
        JButton button = new JButton(
                "<html><center>" + icon + "<br>" + label + "</center></html>"
        );
        button.setFont(Theme.FONT_LABEL);
        button.setBackground(Theme.BG_CARD);
        button.setForeground(Theme.TEXT_MUTED);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 60));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getForeground() != Theme.ACCENT_YELLOW) {
                    button.setForeground(Theme.TEXT_PRIMARY);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.getForeground() != Theme.ACCENT_YELLOW) {
                    button.setForeground(Theme.TEXT_MUTED);
                }
            }
        });

        return button;
    }

    private void setActiveNav(JButton active) {
        JButton[] all = {feedNavButton, dashboardNavButton,
                newPostNavButton, moderationNavButton};
        for (JButton btn : all) {
            if (btn != null) btn.setForeground(Theme.TEXT_MUTED);
        }
        active.setForeground(Theme.ACCENT_YELLOW);
    }

    private void wireNavListeners() {
        feedNavButton.addActionListener(e -> {
            setActiveNav(feedNavButton);
            navigateTo(MainWindow.MAIN_FEED_PANEL);
        });

        dashboardNavButton.addActionListener(e -> {
            setActiveNav(dashboardNavButton);
            navigateTo(MainWindow.DASHBOARD_PANEL);
        });

        if (Session.isClown()) {
            newPostNavButton.addActionListener(e -> {
                setActiveNav(newPostNavButton);
                navigateTo(MainWindow.POST_CREATION_PANEL);
            });
        }

        if (Session.isRingLeader()) {
            moderationNavButton.addActionListener(e -> {
                setActiveNav(moderationNavButton);
                navigateTo(MainWindow.MODERATION_PANEL);
            });
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────



    // ── Rendering ─────────────────────────────────────────────────────────────

    private void renderPosts(JsonArray posts) {
        // Clear everything below the New Joke button if present
        feedContainer.removeAll();

        if (Session.isClown()) {
            JButton newJokeButton = new JButton("New Joke");
            newJokeButton.setFont(Theme.FONT_BUTTON);
            newJokeButton.setBackground(Theme.ACCENT_PINK);
            newJokeButton.setForeground(Theme.BG_DEEP);
            newJokeButton.setBorderPainted(false);
            newJokeButton.setFocusPainted(false);
            newJokeButton.setOpaque(true);
            newJokeButton.setMaximumSize(new Dimension(160, 42));
            newJokeButton.addActionListener(e ->
                    navigateTo(MainWindow.POST_CREATION_PANEL));
            feedContainer.add(newJokeButton);
            feedContainer.add(Box.createVerticalStrut(Theme.PADDING_MEDIUM));
        }

        for (int i = 0; i < posts.size(); i++) {
            JsonObject post = posts.get(i).getAsJsonObject();
            feedContainer.add(buildPostCard(post));
            feedContainer.add(Box.createVerticalStrut(Theme.PADDING_MEDIUM));
        }

        feedContainer.revalidate();
        feedContainer.repaint();
    }

    private JPanel buildPostCard(JsonObject post) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.BG_CARD);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM,
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 4, 0);

        // Body
        JLabel bodyLabel = new JLabel(
                "<html>" + post.get("body").getAsString() + "</html>"
        );
        bodyLabel.setFont(Theme.FONT_BODY);
        bodyLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridy  = 0;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(bodyLabel, gbc);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_DEFAULT);
        sep.setBackground(Theme.BORDER_DEFAULT);
        gbc.gridy  = 1;
        gbc.insets = new Insets(Theme.PADDING_SMALL, 0, Theme.PADDING_SMALL, 0);
        card.add(sep, gbc);

        // Vote buttons
        JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        votePanel.setBackground(Theme.BG_CARD);

        JButton upvote   = createVoteButton("▲  " + post.get("upvotes").getAsString());
        JButton downvote = createVoteButton("▽  " + post.get("downvotes").getAsString());

        String postId = post.get("postId").getAsString();
        upvote.addActionListener(e -> handleVote(postId, "UPVOTE"));
        downvote.addActionListener(e -> handleVote(postId, "DOWNVOTE"));

        votePanel.add(upvote);
        votePanel.add(downvote);

        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(votePanel, gbc);

        return card;
    }

    private JButton createVoteButton(String label) {
        JButton button = new JButton(label);
        button.setFont(Theme.FONT_BUTTON);
        button.setForeground(Theme.ACCENT_YELLOW);
        button.setBackground(Theme.BG_CARD);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.ACCENT_YELLOW);
                button.setForeground(Theme.BG_DEEP);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.BG_CARD);
                button.setForeground(Theme.ACCENT_YELLOW);
            }
        });

        return button;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void handleVote(String postId, String voteType) {
        // Wire up when voting route is ready on the server
    }

    private void loadMockPosts() {

        JsonArray posts = new JsonArray();

        JsonObject post1 = new JsonObject();
        post1.addProperty("postId", "1");
        post1.addProperty("body", "Why did the clown bring a ladder? Because the jokes were on another level 🤡");
        post1.addProperty("upvotes", 12);
        post1.addProperty("downvotes", 2);

        JsonObject post2 = new JsonObject();
        post2.addProperty("postId", "2");
        post2.addProperty("body", "I told my computer a joke... now it won't stop laughing in binary.");
        post2.addProperty("upvotes", 24);
        post2.addProperty("downvotes", 1);

        JsonObject post3 = new JsonObject();
        post3.addProperty("postId", "3");
        post3.addProperty("body", "Null Circus developers debugging at 2AM be like: 'It worked yesterday.'");
        post3.addProperty("upvotes", 42);
        post3.addProperty("downvotes", 0);

        posts.add(post1);
        posts.add(post2);
        posts.add(post3);

        renderPosts(posts);
    }
}