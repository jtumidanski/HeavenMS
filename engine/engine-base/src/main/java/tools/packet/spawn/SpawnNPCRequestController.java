package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleNPC;
import tools.packet.PacketInput;

public class SpawnNPCRequestController implements PacketInput {
   private final MapleNPC npc;

   private final boolean miniMap;

   public SpawnNPCRequestController(MapleNPC npc, boolean miniMap) {
      this.npc = npc;
      this.miniMap = miniMap;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER;
   }

   public MapleNPC getNpc() {
      return npc;
   }

   public boolean isMiniMap() {
      return miniMap;
   }
}
