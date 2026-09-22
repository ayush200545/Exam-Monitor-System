package com.distributedexam.dao;

import com.distributedexam.common.Room;
import java.util.List;

public interface RoomDAO {
    void createRoom(Room room) throws Exception;
    Room getRoom(int roomId) throws Exception;
    List<Room> getAllRooms() throws Exception;
    void updateRoom(Room room) throws Exception;
    void deleteRoom(int roomId) throws Exception;
}
