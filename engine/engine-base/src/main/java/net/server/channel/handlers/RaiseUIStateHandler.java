package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RaiseUIStatePacket;
import net.server.channel.packet.reader.RaiseUIStateReader;
import rest.DelayedQuestUpdate;
import rest.builders.QuestUpdateAttributesBuilder;
import server.processor.QuestProcessor;

public class RaiseUIStateHandler extends AbstractPacketHandler<RaiseUIStatePacket> {
   @Override
   public Class<RaiseUIStateReader> getReaderClass() {
      return RaiseUIStateReader.class;
   }

   @Override
   public void handlePacket(RaiseUIStatePacket packet, MapleClient client) {

      if (client.tryAcquireClient()) {
         try {
            if (QuestProcessor.getInstance().isNotStarted(client.getPlayer(), packet.questId())) {
               QuestProcessor.getInstance().forceStart(client.getPlayer(), packet.questId(), 22000);
               client.getPlayer().getAbstractPlayerInteraction().setQuestProgress(packet.questId(), packet.questId(), 0);
            } else if (QuestProcessor.getInstance().isStarted(client.getPlayer(), packet.questId())) {
               short infoNumber = QuestProcessor.getInstance().getInfoNumber(packet.questId(), "STARTED");
               String progress = QuestProcessor.getInstance().getProgress(client.getPlayer(), packet.questId(), infoNumber);
               client.getPlayer().announceUpdateQuest(
                     new QuestUpdateAttributesBuilder()
                           .setDelayType(DelayedQuestUpdate.UPDATE.name())
                           .setQuestId(packet.questId())
                           .setQuestStatusId(1)
                           .setInfoUpdate(infoNumber > 0)
                           .setInfoNumber(infoNumber)
                           .setProgress(progress)
                           .build()
               );
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}