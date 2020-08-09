package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MaplePlayerShop;
import tools.packet.PacketInput;

public class GetPlayerShop implements PacketInput {
   private final MaplePlayerShop playerShop;

   private final boolean owner;

   public GetPlayerShop(MaplePlayerShop playerShop, boolean owner) {
      this.playerShop = playerShop;
      this.owner = owner;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MaplePlayerShop getPlayerShop() {
      return playerShop;
   }

   public boolean isOwner() {
      return owner;
   }
}
