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
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import client.processor.PetProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetCommandPacket;
import net.server.channel.packet.reader.PetCommandReader;
import tools.MasterBroadcaster;
import tools.Randomizer;
import tools.packet.pet.PetCommandResponse;

public final class PetCommandHandler extends AbstractPacketHandler<PetCommandPacket> {
   @Override
   public Class<PetCommandReader> getReaderClass() {
      return PetCommandReader.class;
   }

   @Override
   public void handlePacket(PetCommandPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      byte petIndex = chr.getPetIndex(packet.petId());
      MaplePet pet;
      if (petIndex == -1) {
         return;
      } else {
         pet = chr.getPet(petIndex);
      }
      PetCommand petCommand = PetDataFactory.getPetCommand(pet.id(), packet.command());
      if (petCommand == null) {
         return;
      }

      if (Randomizer.nextInt(100) < petCommand.probability()) {
         PetProcessor.getInstance().gainClosenessFullness(pet, chr, petCommand.increase(), 0, packet.command());
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new PetCommandResponse(chr.getId(), petIndex, false, packet.command(), false));
      } else {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new PetCommandResponse(chr.getId(), petIndex, true, packet.command(), false));
      }
   }
}
