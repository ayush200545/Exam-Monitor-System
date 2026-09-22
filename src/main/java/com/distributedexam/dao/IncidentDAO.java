package com.distributedexam.dao;
import com.distributedexam.common.Incident; import java.util.List;
public interface IncidentDAO { void create(Incident incident) throws Exception; void update(Incident incident) throws Exception; Incident findById(int id) throws Exception; List<Incident> findOpen() throws Exception; List<Incident> findByRoom(int roomId) throws Exception; }
