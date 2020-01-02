package net.server.channel.handlers;

import client.MapleClient;
import net.opcodes.SendOpcode;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.npc.BaseNPCAnimationPacket;
import net.server.channel.packet.npc.NPCMovePacket;
import net.server.channel.packet.npc.NPCTalkPacket;
import net.server.channel.packet.reader.NPCAnimationReader;
import tools.data.output.MaplePacketLittleEndianWriter;

public final class NPCAnimationHandler extends AbstractPacketHandler<BaseNPCAnimationPacket> {
   @Override
   public Class<NPCAnimationReader> getReaderClass() {
      return NPCAnimationReader.class;
   }

   @Override
   public void handlePacket(BaseNPCAnimationPacket packet, MapleClient client) {
      if (client.getPlayer().isChangingMaps()) {
         return;
      }

      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      if (packet instanceof NPCTalkPacket) {
         writer.writeShort(SendOpcode.NPC_ACTION.getValue());
         writer.writeInt(((NPCTalkPacket) packet).first());
         writer.write(((NPCTalkPacket) packet).second());
         writer.write(((NPCTalkPacket) packet).third());
      } else if (packet instanceof NPCMovePacket) {
         writer.writeShort(SendOpcode.NPC_ACTION.getValue());
         writer.write(((NPCMovePacket) packet).movement());
      }
      client.announce(writer.getPacket());
   }
}
