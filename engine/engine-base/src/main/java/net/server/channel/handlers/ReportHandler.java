package net.server.channel.handlers;

import java.sql.Timestamp;
import java.util.Calendar;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.CharacterProcessor;
import database.DatabaseConnection;
import database.administrator.ReportAdministrator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.ReportReader;
import net.server.channel.packet.report.BaseReportPacket;
import net.server.channel.packet.report.ReportPacket;
import net.server.channel.packet.report.ReportWithChatPacket;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.report.ReportResponse;

public final class ReportHandler extends AbstractPacketHandler<BaseReportPacket> {
   @Override
   public Class<ReportReader> getReaderClass() {
      return ReportReader.class;
   }

   @Override
   public void handlePacket(BaseReportPacket packet, MapleClient client) {
      if (packet instanceof ReportPacket) {
         if (client.getPlayer().getPossibleReports() > 0) {
            if (client.getPlayer().getMeso() > 299) {
               client.getPlayer().decreaseReports();
               client.getPlayer().gainMeso(-300, true);
            } else {
               PacketCreator.announce(client, new ReportResponse((byte) 4));
               return;
            }
         } else {
            PacketCreator.announce(client, new ReportResponse((byte) 2));
            return;
         }
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, I18nMessage.from("WAS_REPORTED_FOR").with(packet.victim(), packet.description()));
         addReport(client.getPlayer().getId(), CharacterProcessor.getInstance().getIdByName(packet.victim()), 0, packet.description(), null);
      } else if (packet instanceof ReportWithChatPacket) {
         if (((ReportWithChatPacket) packet).chatLog() == null) {
            return;
         }
         if (client.getPlayer().getPossibleReports() > 0) {
            if (client.getPlayer().getMeso() > 299) {
               client.getPlayer().decreaseReports();
               client.getPlayer().gainMeso(-300, true);
            } else {
               PacketCreator.announce(client, new ReportResponse((byte) 4));
               return;
            }
         }
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, I18nMessage.from("WAS_REPORTED_FOR").with(packet.victim(), packet.description()));
         addReport(client.getPlayer().getId(), CharacterProcessor.getInstance().getIdByName(packet.victim()), packet.reason(), packet.description(), ((ReportWithChatPacket) packet).chatLog());
      } else {
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, I18nMessage.from("PROBABLY_PACKET_EDITING").with(client.getPlayer().getName()));
      }
   }

   private void addReport(int reporterId, int victimId, int reason, String description, String chatLog) {
      Calendar calendar = Calendar.getInstance();
      Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

      DatabaseConnection.getInstance().withConnection(connection -> ReportAdministrator.getInstance().create(connection, currentTimestamp.toGMTString(), reporterId, victimId, reason, chatLog, description));
   }
}
