package com.distributedexam.remote; import com.distributedexam.common.User; import java.rmi.*;
public interface AuthRemote extends Remote { User login(String username, String password) throws RemoteException, Exception; }
