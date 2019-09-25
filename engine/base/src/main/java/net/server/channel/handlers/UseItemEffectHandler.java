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

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemEffectPacket;
import net.server.channel.packet.reader.UseItemEffectReader;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;

public final class UseItemEffectHandler extends AbstractPacketHandler<UseItemEffectPacket> {
   @Override
   public Class<UseItemEffectReader> getReaderClass() {
      return UseItemEffectReader.class;
   }

   @Override
   public void handlePacket(UseItemEffectPacket packet, MapleClient client) {
      Item toUse;
      int itemId = packet.itemId();
      if (itemId == 4290001 || itemId == 4290000) {
         toUse = client.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId);
      } else {
         toUse = client.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
      }
      if (toUse == null || toUse.quantity() < 1) {
         if (itemId != 0) {
            return;
         }
      }
      client.getPlayer().setItemEffect(itemId);
      MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), character -> MaplePacketCreator.itemEffect(client.getPlayer().getId(), itemId), false, client.getPlayer());
   }
}
