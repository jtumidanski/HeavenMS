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

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemPickupPacket;
import net.server.channel.packet.reader.ItemPickupReader;
import server.maps.MapleMapObject;
import tools.FilePrinter;

/**
 * @author Matze
 * @author Ronan
 */
public final class ItemPickupHandler extends AbstractPacketHandler<ItemPickupPacket> {
   @Override
   public Class<ItemPickupReader> getReaderClass() {
      return ItemPickupReader.class;
   }

   @Override
   public void handlePacket(ItemPickupPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMapObject ob = chr.getMap().getMapObject(packet.objectId());
      if (ob == null) {
         return;
      }

      Point charPos = chr.getPosition();
      Point obPos = ob.getPosition();
      if (Math.abs(charPos.getX() - obPos.getX()) > 800 || Math.abs(charPos.getY() - obPos.getY()) > 600) {
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to pick up an item too far away. Mapid: " + chr.getMapId() + " Player pos: " + charPos + " Object pos: " + obPos);
         return;
      }

      chr.pickupItem(ob);
   }
}
