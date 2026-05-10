package com.thenullcircus.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.tools.javac.Main;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.LoggerUtil;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFeedPanel extends BasePanel {

    private JPanel feedContainer;
    private JLabel bannerLabel;
    private static final Logger logger = LoggerUtil.getLogger(MainFeedPanel.class);

    public MainFeedPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        buildUI();
        loadPosts();
        loadJokeOfDay();
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
                    logger.log(Level.SEVERE, "Failed to load joke of the day", e);
                }
            }
        }.execute();
    }

    //feed

    private JScrollPane buildFeed() {
        feedContainer = new GradientPanel(Theme.GRADIENT_PURPLE_START, Theme.GRADIENT_PINK_END);
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBackground(Theme.BG_DEEP);
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
        loadPosts();
        loadJokeOfDay();
    }

    private void loadPosts() {
        JsonArray posts = new JsonArray();

        String[][] jokes = {
                {"Why don't scientists trust atoms?", "Because they make up everything.", "42", "3"},
                {"I told my wife she was drawing her eyebrows too high.", "She looked surprised.", "38", "7"},
                {"What do you call a fake noodle?", "An impasta.", "27", "12"},
                {"I used to hate facial hair...", "But then it grew on me.", "19", "5"},
                {"Why do cows wear bells?", "Because their horns don't work.", "31", "2"},
                {"I'm reading a book about anti-gravity.", "It's impossible to put down.", "55", "4"},
                {"Did you hear about the mathematician who's afraid of negative numbers?", "He'll stop at nothing to avoid them.", "44", "6"},
                {"Why did the scarecrow win an award?", "Because he was outstanding in his field.", "61", "8"},
                {"I used to play piano by ear.", "Now I use my hands.", "33", "14"},
                {"What do you call cheese that isn't yours?", "Nacho cheese.", "72", "9"},
                {"Why can't you give Elsa a balloon?", "Because she'll let it go.", "88", "11"},
                {"I only know 25 letters of the alphabet.", "I don't know y.", "95", "7"},
                {"What do you call a sleeping dinosaur?", "A dino-snore.", "41", "3"},
                {"Why did the bicycle fall over?", "Because it was two-tired.", "29", "5"},
                {"What do lawyers wear to court?", "Lawsuits.", "37", "2"},
                {"I told my doctor I broke my arm in two places.", "He told me to stop going to those places.", "66", "10"},
                {"Why don't eggs tell jokes?", "They'd crack each other up.", "53", "6"},
                {"What do you call a fish without eyes?", "A fsh.", "48", "13"},
                {"Why did the math book look so sad?", "Because it had too many problems.", "34", "4"},
                {"What do you call a bear with no teeth?", "A gummy bear.", "77", "8"}
        };

        for (String[] joke : jokes) {
            JsonObject post = new JsonObject();
            post.addProperty("postId",    java.util.UUID.randomUUID().toString());
            post.addProperty("username", "clown_" + (int)(Math.random() * 100));
            post.addProperty("body",      joke[0] + " " + joke[1]);
            post.addProperty("upvotes",   joke[2]);
            post.addProperty("downvotes", joke[3]);
            posts.add(post);
        }

        renderPosts(posts);
    }

    private void renderPosts(JsonArray posts) {
        System.out.println("renderPosts() called with " + posts.size() + " posts");
        System.out.println("feedContainer is null: " + (feedContainer == null));

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


    //post card

    private JPanel buildPostCard(JsonObject post) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM,
                        Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM)
        ));

        //header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Theme.BG_CARD);

        JLabel usernameLabel = new JLabel("@" + post.get("username").getAsString());
        usernameLabel.setFont(Theme.FONT_LABEL);
        usernameLabel.setForeground(Theme.ACCENT_PINK);
        headerPanel.add(usernameLabel);

        card.add(headerPanel, BorderLayout.NORTH);

        // Body
        JLabel bodyLabel = new JLabel(
                "<html>" + post.get("body").getAsString() + "</html>"
        );
        bodyLabel.setFont(Theme.FONT_BODY);
        bodyLabel.setPreferredSize(new Dimension(card.getWidth(), 50));
        bodyLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(bodyLabel, BorderLayout.CENTER);

        // Bottom section — divider + vote buttons
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
        upvote.addActionListener(e -> handleVote(postId, "UPVOTE"));
        downvote.addActionListener(e -> handleVote(postId, "DOWNVOTE"));

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
        // Wire up when voting route is ready on the server
    }
}