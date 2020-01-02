package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.newyear.BaseNewYearCardPacket;
import net.server.channel.packet.newyear.CardAcceptedPacket;
import net.server.channel.packet.newyear.CardHasBeenSentPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NewYearCardReader implements PacketReader<BaseNewYearCardPacket> {
   @Override
   public BaseNewYearCardPacket read(SeekableLittleEndianAccessor accessor) {
      byte reqMode = accessor.readByte();                 //[00] -> NewYearReq (0 = Send)
      if (reqMode == 0) {
         short slot = accessor.readShort();                      //[00 2C] -> nPOS (Item Slot Pos)
         int itemId = accessor.readInt();                        //[00 20 F5 E5] -> nItemID (item id)
         String receiver = accessor.readMapleAsciiString();  //[04 00 54 65 73 74] -> sReceiverName (person to send to)
         String message = accessor.readMapleAsciiString();   //[06 00 4C 65 74 74 65 72] -> sContent (message)
         return new CardHasBeenSentPacket(reqMode, slot, itemId, receiver, message);
      } else {
         int cardId = accessor.readInt();
         return new CardAcceptedPacket(reqMode, cardId);
      }
   }
}
