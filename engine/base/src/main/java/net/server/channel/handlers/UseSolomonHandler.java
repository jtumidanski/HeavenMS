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

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseSolomonPacket;
import net.server.channel.packet.reader.UseSolomonReader;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

/**
 * @author XoticStory; modified by kevintjuh93, Ronan
 */
public final class UseSolomonHandler extends AbstractPacketHandler<UseSolomonPacket> {
   @Override
   public Class<UseSolomonReader> getReaderClass() {
      return UseSolomonReader.class;
   }

   @Override
   public void handlePacket(UseSolomonPacket packet, MapleClient client) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (client.tryAcquireClient()) {
         try {
            MapleCharacter chr = client.getPlayer();
            MapleInventory inv = chr.getInventory(MapleInventoryType.USE);
            inv.lockInventory();
            try {
               Item slotItem = inv.getItem(packet.slot());
               if (slotItem == null) {
                  return;
               }

               long gachaexp = ii.getExpById(packet.itemId());
               if (slotItem.getItemId() != packet.itemId() || slotItem.getQuantity() <= 0 || chr.getLevel() > ii.getMaxLevelById(packet.itemId())) {
                  return;
               }
               if (gachaexp + chr.getGachaExp() > Integer.MAX_VALUE) {
                  return;
               }
               chr.addGachaExp((int) gachaexp);
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, packet.slot(), (short) 1, false);
            } finally {
               inv.unlockInventory();
            }
         } finally {
            client.releaseClient();
         }
      }

      client.announce(MaplePacketCreator.enableActions());
   }
}
