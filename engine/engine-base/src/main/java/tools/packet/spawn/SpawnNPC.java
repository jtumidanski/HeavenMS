package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleNPC;
import tools.packet.PacketInput;

public class SpawnNPC implements PacketInput {
   private final MapleNPC npc;

   public SpawnNPC(MapleNPC npc) {
      this.npc = npc;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_NPC;
   }

   public MapleNPC getNpc() {
      return npc;
   }
}
