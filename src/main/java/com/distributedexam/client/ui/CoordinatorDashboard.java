package com.distributedexam.client.ui;

import com.distributedexam.client.RMIClientConnector;
import com.distributedexam.common.DashboardSnapshot;
import com.distributedexam.common.ExamEvent;
import com.distributedexam.common.Incident;
import com.distributedexam.common.Room;
import com.distributedexam.common.RoomDetailSnapshot;
import com.distributedexam.common.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoordinatorDashboard extends JFrame {
    private static final Color BLUE = new Color(30, 94, 177);
    private final RMIClientConnector connector;
    private final JLabel refreshed = new JLabel("Loading server data…");
    private final JLabel[] metrics = new JLabel[7];
    private final DefaultTableModel roomModel = model("Room ID", "Room", "Location", "Capacity", "Status", "Current Exam", "Action");
    private final DefaultTableModel analyticsModel = model("Metric", "Value");
    private final DefaultTableModel eventModel = model("Time", "Room", "Event", "Description");
    private final JTable roomTable = new JTable(roomModel);
    private final List<Integer> roomIds = new ArrayList<>();

    public CoordinatorDashboard(RMIClientConnector connector, User user) {
        super("Coordinator Dashboard — " + user.getUsername());
        this.connector = connector;
        configureRoomTable();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContent(user));
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        load();
    }

    private void configureRoomTable() {
        roomTable.setRowHeight(28);
        roomTable.setFillsViewportHeight(true);
        roomTable.getTableHeader().setReorderingAllowed(false);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openSelectedRoomDetails();
                }
            }
        });
        roomTable.getColumnModel().getColumn(0).setMinWidth(0);
        roomTable.getColumnModel().getColumn(0).setMaxWidth(0);
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        roomTable.getColumnModel().getColumn(0).setWidth(0);
        roomTable.getColumnModel().getColumn(6).setCellRenderer(new ViewDetailsRenderer());
        roomTable.getColumnModel().getColumn(6).setCellEditor(new ViewDetailsEditor());
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
        panel.add(section("Room Status", new JScrollPane(roomTable), 255));
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
        return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return column == 6 || super.isCellEditable(row, column); } };
    }

    private void load() {
        try {
            DashboardSnapshot d = connector.getDashboardService().getCoordinatorSnapshot();
            int[] values = {d.totalRooms, d.onlineRooms, d.offlineRooms, d.runningExams, d.attendanceCount, d.submissionCount, d.openIncidents};
            for (int i = 0; i < values.length; i++) metrics[i].setText(String.valueOf(values[i]));

            roomIds.clear();
            roomModel.setRowCount(0);
            for (Room room : d.rooms) {
                roomIds.add(room.getRoomId());
                roomModel.addRow(new Object[]{room.getRoomId(), room.getRoomName(), room.getLocation(), room.getCapacity(), room.getStatus(), room.getCurrentExamId() == null ? "—" : room.getCurrentExamId(), "View Details"});
            }

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

    private void openSelectedRoomDetails() {
        Integer roomId = getSelectedRoomIdFromTable();
        if (roomId == null) {
            JOptionPane.showMessageDialog(this, "Please select a room row first.", "Room details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openRoomDetails(roomId);
    }

    private Integer getSelectedRoomIdFromTable() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = roomTable.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= roomIds.size()) {
            return null;
        }
        return roomIds.get(modelRow);
    }

    private void openRoomDetails(int roomId) {
        try {
            RoomDetailSnapshot snapshot = connector.getDashboardService().getRoomDetails(roomId);
            if (snapshot == null) {
                JOptionPane.showMessageDialog(this, "No room data found for room id " + roomId, "Room details", JOptionPane.ERROR_MESSAGE);
                return;
            }
            RoomDetailDialog dialog = new RoomDetailDialog(this, snapshot);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to load room details: " + exception.getMessage(), "Room details", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMap(String label, Map<?, ?> values, String suffix) {
        for (Map.Entry<?, ?> entry : values.entrySet()) analyticsModel.addRow(new Object[]{label + " — " + entry.getKey(), entry.getValue() + suffix});
    }

    private final class ViewDetailsRenderer implements TableCellRenderer {
        private final JButton button = new JButton("View Details");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            button.setOpaque(true);
            button.setBackground(isSelected ? new Color(230, 238, 250) : Color.WHITE);
            button.setForeground(BLUE);
            button.setFocusPainted(false);
            return button;
        }
    }

    private final class ViewDetailsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("View Details");

        private ViewDetailsEditor() {
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                int row = roomTable.getSelectedRow();
                if (row >= 0) {
                    Integer roomId = getSelectedRoomIdFromTable();
                    if (roomId != null) {
                        openRoomDetails(roomId);
                    }
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setBackground(new Color(230, 238, 250));
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "View Details";
        }
    }

    private final class RoomDetailDialog extends JDialog {
        private final RoomDetailSnapshot snapshot;
        private final JLabel roomNameLabel = new JLabel("-");
        private final JLabel statusLabel = new JLabel("-");
        private final JLabel locationLabel = new JLabel("-");
        private final JLabel capacityLabel = new JLabel("-");
        private final JLabel currentExamLabel = new JLabel("-");
        private final JLabel subjectCodeLabel = new JLabel("-");
        private final JLabel examStatusLabel = new JLabel("-");
        private final JLabel assignedStudentLabel = new JLabel("-");
        private final JLabel attendanceLabel = new JLabel("-");
        private final JLabel submissionLabel = new JLabel("-");
        private final JLabel incidentsLabel = new JLabel("-");
        private final DefaultTableModel incidentModel = new DefaultTableModel(new String[]{"Time", "Severity", "Type", "Status", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        private final DefaultTableModel eventModelTable = new DefaultTableModel(new String[]{"Time", "Event", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        private RoomDetailDialog(Window owner, RoomDetailSnapshot snapshot) {
            super(owner, ModalityType.APPLICATION_MODAL);
            this.snapshot = snapshot;
            setTitle("Room Details - " + snapshot.getRoomName());
            setSize(new Dimension(860, 700));
            setMinimumSize(new Dimension(760, 520));
            setLocationRelativeTo(owner);
            setContentPane(createDialogContent());
            refreshDetails();
        }

        private JPanel createDialogContent() {
            JPanel root = new JPanel(new BorderLayout(14, 14));
            root.setBorder(new EmptyBorder(16, 18, 16, 18));
            root.setBackground(new Color(246, 248, 251));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            JLabel title = new JLabel("Room Details");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
            JButton refresh = new JButton("Refresh");
            refresh.addActionListener(e -> refreshDetails());
            header.add(title, BorderLayout.WEST);
            header.add(refresh, BorderLayout.EAST);
            root.add(header, BorderLayout.NORTH);

            JPanel summary = new JPanel(new GridLayout(0, 2, 14, 8));
            summary.setOpaque(false);
            summary.add(infoCell("Room name", roomNameLabel));
            summary.add(infoCell("Status", statusLabel));
            summary.add(infoCell("Location", locationLabel));
            summary.add(infoCell("Capacity", capacityLabel));
            summary.add(infoCell("Current exam", currentExamLabel));
            summary.add(infoCell("Subject code", subjectCodeLabel));
            summary.add(infoCell("Exam status", examStatusLabel));
            summary.add(infoCell("Assigned students", assignedStudentLabel));
            summary.add(infoCell("Attendance", attendanceLabel));
            summary.add(infoCell("Submissions", submissionLabel));
            summary.add(infoCell("Open incidents", incidentsLabel));
            root.add(summary, BorderLayout.CENTER);

            JPanel tables = new JPanel(new GridLayout(1, 2, 12, 0));
            tables.setOpaque(false);
            tables.add(sectionTable("Recent Incidents", incidentModel));
            tables.add(sectionTable("Recent Exam Events", eventModelTable));
            root.add(tables, BorderLayout.SOUTH);
            return root;
        }

        private JPanel infoCell(String label, JLabel value) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(10, 12, 10, 12)));
            JLabel title = new JLabel(label);
            title.setForeground(new Color(92, 102, 115));
            value.setFont(value.getFont().deriveFont(Font.BOLD, 14f));
            value.setForeground(BLUE);
            panel.add(title);
            panel.add(Box.createVerticalStrut(6));
            panel.add(value);
            return panel;
        }

        private JPanel sectionTable(String title, DefaultTableModel model) {
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new CompoundBorder(new LineBorder(new Color(220, 225, 232)), new EmptyBorder(12, 12, 12, 12)));
            JLabel label = new JLabel(title);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
            panel.add(label, BorderLayout.NORTH);
            JTable table = new JTable(model);
            table.setRowHeight(24);
            table.setFillsViewportHeight(true);
            table.getTableHeader().setReorderingAllowed(false);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            return panel;
        }

        private void refreshDetails() {
            try {
                RoomDetailSnapshot live = connector.getDashboardService().getRoomDetails(snapshot.getRoomId());
                if (live == null) {
                    JOptionPane.showMessageDialog(this, "Room data is unavailable.", "Room details", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                roomNameLabel.setText(live.getRoomName());
                statusLabel.setText(live.getStatus());
                locationLabel.setText(live.getLocation() == null ? "—" : live.getLocation());
                capacityLabel.setText(String.valueOf(live.getCapacity()));
                currentExamLabel.setText(live.getCurrentExamId() == null ? "No exam assigned" : (live.getCurrentExamName() == null ? "Exam ID " + live.getCurrentExamId() : live.getCurrentExamName() + " (ID " + live.getCurrentExamId() + ")"));
                subjectCodeLabel.setText(live.getSubjectCode() == null ? "—" : live.getSubjectCode());
                examStatusLabel.setText(live.getExamStatus() == null ? "—" : live.getExamStatus());
                assignedStudentLabel.setText(String.valueOf(live.getAssignedStudentCount()));
                attendanceLabel.setText(live.getAttendancePresent() + " present / " + live.getAttendanceAbsent() + " absent / " + String.format("%.1f%%", live.getAttendancePercent()));
                submissionLabel.setText(live.getSubmissionSubmitted() + " submitted / " + live.getSubmissionLate() + " late / " + live.getSubmissionNotSubmitted() + " not submitted / " + String.format("%.1f%%", live.getSubmissionPercent()));
                incidentsLabel.setText(String.valueOf(live.getOpenIncidents()));

                incidentModel.setRowCount(0);
                for (Incident incident : live.getRecentIncidents()) {
                    incidentModel.addRow(new Object[]{incident.getCreatedAt(), incident.getSeverity(), incident.getType(), incident.getStatus(), incident.getDescription()});
                }

                eventModelTable.setRowCount(0);
                for (ExamEvent event : live.getRecentEvents()) {
                    eventModelTable.addRow(new Object[]{event.getEventTimestamp(), event.getEventType(), event.getDescription()});
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to refresh room details: " + exception.getMessage(), "Refresh failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
