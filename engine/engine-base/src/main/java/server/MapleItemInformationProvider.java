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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import client.database.data.MakerCreateData;
import client.inventory.Equip;
import client.inventory.EquipBuilder;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleWeaponType;
import client.processor.ItemProcessor;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.inventory.EquipSlot;
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
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.TriFunction;
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
   protected Map<Integer, MapleData> equipLevelInfoCache = new HashMap<>();
   protected Map<Integer, Integer> equipLevelReqCache = new HashMap<>();
   protected Map<Integer, Integer> equipMaxLevelCache = new HashMap<>();
   protected Map<Integer, List<Integer>> scrollReqsCache = new HashMap<>();
   protected Map<Integer, Integer> wholePriceCache = new HashMap<>();
   protected Map<Integer, Double> unitPriceCache = new HashMap<>();
   protected Map<Integer, Integer> projectileWeaponAttackCache = new HashMap<>();
   protected Map<Integer, String> nameCache = new HashMap<>();
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

   public static Equip improveEquipStats(Equip nEquip, Map<String, Integer> stats) {
      EquipBuilder builder = Equip.newBuilder(nEquip);

      for (Entry<String, Integer> stat : stats.entrySet()) {
         switch (stat.getKey()) {
            case "STR":
               builder.increaseStr(stat.getValue());
               break;
            case "DEX":
               builder.increaseDex(stat.getValue());
               break;
            case "INT":
               builder.increaseIntelligence(stat.getValue());
               break;
            case "LUK":
               builder.increaseLuk(stat.getValue());
               break;
            case "PAD":
               builder.increaseWatk(stat.getValue());
               break;
            case "PDD":
               builder.increaseWdef(stat.getValue());
               break;
            case "MAD":
               builder.increaseMatk(stat.getValue());
               break;
            case "MDD":
               builder.increaseMdef(stat.getValue());
               break;
            case "ACC":
               builder.increaseAcc(stat.getValue());
               break;
            case "EVA":
               builder.increaseAvoid(stat.getValue());
               break;
            case "Speed":
               builder.increaseSpeed(stat.getValue());
               break;
            case "Jump":
               builder.increaseJump(stat.getValue());
               break;
            case "MHP":
               builder.increaseHp(stat.getValue());
               break;
            case "MMP":
               builder.increaseMp(stat.getValue());
               break;
            case "afterImage":
               break;
         }
      }
      return builder.build();
   }

   private static int getCrystalForLevel(int level) {
      int range = (level - 1) / 10;

      if (range < 5) {
         return 4260000;
      } else if (range > 11) {
         return 4260008;
      } else {
         return switch (range) {
            case 5 -> 4260001;
            case 6 -> 4260002;
            case 7 -> 4260003;
            case 8 -> 4260004;
            case 9 -> 4260005;
            case 10 -> 4260006;
            default -> 4260007;
         };
      }
   }

   protected void addItems(Supplier<MapleData> dataSupplier, Function<MapleData, Iterable<MapleData>> iterable,
                           List<Pair<Integer, String>> itemPairs) {
      MapleData itemsData = dataSupplier.get();
      for (MapleData itemFolder : iterable.apply(itemsData)) {
         itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
      }
   }

   public List<Pair<Integer, String>> getAllItems() {
      if (!itemNameCache.isEmpty()) {
         return itemNameCache;
      }
      List<Pair<Integer, String>> itemPairs = new ArrayList<>();
      addItems(() -> stringData.getData("Cash.img"), MapleData::getChildren, itemPairs);
      addItems(() -> stringData.getData("Consume.img"), MapleData::getChildren, itemPairs);
      addItems(() -> stringData.getData("Eqp.img").getChildByPath("Eqp"),
            itemsData -> itemsData.getChildren().stream().map(MapleData::getChildren).flatMap(List::stream)
                  .collect(Collectors.toList()), itemPairs);
      addItems(() -> stringData.getData("Etc.img").getChildByPath("Etc"), MapleData::getChildren, itemPairs);
      addItems(() -> stringData.getData("Ins.img"), MapleData::getChildren, itemPairs);
      addItems(() -> stringData.getData("Pet.img"), MapleData::getChildren, itemPairs);
      return itemPairs;
   }

   public List<Pair<Integer, String>> getAllEtcItems() {
      if (!itemNameCache.isEmpty()) {
         return itemNameCache;
      }

      List<Pair<Integer, String>> itemPairs = new ArrayList<>();
      addItems(() -> stringData.getData("Etc.img").getChildByPath("Etc"), MapleData::getChildren, itemPairs);
      return itemPairs;
   }

   private MapleData getStringData(int itemId) {
      String cat = "null";
      MapleData theData;
      if (itemId >= 5010000) {
         theData = cashStringData;
      } else if (itemId >= 2000000 && itemId < 3000000) {
         theData = consumeStringData;
      } else if ((itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000) || (itemId >= 1132000
            && itemId < 1133000) || (itemId >= 1142000 && itemId < 1143000)) {
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
      return getCacheableBoolean(itemId, noCancelMouseCache, "info/noCancelMouse", value -> value == 1);
   }

   private MapleData getItemData(int itemId) {
      String idStr = "0" + itemId;
      MapleDataDirectoryEntry root = itemData.getRoot();
      for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
         for (MapleDataFileEntry iFile : topDir.getFiles()) {
            if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
               MapleData ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
               if (ret == null) {
                  return null;
               }
               return ret.getChildByPath(idStr);
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
      return null;
   }

   public List<Integer> getItemIdsInRange(int minId, int maxId, boolean ignoreCashItem) {
      if (ignoreCashItem) {
         return IntStream.rangeClosed(minId, maxId)
               .filter(id -> getItemData(id) != null && !isCash(id))
               .boxed()
               .collect(Collectors.toList());
      } else {
         return IntStream.rangeClosed(minId, maxId)
               .filter(id -> getItemData(id) != null)
               .boxed()
               .collect(Collectors.toList());
      }
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

   protected int supplyMeso(int itemId) {
      MapleData item = getItemData(itemId);
      if (item == null) {
         return -1;
      }
      MapleData pData = item.getChildByPath("info/meso");
      if (pData == null) {
         return -1;
      }
      return MapleDataTool.getInt(pData);
   }

   public int getMeso(int itemId) {
      return getCacheableThing(itemId, getMesoCache, this::supplyMeso);
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
      return getCacheableThing(itemId, wholePriceCache, this::supplyWholePrice);
   }

   protected Integer supplyWholePrice(int itemId) {
      return getItemPriceData(itemId).getLeft();
   }

   public double getUnitPrice(int itemId) {
      return getCacheableThing(itemId, unitPriceCache, this::supplyUnitPrice);
   }

   protected Double supplyUnitPrice(int itemId) {
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
      return getCacheableThing(itemId, replaceOnExpireCache, this::supplyReplaceOnExpire);
   }

   protected Pair<Integer, String> supplyReplaceOnExpire(Integer internalItemId) {
      MapleData data = getItemData(internalItemId);
      int itemReplacement = MapleDataTool.getInt("info/replace/itemid", data, 0);
      String msg = MapleDataTool.getString("info/replace/msg", data, "");
      return new Pair<>(itemReplacement, msg);
   }

   protected String getEquipmentSlot(int itemId) {
      return getCacheableThing(itemId, equipmentSlotCache, this::supplyEquipmentSlot);
   }

   protected String supplyEquipmentSlot(Integer internalItemId) {
      MapleData item = getItemData(internalItemId);
      if (item == null) {
         return null;
      }

      MapleData info = item.getChildByPath("info");
      if (info == null) {
         return null;
      }
      return MapleDataTool.getString("islot", info, "");
   }

   public Map<String, Integer> getEquipStats(int itemId) {
      return getCacheableThing(itemId, equipStatsCache, this::supplyEquipmentStatistics);
   }

   protected Map<String, Integer> supplyEquipmentStatistics(Integer internalItemId) {
      Map<String, Integer> ret = new LinkedHashMap<>();
      MapleData item = getItemData(internalItemId);
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
      return ret;
   }

   public Integer getEquipLevelReq(int itemId) {
      return getCacheableThing(itemId, equipLevelReqCache, this::supplyEquipLevelRequirement);
   }

   protected Integer supplyEquipLevelRequirement(Integer internalItemId) {
      int ret = 0;
      MapleData item = getItemData(internalItemId);
      if (item != null) {
         MapleData info = item.getChildByPath("info");
         if (info != null) {
            ret = MapleDataTool.getInt("reqLevel", info, 0);
         }
      }
      return ret;
   }

   public List<Integer> getScrollReqs(int itemId) {
      return getCacheableThing(itemId, scrollReqsCache, this::supplyScrollReqs);
   }

   protected List<Integer> supplyScrollReqs(Integer internalItemId) {
      List<Integer> ret = new ArrayList<>();
      MapleData data = getItemData(internalItemId).getChildByPath("req");
      if (data != null) {
         data.getChildren().forEach(req -> ret.add(MapleDataTool.getInt(req)));
      }
      return ret;
   }

   public MapleWeaponType getWeaponType(int itemId) {
      int cat = (itemId / 10000) % 100;
      MapleWeaponType[] type = {MapleWeaponType.SWORD1H, MapleWeaponType.GENERAL1H_SWING, MapleWeaponType.GENERAL1H_SWING,
            MapleWeaponType.DAGGER_OTHER, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.NOT_A_WEAPON,
            MapleWeaponType.WAND, MapleWeaponType.STAFF, MapleWeaponType.NOT_A_WEAPON, MapleWeaponType.SWORD2H,
            MapleWeaponType.GENERAL2H_SWING, MapleWeaponType.GENERAL2H_SWING, MapleWeaponType.SPEAR_STAB,
            MapleWeaponType.POLE_ARM_SWING, MapleWeaponType.BOW, MapleWeaponType.CROSSBOW, MapleWeaponType.CLAW,
            MapleWeaponType.KNUCKLE, MapleWeaponType.GUN};
      if (cat < 30 || cat > 49) {
         return MapleWeaponType.NOT_A_WEAPON;
      }
      return type[cat - 30];
   }

   public Equip scrollOptionEquipWithChaos(Equip equip, int range, boolean option) {
      return Equip.newBuilder(equip).withChaos(range, option).build();
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
                     nEquip =
                           Equip.newBuilder(nEquip).setFlag(ItemProcessor.getInstance().setFlag(nEquip.id(), (byte) flag)).build();
                     break;
                  case 2041058:
                     flag |= ItemConstants.COLD;
                     nEquip =
                           Equip.newBuilder(nEquip).setFlag(ItemProcessor.getInstance().setFlag(nEquip.id(), (byte) flag)).build();
                     break;
                  case 2049000:
                  case 2049001:
                  case 2049002:
                  case 2049003:
                     if (canUseCleanSlate(nEquip)) {
                        nEquip = Equip.newBuilder(nEquip).setSlots((byte) (nEquip.slots() + 1)).build();
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
                  EquipBuilder builder = Equip.newBuilder(nEquip);
                  if (!assertGM && !ItemConstants.isModifierScroll(scrollId)) {
                     builder.setSlots((byte) (nEquip.slots() - 1));
                  }
                  builder.setLevel((byte) (nEquip.level() + 1));
                  nEquip = builder.build();
               }
            } else {
               if (!YamlConfig.config.server.USE_PERFECT_SCROLLING && !usingWhiteScroll && !ItemConstants.isCleanSlate(scrollId)
                     && !assertGM && !ItemConstants.isModifierScroll(scrollId)) {
                  nEquip = Equip.newBuilder(nEquip).setSlots((byte) (nEquip.slots() - 1)).build();
               }
               if (Randomizer.nextInt(100) < stats.get("cursed")) {
                  return null;
               }
            }
         }
         equip = nEquip;
      }
      return equip;
   }

   public Equip scrollEquipWithChaos(Equip nEquip, int range) {
      return Equip.newBuilder(nEquip).withChaos(range).build();
   }

   public Equip getEquipById(int equipId) {
      return getEquipById(equipId, -1);
   }

   private Equip getEquipById(int equipId, int ringId) {
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(equipId, false) > 1);

      EquipBuilder builder = Equip.newBuilder(equipId)
            .setPosition((short) 0)
            .setSlots(ringId)
            .setElemental(isElemental)
            .setQuantity((short) 1);

      Map<String, Integer> stats = this.getEquipStats(equipId);
      if (stats != null) {
         for (Entry<String, Integer> stat : stats.entrySet()) {
            if (stat.getKey().equals("STR")) {
               builder.setStr((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("DEX")) {
               builder.setDex((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("INT")) {
               builder.setIntelligence((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("LUK")) {
               builder.setLuk((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("PAD")) {
               builder.setWatk((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("PDD")) {
               builder.setWdef((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MAD")) {
               builder.setMatk((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MDD")) {
               builder.setMdef((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("ACC")) {
               builder.setAcc((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("EVA")) {
               builder.setAvoid((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("Speed")) {
               builder.setSpeed((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("Jump")) {
               builder.setJump((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MHP")) {
               builder.setHp((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("MMP")) {
               builder.setMp((short) stat.getValue().intValue());
            } else if (stat.getKey().equals("tuc")) {
               builder.setSlots((byte) stat.getValue().intValue());
            } else if (isUntradeableRestricted(equipId)) {
               builder.orFlag(ItemConstants.UNTRADEABLE);
            } else if (stats.get("fs") > 0) {
               builder.orFlag(ItemConstants.SPIKES);
            }
         }
      }
      return builder.build();
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
      return getCacheableInteger(itemId, projectileWeaponAttackCache, "info/incPAD");
   }

   public String getName(int itemId) {
      return getCacheableString(itemId, nameCache, "name");
   }

   public String getMsg(int itemId) {
      return getCacheableString(itemId, msgCache, "msg");
   }

   public boolean isUntradeableRestricted(int itemId) {
      return getCacheableBoolean(itemId, untradeableCache, "info/tradeBlock", value -> value == 1);
   }

   public boolean isAccountRestricted(int itemId) {
      return getCacheableBoolean(itemId, accountItemRestrictionCache, "info/accountSharable", value -> value == 1);
   }

   public boolean isLootRestricted(int itemId) {
      return getCacheableBoolean(itemId, dropRestrictionCache, "info/tradeBlock", value -> supplyLootRestricted(itemId, value));
   }

   protected Boolean supplyLootRestricted(int itemId, Integer value) {
      boolean restricted = value == 1;
      if (!restricted) {
         restricted = isAccountRestricted(itemId);
      }
      return restricted;
   }

   public boolean isDropRestricted(int itemId) {
      return isLootRestricted(itemId) || isQuestItem(itemId);
   }

   public boolean isPickupRestricted(int itemId) {
      return getCacheableBoolean(itemId, pickupRestrictionCache, "info/only", value -> value == 1);
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
      return getCacheableBoolean(itemId, isQuestItemCache, "info/quest", value -> value == 1);
   }

   public boolean isPartyQuestItem(int itemId) {
      return getCacheableBoolean(itemId, isPartyQuestItemCache, "info/pquest", value -> value == 1);
   }

   private void loadCardIdData() {
      DatabaseConnection.getInstance().withConnection(connection ->
            MonsterCardProvider.getInstance().getMonsterCardData(connection)
                  .forEach(data -> monsterBookID.put(data.cardId(), data.mobId())));
   }

   public int getCardMobId(int id) {
      return monsterBookID.get(id);
   }

   public boolean isUntradeableOnEquip(int itemId) {
      return getCacheableBoolean(itemId, onEquipUntradeableCache, "info/equipTradeBlock", value -> value > 0);
   }

   public ScriptedItem getScriptedItemInfo(int itemId) {
      return getCacheableThing(itemId, scriptedItemCache, this::supplyScriptedItemInfo);
   }

   protected ScriptedItem supplyScriptedItemInfo(Integer internalItemId) {
      if ((internalItemId / 10000) != 243) {
         return null;
      }
      MapleData itemInfo = getItemData(internalItemId);
      return new ScriptedItem(MapleDataTool.getInt("spec/npc", itemInfo, 0),
            MapleDataTool.getString("spec/script", itemInfo, ""),
            MapleDataTool.getInt("spec/runOnPickup", itemInfo, 0) == 1);
   }

   protected boolean getCacheableBoolean(int itemId, Map<Integer, Boolean> cache, String path,
                                         Function<Integer, Boolean> evaluation) {
      return getCacheableThing(itemId, cache, this::getItemData,
            (mapleData, defaultValue) -> evaluation.apply(MapleDataTool.getIntConvert(path, mapleData, 0)), false);
   }

   protected <T> T getCacheableThing(int itemId, Map<Integer, T> cache, BiFunction<Integer, T, T> supplier, T defaultValue) {
      if (cache.containsKey(itemId)) {
         return cache.get(itemId);
      }

      T result = supplier.apply(itemId, defaultValue);
      cache.put(itemId, result);
      return result;
   }

   protected <T> T getCacheableThing(int itemId, Map<Integer, T> cache, Function<Integer, T> supplier) {
      if (cache.containsKey(itemId)) {
         return cache.get(itemId);
      }

      T result = supplier.apply(itemId);
      cache.put(itemId, result);
      return result;
   }

   protected <T> T getCacheableThing(int itemId, Map<Integer, T> cache,
                                     Function<Integer, MapleData> supplier,
                                     BiFunction<MapleData, T, T> converter, T defaultValue) {
      return getCacheableThing(itemId, cache, (internalItemId, internalDefaultValue) -> {
         MapleData mapleData = supplier.apply(itemId);
         if (mapleData == null) {
            return defaultValue;
         }
         return converter.apply(mapleData, defaultValue);
      }, defaultValue);
   }

   protected <T> T getCacheableThing(int itemId, Map<Integer, T> cache, String path,
                                     Function<Integer, MapleData> supplier,
                                     TriFunction<T, String, MapleData, T> converter, T defaultValue) {
      return getCacheableThing(itemId, cache, (internalItemId, internalDefaultValue) -> {
         MapleData mapleData = supplier.apply(itemId);
         if (mapleData == null) {
            return defaultValue;
         }
         return converter.apply(path, mapleData, defaultValue);
      }, defaultValue);
   }

   protected Integer getCacheableInteger(int itemId, Map<Integer, Integer> cache, String path, Integer defaultValue) {
      return getCacheableThing(itemId, cache, path, this::getItemData, MapleDataTool::getIntConvert, defaultValue);
   }

   protected String getCacheableString(int itemId, Map<Integer, String> cache, String path) {
      return getCacheableThing(itemId, cache, path, this::getStringData, MapleDataTool::getString, null);
   }

   protected Integer getCacheableInteger(int itemId, Map<Integer, Integer> cache, String path) {
      return getCacheableInteger(itemId, cache, path, 0);
   }

   public boolean isKarmaAble(int itemId) {
      return getCacheableBoolean(itemId, karmaCache, "info/tradeAvailable", value -> value > 0);
   }

   public int getStateChangeItem(int itemId) {
      return getCacheableInteger(itemId, triggerItemCache, "info/stateChangeItem");
   }

   public int getCreateItem(int itemId) {
      return getCacheableInteger(itemId, createItem, "info/create");
   }

   public int getMobItem(int itemId) {
      return getCacheableInteger(itemId, mobItem, "info/mob");
   }

   public int getUseDelay(int itemId) {
      return getCacheableInteger(itemId, useDelay, "info/useDelay");
   }

   public int getMobHP(int itemId) {
      return getCacheableInteger(itemId, mobHP, "info/mobHP");
   }

   public int getExpById(int itemId) {
      return getCacheableInteger(itemId, expCache, "spec/exp");
   }

   public int getMaxLevelById(int itemId) {
      return getCacheableInteger(itemId, levelCache, "info/maxLevel", 256);
   }

   public Pair<Integer, List<RewardItem>> getItemReward(int itemId) {
      return getCacheableThing(itemId, rewardCache, this::supplyItemReward);
   }

   protected Pair<Integer, List<RewardItem>> supplyItemReward(Integer internalItemId) {
      int totalProbability = 0;
      List<RewardItem> rewards = new ArrayList<>();
      for (MapleData child : getItemData(internalItemId).getChildByPath("reward").getChildren()) {
         RewardItem reward = new RewardItem(
               MapleDataTool.getInt("item", child, 0),
               MapleDataTool.getInt("period", child, -1),
               (byte) MapleDataTool.getInt("prob", child, 0),
               (short) MapleDataTool.getInt("count", child, 0),
               MapleDataTool.getString("Effect", child, ""),
               MapleDataTool.getString("worldMsg", child, null));
         totalProbability += reward.probability();
         rewards.add(reward);
      }
      return new Pair<>(totalProbability, rewards);
   }

   public boolean isConsumeOnPickup(int itemId) {
      return getCacheableThing(itemId, consumeOnPickupCache, this::supplyConsumeOnPickup);
   }

   protected Boolean supplyConsumeOnPickup(int itemId) {
      MapleData data = getItemData(itemId);
      return MapleDataTool.getIntConvert("spec/consumeOnPickup", data, 0) == 1
            || MapleDataTool.getIntConvert("specEx/consumeOnPickup", data, 0) == 1;
   }

   public final boolean isTwoHanded(int itemId) {
      return switch (getWeaponType(itemId)) {
         case GENERAL2H_SWING, BOW, CLAW, CROSSBOW, POLE_ARM_SWING, SPEAR_STAB, SWORD2H, GUN, KNUCKLE -> true;
         default -> false;
      };
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
      Equip eq = this.getEquipById(itemId);
      return (eq.slots() > 0 || eq.str() > 0 || eq.dex() > 0 || eq.intelligence() > 0 || eq.luk() > 0 ||
            eq.watk() > 0 || eq.matk() > 0 || eq.wdef() > 0 || eq.mdef() > 0 || eq.acc() > 0 ||
            eq.avoid() > 0 || eq.speed() > 0 || eq.jump() > 0 || eq.hp() > 0 || eq.mp() > 0);
   }

   public boolean isUnmerchable(int itemId) {
      if (YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_CASH && isCash(itemId)) {
         return true;
      }
      return YamlConfig.config.server.USE_ENFORCE_UNMERCHABLE_PET && ItemConstants.isPet(itemId);
   }

   //TODO JDT update equip?
   public Collection<Item> canWearEquipment(MapleCharacter chr, Collection<Item> items) {
      MapleInventory inv = chr.getInventory(MapleInventoryType.EQUIPPED);
      if (inv.checked()) {
         return items;
      }
      Collection<Item> itemz = new LinkedList<>();
      if (chr.getJob() == MapleJob.SUPER_GM || chr.getJob() == MapleJob.GM) {
         items.stream().map(item -> (Equip) item).forEach(equip -> {
            equip = Equip.newBuilder(equip).setWearing(true).build();
            itemz.add(equip);
         });
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
            tint += equip.intelligence();
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
         equip = Equip.newBuilder(equip).setWearing(true).build();
         itemz.add(equip);
      }
      inv.checked(true);
      return itemz;
   }

   //TODO JDT update equip
   public boolean canWearEquipment(MapleCharacter chr, Equip equip, int dst) {
      int id = equip.id();

      if (ItemConstants.isWeddingRing(id) && chr.hasJustMarried()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT,
               I18nMessage.from("MARRIAGE_WEDDING_RING_EQUIP_ERROR"));  // will dc everyone due to doubled couple effect
         return false;
      }

      String equipmentSlot = getEquipmentSlot(id);
      if (!EquipSlot.getFromTextSlot(equipmentSlot).isAllowed(dst, isCash(id))) {
         equip = Equip.newBuilder(equip).setWearing(false).build();
         String itemName = MapleItemInformationProvider.getInstance().getName(equip.id());
         Server.getInstance().broadcastGMMessage(chr.getWorld(), PacketCreator
               .create(new YellowTip("[Warning]: " + chr.getName() + " tried to equip " + itemName + " into slot " + dst + ".")));
         AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to forcibly equip an item.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS,
               chr.getName() + " tried to equip " + itemName + " into " + dst + " slot.");
         return false;
      }

      if (chr.getJob() == MapleJob.SUPER_GM || chr.getJob() == MapleJob.GM) {
         equip = Equip.newBuilder(equip).setWearing(true).build();
         return true;
      }

      boolean highFiveStamp = false;
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
         equip = Equip.newBuilder(equip).setWearing(false).build();
         return false;
      }
      equip = Equip.newBuilder(equip).setWearing(true).build();
      return true;
   }

   public List<Pair<Integer, String>> getItemDataByName(String name) {
      return MapleItemInformationProvider.getInstance().getAllItems().stream()
            .filter(pair -> pair.getRight().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
   }

   private MapleData getEquipLevelInfo(int itemId) {
      return getCacheableThing(itemId, equipLevelInfoCache, this::supplyEquipLevelInfo);
   }

   private MapleData supplyEquipLevelInfo(int itemId) {
      MapleData iData = getItemData(itemId);
      if (iData != null) {
         MapleData data = iData.getChildByPath("info/level");
         if (data != null) {
            return data.getChildByPath("info");
         }
      }
      return null;
   }

   public int getEquipLevel(int itemId, boolean getMaxLevel) {
      return getCacheableThing(itemId, equipMaxLevelCache, internalItemId -> supplyEquipLevel(itemId, getMaxLevel));
   }

   protected Integer supplyEquipLevel(int itemId, boolean getMaxLevel) {
      int eqLevel = 1;
      MapleData data = getEquipLevelInfo(itemId);
      if (data != null) {
         if (getMaxLevel) {
            int curLevel = 1;

            while (true) {
               MapleData data2 = data.getChildByPath(Integer.toString(curLevel));
               if (data2 == null || data2.getChildren().size() <= 1) {
                  eqLevel = curLevel;
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
                     list.add(new Pair<>("incDEX",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incDEXMax")))));
                  } else if (da.getName().startsWith("incSTRMin")) {
                     list.add(new Pair<>("incSTR",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incSTRMax")))));
                  } else if (da.getName().startsWith("incINTMin")) {
                     list.add(new Pair<>("incINT",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incINTMax")))));
                  } else if (da.getName().startsWith("incLUKMin")) {
                     list.add(new Pair<>("incLUK",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incLUKMax")))));
                  } else if (da.getName().startsWith("incMHPMin")) {
                     list.add(new Pair<>("incMHP",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMHPMax")))));
                  } else if (da.getName().startsWith("incMMPMin")) {
                     list.add(new Pair<>("incMMP",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMMPMax")))));
                  } else if (da.getName().startsWith("incPADMin")) {
                     list.add(new Pair<>("incPAD",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incPADMax")))));
                  } else if (da.getName().startsWith("incMADMin")) {
                     list.add(new Pair<>("incMAD",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMADMax")))));
                  } else if (da.getName().startsWith("incPDDMin")) {
                     list.add(new Pair<>("incPDD",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incPDDMax")))));
                  } else if (da.getName().startsWith("incMDDMin")) {
                     list.add(new Pair<>("incMDD",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incMDDMax")))));
                  } else if (da.getName().startsWith("incACCMin")) {
                     list.add(new Pair<>("incACC",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incACCMax")))));
                  } else if (da.getName().startsWith("incEVAMin")) {
                     list.add(new Pair<>("incEVA",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incEVAMax")))));
                  } else if (da.getName().startsWith("incSpeedMin")) {
                     list.add(new Pair<>("incSpeed",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incSpeedMax")))));
                  } else if (da.getName().startsWith("incJumpMin")) {
                     list.add(new Pair<>("incJump",
                           Randomizer.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data2.getChildByPath("incJumpMax")))));
                  }
               }
            }
         }
      }

      return list;
   }

   public Pair<String, Integer> getMakerReagentStatUpgrade(int itemId) {
      return getCacheableThing(itemId, statUpgradeMakerCache, internalItemId ->
            DatabaseConnection.getInstance().withConnectionResult(connection ->
                  MakerReagentProvider.getInstance().getForItem(connection, itemId)
                        .map(data -> new Pair<>(data.stat(), data.value())).get())
                  .orElse(null));
   }

   public int getMakerCrystalFromLeftover(Integer leftoverId) {
      return getCacheableThing(leftoverId, mobCrystalMakerCache, itemId -> {
         List<Integer> monsterIds = DatabaseConnection.getInstance().withConnectionResult(connection ->
               DropDataProvider.getInstance().getMonstersWhoDrop(connection, leftoverId))
               .orElse(Collections.singletonList(-1));
         return getCrystalForLevel(MapleLifeFactory.getMonsterLevel(monsterIds.get(0)) - 1);
      });
   }

   public MakerItemCreateEntry getMakerItemEntry(int toCreate) {
      return getCacheableThing(toCreate, makerItemCache, this::supplyMakerItemEntry);
   }

   protected MakerItemCreateEntry supplyMakerItemEntry(int itemId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         int reqLevel = -1;
         int reqMakerLevel = -1;
         int cost = -1;
         int toGive = -1;

         Optional<MakerCreateData> makerCreateData =
               MakerCreateProvider.getInstance().getMakerCreateDataForItem(connection, itemId);
         if (makerCreateData.isPresent()) {
            reqLevel = makerCreateData.get().requiredLevel();
            reqMakerLevel = makerCreateData.get().requiredMakerLevel();
            cost = makerCreateData.get().requiredMeso();
            toGive = makerCreateData.get().quantity();
         }
         MakerItemCreateEntry result = new MakerItemCreateEntry(cost, reqLevel, reqMakerLevel);
         result.addGainItem(itemId, toGive);

         MakerRecipeProvider.getInstance().getRecipeForItem(connection, itemId)
               .forEach(data -> result.addReqItem(data.requiredItem(), data.count()));
         makerItemCache.put(itemId, new MakerItemCreateEntry(result));
         return result;
      }).orElseThrow();
   }

   public int getMakerCrystalFromEquip(Integer equipId) {
      return getCrystalForLevel(getEquipLevelReq(equipId));
   }

   public int getMakerStimulantFromEquip(Integer equipId) {
      return getCrystalForLevel(getEquipLevelReq(equipId));
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
         Optional<MakerCreateData> makerCreateData =
               MakerCreateProvider.getInstance().getMakerCreateDataForItem(connection, itemId);
         if (makerCreateData.isEmpty()) {
            return -1;
         }

         float val = (float) (makerCreateData.get().requiredMeso() * 0.13636363636364);
         return (int) (val / 1000) * 1000;
      }).orElse(-1);
   }

   public int getMakerStimulant(int itemId) {
      return getCacheableThing(itemId, makerCatalystCache, this::supplyMakerStimulant);
   }

   protected Integer supplyMakerStimulant(Integer itemId) {
      for (MapleData md : etcData.getData("ItemMake.img").getChildren()) {
         MapleData me = md.getChildByPath(StringUtil.getLeftPaddedStr(Integer.toString(itemId), '0', 8));
         if (me != null) {
            return MapleDataTool.getInt(me.getChildByPath("catalyst"), -1);
         }
      }
      return -1;
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
            .map(skill -> evaluateIfSkillBookCanBeUsed(player, skillData, skill))
            .orElse(false);
   }

   private boolean evaluateIfSkillBookCanBeUsed(MapleCharacter player, Map<String, Integer> skillData, Skill skill) {
      boolean skillExists = skillData.get("skilliid") != 0;
      boolean playerSkillAboveRequirement =
            player.getSkillLevel(skill) >= skillData.get("reqSkillLevel") || skillData.get("reqSkillLevel") == 0;
      boolean masterLevelBelowMax = player.getMasterLevel(skill) < skillData.get("masterLevel");
      return skillExists && playerSkillAboveRequirement && masterLevelBelowMax;
   }

   public List<Integer> usableMasteryBooks(MapleCharacter player) {
      return IntStream.rangeClosed(2290000, 2290139)
            .filter(id -> canUseSkillBook(player, id))
            .boxed()
            .collect(Collectors.toList());
   }

   public List<Integer> usableSkillBooks(MapleCharacter player) {
      return IntStream.rangeClosed(2280000, 2280019)
            .filter(id -> canUseSkillBook(player, id))
            .boxed()
            .collect(Collectors.toList());
   }

   protected QuestConsItem supplyQuestConsumablesInfo(int itemId) {
      MapleData infoData = getItemData(itemId).getChildByPath("info");
      if (infoData.getChildByPath("uiData") != null) {
         Map<Integer, Integer> cItems = new HashMap<>(2);
         MapleData ciData = infoData.getChildByPath("consumeItem");
         if (ciData != null) {
            for (MapleData ciItem : ciData.getChildren()) {
               int consumeItemId = MapleDataTool.getInt("0", ciItem);
               int qty = MapleDataTool.getInt("1", ciItem);

               cItems.put(consumeItemId, qty);
            }
         }

         return new QuestConsItem(
               MapleDataTool.getInt("questId", infoData),
               MapleDataTool.getInt("exp", infoData),
               MapleDataTool.getInt("grade", infoData),
               cItems);
      }
      return null;
   }

   public final QuestConsItem getQuestConsumablesInfo(final int itemId) {
      return getCacheableThing(itemId, questItemConsCache, this::supplyQuestConsumablesInfo);
   }
}