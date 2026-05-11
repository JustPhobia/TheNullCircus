package com.thenullcircus.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thenullcircus.model.User;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class ModerationPanel extends BasePanel {

    private final User user;
    private JLabel bannerLabel;
    private JLabel feedContainer;

    public ModerationPanel(MainWindow mainWindow) {
        super(mainWindow);
        this.user = Session.getCurrentUser();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        buildUI();
    }

    //building ui

    private void buildUI() {
        add(buildBanner(), BorderLayout.NORTH);
        add(buildFeed(),   BorderLayout.CENTER);
    }

    //banner

    private JPanel buildBanner(){
        JPanel banner = new JPanel(new BorderLayout());
        JLabel bannerLabel = new JLabel();
        banner.setBackground(Theme.BG_DEEP);
        banner.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_MEDIUM, Theme.PADDING_SMALL, Theme.PADDING_MEDIUM));
        bannerLabel.setText(this.user.getUsername());
        bannerLabel.setForeground(Theme.ACCENT_YELLOW);
        banner.add(bannerLabel);
        return banner;
    }

    //feed

    private JScrollPane buildFeed(){
        JPanel feedContainer = new GradientPanel(Theme.GRADIENT_PURPLE_START, Theme.GRADIENT_PINK_END);
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));

        JScrollPane scrollPane = new JScrollPane(feedContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    //for loading data
    @Override
    public void onVisible(){
        loadModerationPosts();
    }

    private void loadModerationPosts(){
        new SwingWorker<JsonArray, Void>(){
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_PENDING_POSTS");

                client.sendRequest(request);
                JsonObject response = new JsonObject();
                client.disconnect();

                return response.get("posts").getAsJsonArray();
            }

            @Override
            protected void done() {
                try{
                    JsonArray posts = get();
                    renderPosts(posts);
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Failed to load posts: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void renderPosts(JsonArray posts){
        System.out.println("Render posts called with " + posts.size() + " posts.");
        System.out.println("feedContainer is null " + (feedContainer == null));

        assert  feedContainer != null;
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
        feedContainer.revalidate();
        feedContainer.repaint();
    }

    private JPanel buildPostCard(JsonObject post){
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_DEEP);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.ACCENT_PURPLE, 1),
                BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM))
        );

        //card header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Theme.BG_DEEP);

        JLabel userNameLabel = new JLabel("Username: " + post.get("username").getAsString());
        userNameLabel.setFont(Theme.FONT_LABEL);
        userNameLabel.setForeground(Theme.ACCENT_PINK);
        headerPanel.add(userNameLabel);

        card.add(headerPanel, BorderLayout.NORTH);

        //body
        JLabel bodyLabel = new JLabel("<html>" + post.get("body").getAsString() + "</html>");
        bodyLabel.setFont(Theme.FONT_BODY);
        bodyLabel.setPreferredSize(new Dimension(card.getWidth(), 50));
        bodyLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(bodyLabel, BorderLayout.CENTER);

        //bottom section
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Theme.BG_CARD);

        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.BORDER_DEFAULT);
        separator.setBackground(Theme.BORDER_DEFAULT);
        bottomPanel.add(separator, BorderLayout.NORTH);

        JPanel moderationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        moderationPanel.setBackground(Theme.BG_CARD);
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        String postId = post.get("id").getAsString();
        approveButton.addActionListener(e -> handleApprove(postId));
        rejectButton.addActionListener(e -> handleReject(postId));

        moderationPanel.add(approveButton);
        moderationPanel.add(rejectButton);
        bottomPanel.add(moderationPanel, BorderLayout.SOUTH);

        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;

    }

    private void handleApprove(String postId) {
        new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "APPROVE_POST");
                request.addProperty("postId", postId);
                request.addProperty("moderatorId", Session.getCurrentUser().getUserId().toString());

                client.sendRequest(request);
                client.readResponse();
                client.disconnect();
                return null;
            }
            @Override
            protected void done() {
                loadModerationPosts();
            }
        }.execute();
    }

    private void handleReject(String postId) {
        new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "REJECT_POST");
                request.addProperty("postId", postId);
                request.addProperty("moderatorId", Session.getCurrentUser().getUserId().toString());

                client.sendRequest(request);
                client.readResponse();
                client.disconnect();
                return null;
            }
            @Override
            protected void done() {
                loadModerationPosts();
            }
        }.execute();
    }



}
