package net.server.audit.locks.empty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AbstractEmptyLock {
   protected static String printThreadStack(StackTraceElement[] list) {
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getDefault());
      String df = dateFormat.format(new Date());

      StringBuilder s = new StringBuilder("\r\n" + df + "\r\n");
      for (StackTraceElement stackTraceElement : list) {
         s.append("    ").append(stackTraceElement.toString()).append("\r\n");
      }
      s.append("----------------------------\r\n\r\n");

      return s.toString();
   }
}
