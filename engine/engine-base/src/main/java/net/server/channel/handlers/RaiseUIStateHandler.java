package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RaiseUIStatePacket;
import net.server.channel.packet.reader.RaiseUIStateReader;
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
            MapleQuest quest = MapleQuest.getInstance(packet.questId());
            MapleQuestStatus mqs = client.getPlayer().getQuest(quest);
            if (mqs.getStatus() == MapleQuestStatus.Status.NOT_STARTED) {
               quest.forceStart(client.getPlayer(), 22000);
               client.getPlayer().getAbstractPlayerInteraction().setQuestProgress(quest.getId(), packet.questId(), 0);
            } else if (mqs.getStatus() == MapleQuestStatus.Status.STARTED) {
               client.getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, mqs, mqs.getInfoNumber() > 0);
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}