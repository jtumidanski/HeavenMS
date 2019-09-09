package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.QuestActionPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class QuestActionReader implements PacketReader<QuestActionPacket> {
   @Override
   public QuestActionPacket read(SeekableLittleEndianAccessor accessor) {
      byte action = accessor.readByte();
      short questid = accessor.readShort();
      int itemId = -1;
      int npc = -1;
      int selection = -1;
      int x = -1;
      int y = -1;

      if (action == 0) { // Restore lost item, Credits Darter ( Rajan )
         accessor.readInt();
         itemId = accessor.readInt();
      } else if (action == 1) { //Start Quest
         npc = accessor.readInt();
         if (accessor.available() >= 4) {
            x = accessor.readShort();
            y = accessor.readShort();
         }
      } else if (action == 2) { // Complete Quest
         npc = accessor.readInt();
         if (accessor.available() >= 4) {
            x = accessor.readShort();
            y = accessor.readShort();
         }

         if (accessor.available() >= 2) {
            selection = accessor.readShort();
         }
      } else if (action == 4) { // scripted start quest
         npc = accessor.readInt();
         if (accessor.available() >= 4) {
            x = accessor.readShort();
            y = accessor.readShort();
         }
      } else if (action == 5) { // scripted end quests
         npc = accessor.readInt();
         if (accessor.available() >= 4) {
            x = accessor.readShort();
            y = accessor.readShort();
         }
      }

      return new QuestActionPacket(action, questid, itemId, npc, selection, x, y);
   }
}
