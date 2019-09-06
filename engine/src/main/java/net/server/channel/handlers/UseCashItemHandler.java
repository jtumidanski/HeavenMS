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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.creator.veteran.BowmanCreator;
import client.creator.veteran.MagicianCreator;
import client.creator.veteran.PirateCreator;
import client.creator.veteran.ThiefCreator;
import client.creator.veteran.WarriorCreator;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.AssignAPProcessor;
import client.processor.AssignSPProcessor;
import client.processor.DueyProcessor;
import client.processor.NoteProcessor;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.items.UseableCashItems;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.AbstractUseCashItemPacket;
import net.server.channel.packet.UseApResetPacket;
import net.server.channel.packet.UseAvatarMegaphone;
import net.server.channel.packet.UseChalkboardPacket;
import net.server.channel.packet.UseCharacterCreatorPacket;
import net.server.channel.packet.UseCharacterEffectPacket;
import net.server.channel.packet.UseDueyPacket;
import net.server.channel.packet.UseExtendExpirationPacket;
import net.server.channel.packet.UseHammerPacket;
import net.server.channel.packet.UseIncubatorPacket;
import net.server.channel.packet.UseItemMegaphonePacket;
import net.server.channel.packet.UseItemTagPacket;
import net.server.channel.packet.UseJukeboxPacket;
import net.server.channel.packet.UseScissorsKarmaPacket;
import net.server.channel.packet.UseKitePacket;
import net.server.channel.packet.UseMapEffectPacket;
import net.server.channel.packet.UseMapleTvPacket;
import net.server.channel.packet.UseMegaphonePacket;
import net.server.channel.packet.UseItemBagPacket;
import net.server.channel.packet.UseMiuMiuPacket;
import net.server.channel.packet.UseNameChangePacket;
import net.server.channel.packet.UseNotePacket;
import net.server.channel.packet.UseOwlSearchPacket;
import net.server.channel.packet.UsePetConsumePacket;
import net.server.channel.packet.UsePetNameChangePacket;
import net.server.channel.packet.UseSealingLockPacket;
import net.server.channel.packet.UseSpResetPacket;
import net.server.channel.packet.UseSuperMegaphonePacket;
import net.server.channel.packet.UseTeleportRockPacket;
import net.server.channel.packet.UseTripleMegaphonePacket;
import net.server.channel.packet.UseUnhandledPacket;
import net.server.channel.packet.UseVegaSpellPacket;
import net.server.channel.packet.UseWorldChangePacket;
import net.server.channel.packet.reader.UseCashItemReader;
import server.MapleItemInformationProvider;
import server.MaplePacketOpCodes;
import server.MapleShop;
import server.MapleShopFactory;
import server.TimerManager;
import server.maps.AbstractMapleMapObject;
import server.maps.FieldLimit;
import server.maps.MapleKite;
import server.maps.MapleMap;
import server.maps.MaplePlayerShopItem;
import server.maps.MapleTVEffect;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;

public final class UseCashItemHandler extends AbstractPacketHandler<AbstractUseCashItemPacket, UseCashItemReader> {

