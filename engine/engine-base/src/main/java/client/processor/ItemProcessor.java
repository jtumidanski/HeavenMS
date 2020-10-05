package client.processor;

import java.util.LinkedList;
import java.util.List;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.StatUpgrade;
import client.inventory.manipulator.MapleKarmaManipulator;
import config.YamlConfig;
import constants.game.ExpTable;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.showitemgaininchat.ShowSpecialEffect;

public class ItemProcessor {
   private static ItemProcessor instance;

   public static ItemProcessor getInstance() {
      if (instance == null) {
         instance = new ItemProcessor();
      }
      return instance;
   }

   private ItemProcessor() {
   }

   public short setFlag(int itemId, short b) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii.isAccountRestricted(itemId)) {
         b |= ItemConstants.ACCOUNT_SHARING;
      }
      return b;
   }

   public Equip setMergeFlag(Equip item) {
      short flag = item.flag();
      flag |= ItemConstants.MERGE_UNTRADEABLE;
      flag |= ItemConstants.UNTRADEABLE;
      return Equip.newBuilder(item).setFlag(setFlag(item.id(), flag)).build();
   }

   public boolean hasMergeFlag(Item item) {
      return (item.flag() & ItemConstants.MERGE_UNTRADEABLE) == ItemConstants.MERGE_UNTRADEABLE;
   }

   public boolean isUnableToBeTraded(Item item) {
      return ((item.flag() & ItemConstants.UNTRADEABLE) == ItemConstants.UNTRADEABLE) || (MapleItemInformationProvider.getInstance().isDropRestricted(item.id()) && !MapleKarmaManipulator.hasKarmaFlag(item));
   }

   public int getStatModifier(boolean isAttribute) {
      // each set of stat points grants a chance for a bonus stat point upgrade at equip level up.

      if (YamlConfig.config.server.USE_EQUIPMNT_LVLUP_POWER) {
         if (isAttribute) {
            return 2;
         } else {
            return 4;
         }
      } else {
         if (isAttribute) {
            return 4;
         } else {
            return 16;
         }
      }
   }

   public int randomizeStatUpgrade(int top) {
      int limit = Math.min(top, YamlConfig.config.server.MAX_EQUIPMNT_LVLUP_STAT_UP);

      int poolCount = (limit * (limit + 1) / 2) + limit;
      int rnd = Randomizer.rand(0, poolCount);

      int stat = 0;
      if (rnd >= limit) {
         rnd -= limit;
         stat = 1 + (int) Math.floor((-1 + Math.sqrt((8 * rnd) + 1)) / 2);    // optimized randomizeStatUpgrade author: David A.
      }

      return stat;
   }

   public void getUnitSlotUpgrade(List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name) {
      if (Math.random() < 0.1) {
         stats.add(new Pair<>(name, 1));  // 10% success on getting a slot upgrade.
      }
   }

   public double normalizedMasteryExp(int reqLevel) {
      // Conversion factor between mob exp and equip exp gain. Through many calculations, the expected for equipment level up
      // from level 1 to 2 is killing about 100~200 mobs of the same level range, on a 1x EXP rate scenario.

      if (reqLevel < 5) {
         return 42;
      } else if (reqLevel >= 78) {
         return Math.max((10413.648 * Math.exp(reqLevel * 0.03275)), 15);
      } else if (reqLevel >= 38) {
         return Math.max((4985.818 * Math.exp(reqLevel * 0.02007)), 15);
      } else if (reqLevel >= 18) {
         return Math.max((248.219 * Math.exp(reqLevel * 0.11093)), 15);
      } else {
         return Math.max(((1334.564 * Math.log(reqLevel)) - 1731.976), 15);
      }
   }

   public void showLevelUpMessage(String msg, MapleClient c) {
      c.getPlayer().showHint(msg, 300);
   }

   public String showEquipFeatures(Equip equip) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (!ii.isUpgradeable(equip.id())) {
         return "";
      }

      String eqpName = ii.getName(equip.id());
      String eqpInfo = reachedMaxLevel(equip) ? " #e#rMAX LEVEL#k#n" : (" EXP: #e#b" + (int) equip.itemExp() + "#k#n / " + ExpTable.getEquipExpNeededForLevel(equip.itemLevel()));

      return "'" + eqpName + "' -> LV: #e#b" + equip.itemLevel() + "#k#n    " + eqpInfo + "\r\n";
   }

   private boolean reachedMaxLevel(Equip equip) {
      if (equip.elemental()) {
         if (equip.itemLevel() < MapleItemInformationProvider.getInstance().getEquipLevel(equip.id(), true)) {
            return false;
         }
      }

      return equip.itemLevel() >= YamlConfig.config.server.USE_EQUIPMNT_LVLUP;
   }


   public synchronized void gainItemExp(Equip equip, MapleClient c, int gain) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Equip result = equip;
      if (!ii.isUpgradeable(result.id())) {
         return;
      }

      int equipMaxLevel = Math.min(30, Math.max(ii.getEquipLevel(result.id(), true), YamlConfig.config.server.USE_EQUIPMNT_LVLUP));
      if (result.itemLevel() >= equipMaxLevel) {
         return;
      }

      int reqLevel = ii.getEquipLevelReq(result.id());

      float masteryModifier = (float) (YamlConfig.config.server.EQUIP_EXP_RATE * ExpTable.getExpNeededForLevel(1)) / (float) normalizedMasteryExp(reqLevel);
      float elementModifier = (result.elemental()) ? 0.85f : 0.6f;

      float baseExpGain = gain * elementModifier * masteryModifier;

      result = Equip.newBuilder(result).setItemExp(result.itemExp() + baseExpGain).build();
      int expNeeded = ExpTable.getEquipExpNeededForLevel(result.itemLevel());

      if (YamlConfig.config.server.USE_DEBUG_SHOW_INFO_EQPEXP) {
         LoggerUtil.printDebug(LoggerOriginator.ENGINE, "'" + ii.getName(result.id()) + "' -> EXP Gain: " + gain + " Mastery: " + masteryModifier + " Base gain: " + baseExpGain + " exp: " + result.itemExp() + " / " + expNeeded + ", Kills TNL: " + expNeeded / (baseExpGain / c.getPlayer().getExpRate()));
      }

      if (result.itemExp() >= expNeeded) {
         while (result.itemExp() >= expNeeded) {
            result = Equip.newBuilder(result).setItemExp(result.itemExp() - expNeeded).build();
            result = gainLevel(result, c);

            if (result.itemLevel() >= equipMaxLevel) {
               result = Equip.newBuilder(result).setItemExp(0.0f).build();
               break;
            }

            expNeeded = ExpTable.getEquipExpNeededForLevel(result.itemLevel());
         }
      }

      c.getPlayer().forceUpdateItem(result);
      //if(YamlConfig.config.server.USE_DEBUG) c.getPlayer().dropMessage("'" + ii.getName(this.getItemId()) + "': " + itemExp + " / " + expNeeded);
   }

   private Equip gainLevel(Equip equip, MapleClient c) {
      Equip result = equip;
      List<Pair<StatUpgrade, Integer>> stats = new LinkedList<>();

      if (result.elemental()) {
         List<Pair<String, Integer>> elementalStats = MapleItemInformationProvider.getInstance().getItemLevelUpStats(result.id(), result.itemLevel());

         for (Pair<String, Integer> p : elementalStats) {
            if (p.getRight() > 0) {
               stats.add(new Pair<>(StatUpgrade.valueOf(p.getLeft()), p.getRight()));
            }
         }
      }

      if (!stats.isEmpty()) {
         if (YamlConfig.config.server.USE_EQUIPMNT_LVLUP_SLOTS) {
            if (result.vicious() > 0) {
               getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
            }
            getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
         }
      } else {
         result = Equip.newBuilder(result).setUpgradeable(false).build();
         result = improveDefaultStats(result, stats);
         if (YamlConfig.config.server.USE_EQUIPMNT_LVLUP_SLOTS) {
            if (result.vicious() > 0) {
               getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
            }
            getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
         }

         if (result.upgradeable()) {
            while (stats.isEmpty()) {
               result = improveDefaultStats(result, stats);
               if (YamlConfig.config.server.USE_EQUIPMNT_LVLUP_SLOTS) {
                  if (result.vicious() > 0) {
                     getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
                  }
                  getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
               }
            }
         }
      }

      result = Equip.newBuilder(result).setItemLevel((byte) (equip.itemLevel() + 1)).build();

      String itemName = MapleItemInformationProvider.getInstance().getName(result.id());
      String showStr = "#e'" + itemName + "'#b is now #elevel #r" + result.itemLevel() + "#k#b!";

      Pair<Equip, Pair<StringBuilder, Pair<Boolean, Boolean>>> res = result.gainStats(stats);
      boolean gotSlot = res.getRight().getRight().getLeft();
      boolean gotVicious = res.getRight().getRight().getRight();
      result = res.getLeft();
      c.getPlayer().equipChanged();

      showLevelUpMessage(showStr, c);
      MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ITEM_LEVEL_UP_MESSAGE").with(itemName, result.itemLevel(), res.getLeft(), (gotVicious ? "+VICIOUS" : ""), (gotSlot ? "+UPGSLOT" : "")));

      PacketCreator.announce(c, new ShowSpecialEffect(15));
      c.getPlayer().getMap().broadcastMessage(c.getPlayer(), new ShowForeignEffect(c.getPlayer().getId(), 15));
      c.getPlayer().forceUpdateItem(result);
      return result;
   }

   private Equip getUnitStatUpgrade(Equip equip, List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name, int curStat, boolean isAttribute) {
      Equip result = Equip.newBuilder(equip).setUpgradeable(true).build();

      int maxUpgrade = randomizeStatUpgrade((int) (1 + (curStat / (getStatModifier(isAttribute) * (isNotWeaponAffinity(equip, name) ? 2.7 : 1)))));
      if (maxUpgrade == 0) {
         return result;
      }

      stats.add(new Pair<>(name, maxUpgrade));
      return result;
   }

   private Equip improveDefaultStats(Equip equip, List<Pair<StatUpgrade, Integer>> stats) {
      Equip result = equip;
      if (equip.dex() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incDEX, equip.dex(), true);
      }
      if (equip.str() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incSTR, equip.str(), true);
      }
      if (equip.intelligence() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incINT, equip.intelligence(), true);
      }
      if (equip.luk() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incLUK, equip.luk(), true);
      }
      if (equip.hp() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incMHP, equip.hp(), false);
      }
      if (equip.mp() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incMMP, equip.mp(), false);
      }
      if (equip.watk() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incPAD, equip.watk(), false);
      }
      if (equip.matk() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incMAD, equip.matk(), false);
      }
      if (equip.wdef() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incPDD, equip.wdef(), false);
      }
      if (equip.mdef() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incMDD, equip.mdef(), false);
      }
      if (equip.avoid() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incEVA, equip.avoid(), false);
      }
      if (equip.acc() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incACC, equip.acc(), false);
      }
      if (equip.speed() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incSpeed, equip.speed(), false);
      }
      if (equip.jump() > 0) {
         result = getUnitStatUpgrade(equip, stats, StatUpgrade.incJump, equip.jump(), false);
      }
      return result;
   }

   private boolean isNotWeaponAffinity(Equip equip, StatUpgrade name) {
      if (ItemConstants.isWeapon(equip.id())) {
         if (name.equals(StatUpgrade.incPAD)) {
            return !isPhysicalWeapon(equip.id());
         } else if (name.equals(StatUpgrade.incMAD)) {
            return isPhysicalWeapon(equip.id());
         }
      }

      return false;
   }

   private boolean isPhysicalWeapon(int itemId) {
      Equip eqp = MapleItemInformationProvider.getInstance().getEquipById(itemId);
      return eqp.watk() >= eqp.matk();
   }
}