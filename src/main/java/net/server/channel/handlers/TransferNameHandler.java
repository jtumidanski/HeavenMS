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
import net.AbstractMaplePacketHandler;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Ronan
 */
public final class TransferNameHandler extends AbstractMaplePacketHandler {

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      slea.readInt(); //cid
      int birthday = slea.readInt();
      if (!CashOperationHandler.checkBirthday(c, birthday)) {
         c.announce(MaplePacketCreator.showCashShopMessage((byte) 0xC4));
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (!ServerConstants.ALLOW_CASHSHOP_NAME_CHANGE) {
         c.announce(MaplePacketCreator.sendNameTransferRules(4));
         return;
      }

      MapleCharacter chr = c.getPlayer();
      if (chr.getLevel() < 10) {
         c.announce(MaplePacketCreator.sendNameTransferRules(4));
         return;
      } else if (c.getTempBanCalendar() != null && c.getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) < Calendar.getInstance().getTimeInMillis()) {
         c.announce(MaplePacketCreator.sendNameTransferRules(2));
         return;
      }

      //sql queries
      Optional<Timestamp> completionTime = DatabaseConnection.getInstance().withConnectionResult(connection -> NameChangeProvider.getInstance().getCompletionTimeByCharacterId(connection, chr.getId()).orElse(null));

      if (completionTime.isEmpty()) {
         c.announce(MaplePacketCreator.sendNameTransferRules(1));
         return;
      }

      if (completionTime.get().getTime() + ServerConstants.NAME_CHANGE_COOLDOWN > System.currentTimeMillis()) {
         c.announce(MaplePacketCreator.sendNameTransferRules(3));
         return;
      }

      c.announce(MaplePacketCreator.sendNameTransferRules(0));
   }
}