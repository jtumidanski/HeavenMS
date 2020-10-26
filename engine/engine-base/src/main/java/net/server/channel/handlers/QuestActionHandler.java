package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.QuestActionPacket;
import net.server.channel.packet.reader.QuestActionReader;
import server.processor.QuestProcessor;

public final class QuestActionHandler extends AbstractPacketHandler<QuestActionPacket> {
   @Override
   public Class<QuestActionReader> getReaderClass() {
      return QuestActionReader.class;
   }

   @Override
   public void handlePacket(QuestActionPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (packet.action() == 0) {
         QuestProcessor.getInstance().restoreLostItem(player, packet.questId(), packet.itemId());
      } else if (packet.action() == 1) {
         QuestProcessor.getInstance().startQuest(player.getId(), packet.questId(), packet.npc(), packet.x(), packet.y());
      } else if (packet.action() == 2) {
         QuestProcessor.getInstance().completeQuest(player.getId(), packet.questId(), packet.npc(), packet.selection(), packet.x(),
               packet.y());
      } else if (packet.action() == 3) {
         QuestProcessor.getInstance().forfeit(player, packet.questId());
      } else if (packet.action() == 4) {
         QuestProcessor.getInstance().startScriptedQuest(player, packet.questId(), packet.npc(), packet.x(), packet.y());
      } else if (packet.action() == 5) {
         QuestProcessor.getInstance().completeScriptedQuest(player, packet.questId(), packet.npc(), packet.x(), packet.y());
      }
   }
}
