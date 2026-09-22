package com.distributedexam.server.monitoring; import com.distributedexam.common.ExamEvent; import com.distributedexam.dao.*; import java.util.*;
public class EventManager { private final EventDAO dao; public EventManager(EventDAO dao){this.dao=dao;} public void log(ExamEvent e)throws Exception{dao.create(e);} public List<ExamEvent> recent(int n)throws Exception{return dao.findRecent(n);} }
