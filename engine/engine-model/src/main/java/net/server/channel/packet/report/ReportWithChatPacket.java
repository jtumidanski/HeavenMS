package net.server.channel.packet.report;

public class ReportWithChatPacket extends BaseReportPacket {
   private final String chatLog;

   public ReportWithChatPacket(Integer theType, String victim, Integer reason, String description, String chatLog) {
      super(theType, victim, reason, description);
      this.chatLog = chatLog;
   }

   public String chatLog() {
      return chatLog;
   }
}
