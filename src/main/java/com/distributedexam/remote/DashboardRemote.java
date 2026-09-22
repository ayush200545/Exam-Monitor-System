package com.distributedexam.remote; import com.distributedexam.common.DashboardSnapshot; import java.rmi.*;
public interface DashboardRemote extends Remote { DashboardSnapshot getCoordinatorSnapshot() throws RemoteException, Exception; DashboardSnapshot getRoomSnapshot(int roomId) throws RemoteException, Exception; }
