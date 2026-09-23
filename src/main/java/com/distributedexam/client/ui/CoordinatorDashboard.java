package com.distributedexam.client.ui;

import com.distributedexam.client.RMIClientConnector;
import com.distributedexam.common.DashboardSnapshot;
import com.distributedexam.common.ExamEvent;
import com.distributedexam.common.Room;
import com.distributedexam.common.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class CoordinatorDashboard extends JFrame {
    private static final Color BLUE = new Color(30, 94, 177);
    private final RMIClientConnector connector;
    private final JLabel refreshed = new JLabel("Loading server data…");
    private final JLabel[] metrics = new JLabel[7];
    private final DefaultTableModel roomModel = model("Room", "Location", "Capacity", "Status", "Current Exam");
    private final DefaultTableModel analyticsModel = model("Metric", "Value");
    private final DefaultTableModel eventModel = model("Time", "Room", "Event", "Description");

    public CoordinatorDashboard(RMIClientConnector connector, User user) {
        super("Coordinator Dashboard — " + user.getUsername());
        this.connector = connector;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContent(user));
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        load();
    }

    private JComponent createContent(User user) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(new Color(246, 248, 251));
        root.add(header(user), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(cards());
        content.add(Box.createVerticalStrut(14));
        content.add(topTables());
        content.add(Box.createVerticalStrut(14));
        content.add(section("Recent Events", table(eventModel), 250));
        root.add(new JScrollPane(content), BorderLayout.CENTER);
        refreshed.setForeground(new Color(92, 102, 115));
        root.add(refreshed, BorderLayout.SOUTH);
        return root;
    }

    private JComponent header(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Coordinator Overview");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel welcome = new JLabel("Live examination monitoring • Signed in as " + user.getUsername());
        welcome.setForeground(new Color(92, 102, 115));
        text.add(title); text.add(Box.createVerticalStrut(4)); text.add(welcome);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> load());
        panel.add(text, BorderLayout.WEST); panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private JComponent cards() {
        String[] labels = {"Total Rooms", "Online Rooms", "Offline Rooms", "Running Exams", "Attendance", "Submissions", "Open Incidents"};
        JPanel panel = new JPanel(new GridLayout(1, labels.length, 10, 0));
        panel.setOpaque(false);
        for (int i = 0; i < labels.length; i++) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(Color.WHITE);
            card.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(13, 12, 13, 12)));
            JLabel value = new JLabel("—");
            value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
            value.setForeground(BLUE);
            JLabel label = new JLabel(labels[i]);
            label.setForeground(new Color(92, 102, 115));
            card.add(value); card.add(Box.createVerticalStrut(5)); card.add(label);
            metrics[i] = value;
            panel.add(card);
        }
        return panel;
    }

    private JComponent topTables() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);
        panel.add(section("Room Status", table(roomModel), 255));
        panel.add(section("Analytics", table(analyticsModel), 255));
        return panel;
    }

    private JComponent section(String title, JComponent content, int height) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(12, 12, 12, 12)));
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        content.setPreferredSize(new Dimension(0, height));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane table(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    }

    private void load() {
        try {
            DashboardSnapshot d = connector.getDashboardService().getCoordinatorSnapshot();
            int[] values = {d.totalRooms, d.onlineRooms, d.offlineRooms, d.runningExams, d.attendanceCount, d.submissionCount, d.openIncidents};
            for (int i = 0; i < values.length; i++) metrics[i].setText(String.valueOf(values[i]));
            roomModel.setRowCount(0);
            for (Room room : d.rooms) roomModel.addRow(new Object[]{room.getRoomName(), room.getLocation(), room.getCapacity(), room.getStatus(), room.getCurrentExamId() == null ? "—" : room.getCurrentExamId()});
            analyticsModel.setRowCount(0);
            addMap("Attendance", d.attendancePercentByRoom, "%");
            addMap("Submission progress", d.submissionProgressByRoom, "%");
            addMap("Room status", d.roomStatusSummary, "");
            addMap("Incident", d.incidentSummary, "");
            addMap("Exam", d.examCompletionStatus, "");
            eventModel.setRowCount(0);
            for (ExamEvent event : d.recentEvents) eventModel.addRow(new Object[]{event.getEventTimestamp(), event.getRoomId() == null ? "—" : event.getRoomId(), event.getEventType(), event.getDescription()});
            refreshed.setText("Data refreshed from RMI server");
        } catch (Exception exception) {
            refreshed.setText("Unable to load server data: " + exception.getMessage());
            JOptionPane.showMessageDialog(this, refreshed.getText(), "Dashboard unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMap(String label, Map<?, ?> values, String suffix) {
        for (Map.Entry<?, ?> entry : values.entrySet()) analyticsModel.addRow(new Object[]{label + " — " + entry.getKey(), entry.getValue() + suffix});
    }
}
