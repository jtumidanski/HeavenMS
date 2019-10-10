package tools.packet.factory;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
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
      registry.setHandler(AddOmokBox.class, packet -> create(SendOpcode.UPDATE_CHAR_BOX, this::addOmokBox, packet));
      registry.setHandler(AddMatchCard.class, packet -> create(SendOpcode.UPDATE_CHAR_BOX, this::addMatchCardBox, packet));
      registry.setHandler(RemoveMiniGameBox.class, packet -> create(SendOpcode.UPDATE_CHAR_BOX, this::removeMinigameBox, packet, 7));
      registry.setHandler(UpdatePlayerShopBox.class, packet -> create(SendOpcode.UPDATE_CHAR_BOX, this::updatePlayerShopBox, packet));
      registry.setHandler(RemovePlayerShop.class, packet -> create(SendOpcode.UPDATE_CHAR_BOX, this::removePlayerShopBox, packet, 7));
      registry.setHandler(UseChalkboard.class, packet -> create(SendOpcode.CHALKBOARD, this::useChalkboard, packet));
   }

   protected void addOmokBox(MaplePacketLittleEndianWriter writer, AddOmokBox packet) {
      writer.writeInt(packet.getCharacter().getId());
      addAnnounceBox(writer, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
   }

   protected void addMatchCardBox(MaplePacketLittleEndianWriter writer, AddMatchCard packet) {
      writer.writeInt(packet.getCharacter().getId());
      addAnnounceBox(writer, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
   }

   protected void removeMinigameBox(MaplePacketLittleEndianWriter writer, RemoveMiniGameBox packet) {
      writer.writeInt(packet.characterId());
      writer.write(0);
   }

   protected void addAnnounceBox(final MaplePacketLittleEndianWriter writer, MapleMiniGame game, int amount, int joinable) {
      writer.write(game.getGameType().getValue());
      writer.writeInt(game.getObjectId()); // gameid/shopid
      writer.writeMapleAsciiString(game.getDescription()); // desc
      writer.writeBool(!game.getPassword().isEmpty());    // password here, thanks GabrielSin!
      writer.write(game.getPieceType());
      writer.write(amount);
      writer.write(2);         //player capacity
      writer.write(joinable);
   }

   protected void updatePlayerShopBox(MaplePacketLittleEndianWriter writer, UpdatePlayerShopBox packet) {
      writer.writeInt(packet.getPlayerShop().getOwner().getId());
      updatePlayerShopBoxInfo(writer, packet.getPlayerShop());
   }

   protected void updatePlayerShopBoxInfo(final MaplePacketLittleEndianWriter writer, MaplePlayerShop shop) {
      byte[] roomInfo = shop.getShopRoomInfo();
      writer.write(4);
      writer.writeInt(shop.getObjectId());
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