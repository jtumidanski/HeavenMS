package net.server.guild;

import tools.PacketCreator;
import tools.packet.guild.GenericGuildMessage;
import tools.packet.guild.ResponseGuildMessage;

public enum MapleGuildResponse {
   NOT_IN_CHANNEL(0x2a),
   ALREADY_IN_GUILD(0x28),
   NOT_IN_GUILD(0x2d),
   NOT_FOUND_INVITE(0x2e),
   MANAGING_INVITE(0x36),
   DENIED_INVITE(0x37);

   private int value;

   MapleGuildResponse(int val) {
      value = val;
   }

   public final byte[] getPacket(String targetName) {
      if (value >= MANAGING_INVITE.value) {
         return PacketCreator.create(new ResponseGuildMessage((byte) value, targetName));
      } else {
         return PacketCreator.create(new GenericGuildMessage((byte) value));
      }
   }
}
