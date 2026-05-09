package com.thenullcircus.view;

import com.thenullcircus.util.Theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;

public class PostCreationPanel extends BasePanel {

    private static final int MAX_CHARS = 400; // matches VARCHAR(400) in your posts table

    private JTextArea bodyField;
    private JLabel charCounterLabel;

    public PostCreationPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        setBorder(new EmptyBorder(
                Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                Theme.PADDING_LARGE, Theme.PADDING_LARGE
        ));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(),   BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(0, 0, Theme.PADDING_LARGE, 0)
        ));

        JLabel title = new JLabel("New Post");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Share your joke with the circus");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_SUBTITLE);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        return header;
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // Card — matches BG_CARD + pink border style used across the project
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.ACCENT_PINK, 2),
                new EmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE
                )
        ));

        // ── Body label ────────────────────────────────────────────────────────
        JLabel bodyLabel = new JLabel("Your Joke");
        bodyLabel.setFont(Theme.FONT_LABEL);
        bodyLabel.setForeground(Theme.TEXT_LABEL);
        bodyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Body text area ────────────────────────────────────────────────────
        bodyField = new JTextArea(6, 30);
        bodyField.setLineWrap(true);
        bodyField.setWrapStyleWord(true);
        bodyField.setBackground(Theme.BG_INPUT);
        bodyField.setForeground(Theme.TEXT_PRIMARY);
        bodyField.setCaretColor(Theme.ACCENT_PINK);
        bodyField.setFont(Theme.FONT_BODY);
        bodyField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_DEFAULT),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Wrap in scroll pane in case the joke is long
        JScrollPane scrollPane = new JScrollPane(bodyField);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
        scrollPane.getViewport().setBackground(Theme.BG_INPUT);

        // ── Character counter ─────────────────────────────────────────────────
        // Greyed out, right-aligned, updates dynamically as the user types
        charCounterLabel = new JLabel(String.format("000/%03d characters remaining", MAX_CHARS));
        charCounterLabel.setFont(Theme.FONT_ERROR);
        charCounterLabel.setForeground(Theme.TEXT_MUTED);
        charCounterLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // DocumentFilter caps input at MAX_CHARS and updates the counter on every keystroke
        // Same pattern used in DashboardPanel's role request dialog
        ((AbstractDocument) bodyField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length,
                                String text, AttributeSet attrs) throws BadLocationException {
                int currentLen  = fb.getDocument().getLength();
                int newLen      = currentLen - length + text.length();

                if (newLen <= MAX_CHARS) {
                    super.replace(fb, offset, length, text, attrs);
                    updateCounter(fb.getDocument().getLength());
                }
                // Silently reject input beyond the limit — same behaviour as DashboardPanel
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                super.remove(fb, offset, length);
                updateCounter(fb.getDocument().getLength());
            }
        });

        // ── Post button ───────────────────────────────────────────────────────
        JButton postButton = new GradientButton(
                "Post Joke",
                Theme.GRADIENT_RED_START,
                Theme.GRADIENT_YELLOW_END
        );
        postButton.setPreferredSize(new Dimension(280, 46));
        postButton.setMinimumSize(new Dimension(280, 46));
        postButton.setMaximumSize(new Dimension(280, 46));
        postButton.setForeground(Theme.TEXT_PRIMARY);
        postButton.setFont(Theme.FONT_BUTTON);
        postButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // For now — navigates to dashboard on submit
        // TODO: replace with server call to submit post once DB wiring session
        postButton.addActionListener(e -> {
            String body = bodyField.getText().trim();

            if (body.isEmpty()) {
                // Briefly flash the border red to indicate empty submission
                bodyField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.ERROR, 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                return;
            }

            // Reset border in case it was previously red
            bodyField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_DEFAULT),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            // Clear the field and navigate to dashboard
            bodyField.setText("");
            updateCounter(0);
            navigateTo(MainWindow.DASHBOARD_PANEL);
        });

        // ── Assemble card ─────────────────────────────────────────────────────
        card.add(bodyLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(scrollPane);
        card.add(Box.createVerticalStrut(4));
        card.add(charCounterLabel);
        card.add(Box.createVerticalStrut(Theme.PADDING_LARGE));
        card.add(postButton);

        // Centre the card in the wrapper using GridBagLayout
        wrapper.add(card, new GridBagConstraints());

        return wrapper;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Updates the character counter label.
     * Format: "047/400 characters remaining"
     * Uses %03d to zero-pad both numbers so the label width stays stable
     * and doesn't cause layout shifts as the user types.
     */
    private void updateCounter(int currentLength) {
        int remaining = MAX_CHARS - currentLength;
        charCounterLabel.setText(
                String.format("%03d/%03d characters remaining", remaining, MAX_CHARS)
        );

        // Turn the counter pink when under 50 characters remaining as a warning
        charCounterLabel.setForeground(
                remaining < 50 ? Theme.ACCENT_PINK : Theme.TEXT_MUTED
        );
    }
}