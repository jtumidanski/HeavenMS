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
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.database.provider.WorldTransferProvider;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.TransferWorldPacket;
import net.server.channel.packet.reader.TransferWorldReader;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.cashshop.CashShopMessage;

/**
 * @author Ronan
 */
public final class TransferWorldHandler extends AbstractPacketHandler<TransferWorldPacket> {
   @Override
   public Class<TransferWorldReader> getReaderClass() {
      return TransferWorldReader.class;
   }

   @Override
   public void handlePacket(TransferWorldPacket packet, MapleClient client) {
      if (!CashOperationHandler.checkBirthday(client, packet.birthday())) {
         PacketCreator.announce(client, new ShowCashShopMessage(CashShopMessage.CHECK_BIRTHDAY_CODE));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      MapleCharacter chr = client.getPlayer();
      if (!ServerConstants.ALLOW_CASHSHOP_WORLD_TRANSFER || Server.getInstance().getWorldsSize() <= 1) {
         client.announce(MaplePacketCreator.sendWorldTransferRules(9, client));
         return;
      }
      int worldTransferError = chr.checkWorldTransferEligibility();
      if (worldTransferError != 0) {
         client.announce(MaplePacketCreator.sendWorldTransferRules(worldTransferError, client));
         return;
      }

      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> WorldTransferProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()));

      if (completionTime.isEmpty()) {
         client.announce(MaplePacketCreator.sendWorldTransferRules(6, client));
         return;
      }

      if (completionTime.get().getTime() + ServerConstants.WORLD_TRANSFER_COOLDOWN > System.currentTimeMillis()) {
         client.announce(MaplePacketCreator.sendWorldTransferRules(7, client));
         return;
      }

      client.announce(MaplePacketCreator.sendWorldTransferRules(0, client));
   }
}