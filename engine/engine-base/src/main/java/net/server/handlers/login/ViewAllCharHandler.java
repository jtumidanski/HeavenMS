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

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.ShowAllCharacter;
import tools.packet.ShowAllCharacterInfo;

public final class ViewAllCharHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      if (!client.canRequestCharacterlist()) {   // client breaks if the charlist request pops too soon
         PacketCreator.announce(client, new ShowAllCharacter(0, 0));
         return;
      }

      int accountId = client.getAccID();
      Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loginBlob = Server.getInstance().loadAccountCharlist(accountId, client.getVisibleWorlds());

      List<Pair<Integer, List<MapleCharacter>>> worldChars = loginBlob.getRight();
      int chrTotal = loginBlob.getLeft().getLeft();
      List<MapleCharacter> lastwchars = loginBlob.getLeft().getRight();

      if (chrTotal > 9) {
         int padRight = chrTotal % 3;
         if (padRight > 0 && lastwchars != null) {
            MapleCharacter chr = lastwchars.get(lastwchars.size() - 1);

            for (int i = padRight; i < 3; i++) { // filling the remaining slots with the last character loaded
               chrTotal++;
               lastwchars.add(chr);
            }
         }
      }

      int charsSize = chrTotal;
      int unk = charsSize + (3 - charsSize % 3); //rowSize?
      PacketCreator.announce(client, new ShowAllCharacter(charsSize, unk));

      for (Pair<Integer, List<MapleCharacter>> wchars : worldChars) {
         PacketCreator.announce(client, new ShowAllCharacterInfo(wchars.getLeft(), wchars.getRight(), YamlConfig.config.server.ENABLE_PIC && client.cannotBypassPic()));
      }
   }
}
