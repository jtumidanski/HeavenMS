package server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import client.database.data.MakerCreateData;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.processor.ItemProcessor;
import config.YamlConfig;
import constants.inventory.EquipSlot;
import constants.inventory.ItemConstants;
import constants.skills.Assassin;
import constants.skills.Gunslinger;
import constants.skills.NightWalker;
import database.DatabaseConnection;
import database.provider.DropDataProvider;
import database.provider.MakerCreateProvider;
import database.provider.MakerReagentProvider;
import database.provider.MakerRecipeProvider;
import database.provider.MonsterCardProvider;
import net.server.Server;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MakerItemFactory.MakerItemCreateEntry;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.processor.StatEffectProcessor;
import tools.FilePrinter;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.packet.message.YellowTip;

public class MapleItemInformationProvider {
   private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
   protected MapleDataProvider itemData;
   protected MapleDataProvider equipData;
   protected MapleDataProvider stringData;
   protected MapleDataProvider etcData;
   protected MapleData cashStringData;
   protected MapleData consumeStringData;
   protected MapleData eqpStringData;
   protected MapleData etcStringData;
   protected MapleData insStringData;
   protected MapleData petStringData;
   protected Map<Integer, Short> slotMaxCache = new HashMap<>();
   protected Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();
   protected Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<>();
   protected Map<Integer, Equip> equipCache = new HashMap<>();
   protected Map<Integer, MapleData> equipLevelInfoCache = new HashMap<>();
   protected Map<Integer, Integer> equipLevelReqCache = new HashMap<>();
   protected Map<Integer, Integer> equipMaxLevelCache = new HashMap<>();
   protected Map<Integer, List<Integer>> scrollReqsCache = new HashMap<>();
   protected Map<Integer, Integer> wholePriceCache = new HashMap<>();
   protected Map<Integer, Double> unitPriceCache = new HashMap<>();
   protected Map<Integer, Integer> projectileWeaponAttackCache = new HashMap<>();
   protected Map<Integer, String> nameCache = new HashMap<>();
   protected Map<Integer, String> descCache = new HashMap<>();
   protected Map<Integer, String> msgCache = new HashMap<>();
   protected Map<Integer, Boolean> accountItemRestrictionCache = new HashMap<>();
   protected Map<Integer, Boolean> dropRestrictionCache = new HashMap<>();
   protected Map<Integer, Boolean> pickupRestrictionCache = new HashMap<>();
   protected Map<Integer, Integer> getMesoCache = new HashMap<>();
   protected Map<Integer, Integer> monsterBookID = new HashMap<>();
   protected Map<Integer, Boolean> untradeableCache = new HashMap<>();
   protected Map<Integer, Boolean> onEquipUntradeableCache = new HashMap<>();
   protected Map<Integer, ScriptedItem> scriptedItemCache = new HashMap<>();
   protected Map<Integer, Boolean> karmaCache = new HashMap<>();
   protected Map<Integer, Integer> triggerItemCache = new HashMap<>();
   protected Map<Integer, Integer> expCache = new HashMap<>();
   protected Map<Integer, Integer> createItem = new HashMap<>();
   protected Map<Integer, Integer> mobItem = new HashMap<>();
   protected Map<Integer, Integer> useDelay = new HashMap<>();
   protected Map<Integer, Integer> mobHP = new HashMap<>();
   protected Map<Integer, Integer> levelCache = new HashMap<>();
   protected Map<Integer, Pair<Integer, List<RewardItem>>> rewardCache = new HashMap<>();
   protected List<Pair<Integer, String>> itemNameCache = new ArrayList<>();
   protected Map<Integer, Boolean> consumeOnPickupCache = new HashMap<>();
   protected Map<Integer, Boolean> isQuestItemCache = new HashMap<>();
   protected Map<Integer, Boolean> isPartyQuestItemCache = new HashMap<>();
   protected Map<Integer, Pair<Integer, String>> replaceOnExpireCache = new HashMap<>();
   protected Map<Integer, String> equipmentSlotCache = new HashMap<>();
   protected Map<Integer, Boolean> noCancelMouseCache = new HashMap<>();
   protected Map<Integer, Integer> mobCrystalMakerCache = new HashMap<>();
   protected Map<Integer, Pair<String, Integer>> statUpgradeMakerCache = new HashMap<>();
   protected Map<Integer, MakerItemFactory.MakerItemCreateEntry> makerItemCache = new HashMap<>();
   protected Map<Integer, Integer> makerCatalystCache = new HashMap<>();
   protected Map<Integer, Map<String, Integer>> skillUpgradeCache = new HashMap<>();
   protected Map<Integer, MapleData> skillUpgradeInfoCache = new HashMap<>();
   protected Map<Integer, Pair<Integer, Set<Integer>>> cashPetFoodCache = new HashMap<>();
   protected Map<Integer, QuestConsItem> questItemConsCache = new HashMap<>();

   private MapleItemInformationProvider() {
      loadCardIdData();
      itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Item.wz"));
      equipData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Character.wz"));
      stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
      etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Etc.wz"));
      cashStringData = stringData.getData("Cash.img");
      consumeStringData = stringData.getData("Consume.img");
      eqpStringData = stringData.getData("Eqp.img");
      etcStringData = stringData.getData("Etc.img");
      insStringData = stringData.getData("Ins.img");
      petStringData = stringData.getData("Pet.img");

      isQuestItemCache.put(0, false);
      isPartyQuestItemCache.put(0, false);
   }

   public static MapleItemInformationProvider getInstance() {
      return instance;
   }

   private static short getExtraSlotMaxFromPlayer(MapleClient c, int itemId) {
      short ret = 0;
      if (ItemConstants.isThrowingStar(itemId)) {
         if (c.getPlayer().getJob().isA(MapleJob.NIGHT_WALKER_1)) {
            ret += SkillFactory.getSkillLevel(c.getPlayer(), NightWalker.CLAW_MASTERY) * 10;
         } else {
            ret += SkillFactory.getSkillLevel(c.getPlayer(), Assassin.CLAW_MASTERY) * 10;
         }
      } else if (ItemConstants.isBullet(itemId)) {
         ret += SkillFactory.getSkillLevel(c.getPlayer(), Gunslinger.GUN_MASTERY) * 10;
      }

      return ret;
   }

   private static double getRoundedUnitPrice(double unitPrice, int max) {
      double intPart = Math.floor(unitPrice);
      double fractionPart = unitPrice - intPart;
      if (fractionPart == 0.0) {
         return intPart;
      }

      double fractionMask = 0.0;
      double lastFraction, currentFraction = 1.0;
      int i = 1;

      do {
         lastFraction = currentFraction;
         currentFraction /= 2;

         if (fractionPart == currentFraction) {
            break;
         } else if (fractionPart > currentFraction) {
            fractionMask += currentFraction;
            fractionPart -= currentFraction;
         }

         i++;
      } while (i <= max);

      if (i > max) {
         lastFraction = currentFraction;
         currentFraction = 0.0;
      }

      if (Math.abs(fractionPart - currentFraction) < Math.abs(fractionPart - lastFraction)) {
         return intPart + fractionMask + currentFraction;
      } else {
         return intPart + fractionMask + lastFraction;
      }
   }

   private static double testYourLuck(double prop, int dices) {   // revamped testYourLuck author: David A.
      return Math.pow(1.0 - prop, dices);
   }

   public static boolean rollSuccessChance(double propPercent) {
      return Math.random() >= testYourLuck(propPercent / 100.0, YamlConfig.config.server.SCROLL_CHANCE_ROLLS);
   }

   private static short getMaximumShortMaxIfOverflow(int value1, int value2) {
      return (short) Math.min(Short.MAX_VALUE, Math.max(value1, value2));
   }

   private static short getShortMaxIfOverflow(int value) {
      return (short) Math.min(Short.MAX_VALUE, value);
   }

   private static short chaosScrollRandomizedStat(int range) {
      return (short) Randomizer.rand(-range, range);
   }

   public static void improveEquipStats(Equip nEquip, Map<String, Integer> stats) {
      for (Entry<String, Integer> stat : stats.entrySet()) {
         switch (stat.getKey()) {
            case "STR":
               nEquip.str_$eq(getShortMaxIfOverflow(nEquip.str() + stat.getValue()));
               break;
            case "DEX":
               nEquip.dex_$eq(getShortMaxIfOverflow(nEquip.dex() + stat.getValue()));
               break;
            case "INT":
               nEquip._int_$eq(getShortMaxIfOverflow(nEquip._int() + stat.getValue()));
               break;
            case "LUK":
               nEquip.luk_$eq(getShortMaxIfOverflow(nEquip.luk() + stat.getValue()));
               break;
            case "PAD":
               nEquip.watk_$eq(getShortMaxIfOverflow(nEquip.watk() + stat.getValue()));
               break;
            case "PDD":
               nEquip.wdef_$eq(getShortMaxIfOverflow(nEquip.wdef() + stat.getValue()));
               break;
            case "MAD":
               nEquip.matk_$eq(getShortMaxIfOverflow(nEquip.matk() + stat.getValue()));
               break;
            case "MDD":
               nEquip.mdef_$eq(getShortMaxIfOverflow(nEquip.mdef() + stat.getValue()));
               break;
            case "ACC":
               nEquip.acc_$eq(getShortMaxIfOverflow(nEquip.acc() + stat.getValue()));
               break;
            case "EVA":
               nEquip.avoid_$eq(getShortMaxIfOverflow(nEquip.avoid() + stat.getValue()));
               break;
            case "Speed":
               nEquip.speed_$eq(getShortMaxIfOverflow(nEquip.speed() + stat.getValue()));
               break;
            case "Jump":
               nEquip.jump_$eq(getShortMaxIfOverflow(nEquip.jump() + stat.getValue()));
               break;
            case "MHP":
               nEquip.hp_$eq(getShortMaxIfOverflow(nEquip.hp() + stat.getValue()));
               break;
            case "MMP":
               nEquip.mp_$eq(getShortMaxIfOverflow(nEquip.mp() + stat.getValue()));
               break;
            case "afterImage":
               break;
         }
      }
   }

