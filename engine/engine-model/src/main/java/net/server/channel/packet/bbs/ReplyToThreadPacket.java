package net.server.channel.packet.bbs;

public class ReplyToThreadPacket extends BaseBBSOperationPacket {
   private final Integer threadId;

   private final String message;

   public ReplyToThreadPacket(Byte mode, Integer threadId, String message) {
      super(mode);
      this.threadId = threadId;
      this.message = message;
   }

   public Integer threadId() {
      return threadId;
   }

   public String message() {
      return message;
   }
}
