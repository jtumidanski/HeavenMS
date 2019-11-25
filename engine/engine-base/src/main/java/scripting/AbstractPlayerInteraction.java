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
package scripting;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleQuestStatus;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.BetterItemFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryProof;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import net.server.Server;
import net.server.guild.MapleGuild;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.event.EventInstanceManager;
import scripting.event.EventManager;
import scripting.npc.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.MapleMarriage;
import server.expeditions.MapleExpedition;
import server.expeditions.MapleExpeditionBossLog;
import server.expeditions.MapleExpeditionType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.partyquest.PartyQuest;
import server.partyquest.Pyramid;
import server.quest.MapleQuest;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.DojoWarpUp;
import tools.packet.GetEnergy;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.field.effect.MapEffect;
import tools.packet.field.effect.MapSound;
import tools.packet.field.effect.MusicChange;
import tools.packet.field.effect.ShowEffect;
import tools.packet.foreigneffect.ShowGuideHint;
import tools.packet.foreigneffect.ShowGuideTalk;
import tools.packet.foreigneffect.ShowHint;
import tools.packet.foreigneffect.ShowTitleEarned;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.npctalk.GetNPCTalk;
import tools.packet.remove.RemoveItem;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowIntro;
import tools.packet.showitemgaininchat.ShowItemGainInChat;
import tools.packet.spawn.SpawnGuide;
import tools.packet.spawn.SpawnNPC;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.GetItemMessage;
import tools.packet.statusinfo.ShowInfoText;
import tools.packet.ui.DisableMiniMap;
import tools.packet.ui.DisableUI;
import tools.packet.ui.LockUI;
import tools.packet.ui.OpenUI;

public class AbstractPlayerInteraction {

   public MapleClient c;

   public AbstractPlayerInteraction(MapleClient c) {
      this.c = c;
   }

   public MapleClient getClient() {
      return c;
   }

   public MapleCharacter getPlayer() {
      return c.getPlayer();
   }

   public MapleCharacter getChar() {
      return c.getPlayer();
   }

   public int getJobId() {
      return getPlayer().getJob().getId();
   }

   public MapleJob getJob() {
      return getPlayer().getJob();
   }

   public int getLevel() {
      return getPlayer().getLevel();
   }

   public MapleMap getMap() {
      return c.getPlayer().getMap();
   }

   public int getHourOfDay() {
      return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
   }

   public int getMarketPortalId(int mapId) {
      return getMarketPortalId(getWarpMap(mapId));
   }

   private int getMarketPortalId(MapleMap map) {
      return (map.findMarketPortal() != null) ? map.findMarketPortal().getId() : map.getRandomPlayerSpawnpoint().getId();
   }

   public void warp(int mapid) {
      getPlayer().changeMap(mapid);
   }

   public void warp(int map, int portal) {
      getPlayer().changeMap(map, portal);
   }

   public void warp(int map, String portal) {
      getPlayer().changeMap(map, portal);
   }

   public void warpMap(int map) {
      getPlayer().getMap().warpEveryone(map);
   }

   public void warpParty(int id) {
      warpParty(id, 0);
   }

   public void warpParty(int id, int portalId) {
      int mapid = getMapId();
      warpParty(id, portalId, mapid, mapid);
   }

   public void warpParty(int id, int fromMinId, int fromMaxId) {
      warpParty(id, 0, fromMinId, fromMaxId);
   }

   public void warpParty(int id, int portalId, int fromMinId, int fromMaxId) {
      for (MapleCharacter mc : this.getPlayer().getPartyMembersOnline()) {
         if (mc.isLoggedinWorld()) {
            if (mc.getMapId() >= fromMinId && mc.getMapId() <= fromMaxId) {
               mc.changeMap(id, portalId);
            }
         }
      }
   }

   public MapleMap getWarpMap(int map) {
      return getPlayer().getWarpMap(map);
   }

   public MapleMap getMap(int map) {
      return getWarpMap(map);
   }

   public int countAllMonstersOnMap(int map) {
      return getMap(map).countMonsters();
   }

   public int countMonster() {
      return getPlayer().getMap().countMonsters();
   }

   public void resetMapObjects(int mapid) {
      getWarpMap(mapid).resetMapObjects();
   }

   public EventManager getEventManager(String event) {
      return getClient().getEventManager(event);
   }

   public EventInstanceManager getEventInstance() {
      return getPlayer().getEventInstance();
   }

   public MapleInventory getInventory(int type) {
      return getPlayer().getInventory(MapleInventoryType.getByType((byte) type));
   }

