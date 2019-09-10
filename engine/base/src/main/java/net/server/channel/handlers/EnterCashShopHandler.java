/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.maps.MapleMiniDungeonInfo;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author Flav
 */
public class EnterCashShopHandler extends AbstractPacketHandler<NoOpPacket, NoOpReader> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      try {
         MapleCharacter mc = client.getPlayer();

         if (mc.cannotEnterCashShop()) {
            client.announce(MaplePacketCreator.enableActions());
            return;
         }

         if (mc.getEventInstance() != null) {
            MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.PINK_TEXT, "Entering Cash Shop or MTS are disabled when registered on an event.");
            client.announce(MaplePacketCreator.enableActions());
            return;
         }

         if (MapleMiniDungeonInfo.isDungeonMap(mc.getMapId())) {
            MessageBroadcaster.getInstance().sendServerNotice(mc, ServerNoticeType.PINK_TEXT, "Changing channels or entering Cash Shop or MTS are disabled when inside a Mini-Dungeon.");
            client.announce(MaplePacketCreator.enableActions());
            return;
         }

         if (mc.getCashShop().isOpened()) {
            return;
         }

         mc.closePlayerInteractions();
         mc.closePartySearchInteractions();

         mc.unregisterChairBuff();
         Server.getInstance().getPlayerBuffStorage().addBuffsToStorage(mc.getId(), mc.getAllBuffs());
         Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(mc.getId(), mc.getAllDiseases());
         mc.setAwayFromChannelWorld();
         mc.notifyMapTransferToPartner(-1);
         mc.removeIncomingInvites();
         mc.cancelAllBuffs(true);
         mc.cancelAllDebuffs();
         mc.cancelBuffExpireTask();
         mc.cancelDiseaseExpireTask();
         mc.cancelSkillCooldownTask();
         mc.cancelExpirationTask();

         mc.forfeitExpirableQuests();
         mc.cancelQuestExpirationTask();

         client.announce(MaplePacketCreator.openCashShop(client, false));
         client.announce(MaplePacketCreator.showCashInventory(client));
         client.announce(MaplePacketCreator.showGifts(mc.getCashShop().loadGifts()));
         client.announce(MaplePacketCreator.showWishList(mc, false));
         client.announce(MaplePacketCreator.showCash(mc));

         client.getChannelServer().removePlayer(mc);
         mc.getMap().removePlayer(mc);
         mc.getCashShop().open(true);
         mc.saveCharToDB();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
