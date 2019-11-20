package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.NPCMoreTalkPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NPCMoreTalkReader implements PacketReader<NPCMoreTalkPacket> {
   @Override
   public NPCMoreTalkPacket read(SeekableLittleEndianAccessor accessor) {
      byte lastMsg = accessor.readByte(); // 00 (last msg type I think)
      byte action = accessor.readByte(); // 00 = end chat, 01 == follow
      String returnText = "";
      int selection = -1;

      if (lastMsg == 2) {
         if (action != 0) {
            returnText = accessor.readMapleAsciiString();
         }
      } else {
         if (accessor.available() >= 4) {
            selection = accessor.readInt();
         } else if (accessor.available() > 0) {
            selection = accessor.readByte();
         }
      }

      return new NPCMoreTalkPacket(lastMsg, action, returnText, selection);
   }
}
