package net.server.channel.packet.bbs;

public class DeleteThreadPacket extends BaseBBSOperationPacket {
   private final Integer threadId;

   public DeleteThreadPacket(Byte mode, Integer threadId) {
      super(mode);
      this.threadId = threadId;
   }

   public Integer threadId() {
      return threadId;
   }
}
