package tools.packet.messenger;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class MessengerAddCharacter implements PacketInput {
   private final String characterNameFrom;

   private final MapleCharacter character;

   private final int position;

   private final int channel;

   public MessengerAddCharacter(String characterNameFrom, MapleCharacter character, int position, int channel) {
      this.characterNameFrom = characterNameFrom;
      this.character = character;
      this.position = position;
      this.channel = channel;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.MESSENGER;
   }

   public String getCharacterNameFrom() {
      return characterNameFrom;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public int getPosition() {
      return position;
   }

   public int getChannel() {
      return channel;
   }
}
