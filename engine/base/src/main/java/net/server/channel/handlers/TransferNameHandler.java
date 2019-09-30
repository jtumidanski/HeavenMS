/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.database.provider.NameChangeProvider;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TransferNamePacket;
import net.server.channel.packet.reader.TransferNameReader;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.cashshop.CashShopMessage;

/**
 * @author Ronan
 */
public final class TransferNameHandler extends AbstractPacketHandler<TransferNamePacket> {
   @Override
   public Class<TransferNameReader> getReaderClass() {
      return TransferNameReader.class;
   }

   @Override
   public void handlePacket(TransferNamePacket packet, MapleClient client) {
      if (!CashOperationHandler.checkBirthday(client, packet.birthday())) {
         PacketCreator.announce(client, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (!ServerConstants.ALLOW_CASHSHOP_NAME_CHANGE) {
         client.announce(MaplePacketCreator.sendNameTransferRules(4));
         return;
      }

      MapleCharacter chr = client.getPlayer();
      if (chr.getLevel() < 10) {
         client.announce(MaplePacketCreator.sendNameTransferRules(4));
         return;
      } else if (client.getTempBanCalendar() != null && client.getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) < Calendar.getInstance().getTimeInMillis()) {
         client.announce(MaplePacketCreator.sendNameTransferRules(2));
         return;
      }

      //sql queries
      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> NameChangeProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()).orElse(null));

      if (completionTime.isEmpty()) {
         client.announce(MaplePacketCreator.sendNameTransferRules(1));
         return;
      }

      if (completionTime.get().getTime() + ServerConstants.NAME_CHANGE_COOLDOWN > System.currentTimeMillis()) {
         client.announce(MaplePacketCreator.sendNameTransferRules(3));
         return;
      }

      client.announce(MaplePacketCreator.sendNameTransferRules(0));

   }
}