   public MapleInventory getInventory(MapleInventoryType type) {
      return getPlayer().getInventory(type);
   }

   public boolean hasItem(int itemid) {
      return haveItem(itemid, 1);
   }

   public boolean hasItem(int itemid, int quantity) {
      return haveItem(itemid, quantity);
   }

   public boolean haveItem(int itemid) {
      return haveItem(itemid, 1);
   }

   public boolean haveItem(int itemid, int quantity) {
      return getPlayer().getItemQuantity(itemid, false) >= quantity;
   }

   public int getItemQuantity(int itemid) {
      return getPlayer().getItemQuantity(itemid, false);
   }

   public boolean haveItemWithId(int itemid) {
      return haveItemWithId(itemid, false);
   }

   public boolean haveItemWithId(int itemid, boolean checkEquipped) {
      return getPlayer().haveItemWithId(itemid, checkEquipped);
   }

   public boolean canHold(int itemid) {
      return canHold(itemid, 1);
   }

   public boolean canHold(int itemid, int quantity) {
      return canHoldAll(Collections.singletonList(itemid), Collections.singletonList(quantity), true);
   }

   public boolean canHold(int itemid, int quantity, int removeItemid, int removeQuantity) {
      return canHoldAllAfterRemoving(Collections.singletonList(itemid), Collections.singletonList(quantity), Collections.singletonList(removeItemid), Collections.singletonList(removeQuantity));
   }

   private List<Integer> convertToIntegerArray(List<Object> list) {
      List<Integer> intList = new ArrayList<>();      // JAVA 7 Rhino script engine. Thanks Bruno, felipepm10 for noticing a typecast issue here.

      for (Object d : list) {
         intList.add((Integer) d);
      }

      return intList;
   }

   public boolean canHoldAll(List<Object> itemids) {
      List<Object> quantity = new LinkedList<>();

      Integer intOne = 1;

      for (int i = 0; i < itemids.size(); i++) {
         quantity.add(intOne);
      }

      return canHoldAll(itemids, quantity);
   }

   public boolean canHoldAll(List<Object> itemids, List<Object> quantity) {
      return canHoldAll(convertToIntegerArray(itemids), convertToIntegerArray(quantity), true);
   }

   private boolean canHoldAll(List<Integer> itemids, List<Integer> quantity, boolean isInteger) {
      int size = Math.min(itemids.size(), quantity.size());

      List<Pair<Item, MapleInventoryType>> addedItems = new LinkedList<>();
      for (int i = 0; i < size; i++) {
         Item it = new Item(itemids.get(i), (short) 0, quantity.get(i).shortValue());
         addedItems.add(new Pair<>(it, ItemConstants.getInventoryType(itemids.get(i))));
      }

      return MapleInventory.checkSpots(c.getPlayer(), addedItems, false);
   }

   private List<Pair<Item, MapleInventoryType>> prepareProofInventoryItems(List<Pair<Integer, Integer>> items) {
      List<Pair<Item, MapleInventoryType>> addedItems = new LinkedList<>();
      for (Pair<Integer, Integer> p : items) {
         Item it = new Item(p.getLeft(), (short) 0, p.getRight().shortValue());
         addedItems.add(new Pair<>(it, MapleInventoryType.CANHOLD));
      }

      return addedItems;
   }

   private List<List<Pair<Integer, Integer>>> prepareInventoryItemList(List<Integer> itemids, List<Integer> quantity) {
      int size = Math.min(itemids.size(), quantity.size());

      List<List<Pair<Integer, Integer>>> invList = new ArrayList<>(6);
      for (int i = MapleInventoryType.UNDEFINED.getType(); i < MapleInventoryType.CASH.getType(); i++) {
         invList.add(new LinkedList<>());
      }

      for (int i = 0; i < size; i++) {
         int itemid = itemids.get(i);
         invList.get(ItemConstants.getInventoryType(itemid).getType()).add(new Pair<>(itemid, quantity.get(i)));
      }

      return invList;
   }

