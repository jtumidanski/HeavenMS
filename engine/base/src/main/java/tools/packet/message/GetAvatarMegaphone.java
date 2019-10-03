package tools.packet.message;

import java.util.List;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class GetAvatarMegaphone implements PacketInput {
   private MapleCharacter character;

   private String medal;

   private int channel;

   private int itemId;

   private List<String> messages;

   private boolean ear;

   public GetAvatarMegaphone(MapleCharacter character, String medal, int channel, int itemId, List<String> messages, boolean ear) {
      this.character = character;
      this.medal = medal;
      this.channel = channel;
      this.itemId = itemId;
      this.messages = messages;
      this.ear = ear;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_AVATAR_MEGAPHONE;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public String getMedal() {
      return medal;
   }

   public int getChannel() {
      return channel;
   }

   public int getItemId() {
      return itemId;
   }

   public List<String> getMessages() {
      return messages;
   }

   public boolean isEar() {
      return ear;
   }
}
