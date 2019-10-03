package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;

public class CharacterLook implements PacketInput {
   private MapleClient target;

   private MapleCharacter reference;

   public CharacterLook(MapleClient target, MapleCharacter reference) {
      this.target = target;
      this.reference = reference;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_CHAR_LOOK;
   }

   public MapleClient getTarget() {
      return target;
   }

   public MapleCharacter getReference() {
      return reference;
   }
}
