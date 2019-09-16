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
import client.autoban.AutobanManager;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.pet.PetFoodPacket;
import net.server.channel.packet.reader.PetFoodReader;
import tools.MaplePacketCreator;

public final class PetFoodHandler extends AbstractPacketHandler<PetFoodPacket> {
   @Override
   public Class<PetFoodReader> getReaderClass() {
      return PetFoodReader.class;
   }

   @Override
   public void handlePacket(PetFoodPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      AutobanManager abm = chr.getAutobanManager();
      if (abm.getLastSpam(2) + 500 > currentServerTime()) {
         client.announce(MaplePacketCreator.enableActions());
         return;
      }
      abm.spam(2);
      abm.setTimestamp(1, Server.getInstance().getCurrentTimestamp(), 3);
      if (chr.getNoPets() == 0) {
         client.announce(MaplePacketCreator.enableActions());
         return;
      }
      int previousFullness = 100;
      byte slot = 0;
      MaplePet[] pets = chr.getPets();
      for (byte i = 0; i < 3; i++) {
         if (pets[i] != null) {
            if (pets[i].getFullness() < previousFullness) {
               slot = i;
               previousFullness = pets[i].getFullness();
            }
         }
      }

      MaplePet pet = chr.getPet(slot);
      if (pet == null) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {
            MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);
            useInv.lockInventory();
            try {
               Item use = useInv.getItem(packet.position());
               if (use == null || (packet.itemId() / 10000) != 212 || use.getItemId() != packet.itemId() || use.getQuantity() < 1) {
                  return;
               }

               pet.gainClosenessFullness(chr, (pet.getFullness() <= 75) ? 1 : 0, 30, 1);   // 25+ "emptyness" to get +1 closeness
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, packet.position(), (short) 1, false);
            } finally {
               useInv.unlockInventory();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