   public boolean canHoldAllAfterRemoving(List<Integer> toAddItemids, List<Integer> toAddQuantity, List<Integer> toRemoveItemids, List<Integer> toRemoveQuantity) {
      List<List<Pair<Integer, Integer>>> toAddItemList = prepareInventoryItemList(toAddItemids, toAddQuantity);
      List<List<Pair<Integer, Integer>>> toRemoveItemList = prepareInventoryItemList(toRemoveItemids, toRemoveQuantity);

      MapleInventoryProof prfInv = (MapleInventoryProof) this.getInventory(MapleInventoryType.CANHOLD);
      prfInv.lockInventory();
      try {
         for (int i = MapleInventoryType.EQUIP.getType(); i < MapleInventoryType.CASH.getType(); i++) {
            List<Pair<Integer, Integer>> toAdd = toAddItemList.get(i);

            if (!toAdd.isEmpty()) {
               List<Pair<Integer, Integer>> toRemove = toRemoveItemList.get(i);

               MapleInventory inv = this.getInventory(i);
               prfInv.cloneContents(inv);

               for (Pair<Integer, Integer> p : toRemove) {
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.CANHOLD, p.getLeft(), p.getRight(), false, false);
               }

               List<Pair<Item, MapleInventoryType>> addItems = prepareProofInventoryItems(toAdd);

               boolean canHold = MapleInventory.checkSpots(c.getPlayer(), addItems, false);
               if (!canHold) {
                  return false;
               }
            }
         }
      } finally {
         prfInv.flushContents();
         prfInv.unlockInventory();
      }

