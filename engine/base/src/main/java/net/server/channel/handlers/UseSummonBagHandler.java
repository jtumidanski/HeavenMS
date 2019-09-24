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
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemPacket;
import net.server.channel.packet.reader.UseItemReader;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import tools.MaplePacketCreator;
import tools.Randomizer;

/**
 * @author AngelSL
 */
public final class UseSummonBagHandler extends AbstractPacketHandler<UseItemPacket> {
   @Override
   public Class<UseItemReader> getReaderClass() {
      return UseItemReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         client.announce(MaplePacketCreator.enableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(UseItemPacket packet, MapleClient c) {
      //[4A 00][6C 4C F2 02][02 00][63 0B 20 00]
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(packet.slot());
      if (toUse != null && toUse.quantity() > 0 && toUse.id() == packet.itemId()) {
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, packet.slot(), (short) 1, false);
         int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(packet.itemId());
         for (int[] toSpawnChild : toSpawn) {
            if (Randomizer.nextInt(100) < toSpawnChild[1]) {
               c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), c.getPlayer().getPosition());
            }
         }
      }
      c.announce(MaplePacketCreator.enableActions());
   }
}
