package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import net.AbstractMaplePacketHandler;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.RaiseUIStatePacket;
import net.server.channel.packet.reader.RaiseUIStateReader;
import server.quest.MapleQuest;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Xari
 */
public class RaiseUIStateHandler extends AbstractPacketHandler<RaiseUIStatePacket, RaiseUIStateReader> {
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
               client.getPlayer().updateQuestInfo(quest.getId(), "0");
            } else if (mqs.getStatus() == MapleQuestStatus.Status.STARTED) {
               client.getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, mqs, false);
            } else {
               //c.announce(MaplePacketCreator.updateQuestInfo(mqs.getQuestID(), 22000, "0"));
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}