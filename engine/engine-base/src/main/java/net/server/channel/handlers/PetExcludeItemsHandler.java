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
import client.inventory.MaplePet;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetExcludeItemsPacket;
import net.server.channel.packet.reader.PetExcludeItemsReader;

/**
 * @author BubblesDev
 * @author Ronan
 */
public final class PetExcludeItemsHandler extends AbstractPacketHandler<PetExcludeItemsPacket> {
   @Override
   public Class<PetExcludeItemsReader> getReaderClass() {
      return PetExcludeItemsReader.class;
   }

   @Override
   public void handlePacket(PetExcludeItemsPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      byte petIndex = chr.getPetIndex(packet.petId());
      if (petIndex < 0) {
         return;
      }

      final MaplePet pet = chr.getPet(petIndex);
      if (pet == null) {
         return;
      }

      chr.resetExcluded(packet.petId());
      for (int i = 0; i < packet.amount(); i++) {
         chr.addExcluded(packet.petId(), packet.itemIds()[i]);
      }
      chr.commitExcludedItems();
   }
}
