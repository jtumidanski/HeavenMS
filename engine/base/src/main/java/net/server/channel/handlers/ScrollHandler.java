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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ScrollPacket;
import net.server.channel.packet.reader.ScrollReader;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

/**
 * @author Matze
 * @author Frz
 */
public final class ScrollHandler extends AbstractPacketHandler<ScrollPacket> {
   @Override
   public Class<ScrollReader> getReaderClass() {
      return ScrollReader.class;
   }

   private static void announceCannotScroll(MapleClient c, boolean legendarySpirit) {
      if (legendarySpirit) {
         c.announce(MaplePacketCreator.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.FAIL, false, false));
      } else {
         c.announce(MaplePacketCreator.getInventoryFull());
      }
   }

   private static boolean canScroll(int scrollid, int itemid) {
      int sid = scrollid / 100;

      if (sid == 20492) { //scroll for accessory (pendant, belt, ring)
         return canScroll(2041100, itemid) || canScroll(2041200, itemid) || canScroll(2041300, itemid);
      }
      return (scrollid / 100) % 100 == (itemid / 10000) % 100;
   }

   @Override
   public void handlePacket(ScrollPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            boolean isWhiteScroll = false; // white scroll being used?
            boolean hasLegendarySpirit = false; // legendary spirit skill
            if ((packet.whiteScroll() & 2) == 2) {
               isWhiteScroll = true;
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleCharacter chr = client.getPlayer();
            Equip toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(packet.destination());

            Optional<Skill> legendarySpirit = SkillFactory.getSkill(1003);
            if (legendarySpirit.isPresent() && chr.getSkillLevel(legendarySpirit.get()) > 0 && packet.destination() >= 0) {
               hasLegendarySpirit = true;
               toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(packet.destination());
            }

            byte oldLevel = toScroll.getLevel();
            byte oldSlots = toScroll.getUpgradeSlots();
            MapleInventory useInventory = chr.getInventory(MapleInventoryType.USE);
            Item scroll = useInventory.getItem(packet.slot());
            Item wscroll = null;

            if (ItemConstants.isCleanSlate(scroll.getItemId())) {
               Map<String, Integer> eqStats = ii.getEquipStats(toScroll.getItemId());  // clean slate issue found thanks to Masterrulax
               if (eqStats == null || eqStats.get("tuc") == 0) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }
            } else if (!ItemConstants.isModifierScroll(scroll.getItemId()) && toScroll.getUpgradeSlots() < 1) {
               announceCannotScroll(client, hasLegendarySpirit);   // thanks onechord for noticing zero upgrade slots freezing Legendary Scroll UI
               return;
            }

            List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
            if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
               announceCannotScroll(client, hasLegendarySpirit);
               return;
            }
            if (isWhiteScroll) {
               wscroll = useInventory.findById(2340000);
               if (wscroll == null) {
                  isWhiteScroll = false;
               }
            }

            if (!ItemConstants.isChaosScroll(scroll.getItemId()) && !ItemConstants.isCleanSlate(scroll.getItemId())) {
               if (!canScroll(scroll.getItemId(), toScroll.getItemId())) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }
            }

            if (ItemConstants.isCleanSlate(scroll.getItemId()) && !ii.canUseCleanSlate(toScroll)) {
               announceCannotScroll(client, hasLegendarySpirit);
               return;
            }

            Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), isWhiteScroll, 0, chr.isGM());
            ScrollResult scrollSuccess = Equip.ScrollResult.FAIL; // fail
            if (scrolled == null) {
               scrollSuccess = Equip.ScrollResult.CURSE;
            } else if (scrolled.getLevel() > oldLevel || (ItemConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() == oldSlots + 1) || ItemConstants.isFlagModifier(scroll.getItemId(), scrolled.getFlag())) {
               scrollSuccess = Equip.ScrollResult.SUCCESS;
            }

            useInventory.lockInventory();
            try {
               if (scroll.getQuantity() < 1) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }

               if (isWhiteScroll && !ItemConstants.isCleanSlate(scroll.getItemId())) {
                  if (wscroll.getQuantity() < 1) {
                     announceCannotScroll(client, hasLegendarySpirit);
                     return;
                  }

                  MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
               }

               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, scroll.getPosition(), (short) 1, false);
            } finally {
               useInventory.unlockInventory();
            }

            final List<ModifyInventory> mods = new ArrayList<>();
            if (scrollSuccess == Equip.ScrollResult.CURSE) {
               if (!ItemConstants.isWeddingRing(toScroll.getItemId())) {
                  mods.add(new ModifyInventory(3, toScroll));
                  if (packet.destination() < 0) {
                     MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIPPED);

                     inv.lockInventory();
                     try {
                        chr.unequippedItem(toScroll);
                        inv.removeItem(toScroll.getPosition());
                     } finally {
                        inv.unlockInventory();
                     }
                  } else {
                     MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIP);

                     inv.lockInventory();
                     try {
                        inv.removeItem(toScroll.getPosition());
                     } finally {
                        inv.unlockInventory();
                     }
                  }
               } else {
                  scrolled = toScroll;
                  scrollSuccess = Equip.ScrollResult.FAIL;

                  mods.add(new ModifyInventory(3, scrolled));
                  mods.add(new ModifyInventory(0, scrolled));
               }
            } else {
               mods.add(new ModifyInventory(3, scrolled));
               mods.add(new ModifyInventory(0, scrolled));
            }
            client.announce(MaplePacketCreator.modifyInventory(true, mods));
            chr.getMap().broadcastMessage(MaplePacketCreator.getScrollEffect(chr.getId(), scrollSuccess, hasLegendarySpirit, isWhiteScroll));
            if (packet.destination() < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE)) {
               chr.equipChanged();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
