package tools.packet.factory;

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
      Handler.handle(SetCard.class).decorate(this::addCard).size(11).register(registry);
      Handler.handle(ChangeCover.class).decorate(this::changeCover).size(6).register(registry);
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