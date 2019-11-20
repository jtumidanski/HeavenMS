package tools.packet.guild;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class ShowGuildInfo implements PacketInput {
   private MapleCharacter character;

   public ShowGuildInfo(MapleCharacter character) {
      this.character = character;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }
}
