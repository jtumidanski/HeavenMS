package tools.packet.reactor;

import net.opcodes.SendOpcode;
import server.maps.MapleReactor;
import tools.packet.PacketInput;

public class DestroyReactor implements PacketInput {
   private final MapleReactor reactor;

   public DestroyReactor(MapleReactor reactor) {
      this.reactor = reactor;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.REACTOR_DESTROY;
   }

   public MapleReactor getReactor() {
      return reactor;
   }
}