   private void remove(MapleClient client, short position, int itemId) {
      MapleInventory cashInv = client.getPlayer().getInventory(MapleInventoryType.CASH);
      cashInv.lockInventory();
      try {
         Item it = cashInv.getItem(position);
         if (it == null || it.getItemId() != itemId) {
            it = cashInv.findById(itemId);
            if (it != null) {
               position = it.getPosition();
            }
         }
      } finally {
         cashInv.unlockInventory();
      }

      MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.CASH, position, (short) 1, true, false);
   }

   private boolean getIncubatedItem(MapleClient client, int id) {
      final int[] ids = {1012070, 1302049, 1302063, 1322027, 2000004, 2000005, 2020013, 2020015, 2040307, 2040509, 2040519, 2040521, 2040533, 2040715, 2040717, 2040810, 2040811, 2070005, 2070006, 4020009,};
      final int[] quantitys = {1, 1, 1, 1, 240, 200, 200, 200, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3};
      int amount = 0;
      for (int i = 0; i < ids.length; i++) {
         if (i == id) {
            amount = quantitys[i];
         }
      }
      if (client.getPlayer().getInventory(MapleInventoryType.getByType((byte) (id / 1000000))).isFull()) {
         return false;
      }
      MapleInventoryManipulator.addById(client, id, (short) amount);
      return true;
   }

   @Override
   public Class<UseCashItemReader> getReaderClass() {
      return UseCashItemReader.class;
   }

   @Override
   public final void handlePacket(AbstractUseCashItemPacket packet, MapleClient client) {
      final MapleCharacter player = client.getPlayer();

      long timeNow = currentServerTime();
      if (timeNow - player.getLastUsedCashItem() < 3000) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You have used a cash item recently. Wait a moment, then try again.");
         client.announce(MaplePacketCreator.enableActions());
         return;
      }
      player.setLastUsedCashItem(timeNow);

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      short position = packet.position();
      int itemId = packet.itemId();
      int itemType = itemId / 10000;

      MapleInventory cashInv = player.getInventory(MapleInventoryType.CASH);
      Item toUse = cashInv.getItem(position);
      if (toUse == null || toUse.getItemId() != itemId) {
         toUse = cashInv.findById(itemId);

         if (toUse == null) {
            client.announce(MaplePacketCreator.enableActions());
            return;
         }

         position = toUse.getPosition();
      }

      if (toUse.getQuantity() < 1) {
         client.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (packet instanceof UseTeleportRockPacket) {
         teleportRock(client, player, position, itemId, ((UseTeleportRockPacket) packet).vip(), ((UseTeleportRockPacket) packet).mapId(), ((UseTeleportRockPacket) packet).name());
      } else if (packet instanceof UseApResetPacket) {
         apReset(client, player, position, itemId, ((UseApResetPacket) packet).to(), ((UseApResetPacket) packet).from());
      } else if (packet instanceof UseSpResetPacket) {
         spReset(client, player, position, itemId, ((UseSpResetPacket) packet).to(), ((UseSpResetPacket) packet).from());
      } else if (packet instanceof UseItemTagPacket) {
         itemTag(client, player, position, itemId, ((UseItemTagPacket) packet).slot());
      } else if (packet instanceof UseSealingLockPacket) {
         sealingLock(client, player, position, itemId, ((UseSealingLockPacket) packet).itemType(), ((UseSealingLockPacket) packet).slot());
      } else if (packet instanceof UseIncubatorPacket) {
         incubator(client, player, position, itemId, ((UseIncubatorPacket) packet).itemType(), (short) ((UseIncubatorPacket) packet).slot());
      } else if (packet instanceof UseMegaphonePacket) {
         megaphone(client, player, position, itemId, getMedal(player, ii), ((UseMegaphonePacket) packet).message());
      } else if (packet instanceof UseSuperMegaphonePacket) {
         superMegaphone(client, player, position, itemId, getMedal(player, ii), ((UseSuperMegaphonePacket) packet).message(),
               ((UseSuperMegaphonePacket) packet).ear());
      } else if (packet instanceof UseMapleTvPacket) {
         mapleTv(client, player, position, itemId, ((UseMapleTvPacket) packet).megaMessenger(),
               ((UseMapleTvPacket) packet).ear(), ((UseMapleTvPacket) packet).messages(),
               ((UseMapleTvPacket) packet).characterName(), getMedal(player, ii));
      } else if (packet instanceof UseItemMegaphonePacket) {
         itemMegaphone(client, player, position, itemId, getMedal(player, ii), ((UseItemMegaphonePacket) packet).whisper(),
               ((UseItemMegaphonePacket) packet).message(), ((UseItemMegaphonePacket) packet).selected(),
               ((UseItemMegaphonePacket) packet).inventoryType(), ((UseItemMegaphonePacket) packet).slot());
      } else if (packet instanceof UseTripleMegaphonePacket) {
         tripleMegaphone(client, player, position, itemId, getMedal(player, ii), ((UseTripleMegaphonePacket) packet).lines(), ((UseTripleMegaphonePacket) packet).message(), ((UseTripleMegaphonePacket) packet).whisper());
      } else if (packet instanceof UseKitePacket) {
         kite(client, player, position, itemId, ((UseKitePacket) packet).message());
      } else if (packet instanceof UseNotePacket) {
         note(client, player, position, itemId, ((UseNotePacket) packet).to(), ((UseNotePacket) packet).message());
      } else if (packet instanceof UseJukeboxPacket) {
         jukebox(client, player, position, itemId);
      } else if (packet instanceof UseMapEffectPacket) {
         mapEffect(client, player, ii, position, itemId, ((UseMapEffectPacket) packet).message());
      } else if (packet instanceof UsePetNameChangePacket) {
         petNameChange(client, player, position, itemId, ((UsePetNameChangePacket) packet).newName());
      } else if (packet instanceof UseItemBagPacket) {
         mesoBag(client, player, ii, position, itemId);
      } else if (packet instanceof UseOwlSearchPacket) {
         owlSearch(client, player, position, itemId, ((UseOwlSearchPacket) packet).searchedItemId());
      } else if (packet instanceof UsePetConsumePacket) {
         petConsume(client, player, position, itemId);
      } else if (packet instanceof UseCharacterEffectPacket) {
         characterEffect(client, player, ii, position, itemId);
      } else if (packet instanceof UseDueyPacket) {
         duey(client);
      } else if (packet instanceof UseChalkboardPacket) {
         chalkboard(player, ((UseChalkboardPacket) packet).message());
      } else if (packet instanceof UseAvatarMegaphone) {
         avatarMega(client, player, position, itemId, getMedal(player, ii), ((UseAvatarMegaphone) packet).messages(), ((UseAvatarMegaphone) packet).ear());
      } else if (packet instanceof UseNameChangePacket) {
         nameChange(client, player, position, itemId);
      } else if (packet instanceof UseWorldChangePacket) {
         worldChange(client, player, position, itemId);
      } else if (packet instanceof UseCharacterCreatorPacket) {
         characterCreator(client, player, position, itemId, ((UseCharacterCreatorPacket) packet).name(),
               ((UseCharacterCreatorPacket) packet).face(), ((UseCharacterCreatorPacket) packet).hair(),
               ((UseCharacterCreatorPacket) packet).hairColor(), ((UseCharacterCreatorPacket) packet).skin(),
               ((UseCharacterCreatorPacket) packet).gender(), ((UseCharacterCreatorPacket) packet).jobId(),
               ((UseCharacterCreatorPacket) packet).improveSp());
      } else if (packet instanceof UseMiuMiuPacket) {
         miuMiu(client, player, position, itemId);
      } else if (packet instanceof UseExtendExpirationPacket) {
         extendExpiration(client);
      } else if (packet instanceof UseScissorsKarmaPacket) {
         scissorsOfKarma(client, player, ii, position, itemId, ((UseScissorsKarmaPacket) packet).itemType(), (short) ((UseScissorsKarmaPacket) packet).slot());
      } else if (packet instanceof UseHammerPacket) {
         hammer(client, player, position, itemId, (short) ((UseHammerPacket) packet).slot());
      } else if (packet instanceof UseVegaSpellPacket) { //VEGA'S SPELL
         vegaSpell(client, player, ii, position, itemId, ((UseVegaSpellPacket) packet).firstCheck(),
               ((UseVegaSpellPacket) packet).equipSlot(), ((UseVegaSpellPacket) packet).secondCheck(),
               ((UseVegaSpellPacket) packet).useSlot());
      } else if (packet instanceof UseUnhandledPacket) {
         System.out.println("NEW CASH ITEM: " + itemType + "\n" + ((UseUnhandledPacket) packet).message());
         client.announce(MaplePacketCreator.enableActions());
      } else {
         System.out.println("NEW CASH ITEM: " + itemType + "\n");
         client.announce(MaplePacketCreator.enableActions());
      }
   }

   private String getMedal(MapleCharacter player, MapleItemInformationProvider ii) {
      String medal = "";
      Item medalItem = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
      if (medalItem != null) {
         medal = "<" + ii.getName(medalItem.getItemId()) + "> ";
      }
      return medal;
   }

   private void characterEffect(MapleClient c, MapleCharacter player, MapleItemInformationProvider ii, short position, int itemId) {
      ii.getItemEffect(itemId).applyTo(player);
      remove(c, position, itemId);
   }

   //TODO better variable names
   private void vegaSpell(MapleClient c, MapleCharacter player, MapleItemInformationProvider ii, short position, int itemId, boolean first1, byte eSlot, boolean second1, byte uSlot) {
      if (first1) {
         return;
      }

      final Item eitem = player.getInventory(MapleInventoryType.EQUIP).getItem(eSlot);

      if (second1) {
         return;
      }

      final Item uitem = player.getInventory(MapleInventoryType.USE).getItem(uSlot);
      if (eitem == null || uitem == null) {
         return;
      }

      Equip toScroll = (Equip) eitem;
      if (toScroll.getUpgradeSlots() < 1) {
         c.announce(MaplePacketCreator.getInventoryFull());
         return;
      }

      //should have a check here against PE hacks
      if (itemId / 1000000 != 5) {
         itemId = 0;
      }

      player.toggleBlockCashShop();

      final int currentLevel = toScroll.getLevel();
      c.announce(MaplePacketCreator.sendVegaScroll(MaplePacketOpCodes.VegaScroll.FORTY));

      final Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, uitem.getItemId(), false, itemId, player.isGM());
      c.announce(MaplePacketCreator.sendVegaScroll(scrolled.getLevel() > currentLevel ? MaplePacketOpCodes.VegaScroll.FORTY_ONE : MaplePacketOpCodes.VegaScroll.FORTY_THREE));

      MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, uSlot, (short) 1, false);
      remove(c, position, itemId);

      final MapleClient client = c;
      TimerManager.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!player.isLoggedin()) {
               return;
            }

            player.toggleBlockCashShop();

            final List<ModifyInventory> mods = new ArrayList<>();
            mods.add(new ModifyInventory(3, scrolled));
            mods.add(new ModifyInventory(0, scrolled));
            client.announce(MaplePacketCreator.modifyInventory(true, mods));

            ScrollResult scrollResult = scrolled.getLevel() > currentLevel ? ScrollResult.SUCCESS : ScrollResult.FAIL;
            player.getMap().broadcastMessage(MaplePacketCreator.getScrollEffect(player.getId(), scrollResult, false, false));
            if (eSlot < 0 && (scrollResult == ScrollResult.SUCCESS)) {
               player.equipChanged();
            }

            client.announce(MaplePacketCreator.enableActions());
         }
      }, 1000 * 3);
   }

   private void hammer(MapleClient c, MapleCharacter player, short position, int itemId, short itemSlot) {
      final Equip equip = (Equip) player.getInventory(MapleInventoryType.EQUIP).getItem(itemSlot);
      if (equip.getVicious() >= 2 || player.getInventory(MapleInventoryType.CASH).findById(5570000) == null) {
         return;
      }
      equip.setVicious(equip.getVicious() + 1);
      equip.setUpgradeSlots(equip.getUpgradeSlots() + 1);
      remove(c, position, itemId);
      c.announce(MaplePacketCreator.enableActions());
      c.announce(MaplePacketCreator.sendHammerData(equip.getVicious()));
      player.forceUpdateItem(equip);
   }

   private void scissorsOfKarma(MapleClient c, MapleCharacter player, MapleItemInformationProvider ii, short position, int itemId, byte inventoryType, short slot) {
      MapleInventoryType type = MapleInventoryType.getByType(inventoryType);
      Item item = player.getInventory(type).getItem(slot);
      if (item == null || item.getQuantity() <= 0 || MapleKarmaManipulator.hasKarmaFlag(item) || !ii.isKarmaAble(item.getItemId())) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      MapleKarmaManipulator.setKarmaFlag(item);
      player.forceUpdateItem(item);
      remove(c, position, itemId);
      c.announce(MaplePacketCreator.enableActions());
   }

   private void extendExpiration(MapleClient c) {
      /*
      5500000??
      5500001 - [7days]Magical Sandglass - Drag and drop this onto a piece of equipment that has a time limit to extend the time limit by #c7days#. #This cannot be used on cash items, and the time limit cannot be extended past 30 days, starting from today.#
      5500002 - [20days]Magical Sandglass - Drag and drop this onto a piece of equipment that has a time limit to extend the time limit by #c20days#. #This cannot be used on cash items, and the time limit cannot be extended past 30 days, starting from today.#
       */
      //TODO does this not work?
      c.announce(MaplePacketCreator.enableActions());
   }

   private void miuMiu(MapleClient c, MapleCharacter player, short position, int itemId) {
      if (player.getShop() == null) {
         MapleShop shop = MapleShopFactory.getInstance().getShop(1338);
         if (shop != null) {
            shop.sendShop(c);
            remove(c, position, itemId);
         }
      } else {
         c.announce(MaplePacketCreator.enableActions());
      }
   }

   private void characterCreator(MapleClient c, MapleCharacter player, short position, int itemId, String name, int face, int hair, int hairColor, int skin, int gender, int jobId, int improveSp) {
      if (UseableCashItems.CharacterCreators.MAPLE_LIFE_B.is(itemId) && !c.gainCharacterSlot()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You have already used up all 12 extra character slots.");
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      int createStatus;
      switch (jobId) {
         case 0:
            createStatus = WarriorCreator.createCharacter(c, name, face, hair + hairColor, skin, gender, improveSp);
            break;

         case 1:
            createStatus = MagicianCreator.createCharacter(c, name, face, hair + hairColor, skin, gender, improveSp);
            break;

         case 2:
            createStatus = BowmanCreator.createCharacter(c, name, face, hair + hairColor, skin, gender, improveSp);
            break;

         case 3:
            createStatus = ThiefCreator.createCharacter(c, name, face, hair + hairColor, skin, gender, improveSp);
            break;

         default:
            createStatus = PirateCreator.createCharacter(c, name, face, hair + hairColor, skin, gender, improveSp);
      }

      if (createStatus == 0) {
         c.announce(MaplePacketCreator.sendMapleLifeError(0));   // success!

         player.showHint("#bSuccess#k on creation of the new character through the Maple Life card.");
         remove(c, position, itemId);
      } else {
         if (createStatus == -1) {    // check name
            c.announce(MaplePacketCreator.sendMapleLifeNameError());
         } else {
            c.announce(MaplePacketCreator.sendMapleLifeError(-1 * createStatus));
         }
      }
   }

   private void nameChange(MapleClient c, MapleCharacter player, short position, int itemId) {
      c.announce(MaplePacketCreator.showNameChangeCancel(player.cancelPendingNameChange()));
      remove(c, position, itemId);
      c.announce(MaplePacketCreator.enableActions());
   }

   private void worldChange(MapleClient c, MapleCharacter player, short position, int itemId) {
      c.announce(MaplePacketCreator.showWorldTransferCancel(player.cancelPendingWorldTranfer()));
      remove(c, position, itemId);
      c.announce(MaplePacketCreator.enableActions());
   }

   private void avatarMega(MapleClient c, MapleCharacter player, short position, int itemId, String medal, String[] messages, Boolean ear) {
      final int world = c.getWorld();
      Server.getInstance().broadcastMessage(world, MaplePacketCreator.getAvatarMega(player, medal, c.getChannel(), itemId, Arrays.asList(messages), ear));
      TimerManager.getInstance().schedule(() -> Server.getInstance().broadcastMessage(world, MaplePacketCreator.byeAvatarMega()), 1000 * 10);
      remove(c, position, itemId);
   }

   private void chalkboard(MapleCharacter player, String message) {
      if (GameConstants.isFreeMarketRoom(player.getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You cannot use the chalkboard here.");
         player.getClient().announce(MaplePacketCreator.enableActions());
         return;
      }

      player.setChalkboard(message);
      player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));
      player.getClient().announce(MaplePacketCreator.enableActions());
   }

   private void duey(MapleClient c) {
      DueyProcessor.dueySendTalk(c, true);
   }

   private void petConsume(MapleClient c, MapleCharacter player, short position, int itemId) {
      for (byte i = 0; i < 3; i++) {
         MaplePet pet = player.getPet(i);
         if (pet != null) {
            Pair<Integer, Boolean> p = pet.canConsume(itemId);

            if (p.getRight()) {
               pet.gainClosenessFullness(player, p.getLeft(), 100, 1);
               remove(c, position, itemId);
               break;
            }
         } else {
            break;
         }
      }
      c.announce(MaplePacketCreator.enableActions());
   }

   private void owlSearch(MapleClient c, MapleCharacter player, short position, int itemId, int searchedItemId) {
      if (!ServerConstants.USE_ENFORCE_ITEM_SUGGESTION) {
         c.getWorldServer().addOwlItemSearch(searchedItemId);
      }
      player.setOwlSearch(searchedItemId);
      List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> hmsAvailable = c.getWorldServer().getAvailableItemBundles(searchedItemId);
      if (!hmsAvailable.isEmpty()) {
         remove(c, position, itemId);
      }

      c.announce(MaplePacketCreator.owlOfMinerva(c, searchedItemId, hmsAvailable));
      c.announce(MaplePacketCreator.enableActions());
   }

   private void mesoBag(MapleClient c, MapleCharacter player, MapleItemInformationProvider ii, short position, int itemId) {
      player.gainMeso(ii.getMeso(itemId), true, false, true);
      remove(c, position, itemId);
      c.announce(MaplePacketCreator.enableActions());
   }

   private void petNameChange(MapleClient c, MapleCharacter player, short position, int itemId, String newName) {
      MaplePet pet = player.getPet(0);
      if (pet == null) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }
      pet.setName(newName);
      pet.saveToDb();

      Item item = player.getInventory(MapleInventoryType.CASH).getItem(pet.getPosition());
      if (item != null) {
         player.forceUpdateItem(item);
      }

      player.getMap().broadcastMessage(player, MaplePacketCreator.changePetName(player, newName, 1), true);
      c.announce(MaplePacketCreator.enableActions());
      remove(c, position, itemId);
   }

   private void mapEffect(MapleClient c, MapleCharacter player, MapleItemInformationProvider ii, short position, int itemId, String message) {
      if (ii.getStateChangeItem(itemId) != 0) {
         for (MapleCharacter mChar : player.getMap().getCharacters()) {
            ii.getItemEffect(ii.getStateChangeItem(itemId)).applyTo(mChar);
         }
      }
      player.getMap().startMapEffect(ii.getMsg(itemId).replaceFirst("%s", player.getName()).replaceFirst("%s", message), itemId);
      remove(c, position, itemId);
   }

   private void jukebox(MapleClient c, MapleCharacter player, short position, int itemId) {
      player.getMap().broadcastMessage(MaplePacketCreator.musicChange("Jukebox/Congratulation"));
      remove(c, position, itemId);
   }

   private void note(MapleClient c, MapleCharacter player, short position, int itemId, String to, String message) {
      NoteProcessor.getInstance().sendNote(to, player.getName(), message, (byte) 0);
      remove(c, position, itemId);
   }

   private void kite(MapleClient c, MapleCharacter player, short position, int itemId, String message) {
      MapleKite kite = new MapleKite(player, message, itemId);

      if (!GameConstants.isFreeMarketRoom(player.getMapId())) {
         player.getMap().spawnKite(kite);
         remove(c, position, itemId);
      } else {
         c.announce(MaplePacketCreator.sendCannotSpawnKite());
      }
   }

   private void megaphone(MapleClient c, MapleCharacter player, short position, int itemId, String medal, String message) {
      if (player.getLevel() > 9) {
         MessageBroadcaster.getInstance().sendServerNotice(player.getClient().getChannelServer().getPlayerStorage().getAllCharacters(), ServerNoticeType.MEGAPHONE, medal + player.getName() + " : " + message);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You may not use this until you're level 10.");
         return;
      }
      remove(c, position, itemId);
   }

   private void superMegaphone(MapleClient c, MapleCharacter player, short position, int itemId, String medal, String message, Boolean ear) {
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), c.getChannel(), ServerNoticeType.SUPER_MEGAPHONE, medal + player.getName() + " : " + message, ear);
      remove(c, position, itemId);
   }

   private void mapleTv(MapleClient c, MapleCharacter player, short position, int itemId, Boolean megaMessenger, Boolean ear, String[] messages, String victimName, String medal) {
      MapleCharacter victim = null;

      int tvType = itemId % 10;
      if (tvType != 1) {
         if (tvType != 4) {
            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(victimName).orElse(null);
         }
      }

      if (!MapleTVEffect.broadcastMapleTVIfNotActive(player, victim, Arrays.asList(messages), tvType)) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "MapleTV is already in use.");
         return;
      }

      if (megaMessenger) {

         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), c.getChannel(), ServerNoticeType.SUPER_MEGAPHONE, medal + player.getName() + " : " + String.join(" ", messages), ear);
      }

      remove(c, position, itemId);
   }

   private void itemMegaphone(MapleClient c, MapleCharacter player, short position, int itemId, String medal, boolean whisper, String message, Boolean selected, byte inventoryType, short slot) {
      String msg = medal + player.getName() + " : " + message;
      Item item = null;
      if (selected) { //item
         item = player.getInventory(MapleInventoryType.getByType(inventoryType)).getItem(slot);
         if (item == null) {
            return;
         }
      }
      Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.itemMegaphone(msg, whisper, c.getChannel(), item));

      remove(c, position, itemId);
   }

   private void tripleMegaphone(MapleClient c, MapleCharacter player, short position, int itemId, String medal, int lines, String[] message, Boolean whisper) {
      if (lines < 1 || lines > 3) {
         return;
      }
      String[] msg2 = new String[lines];
      for (int i = 0; i < lines; i++) {
         msg2[i] = medal + player.getName() + " : " + message[i];
      }
      Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.getMultiMegaphone(msg2, c.getChannel(), whisper));
      remove(c, position, itemId);
   }

   private void itemTag(MapleClient c, MapleCharacter player, short position, int itemId, int equipSlot) {
      if (equipSlot == 0) {
         return;
      }
      Item eq = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) equipSlot);
      eq.setOwner(player.getName());
      player.forceUpdateItem(eq);
      remove(c, position, itemId);
   }

   private void sealingLock(MapleClient c, MapleCharacter player, short position, int itemId, byte inventoryType, int equipSlot) {
      Item eq;
      MapleInventoryType type = MapleInventoryType.getByType(inventoryType);
      eq = player.getInventory(type).getItem((short) equipSlot);
      if (eq == null) { //Check if the type is EQUIPMENT?
         return;
      }
      short flag = eq.getFlag();
      flag |= ItemConstants.LOCK;
      if (eq.getExpiration() > -1) {
         return; //No perma items pls
      }
      eq.setFlag(flag);

      long period = 0;
      if (UseableCashItems.ItemAugmenters.ITEM_GUARD_7.is(itemId)) {
         period = 7;
      } else if (UseableCashItems.ItemAugmenters.ITEM_GUARD_30.is(itemId)) {
         period = 30;
      } else if (UseableCashItems.ItemAugmenters.ITEM_GUARD_90.is(itemId)) {
         period = 90;
      } else if (UseableCashItems.ItemAugmenters.ITEM_GUARD_365.is(itemId)) {
         period = 365;
      }

      if (period > 0) {
         eq.setExpiration(currentServerTime() + (period * 60 * 60 * 24 * 1000));
      }

      remove(c, position, itemId);
      player.forceUpdateItem(eq);
   }

   private void incubator(MapleClient c, MapleCharacter player, short position, int itemId, byte inventory2, short slot2) {
      Item item2 = player.getInventory(MapleInventoryType.getByType(inventory2)).getItem(slot2);
      if (item2 == null) {
         // hacking
         return;
      }
      if (getIncubatedItem(c, itemId)) {
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(inventory2), slot2, (short) 1, false);
         remove(c, position, itemId);
      }
   }

   private void apReset(MapleClient c, MapleCharacter player, short position, int itemId, int to, int from) {
      if (!player.isAlive()) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (!AssignAPProcessor.APResetAction(c, from, to)) {
         return;
      }
      remove(c, position, itemId);
   }

   private void spReset(MapleClient c, MapleCharacter player, short position, int itemId, int to, int from) {
      if (!player.isAlive()) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      if (!AssignSPProcessor.canSPAssign(c, to)) {  // exploit found thanks to Arnah
         return;
      }

      Optional<Skill> skillSPTo = SkillFactory.getSkill(to);
      Optional<Skill> skillSPFrom = SkillFactory.getSkill(from);

      if (skillSPTo.isPresent() && skillSPFrom.isPresent()) {
         byte curLevel = player.getSkillLevel(skillSPTo.get());
         byte curLevelSPFrom = player.getSkillLevel(skillSPFrom.get());
         if ((curLevel < skillSPTo.get().getMaxLevel()) && curLevelSPFrom > 0) {
            player.changeSkillLevel(skillSPFrom.get(), (byte) (curLevelSPFrom - 1), player.getMasterLevel(skillSPFrom.get()), -1);
            player.changeSkillLevel(skillSPTo.get(), (byte) (curLevel + 1), player.getMasterLevel(skillSPTo.get()), -1);
         }
      }
      remove(c, position, itemId);
   }

   private void teleportRock(MapleClient c, MapleCharacter player, short position, int itemId, boolean vip, int mapId, String name) {
      String error1 = "Either the player could not be found or you were trying to teleport to an illegal location.";
      remove(c, position, itemId);
      boolean success = false;
      if (!vip) {
         if (itemId / 1000 >= 5041 || mapId / 100000000 == player.getMapId() / 100000000) { //check vip or same continent
            MapleMap targetMap = c.getChannelServer().getMapFactory().getMap(mapId);
            if (!FieldLimit.CANNOTVIPROCK.check(targetMap.getFieldLimit()) && (targetMap.getForcedReturnId() == 999999999 || mapId < 100000000)) {
               player.forceChangeMap(targetMap, targetMap.getRandomPlayerSpawnpoint());
               success = true;
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, error1);
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You cannot teleport between continents with this teleport rock.");
         }
      } else {
         Optional<MapleCharacter> victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
         if (victim.isPresent()) {
            MapleMap targetMap = victim.get().getMap();
            if (!FieldLimit.CANNOTVIPROCK.check(targetMap.getFieldLimit()) && (targetMap.getForcedReturnId() == 999999999 || targetMap.getId() < 100000000)) {
               if (!victim.get().isGM() || victim.get().gmLevel() <= player.gmLevel()) {   // thanks Yoboes for noticing non-GM's being unreachable through rocks
                  player.forceChangeMap(targetMap, targetMap.findClosestPlayerSpawnpoint(victim.get().getPosition()));
                  success = true;
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, error1);
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You cannot teleport to this map.");
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "Player could not be found in this channel.");
         }
      }

      if (!success) {
         MapleInventoryManipulator.addById(c, itemId, (short) 1);
         c.announce(MaplePacketCreator.enableActions());
      }
   }
}
