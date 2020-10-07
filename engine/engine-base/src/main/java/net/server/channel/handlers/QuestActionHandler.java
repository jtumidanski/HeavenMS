package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.QuestActionPacket;
import net.server.channel.packet.reader.QuestActionReader;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;

public final class QuestActionHandler extends AbstractPacketHandler<QuestActionPacket> {
   @Override
   public Class<QuestActionReader> getReaderClass() {
      return QuestActionReader.class;
   }

   @Override
   public void handlePacket(QuestActionPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleQuest quest = QuestProcessor.getInstance().getQuest(packet.questId());

      if (packet.action() == 0) {
         QuestProcessor.getInstance().restoreLostItem(player, quest, packet.itemId());
      } else if (packet.action() == 1) {
         QuestProcessor.getInstance().startQuest(player, quest, packet.npc(), packet.x(), packet.y());
      } else if (packet.action() == 2) {
         QuestProcessor.getInstance().completeQuest(player, quest, packet.npc(), packet.selection(), packet.x(), packet.y());
      } else if (packet.action() == 3) {
         QuestProcessor.getInstance().forfeit(player, quest);
      } else if (packet.action() == 4) {
         QuestProcessor.getInstance().startScriptedQuest(player, quest, packet.npc(), packet.x(), packet.y());
      } else if (packet.action() == 5) {
         QuestProcessor.getInstance().completeScriptedQuest(player, quest, packet.npc(), packet.x(), packet.y());
      }
   }
}
