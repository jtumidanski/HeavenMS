package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.QuestStatus;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RaiseUIStatePacket;
import net.server.channel.packet.reader.RaiseUIStateReader;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;

public class RaiseUIStateHandler extends AbstractPacketHandler<RaiseUIStatePacket> {
   @Override
   public Class<RaiseUIStateReader> getReaderClass() {
      return RaiseUIStateReader.class;
   }

   @Override
   public void handlePacket(RaiseUIStatePacket packet, MapleClient client) {

      if (client.tryAcquireClient()) {
         try {
            MapleQuest quest = QuestProcessor.getInstance().getQuest(packet.questId());
            MapleQuestStatus mqs = QuestProcessor.getInstance().getQuestStatus(client.getPlayer(), quest);
            if (mqs.status() == QuestStatus.NOT_STARTED) {
               QuestProcessor.getInstance().forceStart(client.getPlayer(), quest, 22000);
               client.getPlayer().getAbstractPlayerInteraction().setQuestProgress(quest.id(), packet.questId(), 0);
            } else if (mqs.status() == QuestStatus.STARTED) {
               short infoNumber = QuestProcessor.getInstance().getInfoNumber(mqs);
               client.getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, mqs, infoNumber > 0);
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}