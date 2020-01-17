package tools.packet.factory;

import java.util.List;

import client.MapleCharacter;
import config.YamlConfig;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.CharacterList;

public class CharacterListPacketFactory extends AbstractPacketFactory {
   private static CharacterListPacketFactory instance;

   public static CharacterListPacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterListPacketFactory();
      }
      return instance;
   }

   private CharacterListPacketFactory() {
      Handler.handle(CharacterList.class).decorate(this::getCharList).register(registry);
   }

   /**
    * Gets a packet with a list of characters.
    */
   protected void getCharList(MaplePacketLittleEndianWriter writer, CharacterList packet) {
      writer.write(packet.getStatus());
      List<MapleCharacter> chars = packet.getCharacters();
      writer.write((byte) chars.size());
      for (MapleCharacter chr : chars) {
         addCharEntry(writer, chr, false);
      }

      writer.write(YamlConfig.config.server.ENABLE_PIC && packet.cannotBypassPic() ? (packet.getPic() == null || packet.getPic().equals("") ? 0 : 1) : 2);
      writer.writeInt(YamlConfig.config.server.COLLECTIVE_CHARSLOT ? chars.size() + packet.getAvailableCharacterSlots() : packet.getCharacterSlots());
   }
}