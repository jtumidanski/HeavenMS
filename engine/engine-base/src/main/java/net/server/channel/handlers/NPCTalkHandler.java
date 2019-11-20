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
import client.processor.DueyProcessor;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCTalkPacket;
import net.server.channel.packet.reader.NPCTalkReader;
import scripting.npc.NPCScriptManager;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.maps.MapleMapObject;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

public final class NPCTalkHandler extends AbstractPacketHandler<NPCTalkPacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }

      if (currentServerTime() - client.getPlayer().getNpcCooldown() < ServerConstants.BLOCK_NPC_RACE_CONDT) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<NPCTalkReader> getReaderClass() {
      return NPCTalkReader.class;
   }

   @Override
   public void handlePacket(NPCTalkPacket packet, MapleClient client) {
      MapleMapObject obj = client.getPlayer().getMap().getMapObject(packet.objectId());
      if (obj instanceof MapleNPC) {
         MapleNPC npc = (MapleNPC) obj;
         if (ServerConstants.USE_DEBUG) {
            MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "Talking to NPC " + npc.id());
         }

         if (npc.id() == 9010009) {   //is duey
            DueyProcessor.dueySendTalk(client, false);
         } else {
            if (client.getCM() != null || client.getQM() != null) {
               PacketCreator.announce(client, new EnableActions());
               return;
            }
            if (npc.id() >= 9100100 && npc.id() <= 9100200) {
               // Custom handling for gachapon scripts to reduce the amount of scripts needed.
               NPCScriptManager.getInstance().start(client, npc.id(), "gachapon", null);
            } else {
               boolean hasNpcScript = NPCScriptManager.getInstance().start(client, npc.id(), packet.objectId(), null);
               if (!hasNpcScript) {
                  if (!npc.hasShop()) {
                     FilePrinter.printError(FilePrinter.NPC_UNCODED, "NPC " + npc.getName() + "(" + npc.id() + ") is not coded.");
                     return;
                  } else if (client.getPlayer().getShop() != null) {
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }

                  npc.sendShop(client);
               }
            }
         }
      } else if (obj instanceof MaplePlayerNPC) {
         MaplePlayerNPC pnpc = (MaplePlayerNPC) obj;
         NPCScriptManager nsm = NPCScriptManager.getInstance();

         if (pnpc.getScriptId() < 9977777 && !nsm.isNpcScriptAvailable(client, "" + pnpc.getScriptId())) {
            nsm.start(client, pnpc.getScriptId(), "rank_user", null);
         } else {
            nsm.start(client, pnpc.getScriptId(), null);
         }
      }
   }
}