package tools.packet.factory;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
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
      registry.setHandler(ShowAllCharacter.class, packet -> create(SendOpcode.VIEW_ALL_CHAR, this::showAllCharacter, packet, 11));
      registry.setHandler(ShowAllCharacterInfo.class, packet -> create(SendOpcode.VIEW_ALL_CHAR, this::showAllCharacterInfo, packet));
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