package com.distributedexam.client.ui;

import com.distributedexam.client.RMIClientConnector;
import com.distributedexam.common.DashboardSnapshot;
import com.distributedexam.common.ExamEvent;
import com.distributedexam.common.Incident;
import com.distributedexam.common.Room;
import com.distributedexam.common.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoomDashboard extends JFrame {
    private static final Color BLUE = new Color(30, 94, 177);
    private final RMIClientConnector connector;
    private final User user;
    private final JLabel roomName = new JLabel("Room —");
    private final JLabel roomStatus = new JLabel("Status: —");
    private final JLabel currentExam = new JLabel("No exam assigned");
    private final JLabel attendance = cardValue();
    private final JLabel submissions = cardValue();
    private final JLabel openIncidents = cardValue();
    private final DefaultTableModel incidentModel = model("Status", "Severity", "Type", "Description");
    private final DefaultTableModel eventModel = model("Time", "Event", "Description");

    public RoomDashboard(RMIClientConnector connector, User user) {
        super("Room Dashboard — " + user.getUsername());
        this.connector = connector;
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContent());
        setSize(1000, 680);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        load();
    }

    private JComponent createContent() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(new Color(246, 248, 251));
        root.add(header(), BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(details());
        content.add(Box.createVerticalStrut(14));
        content.add(cards());
        content.add(Box.createVerticalStrut(14));
        JPanel tables = new JPanel(new GridLayout(1, 2, 14, 0));
        tables.setOpaque(false);
        tables.add(section("Incidents", table(incidentModel), 260));
        tables.add(section("Recent Events", table(eventModel), 260));
        content.add(tables);
        root.add(new JScrollPane(content), BorderLayout.CENTER);
        return root;
    }

    private JComponent header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel labels = new JPanel(); labels.setOpaque(false); labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Room Operations"); title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Signed in as " + user.getUsername()); subtitle.setForeground(new Color(92, 102, 115));
        labels.add(title); labels.add(Box.createVerticalStrut(4)); labels.add(subtitle);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); buttons.setOpaque(false);
        JButton refresh = new JButton("Refresh"); refresh.addActionListener(e -> load());
        JButton report = new JButton("Report Incident"); report.setBackground(BLUE); report.setForeground(Color.WHITE); report.setFocusPainted(false); report.addActionListener(e -> report());
        buttons.add(refresh); buttons.add(report); panel.add(labels, BorderLayout.WEST); panel.add(buttons, BorderLayout.EAST);
        return panel;
    }

    private JComponent details() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0)); panel.setOpaque(false);
        panel.add(infoPanel("Room", roomName, roomStatus));
        panel.add(infoPanel("Current Exam", currentExam, new JLabel("Live data supplied by the RMI server")));
        return panel;
    }

    private JComponent infoPanel(String heading, JLabel primary, JLabel secondary) {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(13, 15, 13, 15)));
        JLabel label = new JLabel(heading); label.setForeground(new Color(92, 102, 115));
        primary.setFont(primary.getFont().deriveFont(Font.BOLD, 18f)); primary.setForeground(BLUE);
        secondary.setForeground(new Color(92, 102, 115));
        panel.add(label); panel.add(Box.createVerticalStrut(6)); panel.add(primary); panel.add(Box.createVerticalStrut(4)); panel.add(secondary);
        return panel;
    }

    private JComponent cards() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0)); panel.setOpaque(false);
        panel.add(metricCard("Attendance progress", attendance));
        panel.add(metricCard("Submission progress", submissions));
        panel.add(metricCard("Open incidents", openIncidents));
        return panel;
    }

    private JComponent metricCard(String label, JLabel value) {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(13, 15, 13, 15)));
        JLabel heading = new JLabel(label); heading.setForeground(new Color(92, 102, 115));
        panel.add(heading); panel.add(Box.createVerticalStrut(7)); panel.add(value); return panel;
    }

    private JLabel cardValue() { JLabel label = new JLabel("—"); label.setFont(label.getFont().deriveFont(Font.BOLD, 26f)); label.setForeground(BLUE); return label; }
    private JComponent section(String title, JComponent content, int height) { JPanel panel = new JPanel(new BorderLayout(0, 8)); panel.setBackground(Color.WHITE); panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(12, 12, 12, 12))); JLabel label = new JLabel(title); label.setFont(label.getFont().deriveFont(Font.BOLD, 16f)); panel.add(label, BorderLayout.NORTH); content.setPreferredSize(new Dimension(0, height)); panel.add(content, BorderLayout.CENTER); return panel; }
    private JScrollPane table(DefaultTableModel tableModel) { JTable table = new JTable(tableModel); table.setRowHeight(24); table.setFillsViewportHeight(true); table.getTableHeader().setReorderingAllowed(false); return new JScrollPane(table); }
    private DefaultTableModel model(String... columns) { return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } }; }

    private void load() {
        if (user.getRoomId() == null) { roomName.setText("No room assigned"); roomStatus.setText("Ask a coordinator to assign a room"); return; }
        try {
            DashboardSnapshot d = connector.getDashboardService().getRoomSnapshot(user.getRoomId());
            Room room = d.rooms.isEmpty() ? null : d.rooms.get(0);
            roomName.setText(room == null ? "Room " + user.getRoomId() : room.getRoomName());
            roomStatus.setText("Status: " + (room == null ? "Unavailable" : room.getStatus()));
            currentExam.setText(room == null || room.getCurrentExamId() == null ? "No exam assigned" : "Exam ID " + room.getCurrentExamId());
            attendance.setText(formatPercent(d.attendancePercentByRoom.get(user.getRoomId())));
            submissions.setText(formatPercent(d.submissionProgressByRoom.get(user.getRoomId())));
            openIncidents.setText(String.valueOf(d.openIncidents));
            incidentModel.setRowCount(0);
            for (Incident incident : connector.getMonitoringService().getIncidentsByRoom(user.getRoomId())) incidentModel.addRow(new Object[]{incident.getStatus(), incident.getSeverity(), incident.getType(), incident.getDescription()});
            eventModel.setRowCount(0);
            for (ExamEvent event : d.recentEvents) eventModel.addRow(new Object[]{event.getEventTimestamp(), event.getEventType(), event.getDescription()});
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to load server data: " + exception.getMessage(), "Dashboard unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatPercent(Double value) { return value == null ? "0%" : value + "%"; }

    private void report() {
        if (user.getRoomId() == null) return;
        String description = JOptionPane.showInputDialog(this, "Describe the incident:");
        if (description == null || description.isBlank()) return;
        try {
            Incident incident = new Incident(); incident.setRoomId(user.getRoomId()); incident.setType("GENERAL"); incident.setSeverity("MEDIUM"); incident.setDescription(description);
            connector.getMonitoringService().reportIncident(incident);
            load();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Incident failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
