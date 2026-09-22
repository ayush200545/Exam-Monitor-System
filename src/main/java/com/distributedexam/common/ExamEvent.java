package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class ExamEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private int eventId; private Integer examId; private Integer roomId; private String eventType; private String description; private Timestamp eventTimestamp; private Integer userId;
    public int getEventId(){return eventId;} public void setEventId(int v){eventId=v;} public Integer getExamId(){return examId;} public void setExamId(Integer v){examId=v;} public Integer getRoomId(){return roomId;} public void setRoomId(Integer v){roomId=v;} public String getEventType(){return eventType;} public void setEventType(String v){eventType=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public Timestamp getEventTimestamp(){return eventTimestamp;} public void setEventTimestamp(Timestamp v){eventTimestamp=v;} public Integer getUserId(){return userId;} public void setUserId(Integer v){userId=v;}
}
