package net.server.channel.processor;

import config.YamlConfig;

public class WeddingProcessor {
   private static WeddingProcessor ourInstance = new WeddingProcessor();

   public static WeddingProcessor getInstance() {
      return ourInstance;
   }

   private WeddingProcessor() {
   }

   public long getRelativeWeddingTicketExpireTime(int resSlot) {
      return (resSlot * YamlConfig.config.server.WEDDING_RESERVATION_INTERVAL * 60 * 1000);
   }
}
