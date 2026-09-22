package com.distributedexam.server.auth;
import com.distributedexam.common.User; import com.distributedexam.dao.*; import com.distributedexam.remote.AuthRemote; import java.rmi.*; import java.rmi.server.UnicastRemoteObject;
public class AuthManager extends UnicastRemoteObject implements AuthRemote { private final UserDAO users=new UserDAOImpl(); public AuthManager()throws RemoteException{} public User login(String username,String password)throws Exception{if(username==null||username.isBlank()||!users.authenticate(username,password))return null;return users.findByUsername(username);}}
