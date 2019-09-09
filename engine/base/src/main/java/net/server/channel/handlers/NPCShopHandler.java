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
import client.autoban.AutobanFactory;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCShopPacket;
import net.server.channel.packet.reader.NPCShopReader;
import tools.FilePrinter;

/**
 * @author Matze
 */
public final class NPCShopHandler extends AbstractPacketHandler<NPCShopPacket, NPCShopReader> {
   @Override
   public Class<NPCShopReader> getReaderClass() {
      return NPCShopReader.class;
   }

   @Override
   public void handlePacket(NPCShopPacket packet, MapleClient client) {
      if (packet.mode() == 0) { // mode 0 = buy :)
         if (packet.quantity() < 1) {
            AutobanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit a npc shop.");
            FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to buy quantity " + packet.quantity() + " of item id " + packet.itemId());
            client.disconnect(true, false);
            return;
         }
         client.getPlayer().getShop().buy(client, packet.slot(), packet.itemId(), packet.quantity());
      } else if (packet.mode() == 1) { // sell ;)
         client.getPlayer().getShop().sell(client, ItemConstants.getInventoryType(packet.itemId()), packet.slot(), packet.quantity());
      } else if (packet.mode() == 2) { // recharge ;)
         client.getPlayer().getShop().recharge(client, packet.slot());
      } else if (packet.mode() == 3) { // leaving :(
         client.getPlayer().setShop(null);
      }
   }
}
