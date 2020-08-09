package net.server.channel.packet.alliance;

public class AllianceNoticePacket extends AllianceOperationPacket {
   private final String notice;

   public AllianceNoticePacket(String notice) {
      this.notice = notice;
   }

   public String notice() {
      return notice;
   }
}
