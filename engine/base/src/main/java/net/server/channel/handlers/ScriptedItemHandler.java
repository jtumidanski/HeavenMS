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
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ScriptedItemPacket;
import net.server.channel.packet.reader.ScriptedItemReader;
import scripting.item.ItemScriptManager;
import server.MapleItemInformationProvider;
import server.MapleItemInformationProvider.ScriptedItem;

/**
 * @author Jay Estrella
 */
public final class ScriptedItemHandler extends AbstractPacketHandler<ScriptedItemPacket> {
   @Override
   public Class<ScriptedItemReader> getReaderClass() {
      return ScriptedItemReader.class;
   }

   @Override
   public void handlePacket(ScriptedItemPacket packet, MapleClient client) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      ScriptedItem info = ii.getScriptedItemInfo(packet.itemId());
      if (info == null) {
         return;
      }

      Item item = client.getPlayer().getInventory(ItemConstants.getInventoryType(packet.itemId())).getItem(packet.itemSlot());
      if (item == null || item.getItemId() != packet.itemId() || item.getQuantity() < 1) {
         return;
      }

      ItemScriptManager ism = ItemScriptManager.getInstance();
      ism.runItemScript(client, info);
   }
}
