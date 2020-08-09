package net.server.channel.packet.interaction;

public class AnswerTiePlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Boolean answer;

   public AnswerTiePlayerInteractionPacket(Byte mode, Boolean answer) {
      super(mode);
      this.answer = answer;
   }

   public Boolean answer() {
      return answer;
   }
}
