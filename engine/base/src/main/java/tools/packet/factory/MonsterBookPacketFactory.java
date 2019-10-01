package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.monster.book.ChangeCover;
import tools.packet.monster.book.SetCard;

public class MonsterBookPacketFactory extends AbstractPacketFactory {
   private static MonsterBookPacketFactory instance;

   public static MonsterBookPacketFactory getInstance() {
      if (instance == null) {
         instance = new MonsterBookPacketFactory();
      }
      return instance;
   }

   private MonsterBookPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SetCard) {
         return create(this::addCard, packetInput);
      } else if (packetInput instanceof ChangeCover) {
         return create(this::changeCover, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] addCard(SetCard packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.MONSTER_BOOK_SET_CARD.getValue());
      mplew.write(packet.full() ? 0 : 1);
      mplew.writeInt(packet.cardId());
      mplew.writeInt(packet.level());
      return mplew.getPacket();
   }

   protected byte[] changeCover(ChangeCover packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.MONSTER_BOOK_SET_COVER.getValue());
      mplew.writeInt(packet.cardId());
      return mplew.getPacket();
   }
}