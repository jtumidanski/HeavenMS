package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import server.maps.MapleMiniGame;
import tools.packet.PacketInput;

public class MiniGameTie implements PacketInput {
   private final MapleMiniGame game;

   public MiniGameTie(MapleMiniGame game) {
      this.game = game;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }

   public MapleMiniGame getGame() {
      return game;
   }
}