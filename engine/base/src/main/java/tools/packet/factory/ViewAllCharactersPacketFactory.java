package tools.packet.factory;

import client.MapleCharacter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.ShowAllCharacter;
import tools.packet.ShowAllCharacterInfo;

public class ViewAllCharactersPacketFactory extends AbstractPacketFactory {
   private static ViewAllCharactersPacketFactory instance;

   public static ViewAllCharactersPacketFactory getInstance() {
      if (instance == null) {
         instance = new ViewAllCharactersPacketFactory();
      }
      return instance;
   }

   private ViewAllCharactersPacketFactory() {
      Handler.handle(ShowAllCharacter.class).decorate(this::showAllCharacter).size(11).register(registry);
      Handler.handle(ShowAllCharacterInfo.class).decorate(this::showAllCharacterInfo).register(registry);
   }

   protected void showAllCharacter(MaplePacketLittleEndianWriter writer, ShowAllCharacter packet) {
      writer.write(packet.chars() > 0 ? 1 : 5); // 2: already connected to server, 3 : unk error (view-all-characters), 5 : cannot find any
      writer.writeInt(packet.chars());
      writer.writeInt(packet.unk());
   }

   protected void showAllCharacterInfo(MaplePacketLittleEndianWriter writer, ShowAllCharacterInfo packet) {
      writer.write(0);
      writer.write(packet.getWorldId());
      writer.write(packet.getCharacterList().size());
      for (MapleCharacter chr : packet.getCharacterList()) {
         addCharEntry(writer, chr, true);
      }
      writer.write(packet.isUsePic() ? 1 : 2);
   }
}