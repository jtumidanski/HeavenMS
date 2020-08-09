package server;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import client.inventory.Item;

public class DueyPackage {
   private final Integer packageId;

   private final Optional<Item> item;

   private String sender;

   private Integer mesos;

   private String message;

   private Calendar timestamp;

   public DueyPackage(Integer packageId, Optional<Item> item) {
      this.packageId = packageId;
      this.item = item;
   }

   public DueyPackage(Integer packageId) {
      this(packageId, Optional.empty());
   }

   public Long sentTimeInMilliseconds() {
      if (timestamp == null) {
         return 0L;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(timestamp.getTime());
      cal.add(Calendar.MONTH, 1);
      return cal.getTimeInMillis();
   }

   public Boolean isDeliveringTime() {
      if (timestamp == null) {
         return false;
      }
      return timestamp.getTimeInMillis() >= System.currentTimeMillis();
   }

   public void setSentTime(Timestamp timestamp, Boolean quick) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(timestamp.getTime());
      if (quick) {
         if (System.currentTimeMillis() - timestamp.getTime() < 24 * 60 * 60 * 1000) {
            cal.add(Calendar.DATE, -1);
         }
      }
      this.timestamp = cal;
   }

   public Integer packageId() {
      return packageId;
   }

   public Optional<Item> item() {
      return item;
   }

   public String sender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }

   public Integer mesos() {
      return mesos;
   }

   public void setMesos(Integer mesos) {
      this.mesos = mesos;
   }

   public String message() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
