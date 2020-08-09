package net.server.channel.packet.report;

public class ReportPacket extends BaseReportPacket {
   public ReportPacket(Integer theType, String victim, Integer reason, String description) {
      super(theType, victim, reason, description);
   }
}
