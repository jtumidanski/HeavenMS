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

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemPacket;
import net.server.channel.packet.reader.UseItemReader;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

/**
 * @author Matze
 */
public final class UseItemHandler extends AbstractPacketHandler<UseItemPacket> {
   @Override
   public Class<UseItemReader> getReaderClass() {
      return UseItemReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (!chr.isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(UseItemPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(packet.slot());
      if (toUse != null && toUse.quantity() > 0 && toUse.id() == packet.itemId()) {
         if (packet.itemId() == 2022178 || packet.itemId() == 2050004) {
            chr.dispelDebuffs();
            remove(client, packet.slot());
            return;
         } else if (packet.itemId() == 2050001) {
            chr.dispelDebuff(MapleDisease.DARKNESS);
            remove(client, packet.slot());
            return;
         } else if (packet.itemId() == 2050002) {
            chr.dispelDebuff(MapleDisease.WEAKEN);
            chr.dispelDebuff(MapleDisease.SLOW);
            remove(client, packet.slot());
            return;
         } else if (packet.itemId() == 2050003) {
            chr.dispelDebuff(MapleDisease.SEAL);
            chr.dispelDebuff(MapleDisease.CURSE);
            remove(client, packet.slot());
            return;
         } else if (ItemConstants.isTownScroll(packet.itemId())) {
            int banMap = chr.getMapId();
            int banSp = chr.getMap().findClosestPlayerSpawnpoint(chr.position()).getId();
            long banTime = currentServerTime();

            if (ii.getItemEffect(toUse.id()).applyTo(chr)) {
               if (YamlConfig.config.server.USE_BANISHABLE_TOWN_SCROLL) {
                  chr.setBanishPlayerData(banMap, banSp, banTime);
               }

               remove(client, packet.slot());
            }
            return;
         } else if (ItemConstants.isAntibanishScroll(packet.itemId())) {
            if (ii.getItemEffect(toUse.id()).applyTo(chr)) {
               remove(client, packet.slot());
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You cannot recover from a banish state at the moment.");
            }
            return;
         }

         remove(client, packet.slot());

         if (toUse.id() != 2022153) {
            ii.getItemEffect(toUse.id()).applyTo(chr);
         } else {
            MapleStatEffect mse = ii.getItemEffect(toUse.id());
            for (MapleCharacter player : chr.getMap().getCharacters()) {
               mse.applyTo(player);
            }
         }
      }
   }

   private void remove(MapleClient client, short slot) {
      MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, slot, (short) 1, false);
      PacketCreator.announce(client, new EnableActions());
   }
}
