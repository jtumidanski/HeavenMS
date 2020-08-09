package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.maps.MapleDragon;
import tools.packet.PacketInput;

public class SpawnDragon implements PacketInput {
   private final MapleDragon dragon;

   public SpawnDragon(MapleDragon dragon) {
      this.dragon = dragon;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_DRAGON;
   }

   public MapleDragon getDragon() {
      return dragon;
   }
}
