package com.distributedexam.server.room;

import com.distributedexam.common.Room;
import com.distributedexam.common.RoomStatus;
import com.distributedexam.dao.RoomDAO;
import com.distributedexam.dao.RoomDAOImpl;
import com.distributedexam.remote.RoomRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RoomManager extends UnicastRemoteObject implements RoomRemote {
    private RoomDAO roomDAO;

    public RoomManager() throws RemoteException {
        super();
        this.roomDAO = new RoomDAOImpl();
    }

    @Override
    public synchronized void createRoom(Room room) throws Exception {
        roomDAO.createRoom(room);
    }

    @Override
    public Room getRoom(int roomId) throws Exception {
        return roomDAO.getRoom(roomId);
    }

    @Override
    public List<Room> getAllRooms() throws Exception {
        return roomDAO.getAllRooms();
    }

    @Override
    public synchronized void updateRoom(Room room) throws Exception {
        roomDAO.updateRoom(room);
    }

    @Override
    public synchronized void assignRoomToExam(int roomId, int examId) throws Exception {
        Room room = roomDAO.getRoom(roomId);
        if (room != null && room.getStatus() == RoomStatus.AVAILABLE) {
            room.setCurrentExamId(examId);
            room.setStatus(RoomStatus.ASSIGNED);
            roomDAO.updateRoom(room);
        } else {
            throw new Exception("Room is not available for assignment.");
        }
    }

    @Override
    public synchronized void unassignRoom(int roomId) throws Exception {
        Room room = roomDAO.getRoom(roomId);
        if (room != null) {
            room.setCurrentExamId(null);
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.updateRoom(room);
        }
    }

    @Override
    public synchronized void setRoomStatus(int roomId, RoomStatus status) throws Exception {
        Room room = roomDAO.getRoom(roomId);
        if (room != null) {
            room.setStatus(status);
            roomDAO.updateRoom(room);
        }
    }

    @Override
    public List<Room> getRoomsForExam(int examId) throws Exception {
        List<Room> allRooms = roomDAO.getAllRooms();
        allRooms.removeIf(r -> r.getCurrentExamId() == null || r.getCurrentExamId() != examId);
        return allRooms;
    }

    @Override
    public List<Room> getAvailableRooms() throws Exception {
        List<Room> allRooms = roomDAO.getAllRooms();
        allRooms.removeIf(r -> r.getStatus() != RoomStatus.AVAILABLE);
        return allRooms;
    }
}
