package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(SetCard.class, packet -> this.addCard((SetCard) packet));
      registry.setHandler(ChangeCover.class, packet -> this.changeCover((ChangeCover) packet));
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