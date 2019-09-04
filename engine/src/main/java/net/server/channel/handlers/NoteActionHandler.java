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
import client.processor.NoteProcessor;
import client.database.administrator.NoteAdministrator;
import client.database.provider.NoteProvider;
import net.AbstractMaplePacketHandler;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class NoteActionHandler extends AbstractMaplePacketHandler {
   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      int action = slea.readByte();
      if (action == 0 && c.getPlayer().getCashShop().getAvailableNotes() > 0) {
         String charname = slea.readMapleAsciiString();
         String message = slea.readMapleAsciiString();
         if (c.getPlayer().getCashShop().isOpened()) {
            c.announce(MaplePacketCreator.showCashInventory(c));
         }

         NoteProcessor.getInstance().sendNote(charname, c.getPlayer().getName(), message, (byte) 1);
         c.getPlayer().getCashShop().decreaseNotes();
      } else if (action == 1) {
         int num = slea.readByte();
         slea.readByte();
         slea.readByte();
         int fame = DatabaseConnection.getInstance().withConnectionResult(connection -> {
            int fameCount = 0;
            for (int i = 0; i < num; i++) {
               int id = slea.readInt();
               slea.readByte(); //Fame, but we read it from the database :)
               fameCount += NoteProvider.getInstance().getFameForActiveNotes(connection, id).orElse(0);
               NoteAdministrator.getInstance().clearNote(connection, id);
            }
            return fameCount;
         }).orElse(0);
         if (fame > 0) {
            c.getPlayer().gainFame(fame);
         }
      }
   }
}
