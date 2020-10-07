package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.QuestActionPacket;
import net.server.channel.packet.reader.QuestActionReader;
import scripting.quest.QuestScriptManager;
import server.life.MapleNPC;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public final class QuestActionHandler extends AbstractPacketHandler<QuestActionPacket> {
   @Override
   public Class<QuestActionReader> getReaderClass() {
      return QuestActionReader.class;
   }

   @Override
   public void handlePacket(QuestActionPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleQuest quest = QuestProcessor.getInstance().getQuest(packet.questId());

      if (packet.action() == 0) { // Restore lost item
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
         if (playerP.distance(pos) > 1000) {
            playerP = pos;
         }
      } else {
         playerP = pos;
      }

      if (!quest.autoStart() && !quest.isAutoComplete()) {
         MapleNPC npc = player.getMap().getNPCById(npcId);
         if (npc == null) {
            return false;
         }

         Point npcP = npc.position();
         if (Math.abs(npcP.getX() - playerP.getX()) > 1200 || Math.abs(npcP.getY() - playerP.getY()) > 800) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("NPC_MOVE_CLOSER_TO_NPC"));
            return false;
         }
      }

      return true;
   }
}
