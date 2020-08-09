package net.server.login.packet;

import net.server.MaplePacket;

public record AfterLoginPacket(Byte byte1, Byte byte2, String pin) implements MaplePacket {
   public AfterLoginPacket(Byte byte1) {
      this(byte1, (byte) 5, "");
   }

   public AfterLoginPacket(Byte byte1, Byte byte2) {
      this(byte1, byte2, "");
   }
}
