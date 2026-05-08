package com.thenullcircus.view;

import com.thenullcircus.model.User;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends BasePanel {

    private JPanel contentPanel;

    public SettingsPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        buildUI();
    }

    private void buildUI() {
        // Title banner
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setBackground(Theme.BG_CARD);
        banner.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_MEDIUM, Theme.PADDING_LARGE,
                Theme.PADDING_MEDIUM, Theme.PADDING_LARGE
        ));
        JLabel title = new JLabel("Settings");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT_YELLOW);
        banner.add(title);

        add(banner, BorderLayout.NORTH);
        add(buildProfileCard(), BorderLayout.CENTER);
    }

    private JPanel buildProfileCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_DEEP);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PINK, 2),
                BorderFactory.createEmptyBorder(
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                        Theme.PADDING_LARGE, Theme.PADDING_LARGE)
        ));

        User user = Session.getCurrentUser();

        int row = 0;
        row = addEditableField(contentPanel, "Name",     user != null ? user.getName()     : "", row);
        row = addEditableField(contentPanel, "Surname",  user != null ? user.getSurname()  : "", row);
        row = addEditableField(contentPanel, "Username", user != null ? user.getUsername() : "", row);
        row = addEditableField(contentPanel, "Email",    user != null ? user.getEmail()    : "", row);

        // Add contentPanel to wrapper centered
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridx   = 0;
        wgbc.gridy   = 0;
        wgbc.fill    = GridBagConstraints.NONE;
        wgbc.anchor  = GridBagConstraints.CENTER;
        wgbc.weightx = 1.0;
        wgbc.weighty = 1.0;
        wgbc.insets  = new Insets(Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                Theme.PADDING_LARGE, Theme.PADDING_LARGE);
        wrapper.add(contentPanel, wgbc);

        return wrapper;
    }

    // ── Editable field row ────────────────────────────────────────────────────

    private int addEditableField(JPanel panel, String fieldName,
                                 String currentValue, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy   = row;

        // Field name label — col 0
        JLabel nameLabel = new JLabel(fieldName);
        nameLabel.setFont(Theme.FONT_LABEL);
        nameLabel.setForeground(Theme.TEXT_LABEL);
        nameLabel.setPreferredSize(new Dimension(100, 30));
        gbc.gridx = 0;
        panel.add(nameLabel, gbc);

        // Value label — col 1
        JLabel valueLabel = new JLabel(currentValue);
        valueLabel.setFont(Theme.FONT_BODY);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(valueLabel, gbc);

        // Edit button — col 2
        JButton editButton = new JButton("Edit");
        styleEditButton(editButton);
        gbc.gridx  = 2;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(editButton, gbc);

        // ── Wire edit/save toggle ─────────────────────────────────────────
        editButton.addActionListener(e -> {
            if (editButton.getText().equals("Edit")) {
                // Switch to edit mode
                JTextField editField = new JTextField(valueLabel.getText());
                editField.setFont(Theme.FONT_BODY);
                editField.setForeground(Theme.TEXT_PRIMARY);
                editField.setBackground(Theme.BG_INPUT);
                editField.setCaretColor(Theme.ACCENT_PINK);
                editField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));
                editField.setPreferredSize(new Dimension(200, 30));

                // Replace valueLabel with editField
                GridBagConstraints fieldGbc = new GridBagConstraints();
                fieldGbc.gridx   = 1;
                fieldGbc.gridy   = row;
                fieldGbc.fill    = GridBagConstraints.HORIZONTAL;
                fieldGbc.weightx = 1.0;
                fieldGbc.insets  = new Insets(6, 8, 6, 8);

                panel.remove(valueLabel);
                panel.add(editField, fieldGbc);
                panel.revalidate();
                panel.repaint();

                editButton.setText("Save");

            } else {
                // Switch back to view mode — save the value
                String newValue = "";

                // Find the text field that replaced the label
                for (Component c : panel.getComponents()) {
                    if (c instanceof JTextField tf) {
                        GridBagLayout layout = (GridBagLayout) panel.getLayout();
                        GridBagConstraints c2 = layout.getConstraints(c);
                        if (c2.gridy == row && c2.gridx == 1) {
                            newValue = tf.getText().trim();
                            panel.remove(tf);
                            break;
                        }
                    }
                }

                // Update the label with the new value
                valueLabel.setText(newValue);

                GridBagConstraints labelGbc = new GridBagConstraints();
                labelGbc.gridx   = 1;
                labelGbc.gridy   = row;
                labelGbc.fill    = GridBagConstraints.HORIZONTAL;
                labelGbc.weightx = 1.0;
                labelGbc.insets  = new Insets(6, 8, 6, 8);

                panel.add(valueLabel, labelGbc);
                panel.revalidate();
                panel.repaint();

                editButton.setText("Edit");

                // TODO: send update to server when route is ready
                // sendUpdate(fieldName, newValue);
            }
        });

        return row + 1;
    }

    private void styleEditButton(JButton button) {
        button.setFont(Theme.FONT_LABEL);
        button.setBackground(Theme.BG_CARD);
        button.setForeground(Theme.ACCENT_PINK);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_PINK));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.ACCENT_PINK);
                button.setForeground(Theme.BG_DEEP);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Theme.BG_CARD);
                button.setForeground(Theme.ACCENT_PINK);
            }
        });
    }
}