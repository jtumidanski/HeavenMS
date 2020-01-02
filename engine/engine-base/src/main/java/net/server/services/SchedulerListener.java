package net.server.services;

import java.util.List;

public interface SchedulerListener {
   void removedScheduledEntries(List<Object> entries, boolean update);
}
