package scripting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemQuantity;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryProof;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.game.GameConstants;
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
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.partyquest.PartyQuest;
import server.partyquest.Pyramid;
import server.processor.PlayerInteractionProcessor;
import server.processor.QuestProcessor;
import tools.I18nMessage;
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
import tools.packet.foreigneffect.ShowGuideTalk;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.npctalk.GetNPCTalk;
import tools.packet.remove.RemoveItem;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowItemGainInChat;
import tools.packet.spawn.SpawnGuide;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.GetItemMessage;
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
      return (map.findMarketPortal() != null) ? map.findMarketPortal().getId() : map.getRandomPlayerSpawnPoint().getId();
   }

   public void warp(int mapId) {
      getPlayer().changeMap(mapId);
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
      int mapId = getMapId();
      warpParty(id, portalId, mapId, mapId);
   }

   public void warpParty(int id, int fromMinId, int fromMaxId) {
      warpParty(id, 0, fromMinId, fromMaxId);
   }

   public void warpParty(int id, int portalId, int fromMinId, int fromMaxId) {
      for (MapleCharacter mc : this.getPlayer().getPartyMembersOnline()) {
         if (mc.isLoggedInWorld()) {
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

   public void resetMapObjects(int mapId) {
      getWarpMap(mapId).resetMapObjects();
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

   public boolean hasItem(int itemId) {
      return haveItem(itemId, 1);
   }

   public boolean hasItem(int itemId, int quantity) {
      return haveItem(itemId, quantity);
   }

   public boolean haveItem(int itemId) {
      return haveItem(itemId, 1);
   }

   public boolean haveItem(int itemId, int quantity) {
      return getPlayer().getItemQuantity(itemId, false) >= quantity;
   }

   public int getItemQuantity(int itemId) {
      return getPlayer().getItemQuantity(itemId, false);
   }

   public boolean haveItemWithId(int itemId) {
      return haveItemWithId(itemId, false);
   }

   public boolean haveItemWithId(int itemId, boolean checkEquipped) {
      return getPlayer().haveItemWithId(itemId, checkEquipped);
   }

   public boolean canHold(int itemId) {
      return canHold(itemId, 1);
   }

   public boolean canHold(int itemId, int quantity) {
      return canHoldAll(Collections.singletonList(itemId), Collections.singletonList(quantity), true);
   }

   public boolean canHold(int itemId, int quantity, int removeItemId, int removeQuantity) {
      return canHoldAllAfterRemoving(Collections.singletonList(itemId), Collections.singletonList(quantity),
            Collections.singletonList(removeItemId), Collections.singletonList(removeQuantity));
   }

   private List<Integer> convertToIntegerArray(List<Object> list) {
      return list.stream().map(object -> (Integer) object).collect(Collectors.toList());
   }

   public boolean canHoldAll(List<Object> itemIds) {
      List<Object> quantity = new LinkedList<>();

      Integer intOne = 1;

      for (int i = 0; i < itemIds.size(); i++) {
         quantity.add(intOne);
      }

      return canHoldAll(itemIds, quantity);
   }

   public boolean canHoldAll(int[] itemIds, int[] quantities) {
      return canHoldAll(Arrays.stream(itemIds).boxed().collect(Collectors.toList()),
            Arrays.stream(quantities).boxed().collect(Collectors.toList()), true);
   }

   public boolean canHoldAll(List<Object> itemIds, List<Object> quantity) {
      return canHoldAll(convertToIntegerArray(itemIds), convertToIntegerArray(quantity), true);
   }

   private boolean canHoldAll(List<Integer> itemIds, List<Integer> quantity, boolean isInteger) {
      int size = Math.min(itemIds.size(), quantity.size());

      List<Pair<Item, MapleInventoryType>> addedItems = new LinkedList<>();
      for (int i = 0; i < size; i++) {
         Item it = new Item(itemIds.get(i), (short) 0, quantity.get(i).shortValue());
         addedItems.add(new Pair<>(it, ItemConstants.getInventoryType(itemIds.get(i))));
      }

      return MapleInventory.checkSpots(c.getPlayer(), addedItems, false);
   }

   private List<Pair<Item, MapleInventoryType>> prepareProofInventoryItems(List<ItemQuantity> items) {
      List<Pair<Item, MapleInventoryType>> addedItems = new LinkedList<>();
      for (ItemQuantity p : items) {
         Item it = new Item(p.itemId(), (short) 0, (short) p.quantity());
         addedItems.add(new Pair<>(it, MapleInventoryType.CAN_HOLD));
      }

      return addedItems;
   }

   private List<List<ItemQuantity>> prepareInventoryItemList(List<Integer> itemIds, List<Integer> quantity) {
      int size = Math.min(itemIds.size(), quantity.size());

      List<List<ItemQuantity>> invList = new ArrayList<>(6);
      for (int i = MapleInventoryType.UNDEFINED.getType(); i < MapleInventoryType.CASH.getType(); i++) {
         invList.add(new LinkedList<>());
      }

      for (int i = 0; i < size; i++) {
         int itemId = itemIds.get(i);
         byte inventoryType = ItemConstants.getInventoryType(itemId).getType();
         invList.get(inventoryType).add(new ItemQuantity(itemId, quantity.get(i)));
      }

      return invList;
   }

   public boolean canHoldAllAfterRemoving(List<Integer> toAddItemIds, List<Integer> toAddQuantity, List<Integer> toRemoveItemIds,
                                          List<Integer> toRemoveQuantity) {
      List<List<ItemQuantity>> toAddItemList = prepareInventoryItemList(toAddItemIds, toAddQuantity);
      List<List<ItemQuantity>> toRemoveItemList = prepareInventoryItemList(toRemoveItemIds, toRemoveQuantity);

      MapleInventoryProof prfInv = (MapleInventoryProof) this.getInventory(MapleInventoryType.CAN_HOLD);
      prfInv.lockInventory();
      try {
         for (int i = MapleInventoryType.EQUIP.getType(); i < MapleInventoryType.CASH.getType(); i++) {
            List<ItemQuantity> toAdd = toAddItemList.get(i);

            if (!toAdd.isEmpty()) {
               List<ItemQuantity> toRemove = toRemoveItemList.get(i);

               MapleInventory inv = this.getInventory(i);
               prfInv.cloneContents(inv);

               for (ItemQuantity p : toRemove) {
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.CAN_HOLD, p.itemId(), p.quantity(), false, false);
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

   //---- /\ /\ /\ /\ /\ /\ /\  NOT TESTED  /\ /\ /\ /\ /\ /\ /\ /\ /\ ----

   public void openNpc(int npcId) {
      openNpc(npcId, null);
   }

   public void openNpc(int npcId, String script) {
      if (c.getCM() != null) {
         return;
      }

      c.removeClickedNPC();
      NPCScriptManager.getInstance().dispose(c);
      NPCScriptManager.getInstance().start(c, npcId, script, null);
   }

   public boolean isQuestCompleted(int quest) {
      return QuestProcessor.getInstance().isComplete(c.getPlayer(), quest);
   }

   public boolean isQuestActive(int quest) {
      return isQuestStarted(quest);
   }

   public boolean isQuestStarted(int quest) {
      return QuestProcessor.getInstance().isStarted(c.getPlayer(), quest);
   }

   public boolean isQuestNotStarted(int quest) {
      return QuestProcessor.getInstance().isNotStarted(c.getPlayer(), quest);
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
      QuestProcessor.getInstance().setQuestProgress(c.getPlayer(), id, infoNumber, progress);
   }

   public String getQuestProgress(int id) {
      return getQuestProgress(id, 0);
   }

   public String getQuestProgress(int id, int infoNumber) {
      return QuestProcessor.getInstance().getProgress(c.getPlayer(), id, infoNumber);
   }

   public int getQuestProgressInt(int id) {
      try {
         return Integer.parseInt(getQuestProgress(id));
      } catch (NumberFormatException nfe) {
         return 0;
      }
   }

   public int getQuestProgressInt(int id, int infoNumber) {
      try {
         return Integer.parseInt(getQuestProgress(id, infoNumber));
      } catch (NumberFormatException nfe) {
         return 0;
      }
   }

   public void resetAllQuestProgress(int id) {
      QuestProcessor.getInstance().resetProgress(getPlayer(), id);
   }

   public void resetQuestProgress(int id, int infoNumber) {
      QuestProcessor.getInstance().resetSpecificProgress(getPlayer(), id, infoNumber);
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
         QuestProcessor.getInstance().forceStart(getPlayer(), id, npcId);
         return true;
      } catch (NullPointerException ex) {
         ex.printStackTrace();
         return false;
      }
   }

   public boolean completeQuest(int id, int npcId) {
      try {
         return QuestProcessor.getInstance().forceComplete(getPlayer(), id, npcId);
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
         MessageBroadcaster.getInstance()
               .sendServerNotice(getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PET_EVOLVE_ERROR"));
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
               int fh = getPlayer().getMap().getFootholds().findBelow(evolved.pos()).id();
               String name =
                     from.name().compareTo(MapleItemInformationProvider.getInstance().getName(from.id())) != 0 ? from.name() :
                           MapleItemInformationProvider.getInstance().getName(id);

               evolved = MaplePet.newBuilder(evolved)
                     .setPos(pos)
                     .setFh(fh)
                     .setStance(0)
                     .setSummoned(true)
                     .setName(name)
                     .setCloseness(from.closeness())
                     .setFullness(from.fullness())
                     .setLevel(from.level())
                     .setExpiration(System.currentTimeMillis() + expires)
                     .build();
               PetProcessor.getInstance().saveToDb(evolved);
            }
         }

         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         if (ItemConstants.getInventoryType(id).equals(MapleInventoryType.EQUIP)) {
            item = ii.getEquipById(id);

            if (item != null) {
               if (ItemConstants.isAccessory(item.id()) && ((Equip) item).slots() <= 0) {
                  item = ((Equip) item).setSlots(3);
               }

               if (YamlConfig.config.server.USE_ENHANCED_CRAFTING && c.getPlayer().getCS()) {
                  if (!(c.getPlayer().isGM() && YamlConfig.config.server.USE_PERFECT_GM_SCROLL)) {
                     item = ((Equip) item).setSlots((byte) (((Equip) item).slots() + 1));
                  }
                  item = MapleItemInformationProvider.getInstance()
                        .scrollEquipWithId(item, 2049100, true, 2049100, c.getPlayer().isGM());
               }
            }
         } else {
            MaplePet pet = PetProcessor.getInstance().loadFromDb(id, (short) 0, petId);
            item = Item.newBuilder(id)
                  .setPosition((short) 0)
                  .setQuantity(quantity)
                  .setPet(pet)
                  .setPetId(petId)
                  .build();
         }

         if (expires >= 0) {
            item = item.expiration(System.currentTimeMillis() + expires);
         }

         if (!MapleInventoryManipulator.checkSpace(c, id, quantity, "")) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP,
                  I18nMessage.from("INVENTORY_FULL").with(ItemConstants.getInventoryType(id).name()));
            return null;
         }
         if (ItemConstants.getInventoryType(id) == MapleInventoryType.EQUIP) {
            if (randomStats) {
               Optional<Item> result = MapleInventoryManipulator
                     .addFromDrop(c, Equip.newBuilder((Equip) item).randomizeStats().build(), false, petId);
               if (result.isPresent()) {
                  item = result.get();
               }
            } else {
               Optional<Item> result = MapleInventoryManipulator.addFromDrop(c, item, false, petId);
               if (result.isPresent()) {
                  item = result.get();
               }
            }
         } else {
            Optional<Item> result = MapleInventoryManipulator.addFromDrop(c, item, false, petId);
            if (result.isPresent()) {
               item = result.get();
            }
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
      String intro = switch (c.getPlayer().getMapId()) {
         case 914090010 -> "Effect/Direction1.img/aranTutorial/Scene0";
         case 914090011 -> "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
         case 914090012 -> "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
         case 914090013 -> "Effect/Direction1.img/aranTutorial/Scene3";
         case 914090100 -> "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
         case 914090200 -> "Effect/Direction1.img/aranTutorial/Maha";
         default -> "";
      };
      showIntro(intro);
   }

   public void showIntro(String path) {
      PlayerInteractionProcessor.getInstance().showIntro(getPlayer(), path);
   }

   public void showInfo(String path) {
      PlayerInteractionProcessor.getInstance().showInfo(getPlayer(), path);
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
               if (YamlConfig.config.server.PQ_BONUS_EXP_RATE > 0
                     && System.currentTimeMillis() <= YamlConfig.config.server.EVENT_END_TIMESTAMP) {
                  character.gainExp((int) (exp * YamlConfig.config.server.PQ_BONUS_EXP_RATE), true, true);
               }
            });
   }

   public void removeFromParty(int id, List<MapleCharacter> party) {
      for (MapleCharacter chr : party) {
         MapleInventoryType type = ItemConstants.getInventoryType(id);
         MapleInventory iv = chr.getInventory(type);
         int possessed = iv.countById(id);
         if (possessed > 0) {
            MapleInventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, possessed, true, false);
            PacketCreator.announce(chr, new ShowItemGainInChat(id, (short) -possessed));
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

   public int getPlayerCount(int mapId) {
      return c.getChannelServer().getMapFactory().getMap(mapId).getCharacters().size();
   }

   public void showInstruction(String msg, int width, int height) {
      PlayerInteractionProcessor.getInstance().showHint(getPlayer(), msg, width, height);
   }

   public void disableMiniMap() {
      PacketCreator.announce(c, new DisableMiniMap());
   }

   public boolean isAllReactorState(final int reactorId, final int state) {
      return c.getPlayer().getMap().isAllReactorState(reactorId, state);
   }

   public void resetMap(int mapId) {
      getMap(mapId).resetReactors();
      getMap(mapId).killAllMonsters();
      for (MapleMapObject i : getMap(mapId).getMapObjectsInRange(c.getPlayer().position(), Double.POSITIVE_INFINITY,
            Collections.singletonList(MapleMapObjectType.ITEM))) {
         getMap(mapId).removeMapObject(i);
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

   public void teachSkill(int skillId, byte level, byte masterLevel, long expiration) {
      teachSkill(skillId, level, masterLevel, expiration, false);
   }

   public void teachSkill(int skillId, byte level, byte masterLevel, long expiration, boolean force) {
      SkillFactory.getSkill(skillId).ifPresent(skill -> {
         SkillEntry skillEntry = getPlayer().getSkills().get(skill);
         if (skillEntry != null) {
            if (!force && level > -1) {
               getPlayer().changeSkillLevel(skill, (byte) Math.max(skillEntry.skillLevel(), level),
                     Math.max(skillEntry.masterLevel(), masterLevel),
                     expiration == -1 ? -1 : Math.max(skillEntry.expiration(), expiration));
               return;
            }
         } else if (GameConstants.isAranSkills(skillId)) {
            PacketCreator.announce(c, new ShowInfo("Effect/BasicEff.img/AranGetSkill"));
         }

         getPlayer().changeSkillLevel(skill, level, masterLevel, expiration);
      });
   }

   public void removeEquipFromSlot(short slot) {
      Item tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
      MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, slot, tempItem.quantity(), false, false);
   }

   public void gainAndEquip(int itemId, short slot) {
      final Item old = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
      if (old != null) {
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, slot, old.quantity(), false, false);
      }
      Item newItem = MapleItemInformationProvider.getInstance().getEquipById(itemId);
      newItem = newItem.setPosition(slot);
      c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addItemFromDB(newItem);
      PacketCreator.announce(c, new ModifyInventoryPacket(false, Collections.singletonList(new ModifyInventory(0, newItem))));
   }

   public void spawnMonster(int id, int x, int y) {
      MapleLifeFactory.getMonster(id).ifPresent(monster -> {
         monster.setPosition(new Point(x, y));
         getPlayer().getMap().spawnMonster(monster);
      });
   }

   public Optional<MapleMonster> getMonsterLifeFactory(int mid) {
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
      c.getPlayer().getPartyMembersOnSameMap().forEach(character -> character.setDojoEnergy(0));
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
      PlayerInteractionProcessor.getInstance().guideHint(getPlayer(), hint);
   }

   public void updateAreaInfo(Short area, String info) {
      c.getPlayer().updateAreaInfo(area, info);
      PacketCreator.announce(c, new EnableActions());//idk, nexon does the same :P
   }

   public boolean containsAreaInfo(short area, String info) {
      return c.getPlayer().containsAreaInfo(area, info);
   }

   public void earnTitle(String msg) {
      PlayerInteractionProcessor.getInstance().earnTitle(getPlayer(), msg);
   }

   public void showInfoText(String msg) {
      PlayerInteractionProcessor.getInstance().showInfoText(getPlayer(), msg);
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
      PlayerInteractionProcessor.getInstance().playSound(getPlayer(), sound);
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
      MapleExpedition expedition = new MapleExpedition(player, type, silent, minPlayers, maxPlayers);

      int channel = player.getMap().getChannelServer().getId();
      if (!MapleExpeditionBossLog.attemptBoss(player.getId(), channel, expedition, false)) {
         return 1;
      }

      if (expedition.addChannelExpedition(player.getClient().getChannelServer())) {
         return 0;
      } else {
         return -1;
      }
   }

   public void endExpedition(MapleExpedition expedition) {
      expedition.dispose(true);
      expedition.removeChannelExpedition(getPlayer().getClient().getChannelServer());
   }

   public MapleExpedition getExpedition(MapleExpeditionType type) {
      return getPlayer().getClient().getChannelServer().getExpedition(type);
   }

   public String getExpeditionMemberNames(MapleExpeditionType type) {
      StringBuilder members = new StringBuilder();
      MapleExpedition expedition = getExpedition(type);
      for (String memberName : expedition.getMembers().values()) {
         members.append(memberName).append(", ");
      }
      return members.toString();
   }

   public boolean isLeaderExpedition(MapleExpeditionType type) {
      MapleExpedition expedition = getExpedition(type);
      return expedition.isLeader(getPlayer());
   }

   public long getJailTimeLeft() {
      return getPlayer().getJailExpirationTimeLeft();
   }

   public List<MaplePet> getDriedPets() {
      List<MaplePet> list = new LinkedList<>();

      long curTime = System.currentTimeMillis();
      for (Item it : getPlayer().getInventory(MapleInventoryType.CASH).list()) {
         if (ItemConstants.isPet(it.id()) && it.expiration() < curTime) {
            if (it.pet() != null) {
               list.add(it.pet());
            }
         }
      }

      return list;
   }

   public List<Item> getUnclaimedMarriageGifts() {
      return MapleMarriage.loadGiftItemsFromDb(this.getClient(), this.getPlayer().getId());
   }

   public boolean startDungeonInstance(int dungeonId) {
      return c.getChannelServer().addMiniDungeon(dungeonId);
   }

   public boolean canGetFirstJob(int jobType) {
      if (YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP) {
         return true;
      }

      MapleCharacter chr = this.getPlayer();

      return switch (jobType) {
         case 1 -> chr.getStr() >= 35;
         case 2 -> chr.getInt() >= 20;
         case 3, 4 -> chr.getDex() >= 25;
         case 5 -> chr.getDex() >= 20;
         default -> true;
      };
   }

   public String getFirstJobStatRequirement(int jobType) {
      return switch (jobType) {
         case 1 -> "STR " + 35;
         case 2 -> "INT " + 20;
         case 3, 4 -> "DEX " + 25;
         case 5 -> "DEX " + 20;
         default -> null;
      };
   }

   public void npcTalk(int npcId, String message) {
      PacketCreator.announce(c, new GetNPCTalk(npcId, (byte) 0, message, "00 00", (byte) 0));
   }

   public long getCurrentTime() {
      return Server.getInstance().getCurrentTime();
   }
}