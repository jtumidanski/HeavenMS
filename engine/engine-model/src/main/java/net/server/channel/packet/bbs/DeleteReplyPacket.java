package net.server.channel.packet.bbs;

public class DeleteReplyPacket extends BaseBBSOperationPacket {
   private final Integer replyId;

   public DeleteReplyPacket(Byte mode, Integer replyId) {
      super(mode);
      this.replyId = replyId;
   }

   public Integer replyId() {
      return replyId;
   }
}
