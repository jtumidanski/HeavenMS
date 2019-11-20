package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.maps.MapleHiredMerchant;
import tools.packet.PacketInput;

public class SpawnHiredMerchant implements PacketInput {
   private MapleHiredMerchant hiredMerchant;

   public SpawnHiredMerchant(MapleHiredMerchant hiredMerchant) {
      this.hiredMerchant = hiredMerchant;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_HIRED_MERCHANT;
   }

   public MapleHiredMerchant getHiredMerchant() {
      return hiredMerchant;
   }
}
