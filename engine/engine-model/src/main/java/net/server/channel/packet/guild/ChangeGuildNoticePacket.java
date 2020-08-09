package net.server.channel.packet.guild;

public class ChangeGuildNoticePacket extends BaseGuildOperationPacket {
   private final String notice;

   public ChangeGuildNoticePacket(Byte theType, String notice) {
      super(theType);
      this.notice = notice;
   }

   public String notice() {
      return notice;
   }
}
