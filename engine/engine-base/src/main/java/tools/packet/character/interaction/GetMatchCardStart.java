package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class GetMatchCardStart implements PacketInput {
   private final MapleMiniGame miniGame;

   private final int loser;

   public GetMatchCardStart(MapleMiniGame miniGame, int loser) {
      this.miniGame = miniGame;
      this.loser = loser;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleMiniGame getMiniGame() {
      return miniGame;
   }

   public int getLoser() {
      return loser;
   }
}
