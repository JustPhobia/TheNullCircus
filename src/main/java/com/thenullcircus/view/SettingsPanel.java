package com.thenullcircus.view;

import com.thenullcircus.model.User;
import com.thenullcircus.util.Session;
import com.thenullcircus.util.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SettingsPanel extends BasePanel {

    private JPanel contentPanel;
    private User user;

    public SettingsPanel(MainWindow mainWindow) {
        super(mainWindow);
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DEEP);
        buildUI();
    }

    @Override
    public void onVisible() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        //title banner
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
        //center the card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_DEEP);

        user = Session.getCurrentUser();

        //card
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER_DEFAULT, 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        //title
        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrapper.setBackground(Theme.BG_CARD);
        titleWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel cardTitle = new JLabel("ACCOUNT PROFILE");
        cardTitle.setFont(Theme.FONT_LABEL);
        cardTitle.setForeground(Theme.ACCENT_CYAN);
        cardTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        titleWrapper.add(cardTitle);

        contentPanel.add(titleWrapper);
        contentPanel.add(buildDivider());
        contentPanel.add(Box.createVerticalStrut(10));


        //rows
        contentPanel.add(buildEditableRow("Username",     user != null ? user.getUsername() : ""));
        contentPanel.add(buildEditableRow("Email",        user != null ? user.getEmail()    : ""));
        contentPanel.add(buildEditableRow("Name",         user != null ? user.getName()     : ""));
        contentPanel.add(buildEditableRow("Surname",      user != null ? user.getSurname()  : ""));


        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridx   = 0;
        wgbc.gridy   = 0;
        wgbc.fill    = GridBagConstraints.HORIZONTAL;
        wgbc.anchor  = GridBagConstraints.NORTH;
        wgbc.weightx = 1.0;
        wgbc.weighty = 0;
        wgbc.insets  = new Insets(Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                Theme.PADDING_LARGE, Theme.PADDING_LARGE);
        wrapper.add(contentPanel, wgbc);

        return wrapper;
    }

    //editable row

    private JPanel buildEditableRow(String fieldName, String currentValue) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Theme.BG_CARD);
        row.setBorder(new CompoundBorder(
                new javax.swing.border.MatteBorder(0, 0, 1, 0, Theme.BORDER_DEFAULT),
                new EmptyBorder(8, 0, 8, 0)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        //field name
        JLabel nameLabel = new JLabel(fieldName);
        nameLabel.setFont(Theme.FONT_LABEL);
        nameLabel.setForeground(Theme.TEXT_MUTED);
        nameLabel.setPreferredSize(new Dimension(120, 30));

        //value label
        JLabel valueLabel = new JLabel(currentValue);
        valueLabel.setFont(Theme.FONT_BODY);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        //edit button
        JButton editButton = new JButton("Edit");
        styleEditButton(editButton);

        row.add(nameLabel,  BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        row.add(editButton, BorderLayout.EAST);

        //edit save
        editButton.addActionListener(e -> {
            if (editButton.getText().equals("Edit")) {
                // Swap label for text field
                JTextField editField = new JTextField(valueLabel.getText());
                editField.setFont(Theme.FONT_BODY);
                editField.setForeground(Theme.TEXT_PRIMARY);
                editField.setBackground(Theme.BG_INPUT);
                editField.setCaretColor(Theme.ACCENT_PINK);
                editField.setBorder(BorderFactory.createLineBorder(Theme.BORDER_DEFAULT));

                row.remove(valueLabel);
                row.add(editField, BorderLayout.CENTER);
                row.revalidate();
                row.repaint();

                editButton.setText("Save");

            } else {
                // Find the text field and save value
                Component center = ((BorderLayout) row.getLayout())
                        .getLayoutComponent(BorderLayout.CENTER);

                if (center instanceof JTextField tf) {
                    String newValue = tf.getText().trim();
                    valueLabel.setText(newValue);
                    row.remove(tf);
                    row.add(valueLabel, BorderLayout.CENTER);
                    row.revalidate();
                    row.repaint();
                }

                editButton.setText("Edit");

                // TODO: send update to server when route is ready
                // sendUpdate(fieldName, newValue);
            }
        });

        return row;
    }

    //divider

    private JSeparator buildDivider() {
        JSeparator divider = new JSeparator();
        divider.setForeground(Theme.BORDER_DEFAULT);
        divider.setBackground(Theme.BORDER_DEFAULT);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return divider;
    }

    //button styling

    private void styleEditButton(JButton button) {
        button.setFont(Theme.FONT_LABEL);
        button.setBackground(Theme.ACCENT_YELLOW );
        button.setForeground(Theme.ACCENT_PINK);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_PINK, 1));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 30));

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