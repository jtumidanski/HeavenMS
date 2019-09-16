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
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemMovePacket;
import net.server.channel.packet.reader.ItemMoveReader;
import tools.MaplePacketCreator;

/**
 * @author Matze
 */
public final class ItemMoveHandler extends AbstractPacketHandler<ItemMovePacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      if (client.getPlayer().getAutobanManager().getLastSpam(6) + 300 > currentServerTime()) {
         client.announce(MaplePacketCreator.enableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<ItemMoveReader> getReaderClass() {
      return ItemMoveReader.class;
   }

   @Override
   public void handlePacket(ItemMovePacket packet, MapleClient client) {
      MapleInventoryType type = MapleInventoryType.getByType(packet.inventoryType());

      if (packet.source() < 0 && packet.action() > 0) {
         MapleInventoryManipulator.unequip(client, packet.source(), packet.action());
      } else if (packet.action() < 0) {
         MapleInventoryManipulator.equip(client, packet.source(), packet.action());
      } else if (packet.action() == 0) {
         MapleInventoryManipulator.drop(client, type, packet.source(), packet.quantity());
      } else {
         MapleInventoryManipulator.move(client, type, packet.source(), packet.action());
      }

      if (client.getPlayer().getMap().getHPDec() > 0) {
         client.getPlayer().resetHpDecreaseTask();
      }
      client.getPlayer().getAutobanManager().spam(6);
   }
}