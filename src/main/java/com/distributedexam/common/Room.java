package com.distributedexam.common;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int roomId;
    private String roomName;
    private int capacity;
    private String location;
    private RoomStatus status;
    private Integer currentExamId;

    public Room() {}

    public Room(int roomId, String roomName, int capacity, String location, RoomStatus status, Integer currentExamId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
        this.currentExamId = currentExamId;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Integer getCurrentExamId() { return currentExamId; }
    public void setCurrentExamId(Integer currentExamId) { this.currentExamId = currentExamId; }
}
