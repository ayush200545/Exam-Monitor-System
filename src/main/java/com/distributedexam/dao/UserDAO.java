package com.distributedexam.dao;
import com.distributedexam.common.User;
public interface UserDAO { void create(User user, String password) throws Exception; User findByUsername(String username) throws Exception; boolean authenticate(String username, String password) throws Exception; }
