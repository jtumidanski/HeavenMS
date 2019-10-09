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
import client.MapleMount;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ExpTable;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseMountFoodPacket;
import net.server.channel.packet.reader.UseMountFoodReader;
import tools.MasterBroadcaster;
import tools.packet.character.UpdateMount;

/**
 * @author PurpleMadness
 * @author Ronan
 */
public final class UseMountFoodHandler extends AbstractPacketHandler<UseMountFoodPacket> {
   @Override
   public Class<UseMountFoodReader> getReaderClass() {
      return UseMountFoodReader.class;
   }

   @Override
   public void handlePacket(UseMountFoodPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMount mount = chr.getMount();
      MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);

      if (client.tryAcquireClient()) {
         try {
            Boolean mountLevelup = null;

            useInv.lockInventory();
            try {
               Item item = useInv.getItem(packet.position());
               if (item != null && item.id() == packet.itemId() && mount != null) {
                  int curTiredness = mount.getTiredness();
                  int healedTiredness = Math.min(curTiredness, 30);

                  float healedFactor = (float) healedTiredness / 30;
                  mount.setTiredness(curTiredness - healedTiredness);

                  if (healedFactor > 0.0f) {
                     mount.setExp(mount.getExp() + (int) Math.ceil(healedFactor * (2 * mount.getLevel() + 6)));
                     int level = mount.getLevel();
                     boolean levelup = mount.getExp() >= ExpTable.getMountExpNeededForLevel(level) && level < 31;
                     if (levelup) {
                        mount.setLevel(level + 1);
                     }

                     mountLevelup = levelup;
                  }

                  MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, packet.itemId(), 1, true, false);
               }
            } finally {
               useInv.unlockInventory();
            }

            if (mountLevelup != null) {
               Boolean finalMountLevelup = mountLevelup;
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new UpdateMount(chr.getId(), mount.getLevel(), mount.getExp(), mount.getTiredness(), finalMountLevelup));
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}