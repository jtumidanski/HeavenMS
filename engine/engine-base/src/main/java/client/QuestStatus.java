package client;

public enum QuestStatus {
   UNDEFINED(-1),
   NOT_STARTED(0),
   STARTED(1),
   COMPLETED(2);
   final int status;

   QuestStatus(int id) {
      status = id;
   }

   public static QuestStatus getById(int id) {
      for (QuestStatus l : QuestStatus.values()) {
         if (l.getId() == id) {
            return l;
         }
      }
      return null;
   }

   public int getId() {
      return status;
   }
}
