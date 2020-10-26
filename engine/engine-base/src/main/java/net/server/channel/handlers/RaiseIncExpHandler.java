package net.server.channel.handlers;

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemUIPacket;
import net.server.channel.packet.reader.UseItemUIReader;
import server.MapleItemInformationProvider;
import server.QuestConsItem;
import server.processor.QuestProcessor;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public class RaiseIncExpHandler extends AbstractPacketHandler<UseItemUIPacket> {
   @Override
   public Class<UseItemUIReader> getReaderClass() {
      return UseItemUIReader.class;
   }

   @Override
   public void handlePacket(UseItemUIPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            QuestConsItem consItem = ii.getQuestConsumablesInfo(packet.itemId());
            if (consItem == null) {
               return;
            }

            int infoNumber = consItem.questId();
            Map<Integer, Integer> consumables = consItem.items();

            MapleCharacter chr = client.getPlayer();
            short questId = QuestProcessor.getInstance().getInstanceFromInfoNumber(infoNumber);
            if (!QuestProcessor.getInstance().isStatus(chr, questId, "STARTED")) {
               PacketCreator.announce(client, new EnableActions());
               return;
            }

            int consId;
            MapleInventory inv = client.getPlayer().getInventory(MapleInventoryType.getByType(packet.inventoryType()));
            inv.lockInventory();
            try {
               consId = inv.getItem(packet.slot()).id();
               if (!consumables.containsKey(consId) || !client.getPlayer().haveItem(consId)) {
                  return;
               }

               MapleInventoryManipulator
                     .removeFromSlot(client, MapleInventoryType.getByType(packet.inventoryType()), packet.slot(), (short) 1, false,
                           true);
            } finally {
               inv.unlockInventory();
            }

            int nextValue =
                  Math.min(consumables.get(consId) + client.getAbstractPlayerInteraction().getQuestProgressInt(questId, infoNumber),
                        consItem.exp() * consItem.grade());
            client.getAbstractPlayerInteraction().setQuestProgress(questId, infoNumber, nextValue);

            PacketCreator.announce(client, new EnableActions());
         } finally {
            client.releaseClient();
         }
      }
   }
}
