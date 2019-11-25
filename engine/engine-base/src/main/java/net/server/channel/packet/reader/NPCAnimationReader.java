package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.npc.BaseNPCAnimationPacket;
import net.server.channel.packet.npc.NPCMovePacket;
import net.server.channel.packet.npc.NPCTalkPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NPCAnimationReader implements PacketReader<BaseNPCAnimationPacket> {
   @Override
   public BaseNPCAnimationPacket read(SeekableLittleEndianAccessor accessor) {
      int length = (int) accessor.available();
      if (length == 6) {
         return new NPCTalkPacket(length, accessor.readInt(), accessor.readByte(), accessor.readByte());
      } else if (length > 6) { // NPC Move
         byte[] bytes = accessor.read(length - 9);
         return new NPCMovePacket(length, bytes);
      }
      return new BaseNPCAnimationPacket(length);
   }
}
