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

import java.awt.Point;
import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.ItemFactory;
import constants.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MaplePlayerShop;
import server.maps.MaplePortal;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author XoticStory
 */
public final class HiredMerchantRequest extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      try {
         for (MapleMapObject mmo : chr.getMap().getMapObjectsInRange(chr.getPosition(), 23000, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT, MapleMapObjectType.PLAYER))) {
            if (mmo instanceof MapleCharacter) {
               MapleCharacter mc = (MapleCharacter) mmo;

               MaplePlayerShop shop = mc.getPlayerShop();
               if (shop != null && shop.isOwner(mc)) {
                  chr.announce(MaplePacketCreator.getMiniRoomError(13));
                  return;
               }
            } else {
               chr.announce(MaplePacketCreator.getMiniRoomError(13));
               return;
            }
         }

         Point cpos = chr.getPosition();
         MaplePortal portal = chr.getMap().findClosestTeleportPortal(cpos);
         if (portal != null && portal.getPosition().distance(cpos) < 120.0) {
            chr.announce(MaplePacketCreator.getMiniRoomError(10));
            return;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (GameConstants.isFreeMarketRoom(chr.getMapId())) {
         if (!chr.hasMerchant()) {
            if (ItemFactory.MERCHANT.loadItems(chr.getId(), false).isEmpty() && chr.getMerchantMeso() == 0) {
               client.announce(MaplePacketCreator.hiredMerchantBox());
            } else {
               chr.announce(MaplePacketCreator.retrieveFirstMessage());
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You already have a store open.");
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You cannot open your hired merchant here.");
      }
   }
}
