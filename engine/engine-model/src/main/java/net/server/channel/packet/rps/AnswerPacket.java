package net.server.channel.packet.rps;

public class AnswerPacket extends BaseRPSActionPacket {
   private final Byte answer;

   public AnswerPacket(Boolean available, Byte mode, Byte answer) {
      super(available, mode);
      this.answer = answer;
   }

   public Byte answer() {
      return answer;
   }
}
