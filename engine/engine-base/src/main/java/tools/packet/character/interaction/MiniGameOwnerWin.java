package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class MiniGameOwnerWin implements PacketInput {
   private MapleMiniGame game;

   private boolean forfeit;

   public MiniGameOwnerWin(MapleMiniGame game, boolean forfeit) {
      this.game = game;
      this.forfeit = forfeit;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleMiniGame getGame() {
      return game;
   }

   public boolean isForfeit() {
      return forfeit;
   }
}
