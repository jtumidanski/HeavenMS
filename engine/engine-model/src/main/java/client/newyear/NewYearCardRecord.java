package client.newyear;

import java.util.concurrent.ScheduledFuture;

public class NewYearCardRecord {
   private final int id;

   private final int senderId;

   private final String senderName;

   private final int receiverId;

   private final String receiverName;

   private final String message;

   private final long dateSent;

   private final long dateReceived;

   private final boolean senderDiscardCard;

   private final boolean receiverDiscardCard;

   private final boolean receiverReceivedCard;

   private ScheduledFuture<?> sendTask;

   public NewYearCardRecord(int id, int senderId, String senderName, int receiverId, String receiverName,
                            String message, long dateSent, long dateReceived, boolean senderDiscardCard,
                            boolean receiverDiscardCard, boolean receiverReceivedCard, ScheduledFuture<?> sendTask) {
      this.id = id;
      this.senderId = senderId;
      this.senderName = senderName;
      this.receiverId = receiverId;
      this.receiverName = receiverName;
      this.message = message;
      this.dateSent = dateSent;
      this.dateReceived = dateReceived;
      this.senderDiscardCard = senderDiscardCard;
      this.receiverDiscardCard = receiverDiscardCard;
      this.receiverReceivedCard = receiverReceivedCard;
      this.sendTask = sendTask;
   }

   public NewYearCardRecord(int id, int senderId, String senderName, int receiverId, String receiverName, String message) {
      this(id, senderId, senderName, receiverId, receiverName, message, System.currentTimeMillis(), 0, false, false, false, null);
   }

   public NewYearCardRecord(int senderId, String senderName, int receiverId, String receiverName, String message) {
      this(-1, senderId, senderName, receiverId, receiverName, message);
   }

   public int id() {
      return id;
   }

   public int senderId() {
      return senderId;
   }

   public String senderName() {
      return senderName;
   }

   public int receiverId() {
      return receiverId;
   }

   public String receiverName() {
      return receiverName;
   }

   public String message() {
      return message;
   }

   public Boolean hasSendTask() {
      return sendTask != null;
   }

   public long dateSent() {
      return dateSent;
   }

   public long dateReceived() {
      return dateReceived;
   }

   public boolean senderDiscardCard() {
      return senderDiscardCard;
   }

   public boolean receiverDiscardCard() {
      return receiverDiscardCard;
   }

   public boolean receiverReceivedCard() {
      return receiverReceivedCard;
   }

   public NewYearCardRecord setId(int id) {
      return new NewYearCardRecord(id, senderId, senderName, receiverId, receiverName, message, dateSent, dateReceived,
            senderDiscardCard, receiverDiscardCard, receiverReceivedCard, sendTask);
   }

   public NewYearCardRecord setReceived() {
      return new NewYearCardRecord(id, senderId, senderName, receiverId, receiverName, message, dateSent, System.currentTimeMillis(),
            senderDiscardCard, receiverDiscardCard, true, sendTask);
   }

   public NewYearCardRecord senderDiscard() {
      return new NewYearCardRecord(id, senderId, senderName, receiverId, receiverName, message, dateSent, dateReceived,
            true, receiverDiscardCard, false, sendTask);
   }

   public NewYearCardRecord receiverDiscard() {
      return new NewYearCardRecord(id, senderId, senderName, receiverId, receiverName, message, dateSent, dateReceived,
            senderDiscardCard, true, false, sendTask);
   }

   public void setNewYearCardTask(ScheduledFuture<?> task) {
      sendTask = task;
   }

   public void stopNewYearCardTask() {
      if (sendTask != null) {
         sendTask.cancel(false);
      }
   }
}
