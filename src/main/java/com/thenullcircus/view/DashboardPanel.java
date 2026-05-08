package com.thenullcircus.view;

import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.thenullcircus.util.ScreenUtil;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * DashboardPanel — U6
 *
 * The user's personal dashboard screen. Displays identity information, account
 * details, and a role-specific right panel that changes depending on whether
 * the logged-in user is a Ringleader, Clown, or Member.
 *
 * Layout:
 *   NORTH  — Header (name, role badge, action buttons)
 *   CENTER — Body (two columns: profile card left, role panel right)
 *
 * To wire up real data once N7 (session management) is ready:
 *   Replace: this.user = new User(); (placeholder block)
 *   With:    this.user = Session.getCurrentUser();
 *   And add a null guard before building the UI.
 */
public class DashboardPanel extends BasePanel {

    // The logged-in user — populated from Session once N7 is ready
    private final User user;

    // statsContainer and cardLayout work together to create a swappable right panel.
    // cardLayout.show(statsContainer, "KEY") switches which panel is visible.
    private JPanel statsContainer;
    private CardLayout cardLayout;

    public DashboardPanel(MainWindow mainWindow) {
        super(mainWindow); // passes mainWindow reference to BasePanel for navigation

        // ── PLACEHOLDER USER ─────────────────────────────────────────────────
        // Replace this entire block with: this.user = Session.getCurrentUser();
        // once James delivers N7 (session management).
        // Also add: if (user == null) { mainWindow.navigateTo(MainWindow.LOGIN_PANEL); return; }
        this.user = new User();
        user.setName("Jarryd");
        user.setSurname("Lautenbach");
        user.setUsername("JustPhobia");
        user.setEmail("jarryd@gmail.com");
        user.setGender(Gender.MALE);
        user.setClown(false);
        user.setRingleader(true);
        // ─────────────────────────────────────────────────────────────────────

        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);

        // Responsive padding — scales to the user's screen size via ScreenUtil
        // so the dashboard doesn't feel cramped on small screens or too sparse on large ones
        int hPad = (int) (ScreenUtil.getScreenWidth() * 0.06);
        int vPad = (int) (ScreenUtil.getScreenHeight() * 0.05);
        setBorder(new EmptyBorder(vPad, hPad, vPad, hPad));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // ── Header ───────────────────────────────────────────────────────────────

