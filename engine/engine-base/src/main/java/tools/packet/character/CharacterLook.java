package tools.packet.character;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class CharacterLook implements PacketInput {
   private final MapleClient target;

   private final MapleCharacter reference;

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
