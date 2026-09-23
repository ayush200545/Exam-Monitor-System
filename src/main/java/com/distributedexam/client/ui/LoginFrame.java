package com.distributedexam.client.ui;

import com.distributedexam.client.RMIClientConnector;
import com.distributedexam.common.User;
import com.distributedexam.common.UserRole;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField username = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);
    private final JLabel status = new JLabel(" ");
    private final JButton loginButton = new JButton("Sign in");
    private final RMIClientConnector connector;

    public LoginFrame(String host, int port) {
        super("Exam Monitor System");
        connector = new RMIClientConnector(host, port);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContent());
        getRootPane().setDefaultButton(loginButton);
        pack();
        setMinimumSize(new Dimension(460, 310));
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBorder(new EmptyBorder(28, 34, 28, 34));
        root.setBackground(Color.WHITE);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Exam Monitor System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Sign in to access examination monitoring");
        subtitle.setForeground(new Color(90, 100, 115));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(7));
        heading.add(subtitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 0, 6, 0);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        addField(form, g, 0, "Username", username);
        addField(form, g, 2, "Password", password);
        g.gridy = 4;
        status.setForeground(new Color(180, 45, 45));
        status.setFont(status.getFont().deriveFont(12f));
        form.add(status, g);
        g.gridy = 5;
        g.insets = new Insets(12, 0, 0, 0);
        loginButton.setBackground(new Color(30, 94, 177));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> login());
        form.add(loginButton, g);

        root.add(heading, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        return root;
    }

    private void addField(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row;
        panel.add(new JLabel(label), g);
        g.gridy = row + 1;
        panel.add(field, g);
    }

    private void login() {
        status.setText(" ");
        loginButton.setEnabled(false);
        try {
            connector.connect();
            User user = connector.getAuthService().login(username.getText().trim(), new String(password.getPassword()));
            if (user == null) {
                status.setText("Invalid username or password.");
                return;
            }
            dispose();
            if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.COORDINATOR) {
                new CoordinatorDashboard(connector, user).setVisible(true);
            } else {
                new RoomDashboard(connector, user).setVisible(true);
            }
        } catch (Exception exception) {
            status.setText("Unable to authenticate through RMI: " + exception.getMessage());
        } finally {
            loginButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame(args.length > 0 ? args[0] : "localhost", 1099).setVisible(true));
    }
}
