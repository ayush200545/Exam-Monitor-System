package com.distributedexam.dao;
import com.distributedexam.common.ExamEvent; import java.util.List;
public interface EventDAO { void create(ExamEvent event) throws Exception; List<ExamEvent> findRecent(int limit) throws Exception; List<ExamEvent> findByRoom(int roomId) throws Exception; }
