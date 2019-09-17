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
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.maps.MapleHiredMerchant;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author kevintjuh93 :3
 */
public class RemoteStoreHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleHiredMerchant hm = getMerchant(client);
      if (hm != null && hm.isOwner(chr)) {
         if (hm.getChannel() == chr.getClient().getChannel()) {
            hm.visitShop(chr);
         } else {
            client.announce(MaplePacketCreator.remoteChannelChange((byte) (hm.getChannel() - 1)));
         }
         return;
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You don't have a Merchant open.");
      }
      client.announce(MaplePacketCreator.enableActions());
   }

   private MapleHiredMerchant getMerchant(MapleClient c) {
      if (c.getPlayer().hasMerchant()) {
         return c.getWorldServer().getHiredMerchant(c.getPlayer().getId());
      }
      return null;
   }
}