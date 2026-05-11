package com.thenullcircus.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.LoggerUtil;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFeedPanel extends BasePanel {

    private JPanel feedContainer;
    private JLabel bannerLabel;
    private static final Logger logger = LoggerUtil.getLogger(MainFeedPanel.class);
    private JPanel loadingPanel;
    private javax.swing.Timer spinnerTimer;

    public MainFeedPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);

    }

    //ui

    private void buildUI() {
        add(buildBanner(), BorderLayout.NORTH);
        add(buildFeed(),   BorderLayout.CENTER);
    }

    //banner

    private JPanel buildBanner() {
        JPanel banner = new JPanel();
        banner.setBackground(Theme.ACCENT_YELLOW);
        banner.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_SMALL, Theme.PADDING_MEDIUM,
                Theme.PADDING_SMALL, Theme.PADDING_MEDIUM
        ));
        bannerLabel = new JLabel("⭐ Joke of the Day — Coming Soon");
        bannerLabel.setFont(Theme.FONT_SUBTITLE);
        bannerLabel.setForeground(Theme.BG_DEEP);
        banner.add(bannerLabel);
        return banner;
    }

    //joke of the day
    private void loadJokeOfDay(){
        new SwingWorker<JsonObject, Void>(){
            @Override
            protected JsonObject doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_JOKE_OF_DAY");
                client.sendRequest(request);

                JsonObject response = client.readResponse();
                client.disconnect();
                return response;
            }

            @Override
            protected void done(){
                try{
                    JsonObject response = get();
                    String status = response.get("status").getAsString();

                    if (status.equals("SUCCESS")){
                        JsonObject post = response.getAsJsonObject("post");
                        String jokeBody = post.get("body").getAsString();
                        bannerLabel.setText("⭐ Joke of the Day: " + jokeBody);
                    }else {
                        bannerLabel.setText("⭐ Joke of the Day — None yet today!");
                    }
                } catch (Exception e) {
                    bannerLabel.setText("⭐ Joke of the Day — Could not load.");
                    logger.log(Level.SEVERE, "Failed to load joke of the day.", e);
                }
            }
        }.execute();
    }

    //feed

    private JScrollPane buildFeed() {
        feedContainer = new GradientPanel(Theme.GRADIENT_PURPLE_START, Theme.GRADIENT_PINK_END);
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM,
                Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM
        ));

        JScrollPane scrollPane = new JScrollPane(feedContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BG_DEEP);
        return scrollPane;
    }

    //data loading

    @Override
    public void onVisible() {
        removeAll();
        buildUI();

        loadingPanel = buildLoadingPanel();
        add(loadingPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        loadJokeOfDay();
        loadPosts();
    }

    private void loadPosts() {
        new SwingWorker<JsonArray, Void>() {
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_POSTS");

                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();

                return response.get("posts").getAsJsonArray();
            }

            @Override
            protected void done() {
                try {
                    if (spinnerTimer != null) spinnerTimer.stop();
                    if (loadingPanel != null) remove(loadingPanel);

                    JsonArray posts = get();

                    add(buildFeed(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    renderPosts(posts);
                } catch (Exception e) {
                    System.err.println("Failed to load posts: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void renderPosts(JsonArray posts) {
        System.out.println("renderPosts() called with " + posts.size() + " posts");
        System.out.println("feedContainer is null: " + (feedContainer == null));

        assert feedContainer != null;
        feedContainer.removeAll();

        for (int i = 0; i < posts.size(); i++) {
            JsonObject post = posts.get(i).getAsJsonObject();
            System.out.println("Adding card for: " + post.get("body").getAsString());
            JPanel card = buildPostCard(post);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            feedContainer.add(card);
            feedContainer.add(Box.createVerticalStrut(Theme.PADDING_MEDIUM));
        }

        System.out.println("Done adding cards, revalidating...");
        feedContainer.add(Box.createVerticalGlue());

        feedContainer.revalidate();
        feedContainer.repaint();
    }


    //post card

    private JPanel buildPostCard(JsonObject post) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PURPLE, 1),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM,
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM)
        ));

        //header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Theme.BG_CARD);

        JLabel usernameLabel = new JLabel("Username: " + post.get("username").getAsString());
        usernameLabel.setFont(Theme.FONT_LABEL);
        usernameLabel.setForeground(Theme.ACCENT_PINK);
        headerPanel.add(usernameLabel);

        card.add(headerPanel, BorderLayout.NORTH);

        // Body
        JLabel bodyLabel = new JLabel(
                "<html>" + post.get("body").getAsString() + "</html>"
        );
        bodyLabel.setFont(Theme.FONT_BODY);
        bodyLabel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        bodyLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(bodyLabel, BorderLayout.CENTER);

        // Bottom section
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Theme.BG_CARD);

        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.BORDER_DEFAULT);
        separator.setBackground(Theme.BORDER_DEFAULT);
        bottomPanel.add(separator, BorderLayout.NORTH);

        JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        votePanel.setBackground(Theme.BG_CARD);

        JButton upvote   = createVoteButton("▲  " + post.get("upvotes").getAsString());
        JButton downvote = createVoteButton("▽  " + post.get("downvotes").getAsString());

        String postId = post.get("postId").getAsString();
        upvote.addActionListener(e -> {
            upvote.setEnabled(false);   // ← disable immediately
            downvote.setEnabled(false); // ← disable both to prevent double voting
            handleVote(postId, "UPVOTE");
        });

        downvote.addActionListener(e -> {
            upvote.setEnabled(false);   // ← disable immediately
            downvote.setEnabled(false); // ← disable both
            handleVote(postId, "DOWNVOTE");
        });

        votePanel.add(upvote);
        votePanel.add(downvote);
        bottomPanel.add(votePanel, BorderLayout.SOUTH);

        card.add(bottomPanel, BorderLayout.SOUTH);

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

    //actions

    private void handleVote(String postId, String voteType) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", voteType);
                request.addProperty("postId", postId);
                request.addProperty("userId", Session.getCurrentUser().getUserId().toString());

                client.sendRequest(request);
                client.readResponse();
                client.disconnect();
                return null;
            }

            @Override
            protected void done() {
                refreshPosts();
            }
        }.execute();
    }

    private JPanel buildLoadingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel spinnerBox = new JPanel();
        spinnerBox.setLayout(new BoxLayout(spinnerBox, BoxLayout.Y_AXIS));
        spinnerBox.setOpaque(false);

        // The spinner is just a JLabel that cycles through unicode arc characters
        // javax.swing.Timer updates it on the EDT every 100ms — no gif needed
        String[] frames = { "◜", "◝", "◞", "◟" };
        JLabel spinner = new JLabel(frames[0]);
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 48));
        spinner.setForeground(Theme.ACCENT_PINK);
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("Loading posts...");
        loadingLabel.setFont(Theme.FONT_SUBTITLE);
        loadingLabel.setForeground(Theme.TEXT_MUTED);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        spinnerBox.add(spinner);
        spinnerBox.add(Box.createVerticalStrut(12));
        spinnerBox.add(loadingLabel);

        panel.add(spinnerBox);

        // Timer cycles through frames every 100ms giving the illusion of rotation
        int[] frameIndex = {0};
        spinnerTimer = new javax.swing.Timer(100, e -> {
            frameIndex[0] = (frameIndex[0] + 1) % frames.length;
            spinner.setText(frames[frameIndex[0]]);
        });
        spinnerTimer.start();

        return panel;
    }

    private void refreshPosts() {
        new SwingWorker<JsonArray, Void>() {
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_POSTS");

                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();

                return response.get("posts").getAsJsonArray();
            }

            @Override
            protected void done() {
                try {
                    JsonArray posts = get();
                    // Don't rebuild the feed — just re-render posts into
                    // the existing feedContainer so counts update in place
                    renderPosts(posts);
                } catch (Exception e) {
                    System.err.println("Failed to refresh posts: " + e.getMessage());
                }
            }
        }.execute();
    }
}