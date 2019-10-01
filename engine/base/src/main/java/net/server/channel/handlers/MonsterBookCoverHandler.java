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
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MonsterBookCoverPacket;
import net.server.channel.packet.reader.MonsterBookCoverReader;
import tools.PacketCreator;
import tools.packet.monster.book.ChangeCover;

public final class MonsterBookCoverHandler extends AbstractPacketHandler<MonsterBookCoverPacket> {
   @Override
   public Class<MonsterBookCoverReader> getReaderClass() {
      return MonsterBookCoverReader.class;
   }

   @Override
   public void handlePacket(MonsterBookCoverPacket packet, MapleClient client) {
      if (packet.coverId() == 0 || packet.coverId() / 10000 == 238) {
         client.getPlayer().setMonsterBookCover(packet.coverId());
         PacketCreator.announce(client, new ChangeCover(packet.coverId()));
      }
   }
}
