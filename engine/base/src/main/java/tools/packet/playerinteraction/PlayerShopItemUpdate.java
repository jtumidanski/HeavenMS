package tools.packet.playerinteraction;

import net.opcodes.SendOpcode;
import server.maps.MaplePlayerShop;
import tools.packet.PacketInput;

public class PlayerShopItemUpdate implements PacketInput {
   private MaplePlayerShop playerShop;

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