   private static short getRandStat(int defaultValue, int maxRange) {
      if (defaultValue == 0) {
         return 0;
      }
      int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
      return (short) ((defaultValue - lMaxRange) + Math.floor(Randomizer.nextDouble() * (lMaxRange * 2 + 1)));
   }

   private static short getRandUpgradedStat(int defaultValue, int maxRange) {
      if (defaultValue == 0) {
         return 0;
      }
      return (short) (defaultValue + Math.floor(Randomizer.nextDouble() * (maxRange + 1)));
   }

   private static int getCrystalForLevel(int level) {
      int range = (level - 1) / 10;

      if (range < 5) {
         return 4260000;
      } else if (range > 11) {
         return 4260008;
      } else {
         switch (range) {
            case 5:
               return 4260001;

            case 6:
               return 4260002;

            case 7:
               return 4260003;

            case 8:
               return 4260004;

            case 9:
               return 4260005;

            case 10:
               return 4260006;

            default:
               return 4260007;
         }
      }
   }

   public List<Pair<Integer, String>> getAllItems() {
      if (!itemNameCache.isEmpty()) {
         return itemNameCache;
      }
      List<Pair<Integer, String>> itemPairs = new ArrayList<>();
      MapleData itemsData;
      itemsData = stringData.getData("Cash.img");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      itemsData = stringData.getData("Consume.img");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
      for (MapleData eqpType : itemsData.getChildren()) {
         for (MapleData itemFolder : eqpType.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
         }
      }
      itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      itemsData = stringData.getData("Ins.img");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      itemsData = stringData.getData("Pet.img");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      return itemPairs;
   }

   public List<Pair<Integer, String>> getAllEtcItems() {
      if (!itemNameCache.isEmpty()) {
         return itemNameCache;
      }

      List<Pair<Integer, String>> itemPairs = new ArrayList<>();
      MapleData itemsData;

      itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
      for (MapleData itemFolder : itemsData.getChildren()) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
      return itemPairs;
   }

   private MapleData getStringData(int itemId) {
      String cat = "null";
      MapleData theData;
      if (itemId >= 5010000) {
         theData = cashStringData;
      } else if (itemId >= 2000000 && itemId < 3000000) {
         theData = consumeStringData;
      } else if ((itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000) || (itemId >= 1132000 && itemId < 1133000) || (itemId >= 1142000 && itemId < 1143000)) {
         theData = eqpStringData;
         cat = "Eqp/Accessory";
      } else if (itemId >= 1000000 && itemId < 1010000) {
         theData = eqpStringData;
         cat = "Eqp/Cap";
      } else if (itemId >= 1102000 && itemId < 1103000) {
         theData = eqpStringData;
         cat = "Eqp/Cape";
      } else if (itemId >= 1040000 && itemId < 1050000) {
         theData = eqpStringData;
         cat = "Eqp/Coat";
      } else if (itemId >= 20000 && itemId < 22000) {
         theData = eqpStringData;
         cat = "Eqp/Face";
      } else if (itemId >= 1080000 && itemId < 1090000) {
         theData = eqpStringData;
         cat = "Eqp/Glove";
      } else if (itemId >= 30000 && itemId < 35000) {
         theData = eqpStringData;
         cat = "Eqp/Hair";
      } else if (itemId >= 1050000 && itemId < 1060000) {
         theData = eqpStringData;
         cat = "Eqp/Longcoat";
      } else if (itemId >= 1060000 && itemId < 1070000) {
         theData = eqpStringData;
         cat = "Eqp/Pants";
      } else if (itemId >= 1802000 && itemId < 1842000) {
         theData = eqpStringData;
         cat = "Eqp/PetEquip";
      } else if (itemId >= 1112000 && itemId < 1120000) {
         theData = eqpStringData;
         cat = "Eqp/Ring";
      } else if (itemId >= 1092000 && itemId < 1100000) {
         theData = eqpStringData;
         cat = "Eqp/Shield";
      } else if (itemId >= 1070000 && itemId < 1080000) {
         theData = eqpStringData;
         cat = "Eqp/Shoes";
      } else if (itemId >= 1900000 && itemId < 2000000) {
         theData = eqpStringData;
         cat = "Eqp/Taming";
      } else if (itemId >= 1300000 && itemId < 1800000) {
         theData = eqpStringData;
         cat = "Eqp/Weapon";
      } else if (itemId >= 4000000 && itemId < 5000000) {
         theData = etcStringData;
         cat = "Etc";
      } else if (itemId >= 3000000 && itemId < 4000000) {
         theData = insStringData;
      } else if (ItemConstants.isPet(itemId)) {
         theData = petStringData;
      } else {
         return null;
      }
      if (cat.equalsIgnoreCase("null")) {
         return theData.getChildByPath(String.valueOf(itemId));
      } else {
         return theData.getChildByPath(cat + "/" + itemId);
      }
   }

   public boolean noCancelMouse(int itemId) {
      if (noCancelMouseCache.containsKey(itemId)) {
         return noCancelMouseCache.get(itemId);
      }

      MapleData item = getItemData(itemId);
      if (item == null) {
         noCancelMouseCache.put(itemId, false);
         return false;
      }

      boolean blockMouse = MapleDataTool.getIntConvert("info/noCancelMouse", item, 0) == 1;
      noCancelMouseCache.put(itemId, blockMouse);
      return blockMouse;
   }

