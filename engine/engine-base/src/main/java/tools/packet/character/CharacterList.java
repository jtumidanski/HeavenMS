package tools.packet.character;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class CharacterList implements PacketInput {
   private List<MapleCharacter> characters;

   private int serverId;

   private int status;

   private boolean cannotBypassPic;

   private String pic;

   private short availableCharacterSlots;

   private short characterSlots;

   public CharacterList(List<MapleCharacter> characters, int serverId, int status, boolean cannotBypassPic, String pic,
                        short availableCharacterSlots, short characterSlots) {
      this.characters = characters;
      this.serverId = serverId;
      this.status = status;
      this.cannotBypassPic = cannotBypassPic;
      this.pic = pic;
      this.availableCharacterSlots = availableCharacterSlots;
      this.characterSlots = characterSlots;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHARACTER_LIST;
   }

   public List<MapleCharacter> getCharacters() {
      return characters;
   }

   public int getServerId() {
      return serverId;
   }

   public int getStatus() {
      return status;
   }

   public boolean cannotBypassPic() {
      return cannotBypassPic;
   }

   public String getPic() {
      return pic;
   }

   public short getAvailableCharacterSlots() {
      return availableCharacterSlots;
   }

   public short getCharacterSlots() {
      return characterSlots;
   }
}
