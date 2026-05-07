package com.thenullcircus.view;

import com.thenullcircus.model.User;
import com.thenullcircus.model.Gender;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class DashboardPanel extends BasePanel {

    private final User user;

    public DashboardPanel(MainWindow mainWindow) {
        super(mainWindow);

        // placeholder user
        this.user = Session.getCurrentUser();
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildDetails(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    // ── Header ──────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(Theme.FONT_TITLE);
        usernameLabel.setForeground(Theme.ACCENT_YELLOW);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel(getRole());
        roleLabel.setFont(Theme.FONT_SUBTITLE);
        roleLabel.setForeground(getRoleTextColor());
        roleLabel.setBackground(getRoleBadgeColor());
        roleLabel.setOpaque(true);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(usernameLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(roleLabel);

        return header;
    }

    // ── Details ─────────────────────────────────────────────────────────────

    private JPanel buildDetails() {
        JPanel details = new JPanel();
        details.setLayout(new GridBagLayout());
        details.setBackground(Theme.BG_DEEP);
        details.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(details, gbc, 0, "Name",     user.getName() + " " + user.getSurname());
        addDetailRow(details, gbc, 1, "Username", "@" + user.getUsername());
        addDetailRow(details, gbc, 2, "Email",    user.getEmail());
        addDetailRow(details, gbc, 3, "Gender",   user.getGender().toString());
        addDetailRow(details, gbc, 4, "Role",     getRole());
        addDetailRow(details, gbc, 5, "Posts",    "0");

        return details;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(Theme.FONT_LABEL);
        label.setForeground(Theme.TEXT_LABEL);

        JLabel value = new JLabel(valueText);
        value.setFont(Theme.FONT_BODY);
        value.setForeground(Theme.TEXT_PRIMARY);

        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(value, gbc);
    }

    // ── Actions ─────────────────────────────────────────────────────────────

    private JPanel buildActions() {
        JPanel actions = new JPanel();
        // Use Y_AXIS so the action section stays at the bottom
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(Theme.BG_CARD);
        actions.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Create a sub-panel specifically for the horizontal row
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setBackground(Theme.BG_CARD);

        // Initialize Buttons
        JButton roleRequestButton = new JButton("Request Role Change");
        styleButton(roleRequestButton, Theme.ACCENT_PINK);
        roleRequestButton.addActionListener(e -> showRoleRequestDialog());
        roleRequestButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton settingsButton = new JButton("Settings");
        styleButton(settingsButton, Theme.BG_INPUT);
        settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, Color.GRAY);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            Session.logout();
            mainWindow.navigateTo(MainWindow.LOGIN_PANEL);});

        // --- The Centering Logic ---
        buttonRow.add(Box.createHorizontalGlue()); // Left "Spring"

        buttonRow.add(roleRequestButton);
        buttonRow.add(Box.createHorizontalStrut(10)); // Tight gap

        buttonRow.add(settingsButton); // This will now sit in the middle

        buttonRow.add(Box.createHorizontalStrut(10)); // Tight gap
        buttonRow.add(logoutButton);

        buttonRow.add(Box.createHorizontalGlue()); // Right "Spring"

        actions.add(buttonRow);
        return actions;
    }

    private void styleButton(JButton button, Color background) {
        Dimension btnSize = new Dimension(280, 46);
        button.setPreferredSize(btnSize);
        button.setMaximumSize(btnSize);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(background);
        button.setForeground(Theme.TEXT_PRIMARY);
        button.setFont(Theme.FONT_BUTTON);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    // ── Role Request Dialog ──────────────────────────────────────────────────

    private void showRoleRequestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Request Role Change", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_CARD);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_CARD);
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 1. Initialize the Label and Text Area FIRST
        JLabel rolePickerLabel = new JLabel("Requested Role:");
        // ... (style code)

        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"CLOWN", "RINGLEADER"});
        // ... (style code)

        JLabel reasonLabel = new JLabel("Reason (max 120 characters):");
        // ... (style code)

        JTextArea reasonArea = new JTextArea(4, 20);
        // ... (style code)

        // 2. Initialize the Counter Label BEFORE the filter uses it
        JLabel charCounter = new JLabel("0 / 120");
        charCounter.setFont(Theme.FONT_ERROR);
        charCounter.setForeground(Theme.TEXT_LABEL);
        charCounter.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Now set the DocumentFilter
        ((AbstractDocument) reasonArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= 120) {
                    super.insertString(fb, offset, string, attr);
                    updateCounter(reasonArea, charCounter);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
                int newLength = fb.getDocument().getLength() - length + string.length();
                if (newLength <= 120) {
                    super.replace(fb, offset, length, string, attr);
                    updateCounter(reasonArea, charCounter);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                updateCounter(reasonArea, charCounter);
            }
        });

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ... (rest of the assembly code)
        content.add(rolePickerLabel);
        content.add(roleDropdown);
        content.add(reasonLabel);
        content.add(reasonScroll);
        content.add(charCounter); // This is now safely initialized
        // ...
    }

    private void updateCounter(JTextArea area, JLabel counter) {
        counter.setText(area.getText().length() + " / 120");
    }

    // ── Role helpers ─────────────────────────────────────────────────────────

    private String getRole() {
        if (Boolean.TRUE.equals(user.getRingleader())) return "Ringleader";
        if (Boolean.TRUE.equals(user.getClown())) return "Clown";
        return "User";
    }

    private Color getRoleBadgeColor() {
        if (Boolean.TRUE.equals(user.getRingleader())) return Theme.ACCENT_YELLOW;
        if (Boolean.TRUE.equals(user.getClown())) return Theme.ACCENT_PINK;
        return Color.GRAY;
    }

    private Color getRoleTextColor() {
        return Theme.BG_DEEP;
    }
}