package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MaplePlayerShopSoldItem;
import tools.packet.PacketInput;

public record PlayerShopOwnerUpdate(MaplePlayerShopSoldItem soldItem, Integer position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }
}