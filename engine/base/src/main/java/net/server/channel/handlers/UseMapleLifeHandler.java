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

import client.MapleCharacter;
import client.MapleClient;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseMapleLifePacket;
import net.server.channel.packet.reader.UseMapleLifeReader;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author RonanLana
 */
public class UseMapleLifeHandler extends AbstractPacketHandler<UseMapleLifePacket, UseMapleLifeReader> {
   @Override
   public Class<UseMapleLifeReader> getReaderClass() {
      return UseMapleLifeReader.class;
   }

   @Override
   public void handlePacket(UseMapleLifePacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      long timeNow = currentServerTime();

      if (timeNow - player.getLastUsedCashItem() < 3000) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Please wait a moment before trying again.");
         client.announce(MaplePacketCreator.sendMapleLifeError(3));
         client.announce(MaplePacketCreator.enableActions());
         return;
      }
      player.setLastUsedCashItem(timeNow);

      if (CharacterProcessor.getInstance().canCreateChar(packet.name())) {
         client.announce(MaplePacketCreator.sendMapleLifeCharacterInfo());
      } else {
         client.announce(MaplePacketCreator.sendMapleLifeNameError());
      }
      client.announce(MaplePacketCreator.enableActions());
   }
}
