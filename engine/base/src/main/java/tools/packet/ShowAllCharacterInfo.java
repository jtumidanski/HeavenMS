package tools.packet;

import java.util.List;

import client.MapleCharacter;
import net.opcodes.SendOpcode;

public class ShowAllCharacterInfo implements PacketInput {
   private int worldId;

   private List<MapleCharacter> characterList;

   private boolean usePic;

   public ShowAllCharacterInfo(int worldId, List<MapleCharacter> characterList, boolean usePic) {
      this.worldId = worldId;
      this.characterList = characterList;
      this.usePic = usePic;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.VIEW_ALL_CHAR;
   }

   public int getWorldId() {
      return worldId;
   }

   public List<MapleCharacter> getCharacterList() {
      return characterList;
   }

   public boolean isUsePic() {
      return usePic;
   }
}
