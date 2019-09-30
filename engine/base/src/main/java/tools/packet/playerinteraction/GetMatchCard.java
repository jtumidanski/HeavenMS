package tools.packet.playerinteraction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class GetMatchCard implements PacketInput {
   private MapleMiniGame miniGame;

   private boolean owner;

   private int piece;

   public GetMatchCard(MapleMiniGame miniGame, boolean owner, int piece) {
      this.miniGame = miniGame;
      this.owner = owner;
      this.piece = piece;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleMiniGame getMiniGame() {
      return miniGame;
   }

   public boolean isOwner() {
      return owner;
   }

   public int getPiece() {
      return piece;
   }
}
