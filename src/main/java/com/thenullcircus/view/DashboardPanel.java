package com.thenullcircus.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thenullcircus.model.Post;
import com.thenullcircus.model.Status;
import com.thenullcircus.model.User;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.ScreenUtil;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class DashboardPanel extends BasePanel {

    private User user;
    private JPanel statsContainer;
    private CardLayout cardLayout;

    public DashboardPanel(MainWindow mainWindow) {
        super(mainWindow);
        this.user = Session.getCurrentUser();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);

        int hPad = (int) (ScreenUtil.getScreenWidth() * 0.06);
        int vPad = (int) (ScreenUtil.getScreenHeight() * 0.05);
        setBorder(new EmptyBorder(vPad, hPad, vPad, hPad));

        if (user != null) {
            add(buildHeader(), BorderLayout.NORTH);
            add(buildBody(),   BorderLayout.CENTER);
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(0, 0, 30, 0)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel identity = new JPanel();
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        identity.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName() + " " + user.getSurname());
        nameLabel.setFont(new Font("Serif", Font.BOLD, 42));
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        RoundedLabel roleLabel = new RoundedLabel(getRole().toUpperCase(), 15);
        roleLabel.setFont(Theme.FONT_LABEL);
        roleLabel.setForeground(Theme.BG_DEEP);
        roleLabel.setBackground(getRoleBadgeColor());
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        identity.add(nameLabel);
        identity.add(Box.createVerticalStrut(8));
        identity.add(roleLabel);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        header.add(identity, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton roleBtn = createActionButton("Request Role Change", Theme.ACCENT_PINK, Theme.BG_DEEP, 220);
        roleBtn.addActionListener(e -> showRoleRequestDialog());

        actions.add(roleBtn);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        header.add(actions, gbc);

        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(30, 0, 0, 0));
        body.add(buildDynamicStatsContainer(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildDynamicStatsContainer() {
        cardLayout = new CardLayout();
        statsContainer = new JPanel(cardLayout);
        statsContainer.setOpaque(false);

        if (Boolean.TRUE.equals(user.getRingleader())) {
            statsContainer.add(buildRingleaderView(), "MAIN");
        } else if (Boolean.TRUE.equals(user.getClown())) {
            statsContainer.add(buildPostListView(), "LIST");
        } else {
            statsContainer.add(buildMemberPanel(), "MAIN");
        }

        return statsContainer;
    }

    // Ringleader

    private JPanel buildRingleaderView() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        container.add(buildPostModerationPanel());
        container.add(buildRoleRequestsPanel());
        return container;
    }

    //post moderation panel

    private JPanel buildPostModerationPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel title = new JLabel("PENDING APPROVALS");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_CYAN);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        new SwingWorker<JsonArray, Void>() {
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_PENDING_POSTS");

                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();

                return response.getAsJsonArray("posts");
            }

            @Override
            protected void done() {
                try {
                    JsonArray posts = get();

                    if (posts.isEmpty()) {
                        JLabel empty = new JLabel("No pending posts — all clear!");
                        empty.setFont(Theme.FONT_BODY);
                        empty.setForeground(Theme.TEXT_MUTED);
                        empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                        list.add(empty);
                    } else {
                        for (int i = 0; i < posts.size(); i++) {
                            list.add(createApprovalRow(posts.get(i).getAsJsonObject()));
                            list.add(Box.createVerticalStrut(4));
                        }
                    }

                    list.revalidate();
                    list.repaint();
                } catch (Exception e) {
                    System.err.println("Failed to load pending posts: " + e.getMessage());
                }
            }
        }.execute();

        return card;
    }

    //Role Request Panel

    private JPanel buildRoleRequestsPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel title = new JLabel("ROLE CHANGE REQUESTS");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_YELLOW);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        new SwingWorker<JsonArray, Void>() {
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();

                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_PENDING_ROLE_REQUESTS");

                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();

                return response.getAsJsonArray("requests");
            }

            @Override
            protected void done() {
                try {
                    JsonArray requests = get();

                    if (requests == null || requests.isEmpty()) {
                        JLabel empty = new JLabel("No pending role requests.");
                        empty.setFont(Theme.FONT_BODY);
                        empty.setForeground(Theme.TEXT_MUTED);
                        empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                        list.add(empty);
                    } else {
                        for (int i = 0; i < requests.size(); i++) {
                            list.add(createRoleRequestRow(requests.get(i).getAsJsonObject()));
                            list.add(Box.createVerticalStrut(4));
                        }
                    }

                    list.revalidate();
                    list.repaint();
                } catch (Exception e) {
                    System.err.println("Failed to load role requests: " + e.getMessage());
                }
            }
        }.execute();

        return card;
    }

    private JPanel createRoleRequestRow(JsonObject req) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(10, 0, 10, 0)
        ));

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        JLabel roleLabel = new JLabel("Requesting: " + req.get("requestedRole").getAsString());
        roleLabel.setFont(Theme.FONT_BODY);
        roleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel userLabel = new JLabel("Username: " + req.get("username").getAsString());
        userLabel.setFont(Theme.FONT_ERROR);
        userLabel.setForeground(Theme.TEXT_SUBTITLE);

        JLabel reasonLabel = new JLabel("<html><i>" + req.get("reason").getAsString() + "</i></html>");
        reasonLabel.setFont(Theme.FONT_ERROR);
        reasonLabel.setForeground(Theme.TEXT_MUTED);

        textBlock.add(roleLabel);
        textBlock.add(userLabel);
        textBlock.add(reasonLabel);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        JButton approveBtn = createActionButton("Approve", Theme.SUCCESS, Theme.BG_DEEP,      100);
        JButton rejectBtn  = createActionButton("Reject",  Theme.ERROR,   Theme.TEXT_PRIMARY, 100);

        String requestId = req.get("requestId").getAsString();

        approveBtn.addActionListener(e -> new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "APPROVE_ROLE_REQUEST");
                request.addProperty("requestId", requestId);
                request.addProperty("ringleaderId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();
                return response.get("status").getAsString().equals("SUCCESS");
            }
            @Override
            protected void done() {
                try {
                    if (get()) { row.setVisible(false); row.getParent().revalidate(); row.getParent().repaint(); }
                } catch (Exception ex) { System.err.println("Failed to approve role request: " + ex.getMessage()); }
            }
        }.execute());

        rejectBtn.addActionListener(e -> new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "REJECT_ROLE_REQUEST");
                request.addProperty("requestId", requestId);
                request.addProperty("ringleaderId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();
                return response.get("status").getAsString().equals("SUCCESS");
            }
            @Override
            protected void done() {
                try {
                    if (get()) { row.setVisible(false); row.getParent().revalidate(); row.getParent().repaint(); }
                } catch (Exception ex) { System.err.println("Failed to reject role request: " + ex.getMessage()); }
            }
        }.execute());

        btnGroup.add(approveBtn);
        btnGroup.add(rejectBtn);

        row.add(textBlock, BorderLayout.CENTER);
        row.add(btnGroup,  BorderLayout.EAST);

        return row;
    }

    // approval row

    private JPanel createApprovalRow(JsonObject post) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(10, 0, 10, 0)
        ));

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        JLabel jokeLabel = new JLabel(post.get("body").getAsString());
        jokeLabel.setFont(Theme.FONT_BODY);
        jokeLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel authorLabel = new JLabel("@" + post.get("username").getAsString());
        authorLabel.setFont(Theme.FONT_ERROR);
        authorLabel.setForeground(Theme.TEXT_SUBTITLE);

        textBlock.add(jokeLabel);
        textBlock.add(authorLabel);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        JButton approveBtn = createActionButton("Approve", Theme.SUCCESS, Theme.BG_DEEP,      100);
        JButton rejectBtn  = createActionButton("Reject",  Theme.ERROR,   Theme.TEXT_PRIMARY, 100);

        String postId = post.get("postId").getAsString();

        approveBtn.addActionListener(e -> new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "APPROVE_POST");
                request.addProperty("postId", postId);
                request.addProperty("moderatorId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();
                return response.get("status").getAsString().equals("SUCCESS");
            }
            @Override
            protected void done() {
                try {
                    if (get()) { row.setVisible(false); row.getParent().revalidate(); row.getParent().repaint(); }
                } catch (Exception ex) { System.err.println("Failed to approve post: " + ex.getMessage()); }
            }
        }.execute());

        rejectBtn.addActionListener(e -> new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "REJECT_POST");
                request.addProperty("postId", postId);
                request.addProperty("moderatorId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();
                return response.get("status").getAsString().equals("SUCCESS");
            }
            @Override
            protected void done() {
                try {
                    if (get()) { row.setVisible(false); row.getParent().revalidate(); row.getParent().repaint(); }
                } catch (Exception ex) { System.err.println("Failed to reject post: " + ex.getMessage()); }
            }
        }.execute());

        btnGroup.add(approveBtn);
        btnGroup.add(rejectBtn);

        row.add(textBlock, BorderLayout.WEST);
        row.add(btnGroup,  BorderLayout.EAST);

        return row;
    }

    // clown post list

    private JPanel buildPostListView() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel title = new JLabel("YOUR POSTS");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_CYAN);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        new SwingWorker<ArrayList<Post>, Void>() {
            @Override
            protected ArrayList<Post> doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_MY_POSTS");
                request.addProperty("userId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();

                ArrayList<Post> posts = new ArrayList<>();
                JsonArray postArray = response.getAsJsonArray("posts");
                for (int i = 0; i < postArray.size(); i++) {
                    JsonObject p = postArray.get(i).getAsJsonObject();
                    JsonElement modIdEl = p.get("moderatorId");
                    posts.add(new Post(
                            UUID.fromString(p.get("postId").getAsString()),
                            UUID.fromString(p.get("userId").getAsString()),
                            p.get("body").getAsString(),
                            p.get("comments").getAsString(),                                       // leaving as-is per your call
                            Status.valueOf(p.get("status").getAsString()),
                            (modIdEl == null || modIdEl.isJsonNull()) ? "" : modIdEl.getAsString(),
                            LocalDateTime.parse(p.get("timestamp").getAsString()),
                            p.get("upvotes").getAsInt(),
                            p.get("downvotes").getAsInt()
                    ));
                }
                return posts;
            }

            @Override
            protected void done() {
                try {
                    for (Post post : get()) {
                        list.add(createPostListItem(post));
                        list.add(Box.createVerticalStrut(4));
                    }
                    list.revalidate();
                    list.repaint();
                } catch (Exception e) {
                    System.err.println("Failed to load posts: " + e.getMessage());
                }
            }
        }.execute();

        return card;
    }

    private JPanel buildPostDetailView(Post post) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.ACCENT_PINK, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel title = new JLabel("POST PERFORMANCE");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_PINK);

        JPanel tileGrid = new JPanel(new GridLayout(1, 2, 16, 16));
        tileGrid.setOpaque(false);
        tileGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tileGrid.add(createStatTile("UPVOTES", String.valueOf(post.getUpvotes()), Theme.ACCENT_YELLOW));

        JButton backBtn = createActionButton("Back to List", Theme.BG_INPUT, Theme.TEXT_PRIMARY, 180);
        backBtn.addActionListener(e -> cardLayout.show(statsContainer, "LIST"));

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(tileGrid);
        card.add(Box.createVerticalGlue());
        card.add(backBtn);

        return card;
    }

    //member favourite jokes

    private JPanel buildMemberPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel title = new JLabel("FAVOURITE JOKES");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_CYAN);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        new SwingWorker<JsonArray, Void>() {
            @Override
            protected JsonArray doInBackground() throws Exception {
                Client client = new Client();
                client.connect();
                JsonObject request = new JsonObject();
                request.addProperty("action", "GET_UPVOTED_POSTS");
                request.addProperty("userId", Session.getCurrentUser().getUserId().toString());
                client.sendRequest(request);
                JsonObject response = client.readResponse();
                client.disconnect();
                return response.getAsJsonArray("posts");
            }

            @Override
            protected void done() {
                try {
                    JsonArray posts = get();
                    if (posts.isEmpty()) {
                        JLabel empty = new JLabel("No favourites yet — start upvoting!");
                        empty.setFont(Theme.FONT_BODY);
                        empty.setForeground(Theme.TEXT_MUTED);
                        empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                        list.add(empty);
                    } else {
                        for (int i = 0; i < posts.size(); i++) {
                            JsonObject post = posts.get(i).getAsJsonObject();
                            list.add(createFavouriteRow(
                                    post.get("body").getAsString(),
                                    "@" + post.get("username").getAsString(),
                                    post.get("upvotes").getAsInt(),
                                    post.get("downvotes").getAsInt()
                            ));
                            list.add(Box.createVerticalStrut(4));
                        }
                    }
                    list.revalidate();
                    list.repaint();
                } catch (Exception e) {
                    System.err.println("Failed to load upvoted posts: " + e.getMessage());
                }
            }
        }.execute();

        return card;
    }

    private JPanel createFavouriteRow(String jokeText, String author, int upvotes, int downvotes) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(10, 0, 10, 0)
        ));

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        JLabel jokeLabel = new JLabel(jokeText);
        jokeLabel.setFont(Theme.FONT_BODY);
        jokeLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(Theme.FONT_ERROR);
        authorLabel.setForeground(Theme.TEXT_SUBTITLE);

        textBlock.add(jokeLabel);
        textBlock.add(authorLabel);

        JPanel voteBlock = new JPanel();
        voteBlock.setLayout(new BoxLayout(voteBlock, BoxLayout.Y_AXIS));
        voteBlock.setOpaque(false);

        JLabel upLabel = new JLabel("▲ " + upvotes);
        upLabel.setFont(Theme.FONT_LABEL);
        upLabel.setForeground(Theme.ACCENT_YELLOW);
        upLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel downLabel = new JLabel("▼ " + downvotes);
        downLabel.setFont(Theme.FONT_LABEL);
        downLabel.setForeground(Theme.ERROR);
        downLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        voteBlock.add(upLabel);
        voteBlock.add(Box.createVerticalStrut(2));
        voteBlock.add(downLabel);

        row.add(textBlock, BorderLayout.WEST);
        row.add(voteBlock, BorderLayout.EAST);

        return row;
    }

    private JPanel createPostListItem(Post post) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        p.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT));

        JLabel lbl = new JLabel(post.getBody());
        lbl.setForeground(Theme.TEXT_PRIMARY);

        JButton view = new JButton("View Stats");
        view.setForeground(Theme.ACCENT_PINK);
        view.setContentAreaFilled(false);
        view.setCursor(new Cursor(Cursor.HAND_CURSOR));
        view.addActionListener(e -> {
            statsContainer.removeAll();
            statsContainer.add(buildPostListView(), "LIST");
            statsContainer.add(buildPostDetailView(post), "DETAIL");
            cardLayout.show(statsContainer, "DETAIL");
        });

        p.add(lbl,  BorderLayout.WEST);
        p.add(view, BorderLayout.EAST);
        return p;
    }

    // role request dialogue

    private void showRoleRequestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Request Role Change", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_CARD);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_CARD);
        content.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel heading = new JLabel("Role Request");
        heading.setFont(Theme.FONT_SUBTITLE);
        heading.setForeground(Theme.TEXT_PRIMARY);

        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"CLOWN", "RINGLEADER"});
        roleDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JTextArea reasonArea = new JTextArea(4, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBackground(Theme.BG_INPUT);
        reasonArea.setForeground(Theme.TEXT_PRIMARY);

        JLabel charCounter = new JLabel("0 / 120");
        charCounter.setFont(Theme.FONT_ERROR);

        ((AbstractDocument) reasonArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= 120) {
                    super.replace(fb, offset, length, text, attrs);
                    charCounter.setText(fb.getDocument().getLength() + " / 120");
                }
            }
        });

        JButton submit = createActionButton("Submit Request", Theme.ACCENT_PINK, Theme.BG_DEEP, 300);
        submit.addActionListener(e -> {
            String reason = reasonArea.getText().trim();
            String role   = (String) roleDropdown.getSelectedItem();
            if (reason.isEmpty()) return;
            submit.setEnabled(false);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Client client = new Client();
                    client.connect();
                    JsonObject request = new JsonObject();
                    request.addProperty("action", "SUBMIT_ROLE_REQUEST");
                    request.addProperty("userId", Session.getCurrentUser().getUserId().toString());
                    request.addProperty("requestedRole", role);
                    request.addProperty("reason", reason);
                    client.sendRequest(request);
                    JsonObject response = client.readResponse();
                    client.disconnect();
                    return response.get("status").getAsString().equals("SUCCESS");
                }

                @Override
                protected void done() {
                    try {
                        if (get()) dialog.dispose();
                        else submit.setEnabled(true);
                    } catch (Exception ex) {
                        submit.setEnabled(true);
                    }
                }
            }.execute();
        });

        content.add(heading);
        content.add(Box.createVerticalStrut(20));
        content.add(roleDropdown);
        content.add(Box.createVerticalStrut(20));
        content.add(new JScrollPane(reasonArea));
        content.add(charCounter);
        content.add(Box.createVerticalStrut(20));
        content.add(submit);

        dialog.add(content);
        dialog.setVisible(true);
    }

    //helpers

    private JPanel createStatTile(String label, String value, Color accent) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(Theme.BG_DEEP);
        tile.setBorder(new CompoundBorder(
                new LineBorder(accent, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));
        JLabel l = new JLabel(label);
        l.setForeground(Theme.TEXT_SUBTITLE);
        JLabel v = new JLabel(value);
        v.setFont(Theme.FONT_TITLE);
        v.setForeground(accent);
        tile.add(l);
        tile.add(Box.createVerticalStrut(8));
        tile.add(v);
        return tile;
    }

    private JButton createActionButton(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 42));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JPanel buildDivider() {
        JPanel d = new JPanel();
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setBackground(Theme.BORDER_DEFAULT);
        return d;
    }

    private String getRole() {
        if (Boolean.TRUE.equals(user.getRingleader())) return "Ringleader";
        if (Boolean.TRUE.equals(user.getClown()))      return "Clown";
        return "Member";
    }

    private Color getRoleBadgeColor() {
        if (Boolean.TRUE.equals(user.getRingleader())) return Theme.ACCENT_YELLOW;
        if (Boolean.TRUE.equals(user.getClown()))      return Theme.ACCENT_PINK;
        return Color.GRAY;
    }

    @Override
    public void onVisible() {
        removeAll();
        user = Session.getCurrentUser();

        if (user != null) {
            add(buildHeader(), BorderLayout.NORTH);
            add(buildBody(),   BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }
}