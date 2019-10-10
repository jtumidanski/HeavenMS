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
      registry.setHandler(SetCard.class, packet -> create(SendOpcode.MONSTER_BOOK_SET_CARD, this::addCard, packet, 11));
      registry.setHandler(ChangeCover.class, packet -> create(SendOpcode.MONSTER_BOOK_SET_COVER, this::changeCover, packet, 6));
   }

   protected void addCard(MaplePacketLittleEndianWriter writer, SetCard packet) {
      writer.write(packet.full() ? 0 : 1);
      writer.writeInt(packet.cardId());
      writer.writeInt(packet.level());
   }

   protected void changeCover(MaplePacketLittleEndianWriter writer, ChangeCover packet) {
      writer.writeInt(packet.cardId());
   }
}