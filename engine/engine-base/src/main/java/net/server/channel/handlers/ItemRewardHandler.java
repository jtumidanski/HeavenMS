package net.server.channel.handlers;

import java.util.List;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;
import constants.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemRewardPacket;
import net.server.channel.packet.reader.ItemRewardReader;
import server.MapleItemInformationProvider;
import server.RewardItem;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.ShowInventoryFull;

public final class ItemRewardHandler extends AbstractPacketHandler<ItemRewardPacket> {
   @Override
   public Class<ItemRewardReader> getReaderClass() {
      return ItemRewardReader.class;
   }

   @Override
   public void handlePacket(ItemRewardPacket packet, MapleClient client) {
      Item it = client.getPlayer().getInventory(MapleInventoryType.USE).getItem(packet.slot());
      if (it == null || it.id() != packet.itemId() || client.getPlayer().getInventory(MapleInventoryType.USE).countById(packet.itemId()) < 1) {
         return;
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Pair<Integer, List<RewardItem>> rewards = ii.getItemReward(packet.itemId());
      for (RewardItem reward : rewards.getRight()) {
         if (!MapleInventoryManipulator.checkSpace(client, reward.itemId(), reward.quantity(), "")) {
            PacketCreator.announce(client, new ShowInventoryFull());
            break;
         }
         if (Randomizer.nextInt(rewards.getLeft()) < reward.probability()) {//Is it even possible to get an item with prob 1?
            if (ItemConstants.getInventoryType(reward.itemId()) == MapleInventoryType.EQUIP) {
               Item item = ii.getEquipById(reward.itemId());
               if (reward.period() != -1) {
                  item = Item.newBuilder(item).setExpiration(currentServerTime() + (reward.period() * 60 * 60 * 10)).build();
               }
               MapleInventoryManipulator.addFromDrop(client, item, false);
            } else {
               MapleInventoryManipulator.addById(client, reward.itemId(), reward.quantity(), "", -1);
            }
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, packet.itemId(), 1, false, false);
            if (reward.worldMessage() != null) {
               String msg = reward.worldMessage();
               msg = msg.replaceAll("/name", client.getPlayer().getName());
               msg = msg.replaceAll("/item", ii.getName(reward.itemId()));
               MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, SimpleMessage.from(msg));
            }
            break;
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }
}