      return true;
   }

   //---- \/ \/ \/ \/ \/ \/ \/  NOT TESTED  \/ \/ \/ \/ \/ \/ \/ \/ \/ ----

   public final MapleQuestStatus getQuestRecord(final int id) {
      return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
   }

   public final MapleQuestStatus getQuestNoRecord(final int id) {
      return c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(id));
   }

   //---- /\ /\ /\ /\ /\ /\ /\  NOT TESTED  /\ /\ /\ /\ /\ /\ /\ /\ /\ ----

   public void openNpc(int npcid) {
      openNpc(npcid, null);
   }

   public void openNpc(int npcid, String script) {
      if (c.getCM() != null) {
         return;
      }

      c.removeClickedNPC();
      NPCScriptManager.getInstance().dispose(c);
      NPCScriptManager.getInstance().start(c, npcid, script, null);
   }

   public int getQuestStatus(int id) {
      return c.getPlayer().getQuest(MapleQuest.getInstance(id)).getStatus().getId();
   }

   private MapleQuestStatus.Status getQuestStat(int id) {
      return c.getPlayer().getQuest(MapleQuest.getInstance(id)).getStatus();
   }

   public boolean isQuestCompleted(int quest) {
      try {
         return getQuestStat(quest) == MapleQuestStatus.Status.COMPLETED;
      } catch (NullPointerException e) {
         e.printStackTrace();
         return false;
      }
   }

   public boolean isQuestActive(int quest) {
      return isQuestStarted(quest);
   }

   public boolean isQuestStarted(int quest) {
      try {
         return getQuestStat(quest) == MapleQuestStatus.Status.STARTED;
      } catch (NullPointerException e) {
         e.printStackTrace();
         return false;
      }
   }

   public void setQuestProgress(int id, String progress) {
      setQuestProgress(id, 0, progress);
   }

   public void setQuestProgress(int id, int progress) {
      setQuestProgress(id, 0, "" + progress);
   }

   public void setQuestProgress(int id, int infoNumber, int progress) {
      setQuestProgress(id, infoNumber, "" + progress);
   }

   public void setQuestProgress(int id, int infoNumber, String progress) {
      c.getPlayer().setQuestProgress(id, infoNumber, progress);
   }

   public String getQuestProgress(int id) {
      return getQuestProgress(id, 0);
   }

   public String getQuestProgress(int id, int infoNumber) {
      MapleQuestStatus qs = getPlayer().getQuest(MapleQuest.getInstance(id));

      if (qs.getInfoNumber() == infoNumber && infoNumber > 0) {
         qs = getPlayer().getQuest(MapleQuest.getInstance(infoNumber));
         infoNumber = 0;
      }

      if (qs != null) {
         return qs.getProgress(infoNumber);
      } else {
         return "";
      }
   }

   public int getQuestProgressInt(int id) {
      try {
         return Integer.valueOf(getQuestProgress(id));
      } catch (NumberFormatException nfe) {
         return 0;
      }
   }

   public int getQuestProgressInt(int id, int infoNumber) {
      try {
         return Integer.valueOf(getQuestProgress(id, infoNumber));
      } catch (NumberFormatException nfe) {
         return 0;
      }
   }

   public void resetAllQuestProgress(int id) {
      MapleQuestStatus qs = getPlayer().getQuest(MapleQuest.getInstance(id));
      if (qs != null) {
         qs.resetAllProgress();
         getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, false);
      }
   }

   public void resetQuestProgress(int id, int infoNumber) {
      MapleQuestStatus qs = getPlayer().getQuest(MapleQuest.getInstance(id));
      if (qs != null) {
         qs.resetProgress(infoNumber);
         getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, qs, false);
      }
   }

   public boolean forceStartQuest(int id) {
      return forceStartQuest(id, 9010000);
   }

   public boolean forceStartQuest(int id, int npc) {
      return startQuest(id, npc);
   }

   public boolean forceCompleteQuest(int id) {
      return forceCompleteQuest(id, 9010000);
   }

   public boolean forceCompleteQuest(int id, int npc) {
      return completeQuest(id, npc);
   }

   public boolean startQuest(short id) {
      return startQuest((int) id);
   }

   public boolean completeQuest(short id) {
      return completeQuest((int) id);
   }

   public boolean startQuest(int id) {
      return startQuest(id, 9010000);
   }

   public boolean completeQuest(int id) {
      return completeQuest(id, 9010000);
   }

   public boolean startQuest(short id, int npcId) {
      return startQuest((int) id, npcId);
   }

   public boolean completeQuest(short id, int npcId) {
      return completeQuest((int) id, npcId);
   }

   public boolean startQuest(int id, int npcId) {
      try {
         return MapleQuest.getInstance(id).forceStart(getPlayer(), npcId);
      } catch (NullPointerException ex) {
         ex.printStackTrace();
         return false;
      }
   }

   public boolean completeQuest(int id, int npcId) {
      try {
         return MapleQuest.getInstance(id).forceComplete(getPlayer(), npcId);
      } catch (NullPointerException ex) {
         ex.printStackTrace();
         return false;
      }
   }

   public Item evolvePet(byte slot, int afterId) {
      MaplePet evolved = null;
      MaplePet target;

      long period = (long) 90 * 24 * 60 * 60 * 1000;    //refreshes expiration date: 90 days

      target = getPlayer().getPet(slot);
      if (target == null) {
         MessageBroadcaster.getInstance().sendServerNotice(getPlayer(), ServerNoticeType.PINK_TEXT, "Pet could not be evolved...");
         return (null);
      }

      Item tmp = gainItem(afterId, (short) 1, false, true, period, target);
            
            /*
            evolved = MaplePet.loadFromDb(tmp.getItemId(), tmp.getPosition(), tmp.getPetId());
            
            evolved = tmp.getPet();
            if(evolved == null) {
                getPlayer().message("Pet structure non-existent for " + tmp.getItemId() + "...");
                return(null);
            }
            else if(tmp.getPetId() == -1) {
                getPlayer().message("Pet id -1");
                return(null);
            }
            
            getPlayer().addPet(evolved);
            
            getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showPet(c.getPlayer(), evolved, false, false), true);
            c.announce(MaplePacketCreator.petStatUpdate(c.getPlayer()));
            c.announce(MaplePacketCreator.enableActions());
            chr.getClient().getWorldServer().registerPetHunger(chr, chr.getPetIndex(evolved));
            */

      MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, target.position(), (short) 1, false);

      return evolved;
   }

   public void gainItem(int id, short quantity) {
      gainItem(id, quantity, false, true);
   }

   public void gainItem(int id, short quantity, boolean show) {//this will fk randomStats equip :P
      gainItem(id, quantity, false, show);
   }

   public void gainItem(int id, boolean show) {
      gainItem(id, (short) 1, false, show);
   }

   public void gainItem(int id) {
      gainItem(id, (short) 1, false, true);
   }

   public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage) {
      return gainItem(id, quantity, randomStats, showMessage, -1);
   }

   public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires) {
      return gainItem(id, quantity, randomStats, showMessage, expires, null);
   }

   public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires, MaplePet from) {
      Item item = null;
      MaplePet evolved;
      int petId = -1;

      if (quantity >= 0) {
         if (ItemConstants.isPet(id)) {
            petId = PetProcessor.getInstance().createPet(id);

            if (from != null) {
               evolved = PetProcessor.getInstance().loadFromDb(id, (short) 0, petId);

               Point pos = getPlayer().position();
               pos.y -= 12;
               evolved.pos_$eq(pos);
               evolved.fh_$eq(getPlayer().getMap().getFootholds().findBelow(evolved.pos()).id());
               evolved.stance_$eq(0);
               evolved.summoned_$eq(true);

               evolved.name_$eq(from.name().compareTo(MapleItemInformationProvider.getInstance().getName(from.id())) != 0 ? from.name() : MapleItemInformationProvider.getInstance().getName(id));
               evolved.closeness_$eq(from.closeness());
               evolved.fullness_$eq(from.fullness());
               evolved.level_$eq(from.level());
               evolved.expiration_(System.currentTimeMillis() + expires);
               PetProcessor.getInstance().saveToDb(evolved);
            }

            //MapleInventoryManipulator.addById(c, id, (short) 1, null, petId, expires == -1 ? -1 : System.currentTimeMillis() + expires);
         }

         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         if (ItemConstants.getInventoryType(id).equals(MapleInventoryType.EQUIP)) {
            item = ii.getEquipById(id);

            if (item != null) {
               Equip it = (Equip) item;
               if (ItemConstants.isAccessory(item.id()) && it.slots() <= 0) {
                  it.slots_$eq(3);
               }

               if (YamlConfig.config.server.USE_ENHANCED_CRAFTING && c.getPlayer().getCS()) {
                  Equip eqp = (Equip) item;
                  if (!(c.getPlayer().isGM() && YamlConfig.config.server.USE_PERFECT_GM_SCROLL)) {
                     eqp.slots_$eq((byte) (eqp.slots() + 1));
                  }
                  item = MapleItemInformationProvider.getInstance().scrollEquipWithId(item, 2049100, true, 2049100, c.getPlayer().isGM());
               }
            }
         } else {
            item = BetterItemFactory.getInstance().create(id, (short) 0, quantity, petId);
         }

         if (expires >= 0) {
            item.expiration_(System.currentTimeMillis() + expires);
         }

         if (!MapleInventoryManipulator.checkSpace(c, id, quantity, "")) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full. Please remove an item from your " + ItemConstants.getInventoryType(id).name() + " inventory.");
            return null;
         }
         if (ItemConstants.getInventoryType(id) == MapleInventoryType.EQUIP) {
            if (randomStats) {
               MapleInventoryManipulator.addFromDrop(c, ii.randomizeStats((Equip) item), false, petId);
            } else {
               MapleInventoryManipulator.addFromDrop(c, item, false, petId);
            }
         } else {
            MapleInventoryManipulator.addFromDrop(c, item, false, petId);
         }
      } else {
         MapleInventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, -quantity, true, false);
      }
      if (showMessage) {
         PacketCreator.announce(c, new ShowItemGainInChat(id, quantity));
      }

      return item;
   }

   public void gainFame(int delta) {
      getPlayer().gainFame(delta);
   }

   public void changeMusic(String songName) {
      MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new MusicChange(songName));
   }

   public void mapEffect(String path) {
      PacketCreator.announce(c, new MapEffect(path));
   }

   public void mapSound(String path) {
      PacketCreator.announce(c, new MapSound(path));
   }

   public void displayAranIntro() {
      String intro = "";
      switch (c.getPlayer().getMapId()) {
         case 914090010:
            intro = "Effect/Direction1.img/aranTutorial/Scene0";
            break;
         case 914090011:
            intro = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            break;
         case 914090012:
            intro = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            break;
         case 914090013:
            intro = "Effect/Direction1.img/aranTutorial/Scene3";
            break;
         case 914090100:
            intro = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            break;
         case 914090200:
            intro = "Effect/Direction1.img/aranTutorial/Maha";
            break;
      }
      showIntro(intro);
   }

   public void showIntro(String path) {
      PacketCreator.announce(c, new ShowIntro(path));
   }

   public void showInfo(String path) {
      PacketCreator.announce(c, new ShowInfo(path));
      PacketCreator.announce(c, new EnableActions());
   }

   public MapleGuild getGuild() {
      try {
         return Server.getInstance().getGuild(getPlayer().getGuildId(), getPlayer().getWorld(), null).orElse(null);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public Optional<MapleParty> getParty() {
      return getPlayer().getParty();
   }

   public boolean isLeader() {
      return isPartyLeader();
   }

   public boolean isGuildLeader() {
      return getPlayer().isGuildLeader();
   }

   public boolean isPartyLeader() {
      if (getParty().isEmpty()) {
         return false;
      }

      return getParty().map(party -> party.getLeaderId() == getPlayer().getId()).orElse(false);
   }

   public boolean isEventLeader() {
      return getEventInstance() != null && getPlayer().getId() == getEventInstance().getLeaderId();
   }

   public void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         MapleClient cl = chr.getClient();
         if (quantity >= 0) {
            MapleInventoryManipulator.addById(cl, id, quantity);
         } else {
            MapleInventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, -quantity, true, false);
         }
         PacketCreator.announce(cl, new ShowItemGainInChat(id, quantity));
      }
   }

   public void removeHPQItems() {
      int[] items = {4001095, 4001096, 4001097, 4001098, 4001099, 4001100, 4001101};
      for (int item : items) {
         removePartyItems(item);
      }
   }

   public void removePartyItems(int id) {
      if (getParty().isEmpty()) {
         removeAll(id);
         return;
      }
      getParty()
            .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
            .filter(MaplePartyCharacter::isOnline)
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .filter(character -> character.getClient() != null)
            .forEach(character -> removeAll(id, character.getClient()));
   }

   public void giveCharacterExp(int amount, MapleCharacter chr) {
      chr.gainExp((amount * chr.getExpRate()), true, true);
   }

   public void givePartyExp(int amount, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         giveCharacterExp(amount, chr);
      }
   }

   public void givePartyExp(String PQ) {
      givePartyExp(PQ, true);
   }

   public void givePartyExp(String PQ, boolean instance) {
      //1 player  =  +0% bonus (100)
      //2 players =  +0% bonus (100)
      //3 players =  +0% bonus (100)
      //4 players = +10% bonus (110)
      //5 players = +20% bonus (120)
      //6 players = +30% bonus (130)
      if (getPlayer().getParty().isEmpty()) {
         return;
      }

      MapleParty party = getPlayer().getParty().get();
      int size = party.getMembers().size();

      if (instance) {
         for (MaplePartyCharacter member : party.getMembers()) {
            if (member == null || !member.isOnline()) {
               size--;
            } else {
               Optional<MapleCharacter> chr = member.getPlayer();
               if (chr.isPresent() && chr.get().getEventInstance() == null) {
                  size--;
               }
            }
         }
      }

      int bonus = size < 4 ? 100 : 70 + (size * 10);
      party.getMembers().stream()
            .filter(MaplePartyCharacter::isOnline)
            .map(MaplePartyCharacter::getPlayer)
            .flatMap(Optional::stream)
            .filter(character -> !(instance && character.getEventInstance() == null))
            .forEach(character -> {
               int base = PartyQuest.getExp(PQ, character.getLevel());
               int exp = base * bonus / 100;
               character.gainExp(exp, true, true);
               if (YamlConfig.config.server.PQ_BONUS_EXP_RATE > 0 && System.currentTimeMillis() <= YamlConfig.config.server.EVENT_END_TIMESTAMP) {
                  character.gainExp((int) (exp * YamlConfig.config.server.PQ_BONUS_EXP_RATE), true, true);
               }
            });
   }

   public void removeFromParty(int id, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         MapleInventoryType type = ItemConstants.getInventoryType(id);
         MapleInventory iv = chr.getInventory(type);
         int possesed = iv.countById(id);
         if (possesed > 0) {
            MapleInventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, possesed, true, false);
            PacketCreator.announce(chr, new ShowItemGainInChat(id, (short) -possesed));
         }
      }
   }

   public void removeAll(int id) {
      removeAll(id, c);
   }

   public void removeAll(int id, MapleClient cl) {
      MapleInventoryType invType = ItemConstants.getInventoryType(id);
      int possessed = cl.getPlayer().getInventory(invType).countById(id);
      if (possessed > 0) {
         MapleInventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, possessed, true, false);
         PacketCreator.announce(cl, new ShowItemGainInChat(id, (short) -possessed));
      }

      if (invType == MapleInventoryType.EQUIP) {
         if (cl.getPlayer().getInventory(MapleInventoryType.EQUIPPED).countById(id) > 0) {
            MapleInventoryManipulator.removeById(cl, MapleInventoryType.EQUIPPED, id, 1, true, false);
            PacketCreator.announce(cl, new ShowItemGainInChat(id, (short) -1));
         }
      }
   }

   public int getMapId() {
      return c.getPlayer().getMap().getId();
   }

   public int getPlayerCount(int mapid) {
      return c.getChannelServer().getMapFactory().getMap(mapid).getCharacters().size();
   }

   public void showInstruction(String msg, int width, int height) {
      PacketCreator.announce(c, new ShowHint(msg, width, height));
      PacketCreator.announce(c, new EnableActions());
   }

   public void disableMinimap() {
      PacketCreator.announce(c, new DisableMiniMap());
   }

   public boolean isAllReactorState(final int reactorId, final int state) {
      return c.getPlayer().getMap().isAllReactorState(reactorId, state);
   }

   public void resetMap(int mapid) {
      getMap(mapid).resetReactors();
      getMap(mapid).killAllMonsters();
      for (MapleMapObject i : getMap(mapid).getMapObjectsInRange(c.getPlayer().position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM))) {
         getMap(mapid).removeMapObject(i);
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new RemoveItem(i.objectId(), 0, c.getPlayer().getId()));
      }
   }

   public void useItem(int id) {
      MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
      PacketCreator.announce(c, new GetItemMessage(id));
   }

   public void cancelItem(final int id) {
      getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1);
   }

   public void teachSkill(int skillid, byte level, byte masterLevel, long expiration) {
      teachSkill(skillid, level, masterLevel, expiration, false);
   }

   public void teachSkill(int skillid, byte level, byte masterLevel, long expiration, boolean force) {
      SkillFactory.getSkill(skillid).ifPresent(skill -> {
         SkillEntry skillEntry = getPlayer().getSkills().get(skill);
         if (skillEntry != null) {
            if (!force && level > -1) {
               getPlayer().changeSkillLevel(skill, (byte) Math.max(skillEntry.skillLevel(), level), Math.max(skillEntry.masterLevel(), masterLevel), expiration == -1 ? -1 : Math.max(skillEntry.expiration(), expiration));
               return;
            }
         } else if (GameConstants.isAranSkills(skillid)) {
            PacketCreator.announce(c, new ShowInfo("Effect/BasicEff.img/AranGetSkill"));
         }

         getPlayer().changeSkillLevel(skill, level, masterLevel, expiration);
      });
   }

   public void removeEquipFromSlot(short slot) {
      Item tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
      MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, slot, tempItem.quantity(), false, false);
   }

   public void gainAndEquip(int itemid, short slot) {
      final Item old = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
      if (old != null) {
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, slot, old.quantity(), false, false);
      }
      final Item newItem = MapleItemInformationProvider.getInstance().getEquipById(itemid);
      newItem.position_(slot);
      c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addItemFromDB(newItem);
      PacketCreator.announce(c, new ModifyInventoryPacket(false, Collections.singletonList(new ModifyInventory(0, newItem))));
   }

   public void spawnNpc(int npcId, Point pos, MapleMap map) {
      MapleNPC npc = MapleLifeFactory.getNPC(npcId);
      if (npc != null) {
         npc.position_$eq(pos);
         npc.cy_$eq(pos.y);
         npc.rx0_$eq(pos.x + 50);
         npc.rx1_$eq(pos.x - 50);
         npc.fh_$eq(map.getFootholds().findBelow(pos).id());
         map.addMapObject(npc);
         MasterBroadcaster.getInstance().sendToAllInMap(map, new SpawnNPC(npc));
      }
   }

   public void spawnMonster(int id, int x, int y) {
      MapleMonster monster = MapleLifeFactory.getMonster(id);
      monster.position_$eq(new Point(x, y));
      getPlayer().getMap().spawnMonster(monster);
   }

   public MapleMonster getMonsterLifeFactory(int mid) {
      return MapleLifeFactory.getMonster(mid);
   }

   public MobSkill getMobSkill(int skill, int level) {
      return MobSkillFactory.getMobSkill(skill, level);
   }

   public void spawnGuide() {
      PacketCreator.announce(c, new SpawnGuide(true));
   }

   public void removeGuide() {
      PacketCreator.announce(c, new SpawnGuide(false));
   }

   public void displayGuide(int num) {
      PacketCreator.announce(c, new ShowInfo("UI/tutorial.img/" + num));
   }

   public void goDojoUp() {
      PacketCreator.announce(c, new DojoWarpUp());
   }

   public void resetDojoEnergy() {
      c.getPlayer().setDojoEnergy(0);
   }

   public void resetPartyDojoEnergy() {
      for (MapleCharacter pchr : c.getPlayer().getPartyMembersOnSameMap()) {
         pchr.setDojoEnergy(0);
      }
   }

   public void enableActions() {
      PacketCreator.announce(c, new EnableActions());
   }

   public void showEffect(String effect) {
      PacketCreator.announce(c, new ShowEffect(effect));
   }

   public void dojoEnergy() {
      PacketCreator.announce(c, new GetEnergy("energy", getPlayer().getDojoEnergy()));
   }

   public void talkGuide(String message) {
      PacketCreator.announce(c, new ShowGuideTalk(message));
   }

   public void guideHint(int hint) {
      PacketCreator.announce(c, new ShowGuideHint(hint));
   }

   public void updateAreaInfo(Short area, String info) {
      c.getPlayer().updateAreaInfo(area, info);
      PacketCreator.announce(c, new EnableActions());//idk, nexon does the same :P
   }

   public boolean containsAreaInfo(short area, String info) {
      return c.getPlayer().containsAreaInfo(area, info);
   }

   public void earnTitle(String msg) {
      PacketCreator.announce(c, new ShowTitleEarned(msg));
   }

   public void showInfoText(String msg) {
      PacketCreator.announce(c, new ShowInfoText(msg));
   }

   public void openUI(byte ui) {
      PacketCreator.announce(c, new OpenUI(ui));
   }

   public void lockUI() {
      PacketCreator.announce(c, new DisableUI(true));
      PacketCreator.announce(c, new LockUI(true));
   }

   public void unlockUI() {
      PacketCreator.announce(c, new DisableUI(false));
      PacketCreator.announce(c, new LockUI(false));
   }

   public void playSound(String sound) {
      MasterBroadcaster.getInstance().sendToAllInMap(getPlayer().getMap(), new EnvironmentChange(sound, 4));
   }

   public void environmentChange(String env, int mode) {
      MasterBroadcaster.getInstance().sendToAllInMap(getPlayer().getMap(), new EnvironmentChange(env, mode));
   }

   public String numberWithCommas(int number) {
      return GameConstants.numberWithCommas(number);
   }

   public Pyramid getPyramid() {
      return (Pyramid) getPlayer().getPartyQuest();
   }

   public int createExpedition(MapleExpeditionType type) {
      return createExpedition(type, false, 0, 0);
   }

   public int createExpedition(MapleExpeditionType type, boolean silent, int minPlayers, int maxPlayers) {
      MapleCharacter player = getPlayer();
      MapleExpedition exped = new MapleExpedition(player, type, silent, minPlayers, maxPlayers);

      int channel = player.getMap().getChannelServer().getId();
      if (!MapleExpeditionBossLog.attemptBoss(player.getId(), channel, exped, false)) {    // thanks Conrad for noticing missing expeditions entry limit
         return 1;
      }

      if (exped.addChannelExpedition(player.getClient().getChannelServer())) {
         return 0;
      } else {
         return -1;
      }
   }

   public void endExpedition(MapleExpedition exped) {
      exped.dispose(true);
      exped.removeChannelExpedition(getPlayer().getClient().getChannelServer());
   }

   public MapleExpedition getExpedition(MapleExpeditionType type) {
      return getPlayer().getClient().getChannelServer().getExpedition(type);
   }

   public String getExpeditionMemberNames(MapleExpeditionType type) {
      StringBuilder members = new StringBuilder();
      MapleExpedition exped = getExpedition(type);
      for (String memberName : exped.getMembers().values()) {
         members.append(memberName).append(", ");
      }
      return members.toString();
   }

   public boolean isLeaderExpedition(MapleExpeditionType type) {
      MapleExpedition exped = getExpedition(type);
      return exped.isLeader(getPlayer());
   }

   public long getJailTimeLeft() {
      return getPlayer().getJailExpirationTimeLeft();
   }

   public List<MaplePet> getDriedPets() {
      List<MaplePet> list = new LinkedList<>();

      long curTime = System.currentTimeMillis();
      for (Item it : getPlayer().getInventory(MapleInventoryType.CASH).list()) {
         if (ItemConstants.isPet(it.id()) && it.expiration() < curTime) {
            if (it.pet().isDefined()) {
               list.add(it.pet().get());
            }
         }
      }

      return list;
   }

   public List<Item> getUnclaimedMarriageGifts() {
      return MapleMarriage.loadGiftItemsFromDb(this.getClient(), this.getPlayer().getId());
   }

   public boolean startDungeonInstance(int dungeonid) {
      return c.getChannelServer().addMiniDungeon(dungeonid);
   }

   public boolean canGetFirstJob(int jobType) {
      if (YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP) {
         return true;
      }

      MapleCharacter chr = this.getPlayer();

      switch (jobType) {
         case 1:
            return chr.getStr() >= 35;

         case 2:
            return chr.getInt() >= 20;

         case 3:
         case 4:
            return chr.getDex() >= 25;

         case 5:
            return chr.getDex() >= 20;

         default:
            return true;
      }
   }

   public String getFirstJobStatRequirement(int jobType) {
      switch (jobType) {
         case 1:
            return "STR " + 35;

         case 2:
            return "INT " + 20;

         case 3:
         case 4:
            return "DEX " + 25;

         case 5:
            return "DEX " + 20;
      }

      return null;
   }

   public void npcTalk(int npcid, String message) {
      PacketCreator.announce(c, new GetNPCTalk(npcid, (byte) 0, message, "00 00", (byte) 0));
   }

   public long getCurrentTime() {
      return Server.getInstance().getCurrentTime();
   }
}