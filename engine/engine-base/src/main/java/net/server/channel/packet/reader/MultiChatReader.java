package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MultiChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MultiChatReader implements PacketReader<MultiChatPacket> {
   @Override
   public MultiChatPacket read(SeekableLittleEndianAccessor accessor) {
      int type = accessor.readByte(); // 0 for buddies, 1 for parties
      int numRecipients = accessor.readByte();
      int[] recipients = new int[numRecipients];
      for (int i = 0; i < numRecipients; i++) {
         recipients[i] = accessor.readInt();
      }
      String message = accessor.readMapleAsciiString();
      return new MultiChatPacket(type, numRecipients, recipients, message);
   }
}
