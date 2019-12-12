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

import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetLootPacket;
import net.server.channel.packet.reader.PetLootReader;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

/**
 * @author TheRamon
 * @author Ronan
 */
public final class PetLootHandler extends AbstractPacketHandler<PetLootPacket> {
   @Override
   public Class<PetLootReader> getReaderClass() {
      return PetLootReader.class;
   }

   @Override
   public void handlePacket(PetLootPacket packet, MapleClient c) {
      MapleCharacter chr = c.getPlayer();

      int petIndex = chr.getPetIndex(packet.petIndex());
      MaplePet pet = chr.getPet(petIndex);
      if (pet == null || !pet.summoned()) {
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      MapleMapObject ob = chr.getMap().getMapObject(packet.objectId());
      try {
         MapleMapItem mapitem = (MapleMapItem) ob;
         if (mapitem.getMeso() > 0) {
            if (!chr.isEquippedMesoMagnet()) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            if (chr.isEquippedPetItemIgnore()) {
               final Set<Integer> petIgnore = chr.getExcludedItems();
               if (!petIgnore.isEmpty() && petIgnore.contains(Integer.MAX_VALUE)) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }
         } else {
            if (!chr.isEquippedItemPouch()) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            if (chr.isEquippedPetItemIgnore()) {
               final Set<Integer> petIgnore = chr.getExcludedItems();
               if (!petIgnore.isEmpty() && petIgnore.contains(mapitem.getItem().id())) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }
         }

         chr.pickupItem(ob, petIndex);
      } catch (NullPointerException | ClassCastException e) {
         PacketCreator.announce(c, new EnableActions());
      }
   }
}
