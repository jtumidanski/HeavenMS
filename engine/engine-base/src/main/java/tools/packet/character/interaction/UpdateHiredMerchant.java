package tools.packet.character.interaction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.maps.MapleHiredMerchant;
import tools.packet.PacketInput;

public class UpdateHiredMerchant implements PacketInput {
   private final MapleCharacter character;

   private final MapleHiredMerchant hiredMerchant;

   public UpdateHiredMerchant(MapleCharacter character, MapleHiredMerchant hiredMerchant) {
      this.character = character;
      this.hiredMerchant = hiredMerchant;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public MapleHiredMerchant getHiredMerchant() {
      return hiredMerchant;
   }
}
