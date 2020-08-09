package tools.packet.character.interaction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class MerchantVisitorAdd implements PacketInput {
   private final MapleCharacter character;

   private final int slot;

   public MerchantVisitorAdd(MapleCharacter character, int slot) {
      this.character = character;
      this.slot = slot;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public int getSlot() {
      return slot;
   }
}
