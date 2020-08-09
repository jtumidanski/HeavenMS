package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.messenger.BaseMessengerPacket;
import net.server.channel.packet.messenger.CloseMessenger;
import net.server.channel.packet.messenger.JoinMessengerPacket;
import net.server.channel.packet.messenger.MessengerChat;
import net.server.channel.packet.messenger.MessengerDecline;
import net.server.channel.packet.messenger.MessengerInvite;
import tools.data.input.SeekableLittleEndianAccessor;

public class MessengerReader implements PacketReader<BaseMessengerPacket> {
   @Override
   public BaseMessengerPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      return switch (mode) {
         case 0x00 -> readJoin(accessor, mode);
         case 0x02 -> readClose(mode);
         case 0x03 -> readInvite(accessor, mode);
         case 0x05 -> readDecline(accessor, mode);
         case 0x06 -> readChat(accessor, mode);
         default -> new BaseMessengerPacket(mode);
      };
   }

   private BaseMessengerPacket readChat(SeekableLittleEndianAccessor accessor, byte mode) {
      String input = accessor.readMapleAsciiString();
      return new MessengerChat(mode, input);
   }

   private BaseMessengerPacket readDecline(SeekableLittleEndianAccessor accessor, byte mode) {
      String targeted = accessor.readMapleAsciiString();
      return new MessengerDecline(mode, targeted);
   }

   private BaseMessengerPacket readInvite(SeekableLittleEndianAccessor accessor, byte mode) {
      String input = accessor.readMapleAsciiString();
      return new MessengerInvite(mode, input);
   }

   private BaseMessengerPacket readClose(byte mode) {
      return new CloseMessenger(mode);
   }

   private BaseMessengerPacket readJoin(SeekableLittleEndianAccessor accessor, byte mode) {
      int messengerId = accessor.readInt();
      return new JoinMessengerPacket(mode, messengerId);
   }
}
