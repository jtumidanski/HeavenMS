package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MaplePlayerNPC;
import tools.packet.PacketInput;

public class SpawnPlayerNPC implements PacketInput {
   private final MaplePlayerNPC playerNPC;

   public SpawnPlayerNPC(MaplePlayerNPC playerNPC) {
      this.playerNPC = playerNPC;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER;
   }

   public MaplePlayerNPC getPlayerNPC() {
      return playerNPC;
   }
}
