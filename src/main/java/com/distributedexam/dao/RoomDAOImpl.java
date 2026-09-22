package com.distributedexam.dao;

import com.distributedexam.common.Room;
import com.distributedexam.common.RoomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements RoomDAO {
    @Override
    public void createRoom(Room room) throws Exception {
        String query = "INSERT INTO rooms (room_name, capacity, location, status, current_exam_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getLocation());
            stmt.setString(4, room.getStatus().name());
            if (room.getCurrentExamId() != null) {
                stmt.setInt(5, room.getCurrentExamId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setRoomId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Room getRoom(int roomId) throws Exception {
        String query = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractRoomFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Room> getAllRooms() throws Exception {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) rooms.add(extractRoomFromResultSet(rs));
        }
        return rooms;
    }

    @Override
    public void updateRoom(Room room) throws Exception {
        String query = "UPDATE rooms SET room_name = ?, capacity = ?, location = ?, status = ?, current_exam_id = ? WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getLocation());
            stmt.setString(4, room.getStatus().name());
            if (room.getCurrentExamId() != null) {
                stmt.setInt(5, room.getCurrentExamId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, room.getRoomId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteRoom(int roomId) throws Exception {
        String query = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        }
    }
    
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomName(rs.getString("room_name"));
        room.setCapacity(rs.getInt("capacity"));
        room.setLocation(rs.getString("location"));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        
        int currentExamId = rs.getInt("current_exam_id");
        if (rs.wasNull()) {
            room.setCurrentExamId(null);
        } else {
            room.setCurrentExamId(currentExamId);
        }
        return room;
    }
}
