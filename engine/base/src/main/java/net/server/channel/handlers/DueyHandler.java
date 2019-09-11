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
import client.processor.DueyProcessor;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.duey.BaseDueyPacket;
import net.server.channel.packet.duey.DueyClaimPackagePacket;
import net.server.channel.packet.duey.DueyReceiveItemPacket;
import net.server.channel.packet.duey.DueyRemovePackagePacket;
import net.server.channel.packet.duey.DueySendItemPacket;
import net.server.channel.packet.reader.DueyReader;
import tools.MaplePacketCreator;

public final class DueyHandler extends AbstractPacketHandler<BaseDueyPacket, DueyReader> {
   @Override
   public Class<DueyReader> getReaderClass() {
      return DueyReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!ServerConstants.USE_DUEY) {
         client.announce(MaplePacketCreator.enableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(BaseDueyPacket packet, MapleClient client) {
      if (packet instanceof DueyReceiveItemPacket) {
         receiveItem(client);
      } else if (packet instanceof DueySendItemPacket) {
         sendItem(client, (DueySendItemPacket) packet);
      } else if (packet instanceof DueyClaimPackagePacket) {
         claimPackage(client, ((DueyClaimPackagePacket) packet).packageId());
      } else if (packet instanceof DueyRemovePackagePacket) {
         removePackage(client, ((DueyRemovePackagePacket) packet).packageId());
      }
   }

   private void sendItem(MapleClient client, DueySendItemPacket packet) {
      DueyProcessor.dueySendItem(client, packet.inventoryId(),
            packet.itemPosition(), packet.amount(),
            packet.mesos(), packet.message(),
            packet.recipient(), packet.quick());
   }

   private void claimPackage(MapleClient c, int packageId) {
      DueyProcessor.dueyClaimPackage(c, packageId);
   }

   private void removePackage(MapleClient c, int packageId) {
      DueyProcessor.dueyRemovePackage(c, packageId, true);
   }

   private void receiveItem(MapleClient c) {
      DueyProcessor.dueySendTalk(c, false);
   }
}
