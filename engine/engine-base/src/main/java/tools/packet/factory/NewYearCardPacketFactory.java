package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.NewYearCardResolution;

public class NewYearCardPacketFactory extends AbstractPacketFactory {
   private static NewYearCardPacketFactory instance;

   public static NewYearCardPacketFactory getInstance() {
      if (instance == null) {
         instance = new NewYearCardPacketFactory();
      }
      return instance;
   }

   private NewYearCardPacketFactory() {
      Handler.handle(NewYearCardResolution.class).decorate(this::onNewYearCardRes).register(registry);
   }

   protected void onNewYearCardRes(MaplePacketLittleEndianWriter writer, NewYearCardResolution packet) {
      writer.write(packet.mode());
      switch (packet.mode()) {
         case 4: // Successfully sent a New Year Card\r\n to %s.
         case 6: // Successfully received a New Year Card.
            encodeNewYearCard(packet.newYearCardRecord(), writer);
            break;
         case 8: // Successfully deleted a New Year Card.
            writer.writeInt(packet.newYearCardRecord().id());
            break;
         case 5: // Nexon's stupid and makes 4 modes do the same operation..
         case 7:
         case 9:
         case 0xB:
            // 0x10: You have no free slot to store card.\r\ntry later on please.
            // 0x11: You have no card to send.
            // 0x12: Wrong inventory information !
            // 0x13: Cannot find such character !
            // 0x14: Incoherent Data !
            // 0x15: An error occured during DB operation.
            // 0x16: An unknown error occured !
            // 0xF: You cannot send a card to yourself !
            writer.write(packet.message());
            break;
         case 0xA:   // GetUnreceivedList_Done
            int nSN = 1;
            writer.writeInt(nSN);
            if ((nSN - 1) <= 98 && nSN > 0) {//lol nexon are you kidding
               for (int i = 0; i < nSN; i++) {
                  writer.writeInt(packet.newYearCardRecord().id());
                  writer.writeInt(packet.newYearCardRecord().senderId());
                  writer.writeMapleAsciiString(packet.newYearCardRecord().senderName());
               }
            }
            break;
         case 0xC:   // NotiArrived
            writer.writeInt(packet.newYearCardRecord().id());
            writer.writeMapleAsciiString(packet.newYearCardRecord().senderName());
            break;
         case 0xD:   // BroadCast_AddCardInfo
            writer.writeInt(packet.newYearCardRecord().id());
            writer.writeInt(packet.characterId());
            break;
         case 0xE:   // BroadCast_RemoveCardInfo
            writer.writeInt(packet.newYearCardRecord().id());
            break;
      }
   }
}