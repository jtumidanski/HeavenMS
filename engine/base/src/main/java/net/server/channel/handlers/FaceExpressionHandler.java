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
import constants.ItemConstants;
import net.AbstractMaplePacketHandler;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.FaceExpressionPacket;
import net.server.channel.packet.reader.FaceExpressionReader;
import tools.data.input.SeekableLittleEndianAccessor;

public final class FaceExpressionHandler extends AbstractPacketHandler<FaceExpressionPacket, FaceExpressionReader> {
   @Override
   public Class<FaceExpressionReader> getReaderClass() {
      return FaceExpressionReader.class;
   }

   @Override
   public void handlePacket(FaceExpressionPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (packet.emote() > 7) {
         int itemid = 5159992 + packet.emote();   // thanks Rajan (Darter) for reporting unchecked emote itemid
         if (!ItemConstants.isFaceExpression(itemid) || chr.getInventory(ItemConstants.getInventoryType(itemid)).findById(itemid) == null) {
            return;
         }
      } else if (packet.emote() < 1) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {   // expecting players never intends to wear the emote 0 (default face, that changes back after 5sec timeout)
            if (chr.isLoggedinWorld()) {
               chr.changeFaceExpression(packet.emote());
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
