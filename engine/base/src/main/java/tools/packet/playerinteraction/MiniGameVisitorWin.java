package tools.packet.playerinteraction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class MiniGameVisitorWin implements PacketInput {
   private MapleMiniGame game;

   private boolean forfeit;

   public MiniGameVisitorWin(MapleMiniGame game, boolean forfeit) {
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