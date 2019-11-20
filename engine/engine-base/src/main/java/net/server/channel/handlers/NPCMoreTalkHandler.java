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
import net.server.channel.packet.NPCMoreTalkPacket;
import net.server.channel.packet.reader.NPCMoreTalkReader;
import scripting.npc.NPCScriptManager;
import scripting.quest.QuestScriptManager;

/**
 * @author Matze
 */
public final class NPCMoreTalkHandler extends AbstractPacketHandler<NPCMoreTalkPacket> {
   @Override
   public Class<NPCMoreTalkReader> getReaderClass() {
      return NPCMoreTalkReader.class;
   }

   @Override
   public void handlePacket(NPCMoreTalkPacket packet, MapleClient client) {
      if (packet.lastMessageType() == 2) {
         if (packet.action() != 0) {
            if (client.getQM() != null) {
               client.getQM().setGetText(packet.returnText());
               if (client.getQM().isStart()) {
                  QuestScriptManager.getInstance().start(client, packet.action(), packet.lastMessageType(), -1);
               } else {
                  QuestScriptManager.getInstance().end(client, packet.action(), packet.lastMessageType(), -1);
               }
            } else {
               client.getCM().setGetText(packet.returnText());
               NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), -1);
            }
         } else if (client.getQM() != null) {
            client.getQM().dispose();
         } else {
            client.getCM().dispose();
         }
      } else {
         if (client.getQM() != null) {
            if (client.getQM().isStart()) {
               QuestScriptManager.getInstance().start(client, packet.action(), packet.lastMessageType(), packet.selection());
            } else {
               QuestScriptManager.getInstance().end(client, packet.action(), packet.lastMessageType(), packet.selection());
            }
         } else if (client.getCM() != null) {
            NPCScriptManager.getInstance().action(client, packet.action(), packet.lastMessageType(), packet.selection());
         }
      }
   }
}