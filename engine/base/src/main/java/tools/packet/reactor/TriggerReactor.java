package tools.packet.reactor;

import net.opcodes.SendOpcode;
import server.maps.MapleReactor;
import tools.packet.PacketInput;

public class TriggerReactor implements PacketInput {
   private MapleReactor reactor;

   private int stance;

   public TriggerReactor(MapleReactor reactor, int stance) {
      this.reactor = reactor;
      this.stance = stance;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.REACTOR_HIT;
   }

   public MapleReactor getReactor() {
      return reactor;
   }

   public int getStance() {
      return stance;
   }
}
