package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.report.BaseReportPacket;
import net.server.channel.packet.report.ReportPacket;
import net.server.channel.packet.report.ReportWithChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ReportReader implements PacketReader<BaseReportPacket> {
   @Override
   public BaseReportPacket read(SeekableLittleEndianAccessor accessor) {
      int type = accessor.readByte(); //01 = Conversation claim 00 = illegal program
      String victim = accessor.readMapleAsciiString();
      int reason = accessor.readByte();
      String description = accessor.readMapleAsciiString();
      if (type == 0) {
         return new ReportPacket(type, victim, reason, description);
      } else if (type == 1) {
         String chatLog = accessor.readMapleAsciiString();
         return new ReportWithChatPacket(type, victim, reason, description, chatLog);
      }
      return new BaseReportPacket(type, victim, reason, description);
   }
}
