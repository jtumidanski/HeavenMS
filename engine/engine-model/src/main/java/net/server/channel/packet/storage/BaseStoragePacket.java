package net.server.channel.packet.storage;

import net.server.MaplePacket;

public class BaseStoragePacket implements MaplePacket {
   private final Byte mode;

   public BaseStoragePacket(Byte mode) {
      this.mode = mode;
   }

   public Byte mode() {
      return mode;
   }
}
