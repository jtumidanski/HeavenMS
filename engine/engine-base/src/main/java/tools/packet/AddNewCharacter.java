package tools.packet;

import client.MapleCharacter;
import net.opcodes.SendOpcode;

public class AddNewCharacter implements PacketInput {
   private final MapleCharacter mapleCharacter;

   public AddNewCharacter(MapleCharacter mapleCharacter) {
      this.mapleCharacter = mapleCharacter;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.ADD_NEW_CHAR_ENTRY;
   }

   public MapleCharacter getMapleCharacter() {
      return mapleCharacter;
   }
}
