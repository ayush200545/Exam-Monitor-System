package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;
    private int incidentId; private Integer examId; private Integer roomId; private Integer studentId;
    private String type; private String severity; private String description; private String status; private Timestamp createdAt; private Timestamp resolvedAt;
    public int getIncidentId(){return incidentId;} public void setIncidentId(int v){incidentId=v;} public Integer getExamId(){return examId;} public void setExamId(Integer v){examId=v;} public Integer getRoomId(){return roomId;} public void setRoomId(Integer v){roomId=v;} public Integer getStudentId(){return studentId;} public void setStudentId(Integer v){studentId=v;} public String getType(){return type;} public void setType(String v){type=v;} public String getSeverity(){return severity;} public void setSeverity(String v){severity=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;} public Timestamp getResolvedAt(){return resolvedAt;} public void setResolvedAt(Timestamp v){resolvedAt=v;}
}