    /**
     * Builds the top section of the dashboard.
     *
     * Left side: user's full name + a coloured role badge pill
     * Right side: Settings, Request Role Change, and Logout buttons
     *
     * Uses GridBagLayout with two cells:
     *   gridx=0 (weightx=1.0) — identity panel stretches to fill available space
     *   gridx=1 (weightx=0)   — action buttons sit fixed-width on the right
     *
     * A MatteBorder on the bottom provides a subtle divider line between
     * the header and the body content below it.
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT), // bottom divider line
                new EmptyBorder(0, 0, 30, 0)                        // breathing room below
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // Left: identity panel stacks name and role badge vertically
        JPanel identity = new JPanel();
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        identity.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName() + " " + user.getSurname());
        nameLabel.setFont(new Font("Serif", Font.BOLD, 42));
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        // RoundedLabel is a custom JLabel subclass defined at the bottom of this file.
        // It overrides paintComponent() to draw a rounded rectangle background
        // instead of the default rectangular one — giving it the pill badge appearance.
        // The integer (15) controls the corner arc radius — higher = rounder.
        RoundedLabel roleLabel = new RoundedLabel(getRole().toUpperCase(), 15);
        roleLabel.setFont(Theme.FONT_LABEL);
        roleLabel.setForeground(Theme.BG_DEEP);          // dark text on coloured badge
        roleLabel.setBackground(getRoleBadgeColor());     // colour depends on role
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setBorder(new EmptyBorder(5, 15, 5, 15)); // internal padding inside pill
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);   // left-align within BoxLayout

        identity.add(nameLabel);
        identity.add(Box.createVerticalStrut(8)); // 8px gap between name and badge
        identity.add(roleLabel);

        // weightx=1.0 means this cell gets all the extra horizontal space
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        header.add(identity, gbc);

        // Right: action buttons in a FlowLayout row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton settingsBtn = createActionButton("Settings",            Theme.BG_INPUT,      Theme.TEXT_PRIMARY, 140);
        JButton roleBtn     = createActionButton("Request Role Change", Theme.ACCENT_PINK,   Theme.BG_DEEP,      220);
        JButton logoutBtn   = createActionButton("Logout",              Theme.BORDER_DEFAULT, Theme.TEXT_PRIMARY, 120);

        // Opens the role request dialog (see showRoleRequestDialog())
        roleBtn.addActionListener(e -> showRoleRequestDialog());

        // Clears the session then navigates back to the login screen
        logoutBtn.addActionListener(e -> {
            Session.logout();
            mainWindow.navigateTo(MainWindow.LOGIN_PANEL);
        });

        actions.add(settingsBtn);
        actions.add(roleBtn);
        actions.add(logoutBtn);

        // weightx=0 means this cell only takes as much space as it needs
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        header.add(actions, gbc);

        return header;
    }

    // ── Body ─────────────────────────────────────────────────────────────────

    /**
     * Builds the main content area — a two-column GridLayout:
     *   Left column  — profile card (static account details)
     *   Right column — role-specific panel (changes per role)
     *
     * GridLayout(1, 2, 40, 0) means:
     *   1 row, 2 columns, 40px horizontal gap, 0px vertical gap
     * Both columns get equal width automatically.
     */
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 40, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(30, 0, 0, 0)); // top breathing room below header

        body.add(buildProfileCard());
        body.add(buildDynamicStatsContainer());

        return body;
    }

    /**
     * Builds the left column — a static card showing the user's account details.
     * Uses a BoxLayout (Y_AXIS) to stack rows vertically.
     * Each row is built by createProfileRow() which places label on the left
     * and value on the right using a BorderLayout.
     *
     * TODO: "Account Status" is currently hardcoded — wire to real user status once available.
     */
    private JPanel buildProfileCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),   // outer border
                new EmptyBorder(30, 35, 30, 35)            // inner padding
        ));

        JLabel title = new JLabel("ACCOUNT PROFILE");
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(Theme.ACCENT_CYAN);
        title.setBorder(new EmptyBorder(0, 0, 20, 0)); // space below heading

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(createProfileRow("Username",        "@" + user.getUsername()));
        card.add(createProfileRow("Email Address",   user.getEmail()));
        card.add(createProfileRow("Gender Identity", capitalize(user.getGender().toString())));
        card.add(createProfileRow("Account Status",  "Active / Certified")); // TODO: wire to real status

        return card;
    }

    // ── Role-based Right Panel ────────────────────────────────────────────────

    /**
     * Builds the right column — a CardLayout container that shows a different
     * panel depending on the user's role:
     *
     *   Ringleader → buildRingleaderPanel() — pending post approvals
     *   Clown      → buildPostListView() + buildPostDetailView() — their own posts
     *   Member     → buildMemberPanel() — their favourited jokes
     *
     * CardLayout stacks multiple panels in the same space and shows only one
     * at a time. cardLayout.show(statsContainer, "KEY") switches the visible panel.
     * The string keys ("MAIN", "LIST", "DETAIL") are used to reference each panel.
     *
     * To make this scalable:
     * - Pass real data lists into each build method instead of hardcoded arrays
     * - Each panel could become its own class extending JPanel for cleaner separation
     */
    private JPanel buildDynamicStatsContainer() {
        cardLayout = new CardLayout();
        statsContainer = new JPanel(cardLayout);
        statsContainer.setOpaque(false);

        if (Boolean.TRUE.equals(user.getRingleader())) {
            // Ringleader sees one panel: the approval queue
            statsContainer.add(buildRingleaderPanel(), "MAIN");
        } else if (Boolean.TRUE.equals(user.getClown())) {
            // Clown sees two panels: post list and a drill-down detail view
            // "View Stats" on a post switches from LIST to DETAIL
            // "Back to List" switches back from DETAIL to LIST
            statsContainer.add(buildPostListView(),   "LIST");
            statsContainer.add(buildPostDetailView(), "DETAIL");
        } else {
            // Member (audience) sees one panel: their upvoted jokes
            statsContainer.add(buildMemberPanel(), "MAIN");
        }

        return statsContainer;
    }

    // ── Clown: Post List ─────────────────────────────────────────────────────

    /**
     * The default view for Clown users — shows a list of their submitted posts.
     * Each row has a "View Stats" button that switches the CardLayout to DETAIL.
     *
     * TODO: Replace the hardcoded placeholder list with:
     *   PostDAO.findByUserId(Session.getCurrentUser().getUserId())
     *   once the server connection is available.
     */
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

        // Placeholder posts — replace with real PostDAO data once DB is wired
        list.add(createPostListItem("The Server-Side Punchline"));
        list.add(createPostListItem("Why 404 is the best joke..."));
        list.add(createPostListItem("Clown Protocol v1.2"));

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        return card;
    }

    /**
     * The drill-down view for Clown users — shows stats for a selected post.
     * Switched to from buildPostListView() when "View Stats" is clicked.
     * "Back to List" switches the CardLayout back to LIST.
     *
     * TODO: Pass the selected Post object into this method so the tiles show
     *   real upvote and comment counts instead of hardcoded values.
     */
    private JPanel buildPostDetailView() {
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

        // 1x2 grid of stat tiles showing upvote and comment counts
        JPanel tileGrid = new JPanel(new GridLayout(1, 2, 16, 16));
        tileGrid.setOpaque(false);
        tileGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        tileGrid.add(createStatTile("UPVOTES",  "256", Theme.ACCENT_YELLOW)); // TODO: real data
        tileGrid.add(createStatTile("COMMENTS", "12",  Theme.ACCENT_CYAN));   // TODO: real data

        JButton backBtn = createActionButton("Back to List", Theme.BG_INPUT, Theme.TEXT_PRIMARY, 180);
        backBtn.addActionListener(e -> cardLayout.show(statsContainer, "LIST")); // switch back

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(tileGrid);
        card.add(Box.createVerticalGlue()); // pushes back button to the bottom
        card.add(backBtn);

        return card;
    }

    // ── Ringleader: Pending Approvals ────────────────────────────────────────

    /**
     * The right panel for Ringleader users — shows a list of posts awaiting moderation.
     * Each row has Approve and Reject buttons.
     *
     * Current behaviour: clicking Approve or Reject hides the row visually only.
     * TODO: Wire buttons to PostDAO approve/reject methods via the server
     *   once the server connection is available. The DAO calls should pass:
     *   - postId (from the Post object)
     *   - moderatorId (Session.getCurrentUser().getUserId())
     *
     * TODO: Replace the hardcoded pendingPosts array with:
     *   PostDAO.findAllPending()
     *   once the server connection is available.
     */
    private JPanel buildRingleaderPanel() {
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

        // Placeholder pending posts — replace with PostDAO.findAllPending() once wired
        String[][] pendingPosts = {
                {"Why do Java devs wear glasses?",        "@bytebender"},
                {"A SQL query walks into a bar...",       "@queryqueen"},
                {"Null pointer? More like null pointer.", "@stackoverflower"}
        };

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String[] post : pendingPosts) {
            list.add(createApprovalRow(post[0], post[1]));
            list.add(Box.createVerticalStrut(4)); // small gap between rows
        }

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        return card;
    }

    /**
     * Builds a single row in the Ringleader approval list.
     *
     * Layout (BorderLayout):
     *   WEST — joke text stacked above author username
     *   EAST — Approve and Reject buttons side by side
     *
     * Current approve/reject behaviour: hides the row from the UI only.
     * TODO: Replace the action listeners with real server calls once N7 is ready.
     *
     * @param jokeText  the body of the post being reviewed
     * @param author    the username of the post's author
     */
    private JPanel createApprovalRow(String jokeText, String author) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT), // bottom divider only
                new EmptyBorder(10, 0, 10, 0)
        ));

        // Left side: joke text on top, author below
        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        JLabel jokeLabel = new JLabel(jokeText);
        jokeLabel.setFont(Theme.FONT_BODY);
        jokeLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(Theme.FONT_ERROR); // smaller font — same as error label size
        authorLabel.setForeground(Theme.TEXT_SUBTITLE);

        textBlock.add(jokeLabel);
        textBlock.add(authorLabel);

        // Right side: approve (green/yellow) and reject (red) buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        JButton approveBtn = createActionButton("Approve", Theme.SUCCESS, Theme.BG_DEEP,      100);
        JButton rejectBtn  = createActionButton("Reject",  Theme.ERROR,   Theme.TEXT_PRIMARY, 100);

        // TODO: replace with real DAO calls — PostDAO.approvePost(postId, moderatorId)
        approveBtn.addActionListener(e -> {
            row.setVisible(false);       // hides the row from the list
            row.getParent().revalidate(); // forces the layout to recalculate
            row.getParent().repaint();    // repaints to remove the visual gap
        });
        rejectBtn.addActionListener(e -> {
            row.setVisible(false);
            row.getParent().revalidate();
            row.getParent().repaint();
        });

        btnGroup.add(approveBtn);
        btnGroup.add(rejectBtn);

        row.add(textBlock, BorderLayout.WEST);
        row.add(btnGroup,  BorderLayout.EAST);

        return row;
    }

    // ── Member: Favourite Jokes ──────────────────────────────────────────────

    /**
     * The right panel for Member (audience) users — shows jokes they have upvoted.
     * Each row shows the joke text, author username, and upvote/downvote counts.
     *
     * TODO: Replace the hardcoded favourites array with:
     *   VotesDAO.getUpvotedPostsByUser(Session.getCurrentUser().getUserId())
     *   once the server connection is available.
     *
     * //@see createFavouriteRow for individual row structure
     */
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

        // Placeholder favourites — replace with VotesDAO upvoted posts once wired
        // Structure: { jokeText, authorUsername, upvotes, downvotes }
        Object[][] favourites = {
                {"Why do programmers prefer dark mode?",  "@bytebender", 142, 3},
                {"A SQL query walks into a bar...",        "@queryqueen",  98, 7},
                {"I told a UDP joke. You may not get it.", "@packetpal",  201, 12}
        };

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Object[] joke : favourites) {
            list.add(createFavouriteRow(
                    (String) joke[0],
                    (String) joke[1],
                    (int)    joke[2],
                    (int)    joke[3]
            ));
            list.add(Box.createVerticalStrut(4));
        }

        card.add(title);
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(10));
        card.add(list);

        return card;
    }

    /**
     * Builds a single row in the Member favourites list.
     *
     * Layout (BorderLayout):
     *   WEST — joke text stacked above author username
     *   EAST — upvote count (yellow ▲) stacked above downvote count (red ▼)
     *
     * @param jokeText  the body of the joke
     * @param author    the username of the joke's author
     * @param upvotes   total upvote count on the post
     * @param downvotes total downvote count on the post
     */
    private JPanel createFavouriteRow(String jokeText, String author, int upvotes, int downvotes) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(10, 0, 10, 0)
        ));

        // Left: joke text + author
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

        // Right: vote counts stacked vertically
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

    // ── Role Request Dialog ───────────────────────────────────────────────────

    /**
     * Opens a modal dialog for submitting a role change request.
     * Uses JDialog with modal=true so the user can't interact with the
     * dashboard behind it until the dialog is closed.
     *
     * Fields:
     *   - Role dropdown (CLOWN or RINGLEADER — matching role_requests DB enum)
     *   - Reason text area — hard capped at 120 characters via DocumentFilter
     *     to match the VARCHAR(120) constraint in the role_requests table
     *   - Live character counter (e.g. "47 / 120") updates on every keystroke
     *
     * TODO: Wire the submit button to RoleRequestDAOImpl.submitRequest()
     *   via the server once N7 is ready. Pass:
     *   - userId:        Session.getCurrentUser().getUserId()
     *   - requestedRole: roleDropdown.getSelectedItem()
     *   - reason:        reasonArea.getText().trim()
     */
    private void showRoleRequestDialog() {
        // modal=true blocks interaction with the parent window until closed
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Request Role Change", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this); // centres relative to the dashboard
        dialog.getContentPane().setBackground(Theme.BG_CARD);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_CARD);
        content.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel heading = new JLabel("Role Request");
        heading.setFont(Theme.FONT_SUBTITLE);
        heading.setForeground(Theme.TEXT_PRIMARY);

        // Dropdown restricted to CLOWN and RINGLEADER — matches role_requests DB enum
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"CLOWN", "RINGLEADER"});
        roleDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JTextArea reasonArea = new JTextArea(4, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBackground(Theme.BG_INPUT);
        reasonArea.setForeground(Theme.TEXT_PRIMARY);

        JLabel charCounter = new JLabel("0 / 120");
        charCounter.setFont(Theme.FONT_ERROR);

        // DocumentFilter intercepts all text changes before they're applied.
        // replace() handles typing, pasting, and selecting then typing.
        // It checks the resulting length before allowing the change through —
        // hard stopping at 120 characters to match the DB VARCHAR(120) constraint.
        ((AbstractDocument) reasonArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= 120) {
                    super.replace(fb, offset, length, text, attrs);
                    // update counter after every accepted change
                    charCounter.setText(fb.getDocument().getLength() + " / 120");
                }
                // if over limit, silently reject the input — no error shown
            }
        });

        JButton submit = createActionButton("Submit Request", Theme.ACCENT_PINK, Theme.BG_DEEP, 300);
        // TODO: replace dispose() with real DAO submission call
        submit.addActionListener(e -> dialog.dispose());

        content.add(heading);
        content.add(Box.createVerticalStrut(20));
        content.add(roleDropdown);
        content.add(Box.createVerticalStrut(20));
        content.add(new JScrollPane(reasonArea)); // scroll in case of long reason text
        content.add(charCounter);
        content.add(Box.createVerticalStrut(20));
        content.add(submit);

        dialog.add(content);
        dialog.setVisible(true); // blocks until dialog is closed (modal)
    }

    // ── Shared UI Helpers ─────────────────────────────────────────────────────

    /**
     * Builds a single row in the Clown post list.
     * "View Stats" button switches the CardLayout to the DETAIL view.
     *
     * TODO: Accept a Post object as a parameter so the selected post
     *   can be passed into buildPostDetailView() to show real stats.
     *
     * @param text the joke body to display
     */
    private JPanel createPostListItem(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        p.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.TEXT_PRIMARY);

        // contentAreaFilled=false makes the button text-only with no background box
        JButton view = new JButton("View Stats");
        view.setForeground(Theme.ACCENT_PINK);
        view.setContentAreaFilled(false);
        view.setCursor(new Cursor(Cursor.HAND_CURSOR));
        view.addActionListener(e -> cardLayout.show(statsContainer, "DETAIL"));

        p.add(lbl,  BorderLayout.WEST);
        p.add(view, BorderLayout.EAST);
        return p;
    }

    /**
     * Builds a single label/value row for the profile card.
     * Uses BorderLayout: label on the WEST, value on the EAST.
     * A MatteBorder on the bottom provides a subtle divider between rows.
     *
     * @param label the field name (e.g. "Username")
     * @param value the field value (e.g. "@JustPhobia")
     */
    private JPanel createProfileRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        row.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_SUBTITLE);

        JLabel v = new JLabel(value);
        v.setForeground(Theme.TEXT_PRIMARY);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    /**
     * Builds a stat tile — a small card showing a single metric.
     * Used in the Clown detail view for upvotes and comments.
     * The accent colour is applied to both the border and the value label
     * so each tile is visually distinct.
     *
     * @param label  the metric name (e.g. "UPVOTES")
     * @param value  the metric value (e.g. "256")
     * @param accent the colour for the border and value text
     */
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

    /**
     * Builds a styled action button with a hover effect.
     * Mouse listeners brighten the background on hover and restore it on exit.
     * setOpaque(true) is required on some systems for the background colour to render.
     *
     * @param text   button label
     * @param bg     background colour
     * @param fg     foreground (text) colour
     * @param width  preferred width in pixels
     */
    private JButton createActionButton(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 42));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setFocusPainted(false);  // removes the dotted focus rectangle
        btn.setBorderPainted(false); // removes the default button border
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    /**
     * Builds a 1px horizontal divider line.
     * setMaximumSize limits height to 1px so BoxLayout doesn't stretch it.
     * Used as a visual separator between section headings and content rows.
     */
    private JPanel buildDivider() {
        JPanel d = new JPanel();
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setBackground(Theme.BORDER_DEFAULT);
        return d;
    }

    // ── Role Helpers ──────────────────────────────────────────────────────────

    /**
     * Returns the user's role as a display string.
     * Uses Boolean.TRUE.equals() instead of direct unboxing to safely handle
     * null — the User model uses Boolean (nullable) not boolean (primitive).
     */
    private String getRole() {
        if (Boolean.TRUE.equals(user.getRingleader())) return "Ringleader";
        if (Boolean.TRUE.equals(user.getClown())) return "Clown";
        return "Member";
    }

    /**
     * Returns the badge background colour for the user's role.
     * Ringleader = gold, Clown = pink, Member = gray.
     * Uses Boolean.TRUE.equals() for the same null-safety reason as getRole().
     */
    private Color getRoleBadgeColor() {
        if (Boolean.TRUE.equals(user.getRingleader())) return Theme.ACCENT_YELLOW;
        if (Boolean.TRUE.equals(user.getClown())) return Theme.ACCENT_PINK;
        return Color.GRAY;
    }

    /**
     * Capitalizes only the first letter of a string and lowercases the rest.
     * Used to display gender as "Male" instead of "MALE" from the enum.
     */
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}

// ── RoundedLabel ──────────────────────────────────────────────────────────────

/**
 * A custom JLabel subclass that renders a rounded rectangle background
 * instead of the default rectangular one.
 *
 * How it works:
 *   setOpaque(false) prevents Swing from painting the standard rectangular
 *   background before our custom paint runs. We then override paintComponent()
 *   to manually draw a rounded rectangle filled with getBackground() colour,
 *   then call super.paintComponent() to draw the text on top of it.
 *
 *   RenderingHints.KEY_ANTIALIASING smooths the rounded corners so they
 *   don't appear jagged on screen.
 *
 * Usage: new RoundedLabel("CLOWN", 15)
 *   The integer is the arc radius — higher values = rounder corners.
 *   15 gives a gentle pill shape suitable for role badges.
 */
class RoundedLabel extends JLabel {
    private final int cornerRadius;

    public RoundedLabel(String text, int radius) {
        super(text);
        this.cornerRadius = radius;
        setOpaque(false); // must be false so our custom background doesn't get clipped
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); // create a copy so we don't affect other paint ops
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose(); // always dispose the Graphics2D copy to free resources
        super.paintComponent(g); // paints the label text on top of our background
    }
}