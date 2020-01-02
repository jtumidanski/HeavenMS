package tools.exceptions;

public class EventInstanceInProgressException extends Exception {
   public static String KEY = "Event instance ";

   public EventInstanceInProgressException(String eventName, String eventInstance) {
      super(KEY + "already in progress - " + eventName + ", EM: " + eventInstance);
   }
}
