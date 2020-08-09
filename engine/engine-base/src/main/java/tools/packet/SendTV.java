package tools.packet;

import java.util.List;

import client.MapleCharacter;
import net.opcodes.SendOpcode;

public class SendTV implements PacketInput {
   private final MapleCharacter character;

   private final List<String> messages;

   private final int type;

   private final MapleCharacter partner;

   public SendTV(MapleCharacter character, List<String> messages, int type, MapleCharacter partner) {
      this.character = character;
      this.messages = messages;
      this.type = type;
      this.partner = partner;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SEND_TV;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public List<String> getMessages() {
      return messages;
   }

   public int getType() {
      return type;
   }

   public MapleCharacter getPartner() {
      return partner;
   }
}
