package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.BaseNoteActionPacket;
import net.server.channel.packet.ClearNotePacket;
import net.server.channel.packet.SendNotePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NoteActionReader implements PacketReader<BaseNoteActionPacket> {
   @Override
   public BaseNoteActionPacket read(SeekableLittleEndianAccessor accessor) {
      int action = accessor.readByte();
      if (action == 0) {
         String characterName = accessor.readMapleAsciiString();
         String message = accessor.readMapleAsciiString();
         return new SendNotePacket(action, characterName, message);
      } else if (action == 1) {
         int num = accessor.readByte();
         accessor.readByte();
         accessor.readByte();
         int[] ids = new int[num];
         for (int i = 0; i < num; i++) {
            ids[i] = accessor.readInt();
            accessor.readByte(); //Fame, but we read it from the database :)
         }

         return new ClearNotePacket(action, ids);
      }
      return new BaseNoteActionPacket(action);
   }
}
