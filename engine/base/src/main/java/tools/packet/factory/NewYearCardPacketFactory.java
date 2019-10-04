package tools.packet.factory;

import client.newyear.NewYearCardRecord;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.NewYearCardResolution;
import tools.packet.PacketInput;

public class NewYearCardPacketFactory extends AbstractPacketFactory {
   private static NewYearCardPacketFactory instance;

   public static NewYearCardPacketFactory getInstance() {
      if (instance == null) {
         instance = new NewYearCardPacketFactory();
      }
      return instance;
   }

   private NewYearCardPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof NewYearCardResolution) {
         return create(this::onNewYearCardRes, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] onNewYearCardRes(NewYearCardResolution packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NEW_YEAR_CARD_RES.getValue());
      mplew.write(packet.getMode());
      switch (packet.getMode()) {
         case 4: // Successfully sent a New Year Card\r\n to %s.
         case 6: // Successfully received a New Year Card.
            encodeNewYearCard(packet.getNewYearCardRecord(), mplew);
            break;
         case 8: // Successfully deleted a New Year Card.
            mplew.writeInt(packet.getNewYearCardRecord().getId());
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
            mplew.write(packet.getMessage());
            break;
         case 0xA:   // GetUnreceivedList_Done
            int nSN = 1;
            mplew.writeInt(nSN);
            if ((nSN - 1) <= 98 && nSN > 0) {//lol nexon are you kidding
               for (int i = 0; i < nSN; i++) {
                  mplew.writeInt(packet.getNewYearCardRecord().getId());
                  mplew.writeInt(packet.getNewYearCardRecord().getSenderId());
                  mplew.writeMapleAsciiString(packet.getNewYearCardRecord().getSenderName());
               }
            }
            break;
         case 0xC:   // NotiArrived
            mplew.writeInt(packet.getNewYearCardRecord().getId());
            mplew.writeMapleAsciiString(packet.getNewYearCardRecord().getSenderName());
            break;
         case 0xD:   // BroadCast_AddCardInfo
            mplew.writeInt(packet.getNewYearCardRecord().getId());
            mplew.writeInt(packet.getCharacter().getId());
            break;
         case 0xE:   // BroadCast_RemoveCardInfo
            mplew.writeInt(packet.getNewYearCardRecord().getId());
            break;
      }
      return mplew.getPacket();
   }

   protected void encodeNewYearCard(NewYearCardRecord newyear, MaplePacketLittleEndianWriter mplew) {
      mplew.writeInt(newyear.getId());
      mplew.writeInt(newyear.getSenderId());
      mplew.writeMapleAsciiString(newyear.getSenderName());
      mplew.writeBool(newyear.isSenderCardDiscarded());
      mplew.writeLong(newyear.getDateSent());
      mplew.writeInt(newyear.getReceiverId());
      mplew.writeMapleAsciiString(newyear.getReceiverName());
      mplew.writeBool(newyear.isReceiverCardDiscarded());
      mplew.writeBool(newyear.isReceiverCardReceived());
      mplew.writeLong(newyear.getDateReceived());
      mplew.writeMapleAsciiString(newyear.getMessage());
   }
}