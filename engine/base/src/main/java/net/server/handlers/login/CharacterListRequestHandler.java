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
package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.CharacterListRequestReader;
import net.server.login.packet.CharacterListRequestPacket;
import net.server.world.World;
import tools.PacketCreator;
import tools.packet.character.CharacterList;
import tools.packet.serverlist.GetServerStatus;
import tools.packet.serverlist.ServerStatus;

public final class CharacterListRequestHandler extends AbstractPacketHandler<CharacterListRequestPacket> {
   @Override
   public Class<CharacterListRequestReader> getReaderClass() {
      return CharacterListRequestReader.class;
   }

   @Override
   public void handlePacket(CharacterListRequestPacket packet, MapleClient client) {
      World world = Server.getInstance().getWorld(packet.world());
      if (world == null || world.isWorldCapacityFull()) {
         PacketCreator.announce(client, new GetServerStatus(ServerStatus.FULL));
         return;
      }

      if (world.getChannel(packet.channel()) == null) {
         PacketCreator.announce(client, new GetServerStatus(ServerStatus.FULL));
         return;
      }

      client.setWorld(packet.world());
      client.setChannel(packet.channel());
      PacketCreator.announce(client, new CharacterList(client, packet.world(), 0));
   }
}