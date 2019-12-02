/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import java.sql.Timestamp;
import java.util.Calendar;

import client.MapleCharacter;
import client.MapleClient;
import database.administrator.ReportAdministrator;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.ReportReader;
import net.server.channel.packet.report.BaseReportPacket;
import net.server.channel.packet.report.ReportPacket;
import net.server.channel.packet.report.ReportWithChatPacket;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.report.ReportResponse;

/*
 *
 * @author BubblesDev
 */
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
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, packet.victim() + " was reported for: " + packet.description());
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
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, packet.victim() + " was reported for: " + packet.description());
         addReport(client.getPlayer().getId(), CharacterProcessor.getInstance().getIdByName(packet.victim()), packet.reason(), packet.description(), ((ReportWithChatPacket) packet).chatLog());
      } else {
         MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, client.getPlayer().getName() + " is probably packet editing. Got unknown report type, which is impossible.");
      }
   }

   private void addReport(int reporterid, int victimid, int reason, String description, String chatlog) {
      Calendar calendar = Calendar.getInstance();
      Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

      DatabaseConnection.getInstance().withConnection(connection -> ReportAdministrator.getInstance().create(connection, currentTimestamp.toGMTString(), reporterid, victimid, reason, chatlog, description));
   }
}
