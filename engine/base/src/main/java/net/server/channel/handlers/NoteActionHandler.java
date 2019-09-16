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
import client.database.administrator.NoteAdministrator;
import client.database.provider.NoteProvider;
import client.processor.NoteProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.BaseNoteActionPacket;
import net.server.channel.packet.ClearNotePacket;
import net.server.channel.packet.SendNotePacket;
import net.server.channel.packet.reader.NoteActionReader;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

public final class NoteActionHandler extends AbstractPacketHandler<BaseNoteActionPacket> {
   @Override
   public Class<NoteActionReader> getReaderClass() {
      return NoteActionReader.class;
   }

   @Override
   public void handlePacket(BaseNoteActionPacket packet, MapleClient client) {
      if (packet instanceof SendNotePacket && client.getPlayer().getCashShop().getAvailableNotes() > 0) {
         sendNote((SendNotePacket) packet, client);
      } else if (packet instanceof ClearNotePacket) {
         clearNotes((ClearNotePacket) packet, client);
      }
   }

   private void clearNotes(ClearNotePacket packet, MapleClient client) {
      int fame = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int fameCount = 0;
         for (int i = 0; i < packet.ids().length; i++) {
            int id = packet.ids()[i];
            fameCount += NoteProvider.getInstance().getFameForActiveNotes(connection, id).orElse(0);
            NoteAdministrator.getInstance().clearNote(connection, id);
         }
         return fameCount;
      }).orElse(0);
      if (fame > 0) {
         client.getPlayer().gainFame(fame);
      }
   }

   private void sendNote(SendNotePacket packet, MapleClient client) {
      if (client.getPlayer().getCashShop().isOpened()) {
         client.announce(MaplePacketCreator.showCashInventory(client));
      }

      NoteProcessor.getInstance().sendNote(packet.characterName(), client.getPlayer().getName(), packet.message(), (byte) 1);
      client.getPlayer().getCashShop().decreaseNotes();
   }
}
