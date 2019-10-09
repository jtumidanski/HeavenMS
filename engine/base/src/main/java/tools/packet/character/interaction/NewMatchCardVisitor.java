package tools.packet.character.interaction;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class NewMatchCardVisitor implements PacketInput {
   private MapleMiniGame miniGame;

   private MapleCharacter character;

   private int slot;

   public NewMatchCardVisitor(MapleMiniGame miniGame, MapleCharacter character, int slot) {
      this.miniGame = miniGame;
      this.character = character;
      this.slot = slot;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleMiniGame getMiniGame() {
      return miniGame;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public int getSlot() {
      return slot;
   }
}