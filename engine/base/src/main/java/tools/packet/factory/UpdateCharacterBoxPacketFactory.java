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
      registry.setHandler(AddOmokBox.class, packet -> this.addOmokBox((AddOmokBox) packet));
      registry.setHandler(AddMatchCard.class, packet -> this.addMatchCardBox((AddMatchCard) packet));
      registry.setHandler(RemoveMiniGameBox.class, packet -> this.removeMinigameBox((RemoveMiniGameBox) packet));
      registry.setHandler(UpdatePlayerShopBox.class, packet -> this.updatePlayerShopBox((UpdatePlayerShopBox) packet));
      registry.setHandler(RemovePlayerShop.class, packet -> this.removePlayerShopBox((RemovePlayerShop) packet));
      registry.setHandler(UseChalkboard.class, packet -> this.useChalkboard((UseChalkboard) packet));
   }

   protected byte[] addOmokBox(AddOmokBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(packet.getCharacter().getId());
      addAnnounceBox(mplew, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
      return mplew.getPacket();
   }

   protected byte[] addMatchCardBox(AddMatchCard packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(packet.getCharacter().getId());
      addAnnounceBox(mplew, packet.getCharacter().getMiniGame(), packet.getAmount(), packet.getType());
      return mplew.getPacket();
   }

   protected byte[] removeMinigameBox(RemoveMiniGameBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0);
      return mplew.getPacket();
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

   protected byte[] updatePlayerShopBox(UpdatePlayerShopBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(packet.getPlayerShop().getOwner().getId());

      updatePlayerShopBoxInfo(mplew, packet.getPlayerShop());
      return mplew.getPacket();
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

   protected byte[] removePlayerShopBox(RemovePlayerShop packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] useChalkboard(UseChalkboard packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHALKBOARD.getValue());
      mplew.writeInt(packet.characterId());
      if (packet.close()) {
         mplew.write(0);
      } else {
         mplew.write(1);
         mplew.writeMapleAsciiString(packet.text());
      }
      return mplew.getPacket();
   }
}