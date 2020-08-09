package net.server.channel.packet.bbs;

public class ListThreadsPacket extends BaseBBSOperationPacket {
   private final Integer start;

   public ListThreadsPacket(Byte mode, Integer start) {
      super(mode);
      this.start = start;
   }

   public Integer start() {
      return start;
   }
}
