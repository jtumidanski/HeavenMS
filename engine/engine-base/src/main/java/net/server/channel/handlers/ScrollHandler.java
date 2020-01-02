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
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ScrollResult;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ScrollPacket;
import net.server.channel.packet.reader.ScrollReader;
import server.MapleItemInformationProvider;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowScrollEffect;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;

public final class ScrollHandler extends AbstractPacketHandler<ScrollPacket> {
   @Override
   public Class<ScrollReader> getReaderClass() {
      return ScrollReader.class;
   }

   private static void announceCannotScroll(MapleClient c, boolean legendarySpirit) {
      if (legendarySpirit) {
         PacketCreator.announce(c, new ShowScrollEffect(c.getPlayer().getId(), ScrollResult.FAIL, false, false));
      } else {
         PacketCreator.announce(c, new InventoryFull());
      }
   }

   private static boolean canScroll(int scrollId, int itemId) {
      int sid = scrollId / 100;

      if (sid == 20492) { //scroll for accessory (pendant, belt, ring)
         return canScroll(2041100, itemId) || canScroll(2041200, itemId) || canScroll(2041300, itemId);
      }
      return (scrollId / 100) % 100 == (itemId / 10000) % 100;
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

            byte oldLevel = toScroll.level();
            byte oldSlots = (byte) toScroll.slots();
            MapleInventory useInventory = chr.getInventory(MapleInventoryType.USE);
            Item scroll = useInventory.getItem(packet.slot());
            Item whiteScroll = null;

            if (ItemConstants.isCleanSlate(scroll.id())) {
               Map<String, Integer> eqStats = ii.getEquipStats(toScroll.id());
               if (eqStats == null || eqStats.get("tuc") == 0) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }
            } else if (!ItemConstants.isModifierScroll(scroll.id()) && toScroll.slots() < 1) {
               announceCannotScroll(client, hasLegendarySpirit);
               return;
            }

            List<Integer> scrollReqs = ii.getScrollReqs(scroll.id());
            if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.id())) {
               announceCannotScroll(client, hasLegendarySpirit);
               return;
            }
            if (isWhiteScroll) {
               whiteScroll = useInventory.findById(2340000);
               if (whiteScroll == null) {
                  isWhiteScroll = false;
               }
            }

            if (!ItemConstants.isChaosScroll(scroll.id()) && !ItemConstants.isCleanSlate(scroll.id())) {
               if (!canScroll(scroll.id(), toScroll.id())) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }
            }

            if (ItemConstants.isCleanSlate(scroll.id()) && !ii.canUseCleanSlate(toScroll)) {
               announceCannotScroll(client, hasLegendarySpirit);
               return;
            }

            Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll.id(), isWhiteScroll, 0, chr.isGM());
            ScrollResult scrollSuccess = ScrollResult.FAIL; // fail
            if (scrolled == null) {
               scrollSuccess = ScrollResult.CURSE;
            } else if (scrolled.level() > oldLevel || (ItemConstants.isCleanSlate(scroll.id()) && scrolled.slots() == oldSlots + 1) || ItemConstants.isFlagModifier(scroll.id(), scrolled.flag())) {
               scrollSuccess = ScrollResult.SUCCESS;
            }

            useInventory.lockInventory();
            try {
               if (scroll.quantity() < 1) {
                  announceCannotScroll(client, hasLegendarySpirit);
                  return;
               }

               if (isWhiteScroll && !ItemConstants.isCleanSlate(scroll.id())) {
                  if (whiteScroll.quantity() < 1) {
                     announceCannotScroll(client, hasLegendarySpirit);
                     return;
                  }

                  MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, whiteScroll.position(), (short) 1, false, false);
               }

               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, scroll.position(), (short) 1, false);
            } finally {
               useInventory.unlockInventory();
            }

            final List<ModifyInventory> mods = new ArrayList<>();
            if (scrollSuccess == ScrollResult.CURSE) {
               if (!ItemConstants.isWeddingRing(toScroll.id())) {
                  mods.add(new ModifyInventory(3, toScroll));
                  if (packet.destination() < 0) {
                     MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIPPED);

                     inv.lockInventory();
                     try {
                        chr.unequippedItem(toScroll);
                        inv.removeItem(toScroll.position());
                     } finally {
                        inv.unlockInventory();
                     }
                  } else {
                     MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIP);

                     inv.lockInventory();
                     try {
                        inv.removeItem(toScroll.position());
                     } finally {
                        inv.unlockInventory();
                     }
                  }
               } else {
                  scrolled = toScroll;
                  scrollSuccess = ScrollResult.FAIL;

                  mods.add(new ModifyInventory(3, scrolled));
                  mods.add(new ModifyInventory(0, scrolled));
               }
            } else {
               mods.add(new ModifyInventory(3, scrolled));
               mods.add(new ModifyInventory(0, scrolled));
            }
            PacketCreator.announce(client, new ModifyInventoryPacket(true, mods));
            ScrollResult finalScrollSuccess = scrollSuccess;
            boolean finalHasLegendarySpirit = hasLegendarySpirit;
            boolean finalIsWhiteScroll = isWhiteScroll;
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowScrollEffect(chr.getId(), finalScrollSuccess, finalHasLegendarySpirit, finalIsWhiteScroll));
            if (packet.destination() < 0 && (scrollSuccess == ScrollResult.SUCCESS || scrollSuccess == ScrollResult.CURSE)) {
               chr.equipChanged();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
