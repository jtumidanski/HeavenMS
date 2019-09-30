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

import java.util.List;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemRewardPacket;
import net.server.channel.packet.reader.ItemRewardReader;
import server.MapleItemInformationProvider;
import server.MapleItemInformationProvider.RewardItem;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

/**
 * @author Jay Estrella
 * @author kevintjuh93
 */
public final class ItemRewardHandler extends AbstractPacketHandler<ItemRewardPacket> {
   @Override
   public Class<ItemRewardReader> getReaderClass() {
      return ItemRewardReader.class;
   }

   @Override
   public void handlePacket(ItemRewardPacket packet, MapleClient client) {
      Item it = client.getPlayer().getInventory(MapleInventoryType.USE).getItem(packet.slot());   // null check here thanks to Thora
      if (it == null || it.id() != packet.itemId() || client.getPlayer().getInventory(MapleInventoryType.USE).countById(packet.itemId()) < 1) {
         return;
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Pair<Integer, List<RewardItem>> rewards = ii.getItemReward(packet.itemId());
      for (RewardItem reward : rewards.getRight()) {
         if (!MapleInventoryManipulator.checkSpace(client, reward.itemid, reward.quantity, "")) {
            client.announce(MaplePacketCreator.getShowInventoryFull());
            break;
         }
         if (Randomizer.nextInt(rewards.getLeft()) < reward.prob) {//Is it even possible to get an item with prob 1?
            if (ItemConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
               final Item item = ii.getEquipById(reward.itemid);
               if (reward.period != -1) {
                  item.expiration_(currentServerTime() + (reward.period * 60 * 60 * 10));
               }
               MapleInventoryManipulator.addFromDrop(client, item, false);
            } else {
               MapleInventoryManipulator.addById(client, reward.itemid, reward.quantity, "", -1);
            }
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, packet.itemId(), 1, false, false);
            if (reward.worldmsg != null) {
               String msg = reward.worldmsg;
               msg.replaceAll("/name", client.getPlayer().getName());
               msg.replaceAll("/item", ii.getName(reward.itemid));
               MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.LIGHT_BLUE, msg);
            }
            break;
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }
}
