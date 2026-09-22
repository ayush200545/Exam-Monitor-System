package com.distributedexam.remote;

import com.distributedexam.common.Room;
import com.distributedexam.common.RoomStatus;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RoomRemote extends Remote {
    void createRoom(Room room) throws RemoteException, Exception;
    Room getRoom(int roomId) throws RemoteException, Exception;
    List<Room> getAllRooms() throws RemoteException, Exception;
    void updateRoom(Room room) throws RemoteException, Exception;
    void assignRoomToExam(int roomId, int examId) throws RemoteException, Exception;
    void unassignRoom(int roomId) throws RemoteException, Exception;
    void setRoomStatus(int roomId, RoomStatus status) throws RemoteException, Exception;
    List<Room> getRoomsForExam(int examId) throws RemoteException, Exception;
    List<Room> getAvailableRooms() throws RemoteException, Exception;
}
