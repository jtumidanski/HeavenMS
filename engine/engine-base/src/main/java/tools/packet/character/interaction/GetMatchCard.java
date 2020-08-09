package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class GetMatchCard implements PacketInput {
   private final MapleMiniGame miniGame;

   private final boolean owner;

   private final int piece;

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
