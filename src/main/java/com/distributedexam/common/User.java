package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Timestamp;

/** Password hashes are intentionally server-side only. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId; private String username; private UserRole role; private Integer roomId; private Timestamp createdAt;
    public int getUserId() { return userId; } public void setUserId(int value) { userId = value; }
    public String getUsername() { return username; } public void setUsername(String value) { username = value; }
    public UserRole getRole() { return role; } public void setRole(UserRole value) { role = value; }
    public Integer getRoomId() { return roomId; } public void setRoomId(Integer value) { roomId = value; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp value) { createdAt = value; }
}
