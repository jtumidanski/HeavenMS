package tools.packet.reactor;

import net.opcodes.SendOpcode;
import server.maps.MapleReactor;
import tools.packet.PacketInput;

public class SpawnReactor implements PacketInput {
   private final MapleReactor reactor;

   public SpawnReactor(MapleReactor reactor) {
      this.reactor = reactor;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.REACTOR_SPAWN;
   }

   public MapleReactor getReactor() {
      return reactor;
   }
}
