package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MaplePlayerShop;
import tools.packet.PacketInput;

public class PlayerShopItemUpdate implements PacketInput {
   private final MaplePlayerShop playerShop;

   public PlayerShopItemUpdate(MaplePlayerShop playerShop) {
      this.playerShop = playerShop;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MaplePlayerShop getPlayerShop() {
      return playerShop;
   }
}
