package net.server.channel.processor;

import constants.ServerConstants;

public class WeddingProcessor {
   private static WeddingProcessor ourInstance = new WeddingProcessor();

   public static WeddingProcessor getInstance() {
      return ourInstance;
   }

   private WeddingProcessor() {
   }

   public long getRelativeWeddingTicketExpireTime(int resSlot) {
      return (resSlot * ServerConstants.WEDDING_RESERVATION_INTERVAL * 60 * 1000);
   }
}