   private MapleData getItemData(int itemId) {
      MapleData ret = null;
      String idStr = "0" + itemId;
      MapleDataDirectoryEntry root = itemData.getRoot();
      for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
         for (MapleDataFileEntry iFile : topDir.getFiles()) {
            if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
               ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
               if (ret == null) {
                  return null;
               }
               ret = ret.getChildByPath(idStr);
               return ret;
            } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
               return itemData.getData(topDir.getName() + "/" + iFile.getName());
            }
         }
      }
      root = equipData.getRoot();
      for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
         for (MapleDataFileEntry iFile : topDir.getFiles()) {
            if (iFile.getName().equals(idStr + ".img")) {
               return equipData.getData(topDir.getName() + "/" + iFile.getName());
            }
         }
      }
      return ret;
   }

   public List<Integer> getItemIdsInRange(int minId, int maxId, boolean ignoreCashItem) {
      List<Integer> list = new ArrayList<>();

      if (ignoreCashItem) {
         for (int i = minId; i <= maxId; i++) {
            if (getItemData(i) != null && !isCash(i)) {
               list.add(i);
            }
         }
      } else {
         for (int i = minId; i <= maxId; i++) {
            if (getItemData(i) != null) {
               list.add(i);
            }
         }
      }


      return list;
   }

   public short getSlotMax(MapleClient c, int itemId) {
      Short slotMax = slotMaxCache.get(itemId);
      if (slotMax != null) {
         return (short) (slotMax + getExtraSlotMaxFromPlayer(c, itemId));
      }
      short ret = 0;
      MapleData item = getItemData(itemId);
      if (item != null) {
         MapleData smEntry = item.getChildByPath("info/slotMax");
         if (smEntry == null) {
            if (ItemConstants.getInventoryType(itemId).getType() == MapleInventoryType.EQUIP.getType()) {
               ret = 1;
            } else {
               ret = 100;
            }
         } else {
            ret = (short) MapleDataTool.getInt(smEntry);
         }
      }

      slotMaxCache.put(itemId, ret);
      return (short) (ret + getExtraSlotMaxFromPlayer(c, itemId));
   }

   public int getMeso(int itemId) {
      if (getMesoCache.containsKey(itemId)) {
         return getMesoCache.get(itemId);
      }
      MapleData item = getItemData(itemId);
      if (item == null) {
         return -1;
      }
      int pEntry;
      MapleData pData = item.getChildByPath("info/meso");
      if (pData == null) {
         return -1;
      }
      pEntry = MapleDataTool.getInt(pData);
      getMesoCache.put(itemId, pEntry);
      return pEntry;
   }

   private Pair<Integer, Double> getItemPriceData(int itemId) {
      MapleData item = getItemData(itemId);
      if (item == null) {
         wholePriceCache.put(itemId, -1);
         unitPriceCache.put(itemId, 0.0);
         return new Pair<>(-1, 0.0);
      }

      int pEntry = -1;
      MapleData pData = item.getChildByPath("info/price");
      if (pData != null) {
         pEntry = MapleDataTool.getInt(pData);
      }

      double fEntry = 0.0f;
      pData = item.getChildByPath("info/unitPrice");
      if (pData != null) {
         try {
            fEntry = getRoundedUnitPrice(MapleDataTool.getDouble(pData), 5);
         } catch (Exception e) {
            fEntry = MapleDataTool.getInt(pData);
         }
      }

      wholePriceCache.put(itemId, pEntry);
      unitPriceCache.put(itemId, fEntry);
      return new Pair<>(pEntry, fEntry);
   }

   public int getWholePrice(int itemId) {
      if (wholePriceCache.containsKey(itemId)) {
         return wholePriceCache.get(itemId);
      }

      return getItemPriceData(itemId).getLeft();
   }

   public double getUnitPrice(int itemId) {
      if (unitPriceCache.containsKey(itemId)) {
         return unitPriceCache.get(itemId);
      }

      return getItemPriceData(itemId).getRight();
   }

   public int getPrice(int itemId, int quantity) {
      int retPrice = getWholePrice(itemId);
      if (retPrice == -1) {
         return -1;
      }

      if (!ItemConstants.isRechargeable(itemId)) {
         retPrice *= quantity;
      } else {
         retPrice += Math.ceil(quantity * getUnitPrice(itemId));
      }

      return retPrice;
   }

   public Pair<Integer, String> getReplaceOnExpire(int itemId) {
      if (replaceOnExpireCache.containsKey(itemId)) {
         return replaceOnExpireCache.get(itemId);
      }

      MapleData data = getItemData(itemId);
      int itemReplacement = MapleDataTool.getInt("info/replace/itemid", data, 0);
      String msg = MapleDataTool.getString("info/replace/msg", data, "");

      Pair<Integer, String> ret = new Pair<>(itemReplacement, msg);
      replaceOnExpireCache.put(itemId, ret);

      return ret;
   }

   protected String getEquipmentSlot(int itemId) {
      if (equipmentSlotCache.containsKey(itemId)) {
         return equipmentSlotCache.get(itemId);
      }

      String ret;

      MapleData item = getItemData(itemId);

      if (item == null) {
         return null;
      }

      MapleData info = item.getChildByPath("info");

      if (info == null) {
         return null;
      }

      ret = MapleDataTool.getString("islot", info, "");

      equipmentSlotCache.put(itemId, ret);

      return ret;
   }

   public Map<String, Integer> getEquipStats(int itemId) {
      if (equipStatsCache.containsKey(itemId)) {
         return equipStatsCache.get(itemId);
      }
      Map<String, Integer> ret = new LinkedHashMap<>();
      MapleData item = getItemData(itemId);
      if (item == null) {
         return null;
      }
      MapleData info = item.getChildByPath("info");
      if (info == null) {
         return null;
      }
      for (MapleData data : info.getChildren()) {
         if (data.getName().startsWith("inc")) {
            ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
         }
            /*else if (data.getName().startsWith("req"))
             ret.put(data.getName(), MapleDataTool.getInt(data.getName(), info, 0));*/
      }
      ret.put("reqJob", MapleDataTool.getInt("reqJob", info, 0));
      ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
      ret.put("reqDEX", MapleDataTool.getInt("reqDEX", info, 0));
      ret.put("reqSTR", MapleDataTool.getInt("reqSTR", info, 0));
      ret.put("reqINT", MapleDataTool.getInt("reqINT", info, 0));
      ret.put("reqLUK", MapleDataTool.getInt("reqLUK", info, 0));
      ret.put("reqPOP", MapleDataTool.getInt("reqPOP", info, 0));
      ret.put("cash", MapleDataTool.getInt("cash", info, 0));
      ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
      ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
      ret.put("success", MapleDataTool.getInt("success", info, 0));
      ret.put("fs", MapleDataTool.getInt("fs", info, 0));
      equipStatsCache.put(itemId, ret);
      return ret;
   }

   public Integer getEquipLevelReq(int itemId) {
      if (equipLevelReqCache.containsKey(itemId)) {
         return equipLevelReqCache.get(itemId);
      }

      int ret = 0;
      MapleData item = getItemData(itemId);
      if (item != null) {
         MapleData info = item.getChildByPath("info");
         if (info != null) {
            ret = MapleDataTool.getInt("reqLevel", info, 0);
         }
      }

      equipLevelReqCache.put(itemId, ret);
      return ret;
   }

   public List<Integer> getScrollReqs(int itemId) {
      if (scrollReqsCache.containsKey(itemId)) {
         return scrollReqsCache.get(itemId);
      }

      List<Integer> ret = new ArrayList<>();
      MapleData data = getItemData(itemId);
      data = data.getChildByPath("req");
      if (data != null) {
         for (MapleData req : data.getChildren()) {
            ret.add(MapleDataTool.getInt(req));
         }
      }
      scrollReqsCache.put(itemId, ret);
      return ret;
   }

   public MapleWeaponType getWeaponType(int itemId) {
      int cat = (itemId / 10000) % 100;
      MapleWeaponType[] type = {MapleWeaponType.SWORD1H, MapleWeaponType.GENERAL1H_SWING, MapleWeaponType.GENERAL1H_SWING, MapleWeaponType.DAGGER_OTHER, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.WAND, MapleWeaponType.STAFF, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.SWORD2H, MapleWeaponType.GENERAL2H_SWING, MapleWeaponType.GENERAL2H_SWING, MapleWeaponType.SPEAR_STAB, MapleWeaponType.POLE_ARM_SWING, MapleWeaponType.BOW, MapleWeaponType.CROSSBOW, MapleWeaponType.CLAW, MapleWeaponType.KNUCKLE, MapleWeaponType.GUN};
      if (cat < 30 || cat > 49) {
         return MapleWeaponType.NOT_A_WEAPON;
      }
      return type[cat - 30];
   }

   public void scrollOptionEquipWithChaos(Equip equip, int range, boolean option) {
      if (!option) {
         if (equip.str() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.str_$eq(getMaximumShortMaxIfOverflow(equip.str(), (equip.str() + chaosScrollRandomizedStat(range))));
            } else {
               equip.str_$eq(getMaximumShortMaxIfOverflow(0, (equip.str() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.dex() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.dex_$eq(getMaximumShortMaxIfOverflow(equip.dex(), (equip.dex() + chaosScrollRandomizedStat(range))));
            } else {
               equip.dex_$eq(getMaximumShortMaxIfOverflow(0, (equip.dex() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip._int() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip._int_$eq(getMaximumShortMaxIfOverflow(equip._int(), (equip._int() + chaosScrollRandomizedStat(range))));
            } else {
               equip._int_$eq(getMaximumShortMaxIfOverflow(0, (equip._int() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.luk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.luk_$eq(getMaximumShortMaxIfOverflow(equip.luk(), (equip.luk() + chaosScrollRandomizedStat(range))));
            } else {
               equip.luk_$eq(getMaximumShortMaxIfOverflow(0, (equip.luk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.acc() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.acc_$eq(getMaximumShortMaxIfOverflow(equip.acc(), (equip.acc() + chaosScrollRandomizedStat(range))));
            } else {
               equip.acc_$eq(getMaximumShortMaxIfOverflow(0, (equip.acc() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.avoid() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.avoid_$eq(getMaximumShortMaxIfOverflow(equip.avoid(), (equip.avoid() + chaosScrollRandomizedStat(range))));
            } else {
               equip.avoid_$eq(getMaximumShortMaxIfOverflow(0, (equip.avoid() + chaosScrollRandomizedStat(range))));
            }
         }
      } else {
         if (equip.watk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.watk_$eq(getMaximumShortMaxIfOverflow(equip.watk(), (equip.watk() + chaosScrollRandomizedStat(range))));
            } else {
               equip.watk_$eq(getMaximumShortMaxIfOverflow(0, (equip.watk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.wdef() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.wdef_$eq(getMaximumShortMaxIfOverflow(equip.wdef(), (equip.wdef() + chaosScrollRandomizedStat(range))));
            } else {
               equip.wdef_$eq(getMaximumShortMaxIfOverflow(0, (equip.wdef() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.matk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.matk_$eq(getMaximumShortMaxIfOverflow(equip.matk(), (equip.matk() + chaosScrollRandomizedStat(range))));
            } else {
               equip.matk_$eq(getMaximumShortMaxIfOverflow(0, (equip.matk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.mdef() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.mdef_$eq(getMaximumShortMaxIfOverflow(equip.mdef(), (equip.mdef() + chaosScrollRandomizedStat(range))));
            } else {
               equip.mdef_$eq(getMaximumShortMaxIfOverflow(0, (equip.mdef() + chaosScrollRandomizedStat(range))));
            }
         }

         if (equip.speed() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.speed_$eq(getMaximumShortMaxIfOverflow(equip.speed(), (equip.speed() + chaosScrollRandomizedStat(range))));
            } else {
               equip.speed_$eq(getMaximumShortMaxIfOverflow(0, (equip.speed() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.jump() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.jump_$eq(getMaximumShortMaxIfOverflow(equip.jump(), (equip.jump() + chaosScrollRandomizedStat(range))));
            } else {
               equip.jump_$eq(getMaximumShortMaxIfOverflow(0, (equip.jump() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.hp() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.hp_$eq(getMaximumShortMaxIfOverflow(equip.hp(), (equip.hp() + chaosScrollRandomizedStat(range))));
            } else {
               equip.hp_$eq(getMaximumShortMaxIfOverflow(0, (equip.hp() + chaosScrollRandomizedStat(range))));
            }
         }
         if (equip.mp() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               equip.mp_$eq(getMaximumShortMaxIfOverflow(equip.mp(), (equip.mp() + chaosScrollRandomizedStat(range))));
            } else {
               equip.mp_$eq(getMaximumShortMaxIfOverflow(0, (equip.mp() + chaosScrollRandomizedStat(range))));
            }
         }
      }
   }

   private void scrollEquipWithChaos(Equip nEquip, int range) {
      if (YamlConfig.config.server.CHSCROLL_STAT_RATE > 0) {
         int temp;
         int currentStrength, currentDexterity, currentIntelligence, currentLuck, currentWeaponAttack, currentWeaponDefense,
               currentMagicAttack, currentMagicDefense, currentAccuracy, currentAvoidability, currentSpeed, currentJump,
               currentHp, currentMp;

         if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
            currentStrength = nEquip.str();
            currentDexterity = nEquip.dex();
            currentIntelligence = nEquip._int();
            currentLuck = nEquip.luk();
            currentWeaponAttack = nEquip.watk();
            currentWeaponDefense = nEquip.wdef();
            currentMagicAttack = nEquip.matk();
            currentMagicDefense = nEquip.mdef();
            currentAccuracy = nEquip.acc();
            currentAvoidability = nEquip.avoid();
            currentSpeed = nEquip.speed();
            currentJump = nEquip.jump();
            currentHp = nEquip.hp();
            currentMp = nEquip.mp();
         } else {
            currentStrength = Short.MIN_VALUE;
            currentDexterity = Short.MIN_VALUE;
            currentIntelligence = Short.MIN_VALUE;
            currentLuck = Short.MIN_VALUE;
            currentWeaponAttack = Short.MIN_VALUE;
            currentWeaponDefense = Short.MIN_VALUE;
            currentMagicAttack = Short.MIN_VALUE;
            currentMagicDefense = Short.MIN_VALUE;
            currentAccuracy = Short.MIN_VALUE;
            currentAvoidability = Short.MIN_VALUE;
            currentSpeed = Short.MIN_VALUE;
            currentJump = Short.MIN_VALUE;
            currentHp = Short.MIN_VALUE;
            currentMp = Short.MIN_VALUE;
         }

         for (int i = 0; i < YamlConfig.config.server.CHSCROLL_STAT_RATE; i++) {
            if (nEquip.str() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentStrength + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.str() + chaosScrollRandomizedStat(range);
               }

               currentStrength = getMaximumShortMaxIfOverflow(temp, currentStrength);
            }

            if (nEquip.dex() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentDexterity + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.dex() + chaosScrollRandomizedStat(range);
               }

               currentDexterity = getMaximumShortMaxIfOverflow(temp, currentDexterity);
            }

            if (nEquip._int() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentIntelligence + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip._int() + chaosScrollRandomizedStat(range);
               }

               currentIntelligence = getMaximumShortMaxIfOverflow(temp, currentIntelligence);
            }

            if (nEquip.luk() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentLuck + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.luk() + chaosScrollRandomizedStat(range);
               }

               currentLuck = getMaximumShortMaxIfOverflow(temp, currentLuck);
            }

            if (nEquip.watk() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentWeaponAttack + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.watk() + chaosScrollRandomizedStat(range);
               }

               currentWeaponAttack = getMaximumShortMaxIfOverflow(temp, currentWeaponAttack);
            }

            if (nEquip.wdef() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentWeaponDefense + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.wdef() + chaosScrollRandomizedStat(range);
               }

               currentWeaponDefense = getMaximumShortMaxIfOverflow(temp, currentWeaponDefense);
            }

            if (nEquip.matk() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentMagicAttack + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.matk() + chaosScrollRandomizedStat(range);
               }

               currentMagicAttack = getMaximumShortMaxIfOverflow(temp, currentMagicAttack);
            }

            if (nEquip.mdef() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentMagicDefense + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.mdef() + chaosScrollRandomizedStat(range);
               }

               currentMagicDefense = getMaximumShortMaxIfOverflow(temp, currentMagicDefense);
            }

            if (nEquip.acc() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentAccuracy + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.acc() + chaosScrollRandomizedStat(range);
               }

               currentAccuracy = getMaximumShortMaxIfOverflow(temp, currentAccuracy);
            }

            if (nEquip.avoid() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentAvoidability + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.avoid() + chaosScrollRandomizedStat(range);
               }

               currentAvoidability = getMaximumShortMaxIfOverflow(temp, currentAvoidability);
            }

            if (nEquip.speed() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentSpeed + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.speed() + chaosScrollRandomizedStat(range);
               }

               currentSpeed = getMaximumShortMaxIfOverflow(temp, currentSpeed);
            }

            if (nEquip.jump() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentJump + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.jump() + chaosScrollRandomizedStat(range);
               }

               currentJump = getMaximumShortMaxIfOverflow(temp, currentJump);
            }

            if (nEquip.hp() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentHp + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.hp() + chaosScrollRandomizedStat(range);
               }

               currentHp = getMaximumShortMaxIfOverflow(temp, currentHp);
            }

            if (nEquip.mp() > 0) {
               if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
                  temp = currentMp + chaosScrollRandomizedStat(range);
               } else {
                  temp = nEquip.mp() + chaosScrollRandomizedStat(range);
               }

               currentMp = getMaximumShortMaxIfOverflow(temp, currentMp);
            }
         }

         nEquip.str_$eq((short) Math.max(0, currentStrength));
         nEquip.dex_$eq((short) Math.max(0, currentDexterity));
         nEquip._int_$eq((short) Math.max(0, currentIntelligence));
         nEquip.luk_$eq((short) Math.max(0, currentLuck));
         nEquip.watk_$eq((short) Math.max(0, currentWeaponAttack));
         nEquip.wdef_$eq((short) Math.max(0, currentWeaponDefense));
         nEquip.matk_$eq((short) Math.max(0, currentMagicAttack));
         nEquip.mdef_$eq((short) Math.max(0, currentMagicDefense));
         nEquip.acc_$eq((short) Math.max(0, currentAccuracy));
         nEquip.avoid_$eq((short) Math.max(0, currentAvoidability));
         nEquip.speed_$eq((short) Math.max(0, currentSpeed));
         nEquip.jump_$eq((short) Math.max(0, currentJump));
         nEquip.hp_$eq((short) Math.max(0, currentHp));
         nEquip.mp_$eq((short) Math.max(0, currentMp));
      } else {
         if (nEquip.str() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.str_$eq(getMaximumShortMaxIfOverflow(nEquip.str(), (nEquip.str() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.str_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.str() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.dex() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.dex_$eq(getMaximumShortMaxIfOverflow(nEquip.dex(), (nEquip.dex() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.dex_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.dex() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip._int() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip._int_$eq(getMaximumShortMaxIfOverflow(nEquip._int(), (nEquip._int() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip._int_$eq(getMaximumShortMaxIfOverflow(0, (nEquip._int() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.luk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.luk_$eq(getMaximumShortMaxIfOverflow(nEquip.luk(), (nEquip.luk() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.luk_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.luk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.watk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.watk_$eq(getMaximumShortMaxIfOverflow(nEquip.watk(), (nEquip.watk() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.watk_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.watk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.wdef() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.wdef_$eq(getMaximumShortMaxIfOverflow(nEquip.wdef(), (nEquip.wdef() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.wdef_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.wdef() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.matk() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.matk_$eq(getMaximumShortMaxIfOverflow(nEquip.matk(), (nEquip.matk() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.matk_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.matk() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.mdef() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.mdef_$eq(getMaximumShortMaxIfOverflow(nEquip.mdef(), (nEquip.mdef() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.mdef_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.mdef() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.acc() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.acc_$eq(getMaximumShortMaxIfOverflow(nEquip.acc(), (nEquip.acc() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.acc_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.acc() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.avoid() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.avoid_$eq(getMaximumShortMaxIfOverflow(nEquip.avoid(), (nEquip.avoid() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.avoid_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.avoid() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.speed() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.speed_$eq(getMaximumShortMaxIfOverflow(nEquip.speed(), (nEquip.speed() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.speed_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.speed() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.jump() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.jump_$eq(getMaximumShortMaxIfOverflow(nEquip.jump(), (nEquip.jump() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.jump_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.jump() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.hp() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.hp_$eq(getMaximumShortMaxIfOverflow(nEquip.hp(), (nEquip.hp() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.hp_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.hp() + chaosScrollRandomizedStat(range))));
            }
         }
         if (nEquip.mp() > 0) {
            if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
               nEquip.mp_$eq(getMaximumShortMaxIfOverflow(nEquip.mp(), (nEquip.mp() + chaosScrollRandomizedStat(range))));
            } else {
               nEquip.mp_$eq(getMaximumShortMaxIfOverflow(0, (nEquip.mp() + chaosScrollRandomizedStat(range))));
            }
         }
      }
   }

   public boolean canUseCleanSlate(Equip equip) {
      Map<String, Integer> equipmentStats = this.getEquipStats(equip.id());
      return YamlConfig.config.server.USE_ENHANCED_CLNSLATE || equip.slots() < (byte) (equipmentStats.get("tuc") + equip.vicious());
   }

   public Item scrollEquipWithId(Item equip, int scrollId, boolean usingWhiteScroll, int vegaItemId, boolean isGM) {
      boolean assertGM = (isGM && YamlConfig.config.server.USE_PERFECT_GM_SCROLL);

      if (equip instanceof Equip) {
         Equip nEquip = (Equip) equip;
         Map<String, Integer> stats = this.getEquipStats(scrollId);

         if (((nEquip.slots() > 0 || ItemConstants.isCleanSlate(scrollId))) || assertGM) {
            double prop = (double) stats.get("success");

            switch (vegaItemId) {
               case 5610000:
                  if (prop == 10.0f) {
                     prop = 30.0f;
                  }
                  break;
               case 5610001:
                  if (prop == 60.0f) {
                     prop = 90.0f;
                  }
                  break;
               case 2049100:
                  prop = 100.0f;
                  break;
            }

            if (assertGM || rollSuccessChance(prop)) {
               short flag = nEquip.flag();
               switch (scrollId) {
                  case 2040727:
                     flag |= ItemConstants.SPIKES;
                     ItemProcessor.getInstance().setFlag(nEquip, (byte) flag);
                     break;
                  case 2041058:
                     flag |= ItemConstants.COLD;
                     ItemProcessor.getInstance().setFlag(nEquip, (byte) flag);
                     break;
                  case 2049000:
                  case 2049001:
                  case 2049002:
                  case 2049003:
                     if (canUseCleanSlate(nEquip)) {
                        nEquip.slots_$eq((byte) (nEquip.slots() + 1));
                     }
                     break;
                  case 2049100:
                  case 2049101:
                  case 2049102:
                     scrollEquipWithChaos(nEquip, YamlConfig.config.server.CHSCROLL_STAT_RANGE);
                     break;

                  default:
                     improveEquipStats(nEquip, stats);
                     break;
               }
               if (!ItemConstants.isCleanSlate(scrollId)) {
                  if (!assertGM && !ItemConstants.isModifierScroll(scrollId)) {
                     nEquip.slots_$eq((byte) (nEquip.slots() - 1));
                  }
                  nEquip.level_$eq((byte) (nEquip.level() + 1));
               }
            } else {
               if (!YamlConfig.config.server.USE_PERFECT_SCROLLING && !usingWhiteScroll && !ItemConstants.isCleanSlate(scrollId) && !assertGM && !ItemConstants.isModifierScroll(scrollId)) {
                  nEquip.slots_$eq((byte) (nEquip.slots() - 1));
               }
               if (Randomizer.nextInt(100) < stats.get("cursed")) {
                  return null;
               }
            }
         }
      }
      return equip;
   }

   public Item getEquipById(int equipId) {
      return getEquipById(equipId, -1);
   }

   private Item getEquipById(int equipId, int ringId) {
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(equipId, false) > 1);
      Equip nEquip = new Equip(equipId, (byte) 0, ringId, isElemental);
      nEquip.quantity_$eq((short) 1);
      Map<String, Integer> stats = this.getEquipStats(equipId);
      if (stats != null) {
         for (Entry<String, Integer> stat : stats.entrySet()) {
            if (stat.getKey().equals("STR")) {
               nEquip.str_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("DEX")) {
               nEquip.dex_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("INT")) {
               nEquip._int_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("LUK")) {
               nEquip.luk_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("PAD")) {
               nEquip.watk_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("PDD")) {
               nEquip.wdef_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MAD")) {
               nEquip.matk_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MDD")) {
               nEquip.mdef_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("ACC")) {
               nEquip.acc_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("EVA")) {
               nEquip.avoid_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("Speed")) {
               nEquip.speed_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("Jump")) {
               nEquip.jump_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MHP")) {
               nEquip.hp_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MMP")) {
               nEquip.mp_$eq((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("tuc")) {
               nEquip.slots_$eq((byte) stat.getValue().intValue());
            } else if (isUntradeableRestricted(equipId)) {
               short flag = nEquip.flag();
               flag |= ItemConstants.UNTRADEABLE;
               ItemProcessor.getInstance().setFlag(nEquip, flag);
            } else if (stats.get("fs") > 0) {
               short flag = nEquip.flag();
               flag |= ItemConstants.SPIKES;
               ItemProcessor.getInstance().setFlag(nEquip, flag);
               equipCache.put(equipId, nEquip);
            }
         }
      }
      return nEquip.copy();
   }

   public Equip randomizeStats(Equip equip) {
      equip.str_$eq(getRandStat(equip.str(), 5));
      equip.dex_$eq(getRandStat(equip.dex(), 5));
      equip._int_$eq(getRandStat(equip._int(), 5));
      equip.luk_$eq(getRandStat(equip.luk(), 5));
      equip.matk_$eq(getRandStat(equip.matk(), 5));
      equip.watk_$eq(getRandStat(equip.watk(), 5));
      equip.acc_$eq(getRandStat(equip.acc(), 5));
      equip.avoid_$eq(getRandStat(equip.avoid(), 5));
      equip.jump_$eq(getRandStat(equip.jump(), 5));
      equip.speed_$eq(getRandStat(equip.speed(), 5));
      equip.wdef_$eq(getRandStat(equip.wdef(), 10));
      equip.mdef_$eq(getRandStat(equip.mdef(), 10));
      equip.hp_$eq(getRandStat(equip.hp(), 10));
      equip.mp_$eq(getRandStat(equip.mp(), 10));
      return equip;
   }

   public Equip randomizeUpgradeStats(Equip equip) {
      equip.str_$eq(getRandUpgradedStat(equip.str(), 2));
      equip.dex_$eq(getRandUpgradedStat(equip.dex(), 2));
      equip._int_$eq(getRandUpgradedStat(equip._int(), 2));
      equip.luk_$eq(getRandUpgradedStat(equip.luk(), 2));
      equip.matk_$eq(getRandUpgradedStat(equip.matk(), 2));
      equip.watk_$eq(getRandUpgradedStat(equip.watk(), 2));
      equip.acc_$eq(getRandUpgradedStat(equip.acc(), 2));
      equip.avoid_$eq(getRandUpgradedStat(equip.avoid(), 2));
      equip.jump_$eq(getRandUpgradedStat(equip.jump(), 2));
      equip.wdef_$eq(getRandUpgradedStat(equip.wdef(), 5));
      equip.mdef_$eq(getRandUpgradedStat(equip.mdef(), 5));
      equip.hp_$eq(getRandUpgradedStat(equip.hp(), 5));
      equip.mp_$eq(getRandUpgradedStat(equip.mp(), 5));
      return equip;
   }

   public MapleStatEffect getItemEffect(int itemId) {
      MapleStatEffect ret = itemEffects.get(itemId);
      if (ret == null) {
         MapleData item = getItemData(itemId);
         if (item == null) {
            return null;
         }
         MapleData spec = item.getChildByPath("specEx");
         if (spec == null) {
            spec = item.getChildByPath("spec");
         }
         ret = StatEffectProcessor.getInstance().loadItemEffectFromData(spec, itemId);
         itemEffects.put(itemId, ret);
      }
      return ret;
   }

   public int[][] getSummonMobs(int itemId) {
      MapleData data = getItemData(itemId);
      int theInt = data.getChildByPath("mob").getChildren().size();
      int[][] mobs2spawn = new int[theInt][2];
      for (int x = 0; x < theInt; x++) {
         mobs2spawn[x][0] = MapleDataTool.getIntConvert("mob/" + x + "/id", data);
         mobs2spawn[x][1] = MapleDataTool.getIntConvert("mob/" + x + "/prob", data);
      }
      return mobs2spawn;
   }

   public int getWeaponAttackForProjectile(int itemId) {
      Integer atk = projectileWeaponAttackCache.get(itemId);
      if (atk != null) {
         return atk;
      }
      MapleData data = getItemData(itemId);
      atk = MapleDataTool.getInt("info/incPAD", data, 0);
      projectileWeaponAttackCache.put(itemId, atk);
      return atk;
   }

   public String getName(int itemId) {
      if (nameCache.containsKey(itemId)) {
         return nameCache.get(itemId);
      }
      MapleData strings = getStringData(itemId);
      if (strings == null) {
         return null;
      }
      String ret = MapleDataTool.getString("name", strings, null);
      nameCache.put(itemId, ret);
      return ret;
   }

   public String getMsg(int itemId) {
      if (msgCache.containsKey(itemId)) {
         return msgCache.get(itemId);
      }
      MapleData strings = getStringData(itemId);
      if (strings == null) {
         return null;
      }
      String ret = MapleDataTool.getString("msg", strings, null);
      msgCache.put(itemId, ret);
      return ret;
   }

   public boolean isUntradeableRestricted(int itemId) {
      if (untradeableCache.containsKey(itemId)) {
         return untradeableCache.get(itemId);
      }

      boolean bRestricted = false;
      if (itemId != 0) {
         MapleData data = getItemData(itemId);
         if (data != null) {
            bRestricted = MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
         }
      }

      untradeableCache.put(itemId, bRestricted);
      return bRestricted;
   }

   public boolean isAccountRestricted(int itemId) {
      if (accountItemRestrictionCache.containsKey(itemId)) {
         return accountItemRestrictionCache.get(itemId);
      }

      boolean bRestricted = false;
      if (itemId != 0) {
         MapleData data = getItemData(itemId);
         if (data != null) {
            bRestricted = MapleDataTool.getIntConvert("info/accountSharable", data, 0) == 1;
         }
      }

      accountItemRestrictionCache.put(itemId, bRestricted);
      return bRestricted;
   }

   public boolean isLootRestricted(int itemId) {
      if (dropRestrictionCache.containsKey(itemId)) {
         return dropRestrictionCache.get(itemId);
      }

      boolean bRestricted = false;
      if (itemId != 0) {
         MapleData data = getItemData(itemId);
         if (data != null) {
            bRestricted = MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
            if (!bRestricted) {
               bRestricted = isAccountRestricted(itemId);
            }
         }
      }

      dropRestrictionCache.put(itemId, bRestricted);
      return bRestricted;
   }

   public boolean isDropRestricted(int itemId) {
      return isLootRestricted(itemId) || isQuestItem(itemId);
   }

   public boolean isPickupRestricted(int itemId) {
      if (pickupRestrictionCache.containsKey(itemId)) {
         return pickupRestrictionCache.get(itemId);
      }

      boolean bRestricted = false;
      if (itemId != 0) {
         MapleData data = getItemData(itemId);
         if (data != null) {
            bRestricted = MapleDataTool.getIntConvert("info/only", data, 0) == 1;
         }
      }

      pickupRestrictionCache.put(itemId, bRestricted);
      return bRestricted;
   }

   private Pair<Map<String, Integer>, MapleData> getSkillStatsInternal(int itemId) {
      Map<String, Integer> ret = skillUpgradeCache.get(itemId);
      MapleData retSkill = skillUpgradeInfoCache.get(itemId);

      if (ret != null) {
         return new Pair<>(ret, retSkill);
      }

      retSkill = null;
      ret = new LinkedHashMap<>();
      MapleData item = getItemData(itemId);
      if (item != null) {
         MapleData info = item.getChildByPath("info");
         if (info != null) {
            for (MapleData data : info.getChildren()) {
               if (data.getName().startsWith("inc")) {
                  ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
               }
            }
            ret.put("masterLevel", MapleDataTool.getInt("masterLevel", info, 0));
            ret.put("reqSkillLevel", MapleDataTool.getInt("reqSkillLevel", info, 0));
            ret.put("success", MapleDataTool.getInt("success", info, 0));

            retSkill = info.getChildByPath("skill");
         }
      }

      skillUpgradeCache.put(itemId, ret);
      skillUpgradeInfoCache.put(itemId, retSkill);
      return new Pair<>(ret, retSkill);
   }

   public Map<String, Integer> getSkillStats(int itemId, double playerJob) {
      Pair<Map<String, Integer>, MapleData> retData = getSkillStatsInternal(itemId);
      if (retData.getLeft().isEmpty()) {
         return null;
      }

      Map<String, Integer> ret = new LinkedHashMap<>(retData.getLeft());
      MapleData skill = retData.getRight();
      int currentSkill;
      for (int i = 0; i < skill.getChildren().size(); i++) {
         currentSkill = MapleDataTool.getInt(Integer.toString(i), skill, 0);
         if (currentSkill == 0) {
            break;
         }
         if (currentSkill / 10000 == playerJob) {
            ret.put("skillid", currentSkill);
            break;
         }
      }
      ret.putIfAbsent("skillid", 0);
      return ret;
   }

   public Pair<Integer, Boolean> canPetConsume(Integer petId, Integer itemId) {
      Pair<Integer, Set<Integer>> foodData = cashPetFoodCache.get(itemId);

      if (foodData == null) {
         Set<Integer> pets = new HashSet<>(4);
         int inc = 1;

         MapleData data = getItemData(itemId);
         if (data != null) {
            MapleData specData = data.getChildByPath("spec");
            for (MapleData specItem : specData.getChildren()) {
               String itemName = specItem.getName();

               try {
                  Integer.parseInt(itemName); // check if it's a pet id node

                  Integer petid = MapleDataTool.getInt(specItem, 0);
                  pets.add(petid);
               } catch (NumberFormatException npe) {
                  if (itemName.contentEquals("inc")) {
                     inc = MapleDataTool.getInt(specItem, 1);
                  }
               }
            }
         }

         foodData = new Pair<>(inc, pets);
         cashPetFoodCache.put(itemId, foodData);
      }

      return new Pair<>(foodData.getLeft(), foodData.getRight().contains(petId));
   }

   public boolean isQuestItem(int itemId) {
      if (isQuestItemCache.containsKey(itemId)) {
         return isQuestItemCache.get(itemId);
      }
      MapleData data = getItemData(itemId);
      boolean questItem = (data != null && MapleDataTool.getIntConvert("info/quest", data, 0) == 1);
      isQuestItemCache.put(itemId, questItem);
      return questItem;
   }

   public boolean isPartyQuestItem(int itemId) {
      if (isPartyQuestItemCache.containsKey(itemId)) {
         return isPartyQuestItemCache.get(itemId);
      }
      MapleData data = getItemData(itemId);
      boolean partyQuestItem = (data != null && MapleDataTool.getIntConvert("info/pquest", data, 0) == 1);
      isPartyQuestItemCache.put(itemId, partyQuestItem);
      return partyQuestItem;
   }

   private void loadCardIdData() {
      DatabaseConnection.getInstance().withConnection(connection -> MonsterCardProvider.getInstance().getMonsterCardData(connection).forEach(data -> monsterBookID.put(data.cardId(), data.mobId())));
   }

   public int getCardMobId(int id) {
      return monsterBookID.get(id);
   }

   public boolean isUntradeableOnEquip(int itemId) {
      if (onEquipUntradeableCache.containsKey(itemId)) {
         return onEquipUntradeableCache.get(itemId);
      }
      boolean untradeableOnEquip = MapleDataTool.getIntConvert("info/equipTradeBlock", getItemData(itemId), 0) > 0;
      onEquipUntradeableCache.put(itemId, untradeableOnEquip);
      return untradeableOnEquip;
   }

   public ScriptedItem getScriptedItemInfo(int itemId) {
      if (scriptedItemCache.containsKey(itemId)) {
         return scriptedItemCache.get(itemId);
      }
      if ((itemId / 10000) != 243) {
         return null;
      }
      MapleData itemInfo = getItemData(itemId);
      ScriptedItem script = new ScriptedItem(MapleDataTool.getInt("spec/npc", itemInfo, 0),
            MapleDataTool.getString("spec/script", itemInfo, ""),
            MapleDataTool.getInt("spec/runOnPickup", itemInfo, 0) == 1);
      scriptedItemCache.put(itemId, script);
      return scriptedItemCache.get(itemId);
   }

   public boolean isKarmaAble(int itemId) {
      if (karmaCache.containsKey(itemId)) {
         return karmaCache.get(itemId);
      }
      boolean bRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", getItemData(itemId), 0) > 0;
      karmaCache.put(itemId, bRestricted);
      return bRestricted;
   }

   public int getStateChangeItem(int itemId) {
      if (triggerItemCache.containsKey(itemId)) {
         return triggerItemCache.get(itemId);
      } else {
         int triggerItem = MapleDataTool.getIntConvert("info/stateChangeItem", getItemData(itemId), 0);
         triggerItemCache.put(itemId, triggerItem);
         return triggerItem;
      }
   }

   public int getCreateItem(int itemId) {
      if (createItem.containsKey(itemId)) {
         return createItem.get(itemId);
      } else {
         int itemFrom = MapleDataTool.getIntConvert("info/create", getItemData(itemId), 0);
         createItem.put(itemId, itemFrom);
         return itemFrom;
      }
   }

   public int getMobItem(int itemId) {
      if (mobItem.containsKey(itemId)) {
         return mobItem.get(itemId);
      } else {
         int mobItemCatch = MapleDataTool.getIntConvert("info/mob", getItemData(itemId), 0);
         mobItem.put(itemId, mobItemCatch);
         return mobItemCatch;
      }
   }

   public int getUseDelay(int itemId) {
      if (useDelay.containsKey(itemId)) {
         return useDelay.get(itemId);
      } else {
         int mobUseDelay = MapleDataTool.getIntConvert("info/useDelay", getItemData(itemId), 0);
         useDelay.put(itemId, mobUseDelay);
         return mobUseDelay;
      }
   }

   public int getMobHP(int itemId) {
      if (mobHP.containsKey(itemId)) {
         return mobHP.get(itemId);
      } else {
         int mobHPItem = MapleDataTool.getIntConvert("info/mobHP", getItemData(itemId), 0);
         mobHP.put(itemId, mobHPItem);
         return mobHPItem;
      }
   }

   public int getExpById(int itemId) {
      if (expCache.containsKey(itemId)) {
         return expCache.get(itemId);
      } else {
         int exp = MapleDataTool.getIntConvert("spec/exp", getItemData(itemId), 0);
         expCache.put(itemId, exp);
         return exp;
      }
   }

   public int getMaxLevelById(int itemId) {
      if (levelCache.containsKey(itemId)) {
         return levelCache.get(itemId);
      } else {
         int level = MapleDataTool.getIntConvert("info/maxLevel", getItemData(itemId), 256);
         levelCache.put(itemId, level);
         return level;
      }
   }

   public Pair<Integer, List<RewardItem>> getItemReward(int itemId) {
      if (rewardCache.containsKey(itemId)) {
         return rewardCache.get(itemId);
      }
      int totalProbability = 0;
      List<RewardItem> rewards = new ArrayList<>();
      for (MapleData child : getItemData(itemId).getChildByPath("reward").getChildren()) {
         RewardItem reward = new RewardItem();
         reward.itemId = MapleDataTool.getInt("item", child, 0);
         reward.prob = (byte) MapleDataTool.getInt("prob", child, 0);
         reward.quantity = (short) MapleDataTool.getInt("count", child, 0);
         reward.effect = MapleDataTool.getString("Effect", child, "");
         reward.worldMessage = MapleDataTool.getString("worldMsg", child, null);
         reward.period = MapleDataTool.getInt("period", child, -1);

         totalProbability += reward.prob;

         rewards.add(reward);
      }
      Pair<Integer, List<RewardItem>> hmm = new Pair<>(totalProbability, rewards);
      rewardCache.put(itemId, hmm);
      return hmm;
   }

   public boolean isConsumeOnPickup(int itemId) {
      if (consumeOnPickupCache.containsKey(itemId)) {
         return consumeOnPickupCache.get(itemId);
      }
      MapleData data = getItemData(itemId);
      boolean consume = MapleDataTool.getIntConvert("spec/consumeOnPickup", data, 0) == 1 || MapleDataTool.getIntConvert("specEx/consumeOnPickup", data, 0) == 1;
      consumeOnPickupCache.put(itemId, consume);
      return consume;
   }

   public final boolean isTwoHanded(int itemId) {
      switch (getWeaponType(itemId)) {
         case GENERAL2H_SWING:
         case BOW:
         case CLAW:
         case CROSSBOW:
         case POLE_ARM_SWING:
         case SPEAR_STAB:
         case SWORD2H:
         case GUN:
         case KNUCKLE:
            return true;
         default:
            return false;
      }
   }

   public boolean isCash(int itemId) {
      int itemType = itemId / 1000000;
      if (itemType == 5) {
         return true;
      }
      if (itemType != 1) {
         return false;
      }

      Map<String, Integer> eqpStats = getEquipStats(itemId);
      return eqpStats != null && eqpStats.get("cash") == 1;
   }

   public boolean isUpgradeable(int itemId) {
      Item it = this.getEquipById(itemId);
      Equip eq = (Equip) it;

      return (eq.slots() > 0 || eq.str() > 0 || eq.dex() > 0 || eq._int() > 0 || eq.luk() > 0 ||
            eq.watk() > 0 || eq.matk() > 0 || eq.wdef() > 0 || eq.mdef() > 0 || eq.acc() > 0 ||
            eq.avoid() > 0 || eq.speed() > 0 || eq.jump() > 0 || eq.hp() > 0 || eq.mp() > 0);
   }

   public boolean isUnmerchable(int itemId) {
      if (YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_CASH && isCash(itemId)) {
         return true;
      }

      return YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_PET && ItemConstants.isPet(itemId);
   }

   public Collection<Item> canWearEquipment(MapleCharacter chr, Collection<Item> items) {
      MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIPPED);
      if (inv.checked()) {
         return items;
      }
      Collection<Item> itemz = new LinkedList<>();
      if (chr.getJob() == MapleJob.SUPER_GM || chr.getJob() == MapleJob.GM) {
         for (Item item : items) {
            Equip equip = (Equip) item;
            equip.wearing_$eq(true);
            itemz.add(item);
         }
         return itemz;
      }
      boolean highFiveStamp = false;
      int tdex = chr.getDex(), tstr = chr.getStr(), tint = chr.getInt(), tluk = chr.getLuk(), fame = chr.getFame();
      if (chr.getJob() != MapleJob.SUPER_GM || chr.getJob() != MapleJob.GM) {
         for (Item item : inv.list()) {
            Equip equip = (Equip) item;
            tdex += equip.dex();
            tstr += equip.str();
            tluk += equip.luk();
            tint += equip._int();
         }
      }
      for (Item item : items) {
         Equip equip = (Equip) item;
         int reqLevel = getEquipLevelReq(equip.id());
         if (highFiveStamp) {
            reqLevel -= 5;
            if (reqLevel < 0) {
               reqLevel = 0;
            }
         }
            /*
             int reqJob = getEquipStats(equip.getItemId()).get("reqJob");
             if (reqJob != 0) {
             Really hard check, and not really needed in this one
             Gm's should just be GM job, and players cannot change jobs.
             }*/
         if (reqLevel > chr.getLevel()) {
            continue;
         } else if (getEquipStats(equip.id()).get("reqDEX") > tdex) {
            continue;
         } else if (getEquipStats(equip.id()).get("reqSTR") > tstr) {
            continue;
         } else if (getEquipStats(equip.id()).get("reqLUK") > tluk) {
            continue;
         } else if (getEquipStats(equip.id()).get("reqINT") > tint) {
            continue;
         }
         int reqPOP = getEquipStats(equip.id()).get("reqPOP");
         if (reqPOP > 0) {
            if (getEquipStats(equip.id()).get("reqPOP") > fame) {
               continue;
            }
         }
         equip.wearing_$eq(true);
         itemz.add(equip);
      }
      inv.checked(true);
      return itemz;
   }

   public boolean canWearEquipment(MapleCharacter chr, Equip equip, int dst) {
      int id = equip.id();

      if (ItemConstants.isWeddingRing(id) && chr.hasJustMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "The Wedding Ring cannot be equipped on this map.");  // will dc everyone due to doubled couple effect
         return false;
      }

      String equipmentSlot = getEquipmentSlot(id);
      if (!EquipSlot.getFromTextSlot(equipmentSlot).isAllowed(dst, isCash(id))) {
         equip.wearing_$eq(false);
         String itemName = MapleItemInformationProvider.getInstance().getName(equip.id());
         Server.getInstance().broadcastGMMessage(chr.getWorld(), PacketCreator.create(new YellowTip("[Warning]: " + chr.getName() + " tried to equip " + itemName + " into slot " + dst + ".")));
         AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to forcibly equip an item.");
         FilePrinter.printError(FilePrinter.EXPLOITS + chr.getName() + ".txt", chr.getName() + " tried to equip " + itemName + " into " + dst + " slot.");
         return false;
      }

      if (chr.getJob() == MapleJob.SUPER_GM || chr.getJob() == MapleJob.GM) {
         equip.wearing_$eq(true);
         return true;
      }


      boolean highFiveStamp = false;
        /* Removed check above for message ><
         try {
         for (Pair<Item, MapleInventoryType> ii : ItemFactory.INVENTORY.loadItems(chr.getId(), false)) {
         if (ii.getRight() == MapleInventoryType.CASH) {
         if (ii.getLeft().getItemId() == 5590000) {
         highFiveStamp = true;
         }
         }
         }
         } catch (SQLException ex) {
            ex.printStackTrace();
         }*/

      int reqLevel = getEquipLevelReq(equip.id());
      if (highFiveStamp) {
         reqLevel -= 5;
      }
      int i = 0; //lol xD
      //Removed job check. Shouldn't really be needed.
      if (reqLevel > chr.getLevel()) {
         i++;
      } else if (getEquipStats(equip.id()).get("reqDEX") > chr.getTotalDex()) {
         i++;
      } else if (getEquipStats(equip.id()).get("reqSTR") > chr.getTotalStr()) {
         i++;
      } else if (getEquipStats(equip.id()).get("reqLUK") > chr.getTotalLuk()) {
         i++;
      } else if (getEquipStats(equip.id()).get("reqINT") > chr.getTotalInt()) {
         i++;
      }
      int reqPOP = getEquipStats(equip.id()).get("reqPOP");
      if (reqPOP > 0) {
         if (getEquipStats(equip.id()).get("reqPOP") > chr.getFame()) {
            i++;
         }
      }

      if (i > 0) {
         equip.wearing_$eq(false);
         return false;
      }
      equip.wearing_$eq(true);
      return true;
   }

   public ArrayList<Pair<Integer, String>> getItemDataByName(String name) {
      ArrayList<Pair<Integer, String>> ret = new ArrayList<>();
      for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
         if (itemPair.getRight().toLowerCase().contains(name.toLowerCase())) {
            ret.add(itemPair);
         }
      }
      return ret;
   }

   private MapleData getEquipLevelInfo(int itemId) {
      MapleData equipLevelData = equipLevelInfoCache.get(itemId);
      if (equipLevelData == null) {
         if (equipLevelInfoCache.containsKey(itemId)) {
            return null;
         }

         MapleData iData = getItemData(itemId);
         if (iData != null) {
            MapleData data = iData.getChildByPath("info/level");
            if (data != null) {
               equipLevelData = data.getChildByPath("info");
            }
         }

         equipLevelInfoCache.put(itemId, equipLevelData);
      }

      return equipLevelData;
   }

   public int getEquipLevel(int itemId, boolean getMaxLevel) {
      Integer eqLevel = equipMaxLevelCache.get(itemId);
      if (eqLevel == null) {
         eqLevel = 1;

         MapleData data = getEquipLevelInfo(itemId);
         if (data != null) {
            if (getMaxLevel) {
               int curLevel = 1;

               while (true) {
                  MapleData data2 = data.getChildByPath(Integer.toString(curLevel));
                  if (data2 == null || data2.getChildren().size() <= 1) {
                     eqLevel = curLevel;
                     equipMaxLevelCache.put(itemId, eqLevel);
                     break;
                  }

                  curLevel++;
               }
            } else {
               MapleData data2 = data.getChildByPath("1");
               if (data2 != null && data2.getChildren().size() > 1) {
                  eqLevel = 2;
               }
            }
         }
      }

      return eqLevel;
   }

   public List<Pair<String, Integer>> getItemLevelUpStats(int itemId, int level) {
      List<Pair<String, Integer>> list = new LinkedList<>();
      MapleData data = getEquipLevelInfo(itemId);
      if (data != null) {
         MapleData data2 = data.getChildByPath(Integer.toString(level));
         if (data2 != null) {
            for (MapleData da : data2.getChildren()) {
               if (Math.random() < 0.9) {
                  if (da.getName().startsWith("incDEXMin")) {
                     list.add(new Pair<>("incDEX", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incDEXMax")))));
                  } else if (da.getName().startsWith("incSTRMin")) {
                     list.add(new Pair<>("incSTR", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incSTRMax")))));
                  } else if (da.getName().startsWith("incINTMin")) {
                     list.add(new Pair<>("incINT", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incINTMax")))));
                  } else if (da.getName().startsWith("incLUKMin")) {
                     list.add(new Pair<>("incLUK", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incLUKMax")))));
                  } else if (da.getName().startsWith("incMHPMin")) {
                     list.add(new Pair<>("incMHP", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMHPMax")))));
                  } else if (da.getName().startsWith("incMMPMin")) {
                     list.add(new Pair<>("incMMP", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMMPMax")))));
                  } else if (da.getName().startsWith("incPADMin")) {
                     list.add(new Pair<>("incPAD", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incPADMax")))));
                  } else if (da.getName().startsWith("incMADMin")) {
                     list.add(new Pair<>("incMAD", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMADMax")))));
                  } else if (da.getName().startsWith("incPDDMin")) {
                     list.add(new Pair<>("incPDD", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incPDDMax")))));
                  } else if (da.getName().startsWith("incMDDMin")) {
                     list.add(new Pair<>("incMDD", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMDDMax")))));
                  } else if (da.getName().startsWith("incACCMin")) {
                     list.add(new Pair<>("incACC", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incACCMax")))));
                  } else if (da.getName().startsWith("incEVAMin")) {
                     list.add(new Pair<>("incEVA", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incEVAMax")))));
                  } else if (da.getName().startsWith("incSpeedMin")) {
                     list.add(new Pair<>("incSpeed", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incSpeedMax")))));
                  } else if (da.getName().startsWith("incJumpMin")) {
                     list.add(new Pair<>("incJump", Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incJumpMax")))));
                  }
               }
            }
         }
      }

      return list;
   }

   public Pair<String, Integer> getMakerReagentStatUpgrade(int itemId) {
      Pair<String, Integer> statUpgrade = statUpgradeMakerCache.get(itemId);
      if (statUpgrade != null) {
         return statUpgrade;
      } else if (statUpgradeMakerCache.containsKey(itemId)) {
         return null;
      }

      statUpgrade = DatabaseConnection.getInstance().withConnectionResult(connection -> MakerReagentProvider.getInstance().getForItem(connection, itemId).map(data -> new Pair<>(data.stat(), data.value())).get()).orElse(null);
      statUpgradeMakerCache.put(itemId, statUpgrade);
      return statUpgrade;
   }

   public int getMakerCrystalFromLeftover(Integer leftoverId) {
      Integer itemId = mobCrystalMakerCache.get(leftoverId);
      if (itemId != null) {
         return itemId;
      }

      List<Integer> monsterIds = DatabaseConnection.getInstance().withConnectionResult(connection ->
            DropDataProvider.getInstance().getMonstersWhoDrop(connection, leftoverId))
            .orElse(Collections.singletonList(-1));
      itemId = getCrystalForLevel(MapleLifeFactory.getMonsterLevel(monsterIds.get(0)) - 1);
      mobCrystalMakerCache.put(leftoverId, itemId);
      return itemId;
   }

   public MakerItemCreateEntry getMakerItemEntry(int toCreate) {
      MakerItemCreateEntry makerEntry;

      if ((makerEntry = makerItemCache.get(toCreate)) != null) {
         return new MakerItemCreateEntry(makerEntry);
      } else {
         makerEntry = DatabaseConnection.getInstance().withConnectionResult(connection -> {
            int reqLevel = -1;
            int reqMakerLevel = -1;
            int cost = -1;
            int toGive = -1;

            Optional<MakerCreateData> makerCreateData = MakerCreateProvider.getInstance().getMakerCreateDataForItem(connection, toCreate);
            if (makerCreateData.isPresent()) {
               reqLevel = makerCreateData.get().requiredLevel();
               reqMakerLevel = makerCreateData.get().requiredMakerLevel();
               cost = makerCreateData.get().requiredMeso();
               toGive = makerCreateData.get().quantity();
            }
            MakerItemCreateEntry result = new MakerItemCreateEntry(cost, reqLevel, reqMakerLevel);
            result.addGainItem(toCreate, toGive);

            MakerRecipeProvider.getInstance().getRecipeForItem(connection, toCreate).forEach(data -> result.addReqItem(data.requiredItem(), data.count()));
            makerItemCache.put(toCreate, new MakerItemCreateEntry(result));
            return result;
         }).orElseThrow();
      }

      return makerEntry;
   }

   public int getMakerCrystalFromEquip(Integer equipId) {
      try {
         return getCrystalForLevel(getEquipLevelReq(equipId));
      } catch (Exception e) {
         e.printStackTrace();
      }

      return -1;
   }

   public int getMakerStimulantFromEquip(Integer equipId) {
      try {
         return getCrystalForLevel(getEquipLevelReq(equipId));
      } catch (Exception e) {
         e.printStackTrace();
      }

      return -1;
   }

   public List<Pair<Integer, Integer>> getMakerDisassembledItems(Integer itemId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection ->
            MakerRecipeProvider.getInstance().getMakerDisassembledItems(connection, itemId).stream()
                  .map(data -> new Pair<>(data.requiredItem(), data.count()))
                  .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
   }

   public int getMakerDisassembledFee(Integer itemId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<MakerCreateData> makerCreateData = MakerCreateProvider.getInstance().getMakerCreateDataForItem(connection, itemId);
         if (makerCreateData.isEmpty()) {
            return -1;
         }

         float val = (float) (makerCreateData.get().requiredMeso() * 0.13636363636364);
         return (int) (val / 1000) * 1000;
      }).orElse(-1);
   }

   public int getMakerStimulant(int itemId) {
      Integer cachedItemId = makerCatalystCache.get(itemId);
      if (cachedItemId != null) {
         return cachedItemId;
      }

      cachedItemId = -1;
      for (MapleData md : etcData.getData("ItemMake.img").getChildren()) {
         MapleData me = md.getChildByPath(StringUtil.getLeftPaddedStr(Integer.toString(itemId), '0', 8));

         if (me != null) {
            cachedItemId = MapleDataTool.getInt(me.getChildByPath("catalyst"), -1);
            break;
         }
      }

      makerCatalystCache.put(itemId, cachedItemId);
      return cachedItemId;
   }

   public Set<String> getWhoDrops(Integer itemId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection ->
            DropDataProvider.getInstance().getMonstersWhoDrop(connection, itemId).stream()
                  .map(monsterId -> MapleMonsterInformationProvider.getInstance().getMobNameFromId(monsterId))
                  .collect(Collectors.toSet()))
            .orElse(new HashSet<>());
   }

   private boolean canUseSkillBook(MapleCharacter player, Integer skillBookId) {
      Map<String, Integer> skillData = getSkillStats(skillBookId, player.getJob().getId());
      if (skillData == null || skillData.get("skillid") == 0) {
         return false;
      }

      return SkillFactory.getSkill(skillData.get("skillid"))
            .map(skill -> (skillData.get("skillid") != 0 && ((player.getSkillLevel(skill) >= skillData.get("reqSkillLevel") || skillData.get("reqSkillLevel") == 0) && player.getMasterLevel(skill) < skillData.get("masterLevel"))))
            .orElse(false);
   }

   public List<Integer> usableMasteryBooks(MapleCharacter player) {
      List<Integer> masteryBooks = new LinkedList<>();
      for (Integer i = 2290000; i <= 2290139; i++) {
         if (canUseSkillBook(player, i)) {
            masteryBooks.add(i);
         }
      }

      return masteryBooks;
   }

   public List<Integer> usableSkillBooks(MapleCharacter player) {
      List<Integer> skillBook = new LinkedList<>();
      for (Integer i = 2280000; i <= 2280019; i++) {
         if (canUseSkillBook(player, i)) {
            skillBook.add(i);
         }
      }

      return skillBook;
   }

   public final QuestConsItem getQuestConsumablesInfo(final int itemId) {
      if (questItemConsCache.containsKey(itemId)) {
         return questItemConsCache.get(itemId);
      }
      MapleData data = getItemData(itemId);
      QuestConsItem qcItem = null;

      MapleData infoData = data.getChildByPath("info");
      if (infoData.getChildByPath("uiData") != null) {
         qcItem = new QuestConsItem();
         qcItem.exp = MapleDataTool.getInt("exp", infoData);
         qcItem.grade = MapleDataTool.getInt("grade", infoData);
         qcItem.questId = MapleDataTool.getInt("questId", infoData);
         qcItem.items = new HashMap<>(2);

         Map<Integer, Integer> cItems = qcItem.items;
         MapleData ciData = infoData.getChildByPath("consumeItem");
         if (ciData != null) {
            for (MapleData ciItem : ciData.getChildren()) {
               int consumeItemId = MapleDataTool.getInt("0", ciItem);
               int qty = MapleDataTool.getInt("1", ciItem);

               cItems.put(consumeItemId, qty);
            }
         }
      }

      questItemConsCache.put(itemId, qcItem);
      return qcItem;
   }

   public static final class RewardItem {

      public int itemId, period;
      public short prob, quantity;
      public String effect, worldMessage;
   }

   public static final class QuestConsItem {

      public int questId, exp, grade;
      public Map<Integer, Integer> items;

      public Integer getItemRequirement(int itemId) {
         return items.get(itemId);
      }

   }
}
