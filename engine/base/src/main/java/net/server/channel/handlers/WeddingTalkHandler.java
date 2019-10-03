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
import net.server.channel.packet.reader.WeddingTalkReader;
import net.server.channel.packet.wedding.BaseWeddingTalkPacket;
import scripting.event.EventInstanceManager;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;
import tools.packet.wedding.WeddingProgress;

/**
 * @author Ronan
 */
public final class WeddingTalkHandler extends AbstractPacketHandler<BaseWeddingTalkPacket> {
   @Override
   public Class<WeddingTalkReader> getReaderClass() {
      return WeddingTalkReader.class;
   }

   @Override
   public void handlePacket(BaseWeddingTalkPacket packet, MapleClient client) {
      if (packet.action() == 1) {
         EventInstanceManager eim = client.getPlayer().getEventInstance();

         if (eim != null && !(client.getPlayer().getId() == eim.getIntProperty("groomId") || client.getPlayer().getId() == eim.getIntProperty("brideId"))) {
            PacketCreator.announce(client, new WeddingProgress(false, 0, 0, (byte) 2));
         } else {
            PacketCreator.announce(client, new WeddingProgress(true, 0, 0, (byte) 3));
         }
      } else {
         PacketCreator.announce(client, new WeddingProgress(true, 0, 0, (byte) 3));
      }

      PacketCreator.announce(client, new EnableActions());
   }
}