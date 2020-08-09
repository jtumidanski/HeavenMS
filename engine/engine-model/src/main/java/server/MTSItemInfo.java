package server;

import java.util.Calendar;

import client.inventory.Item;

public record MTSItemInfo(Item item, Integer price, Integer id, Integer characterId, String seller, String date) {
   public Integer year() {
      return Integer.parseInt(date.substring(0, 4));
   }

   public Integer month() {
      return Integer.parseInt(date.substring(5, 7));
   }

   public Integer day() {
      return Integer.parseInt(date.substring(8, 10));
   }

   public Integer taxes() {
      return 100 + price / 10;
   }

   public Long endingDate() {
      Calendar now = Calendar.getInstance();
      now.set(year(), month() - 1, day());
      return now.getTimeInMillis();
   }
}
