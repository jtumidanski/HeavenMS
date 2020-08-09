package tools.packet.character.npc;

import net.opcodes.SendOpcode;
import server.life.MaplePlayerNPC;
import tools.packet.PacketInput;

public class GetPlayerNPC implements PacketInput {
   private final MaplePlayerNPC playerNPC;

   public GetPlayerNPC(MaplePlayerNPC playerNPC) {
      this.playerNPC = playerNPC;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.IMITATED_NPC_DATA;
   }

   public MaplePlayerNPC getPlayerNPC() {
      return playerNPC;
   }
}
