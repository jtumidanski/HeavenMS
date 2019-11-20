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

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.QuestActionPacket;
import net.server.channel.packet.reader.QuestActionReader;
import scripting.quest.QuestScriptManager;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author Matze
 */
public final class QuestActionHandler extends AbstractPacketHandler<QuestActionPacket> {
   @Override
   public Class<QuestActionReader> getReaderClass() {
      return QuestActionReader.class;
   }

   @Override
   public void handlePacket(QuestActionPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleQuest quest = MapleQuest.getInstance(packet.questId());

      if (packet.action() == 0) { // Restore lost item, Credits Darter ( Rajan )
         quest.restoreLostItem(player, packet.itemId());
      } else if (packet.action() == 1) { //Start Quest
         if (!isNpcNearby(packet, player, quest, packet.npc())) {
            return;
         }

         if (quest.canStart(player, packet.npc())) {
            quest.start(player, packet.npc());
         }
      } else if (packet.action() == 2) { // Complete Quest
         if (!isNpcNearby(packet, player, quest, packet.npc())) {
            return;
         }

         if (quest.canComplete(player, packet.npc())) {
            if (packet.selection() > -1) {
               quest.complete(player, packet.npc(), packet.selection());
            } else {
               quest.complete(player, packet.npc());
            }
         }
      } else if (packet.action() == 3) {// forfeit quest
         quest.forfeit(player);
      } else if (packet.action() == 4) { // scripted start quest
         if (!isNpcNearby(packet, player, quest, packet.npc())) {
            return;
         }

         if (quest.canStart(player, packet.npc())) {
            QuestScriptManager.getInstance().start(client, packet.questId(), packet.npc());
         }
      } else if (packet.action() == 5) { // scripted end quests
         if (!isNpcNearby(packet, player, quest, packet.npc())) {
            return;
         }

         if (quest.canComplete(player, packet.npc())) {
            QuestScriptManager.getInstance().end(client, packet.questId(), packet.npc());
         }
      }
   }

   private boolean isNpcNearby(QuestActionPacket packet, MapleCharacter player, MapleQuest quest, int npcId) {
      Point playerP;
      Point pos = player.position();

      if (packet.x() != -1 && packet.y() != -1) {
         playerP = new Point(packet.x(), packet.y());
         if (playerP.distance(pos) > 1000) {     // thanks Darter (YungMoozi) for reporting unchecked player position
            playerP = pos;
         }
      } else {
         playerP = pos;
      }

      if (!quest.isAutoStart() && !quest.isAutoComplete()) {
         MapleNPC npc = player.getMap().getNPCById(npcId);
         if (npc == null) {
            return false;
         }

         Point npcP = npc.position();
         if (Math.abs(npcP.getX() - playerP.getX()) > 1200 || Math.abs(npcP.getY() - playerP.getY()) > 800) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Approach the NPC to fulfill this quest operation.");
            return false;
         }
      }

      return true;
   }
}
