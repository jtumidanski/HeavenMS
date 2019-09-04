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
import net.AbstractMaplePacketHandler;
import net.server.Server;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Ronan
 */
public final class TransferWorldHandler extends AbstractMaplePacketHandler {

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      slea.readInt(); //cid
      int birthday = slea.readInt();
      if (!CashOperationHandler.checkBirthday(c, birthday)) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC4));
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      MapleCharacter chr = c.getPlayer();
      if (!ServerConstants.ALLOW_CASHSHOP_WORLD_TRANSFER || Server.getInstance().getWorldsSize() <= 1) {
         c.announce(MaplePacketCreator.sendWorldTransferRules(9, c));
         return;
      }
      int worldTransferError = chr.checkWorldTransferEligibility();
      if (worldTransferError != 0) {
         c.announce(MaplePacketCreator.sendWorldTransferRules(worldTransferError, c));
         return;
      }

      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> WorldTransferProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()));

      if (completionTime.isEmpty()) {
         c.announce(MaplePacketCreator.sendWorldTransferRules(6, c));
         return;
      }

      if (completionTime.get().getTime() + ServerConstants.WORLD_TRANSFER_COOLDOWN > System.currentTimeMillis()) {
         c.announce(MaplePacketCreator.sendWorldTransferRules(7, c));
         return;
      }

      c.announce(MaplePacketCreator.sendWorldTransferRules(0, c));
   }
}