package tools.packet.factory;

import server.maps.MaplePlayerShop;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.box.AddMatchCard;
import tools.packet.character.box.AddOmokBox;
import tools.packet.character.box.RemoveMiniGameBox;
import tools.packet.character.box.RemovePlayerShop;
import tools.packet.character.box.UpdatePlayerShopBox;
import tools.packet.character.box.UseChalkboard;

public class UpdateCharacterBoxPacketFactory extends AbstractPacketFactory {
   private static UpdateCharacterBoxPacketFactory instance;

   public static UpdateCharacterBoxPacketFactory getInstance() {
      if (instance == null) {
         instance = new UpdateCharacterBoxPacketFactory();
      }
      return instance;
   }

   private UpdateCharacterBoxPacketFactory() {
      Handler.handle(AddOmokBox.class).decorate(this::addOmokBox).register(registry);
      Handler.handle(AddMatchCard.class).decorate(this::addMatchCardBox).register(registry);
      Handler.handle(RemoveMiniGameBox.class).decorate(this::removeMiniGameBox).size(7).register(registry);
      Handler.handle(UpdatePlayerShopBox.class).decorate(this::updatePlayerShopBox).register(registry);
      Handler.handle(RemovePlayerShop.class).decorate(this::removePlayerShopBox).size(7).register(registry);
      Handler.handle(UseChalkboard.class).decorate(this::useChalkboard).register(registry);
   }

   protected void addOmokBox(MaplePacketLittleEndianWriter writer, AddOmokBox packet) {
      writer.writeInt(packet.getCharacter().getId());
      addAnnounceBox(writer, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
   }

   protected void addMatchCardBox(MaplePacketLittleEndianWriter writer, AddMatchCard packet) {
      writer.writeInt(packet.getCharacter().getId());
      addAnnounceBox(writer, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
   }

   protected void removeMiniGameBox(MaplePacketLittleEndianWriter writer, RemoveMiniGameBox packet) {
      writer.writeInt(packet.characterId());
      writer.write(0);
   }

   protected void updatePlayerShopBox(MaplePacketLittleEndianWriter writer, UpdatePlayerShopBox packet) {
      writer.writeInt(packet.getPlayerShop().getOwner().getId());
      updatePlayerShopBoxInfo(writer, packet.getPlayerShop());
   }

   protected void updatePlayerShopBoxInfo(final MaplePacketLittleEndianWriter writer, MaplePlayerShop shop) {
      byte[] roomInfo = shop.getShopRoomInfo();
      writer.write(4);
      writer.writeInt(shop.objectId());
      writer.writeMapleAsciiString(shop.getDescription());
      writer.write(0);                 // pw
      writer.write(shop.getItemId() % 100);
      writer.write(roomInfo[0]);       // curPlayers
      writer.write(roomInfo[1]);       // maxPlayers
      writer.write(0);
   }

   protected void removePlayerShopBox(MaplePacketLittleEndianWriter writer, RemovePlayerShop packet) {
      writer.writeInt(packet.characterId());
      writer.write(0);
   }

   protected void useChalkboard(MaplePacketLittleEndianWriter writer, UseChalkboard packet) {
      writer.writeInt(packet.characterId());
      if (packet.close()) {
         writer.write(0);
      } else {
         writer.write(1);
         writer.writeMapleAsciiString(packet.text());
      }
   }
}