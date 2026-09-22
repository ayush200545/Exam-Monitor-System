package com.exam.client.service;

import com.exam.common.model.ExamEvent;
import com.exam.common.model.EventType;
import com.exam.common.rmi.RemoteMonitoringService;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Buffers events generated at the Room Client if the server is temporarily unreachable.
 * Flushes all pending events once the RMI connection is restored.
 */
public class LocalEventQueue {
    private final String roomId;
    private final Queue<ExamEvent> pendingQueue = new ConcurrentLinkedQueue<>();

    public LocalEventQueue(String roomId) {
        this.roomId = roomId;
    }

    public void queueEvent(EventType type, String description) {
        ExamEvent event = new ExamEvent("LOCAL-" + System.currentTimeMillis(), roomId, type, description);
        pendingQueue.add(event);
        System.out.printf("[%s LocalQueue] Buffered offline event: %s%n", roomId, event);
    }

    public int flushPendingEvents(RemoteMonitoringService service) {
        int count = 0;
        while (!pendingQueue.isEmpty()) {
            ExamEvent event = pendingQueue.peek();
            try {
                service.logEvent(event.getRoomId(), event.getEventType(),
                        "[FLUSHED] " + event.getDescription());
                pendingQueue.poll();
                count++;
            } catch (RemoteException e) {
                System.err.printf("[%s LocalQueue] Flush interrupted: %s%n", roomId, e.getMessage());
                break;
            }
        }
        if (count > 0) {
            System.out.printf("[%s LocalQueue] Successfully flushed %d buffered events.%n", roomId, count);
        }
        return count;
    }

    public int getPendingCount() {
        return pendingQueue.size();
    }
}
