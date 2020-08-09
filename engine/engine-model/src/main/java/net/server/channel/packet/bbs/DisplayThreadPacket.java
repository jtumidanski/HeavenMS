package net.server.channel.packet.bbs;

public class DisplayThreadPacket extends BaseBBSOperationPacket {
   private final Integer threadId;

   public DisplayThreadPacket(Byte mode, Integer threadId) {
      super(mode);
      this.threadId = threadId;
   }

   public Integer threadId() {
      return threadId;
   }
}
