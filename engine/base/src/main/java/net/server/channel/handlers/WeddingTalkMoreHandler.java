/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import scripting.event.EventInstanceManager;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.wedding.WeddingEnd;

/**
 * @author Ronan
 */
public final class WeddingTalkMoreHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      EventInstanceManager eim = client.getPlayer().getEventInstance();
      if (eim != null && !(client.getPlayer().getId() == eim.getIntProperty("groomId") || client.getPlayer().getId() == eim.getIntProperty("brideId"))) {
         eim.gridInsert(client.getPlayer(), 1);
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "High Priest John: Your blessings have been added to their love. What a noble act for a lovely couple!");
      }

      PacketCreator.announce(client, new WeddingEnd(true, 0, 0, (byte) 3));
      PacketCreator.announce(client, new EnableActions());
   }
}