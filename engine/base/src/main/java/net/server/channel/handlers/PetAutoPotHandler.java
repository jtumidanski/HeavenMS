/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    Copyleft (L) 2016 - 2018 RonanLana (HeavenMS)

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
import client.processor.PetAutopotProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetAutoPotPacket;
import net.server.channel.packet.reader.PetAutoPotReader;
import tools.MaplePacketCreator;

/**
 * @author Ronan - multi-pot consumption feature
 */
public final class PetAutoPotHandler extends AbstractPacketHandler<PetAutoPotPacket> {
   @Override
   public Class<PetAutoPotReader> getReaderClass() {
      return PetAutoPotReader.class;
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
   public void handlePacket(PetAutoPotPacket packet, MapleClient client) {
      PetAutopotProcessor.getInstance().runAutopotAction(client, packet.slot(), packet.itemId());
   }
}
