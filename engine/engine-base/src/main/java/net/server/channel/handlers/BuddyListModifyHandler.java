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
import client.processor.BuddyListProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.buddy.AcceptBuddyPacket;
import net.server.channel.packet.buddy.AddBuddyPacket;
import net.server.channel.packet.buddy.BaseBuddyPacket;
import net.server.channel.packet.buddy.DeleteBuddyPacket;
import net.server.channel.packet.reader.BuddyReader;

public class BuddyListModifyHandler extends AbstractPacketHandler<BaseBuddyPacket> {
   @Override
   public Class<BuddyReader> getReaderClass() {
      return BuddyReader.class;
   }

   @Override
   public void handlePacket(BaseBuddyPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (packet instanceof AddBuddyPacket) {
         BuddyListProcessor.getInstance().addBuddy(player, ((AddBuddyPacket) packet).name(), ((AddBuddyPacket) packet).group());
      } else if (packet instanceof AcceptBuddyPacket) {
         BuddyListProcessor.getInstance().accept(player, ((AcceptBuddyPacket) packet).otherCharacterId());
      } else if (packet instanceof DeleteBuddyPacket) {
         BuddyListProcessor.getInstance().deleteBuddy(player, ((DeleteBuddyPacket) packet).otherCharacterId());
      }
   }
}
