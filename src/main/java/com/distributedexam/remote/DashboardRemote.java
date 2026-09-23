package com.distributedexam.remote; 

import com.distributedexam.common.DashboardSnapshot;
import com.distributedexam.common.RoomDetailSnapshot;
import java.rmi.*;

public interface DashboardRemote extends Remote {
    DashboardSnapshot getCoordinatorSnapshot() throws RemoteException, Exception;
    DashboardSnapshot getRoomSnapshot(int roomId) throws RemoteException, Exception;
    RoomDetailSnapshot getRoomDetails(int roomId) throws RemoteException, Exception;
}
