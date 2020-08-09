package tools.packet.character.box;

import net.opcodes.SendOpcode;
import server.maps.MaplePlayerShop;
import tools.packet.PacketInput;

public class UpdatePlayerShopBox implements PacketInput {
   private final MaplePlayerShop playerShop;

   public UpdatePlayerShopBox(MaplePlayerShop playerShop) {
      this.playerShop = playerShop;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_CHAR_BOX;
   }

   public MaplePlayerShop getPlayerShop() {
      return playerShop;
   }
}
