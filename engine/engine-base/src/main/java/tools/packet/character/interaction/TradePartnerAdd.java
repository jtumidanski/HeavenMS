package tools.packet.character.interaction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class TradePartnerAdd implements PacketInput {
   private final MapleCharacter character;

   public TradePartnerAdd(MapleCharacter character) {
      this.character = character;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleCharacter getCharacter() {
      return character;
   }
}
