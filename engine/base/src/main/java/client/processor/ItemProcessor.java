package client.processor;

import java.util.LinkedList;
import java.util.List;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.StatUpgrade;
import client.inventory.manipulator.MapleKarmaManipulator;
import constants.ExpTable;
import constants.ItemConstants;
import constants.ServerConstants;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowForeignEffect;

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

   public void setFlag(Item item, short b) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii.isAccountRestricted(item.id())) {
         b |= ItemConstants.ACCOUNT_SHARING; // thanks Shinigami15 for noticing ACCOUNT_SHARING flag not being applied properly to items server-side
      }
      item.flag_$eq(b);
   }

   public void setMergeFlag(Item item) {
      short flag = item.flag();
      flag |= ItemConstants.MERGE_UNTRADEABLE;
      flag |= ItemConstants.UNTRADEABLE;
      setFlag(item, flag);
   }

   public boolean hasMergeFlag(Item item) {
      return (item.flag() & ItemConstants.MERGE_UNTRADEABLE) == ItemConstants.MERGE_UNTRADEABLE;
   }

   public boolean isUntradeable(Item item) {
      return ((item.flag() & ItemConstants.UNTRADEABLE) == ItemConstants.UNTRADEABLE) || (MapleItemInformationProvider.getInstance().isDropRestricted(item.id()) && !MapleKarmaManipulator.hasKarmaFlag(item));
   }

   public int getStatModifier(boolean isAttribute) {
      // each set of stat points grants a chance for a bonus stat point upgrade at equip level up.

      if (ServerConstants.USE_EQUIPMNT_LVLUP_POWER) {
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
      int limit = Math.min(top, ServerConstants.MAX_EQUIPMNT_LVLUP_STAT_UP);

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
      // Conversion factor between mob exp and equip exp gain. Through many calculations, the expected for equipment levelup
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

   public void showLevelupMessage(String msg, MapleClient c) {
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

      return equip.itemLevel() >= ServerConstants.USE_EQUIPMNT_LVLUP;
   }


   public synchronized void gainItemExp(Equip equip, MapleClient c, int gain) {  // Ronan's Equip Exp gain method
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (!ii.isUpgradeable(equip.id())) {
         return;
      }

      int equipMaxLevel = Math.min(30, Math.max(ii.getEquipLevel(equip.id(), true), ServerConstants.USE_EQUIPMNT_LVLUP));
      if (equip.itemLevel() >= equipMaxLevel) {
         return;
      }

      int reqLevel = ii.getEquipLevelReq(equip.id());

      float masteryModifier = (float) (ServerConstants.EQUIP_EXP_RATE * ExpTable.getExpNeededForLevel(1)) / (float) normalizedMasteryExp(reqLevel);
      float elementModifier = (equip.elemental()) ? 0.85f : 0.6f;

      float baseExpGain = gain * elementModifier * masteryModifier;

      equip.itemExp_$eq(equip.itemExp() + baseExpGain);
      int expNeeded = ExpTable.getEquipExpNeededForLevel(equip.itemLevel());

      if (ServerConstants.USE_DEBUG_SHOW_INFO_EQPEXP) {
         System.out.println("'" + ii.getName(equip.id()) + "' -> EXP Gain: " + gain + " Mastery: " + masteryModifier + " Base gain: " + baseExpGain + " exp: " + equip.itemExp() + " / " + expNeeded + ", Kills TNL: " + expNeeded / (baseExpGain / c.getPlayer().getExpRate()));
      }

      if (equip.itemExp() >= expNeeded) {
         while (equip.itemExp() >= expNeeded) {
            equip.itemExp_$eq(equip.itemExp() - expNeeded);
            gainLevel(equip, c);

            if (equip.itemLevel() >= equipMaxLevel) {
               equip.itemExp_$eq(0.0f);
               break;
            }

            expNeeded = ExpTable.getEquipExpNeededForLevel(equip.itemLevel());
         }
      }

      c.getPlayer().forceUpdateItem(equip);
      //if(ServerConstants.USE_DEBUG) c.getPlayer().dropMessage("'" + ii.getName(this.getItemId()) + "': " + itemExp + " / " + expNeeded);
   }

   private void gainLevel(Equip equip, MapleClient c) {
      List<Pair<StatUpgrade, Integer>> stats = new LinkedList<>();

      if (equip.elemental()) {
         List<Pair<String, Integer>> elementalStats = MapleItemInformationProvider.getInstance().getItemLevelupStats(equip.id(), equip.itemLevel());

         for (Pair<String, Integer> p : elementalStats) {
            if (p.getRight() > 0) {
               stats.add(new Pair<>(StatUpgrade.valueOf(p.getLeft()), p.getRight()));
            }
         }
      }

      if (!stats.isEmpty()) {
         if (ServerConstants.USE_EQUIPMNT_LVLUP_SLOTS) {
            if (equip.vicious() > 0) {
               getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
            }
            getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
         }
      } else {
         equip.upgradeable_$eq(false);

         improveDefaultStats(equip, stats);
         if (ServerConstants.USE_EQUIPMNT_LVLUP_SLOTS) {
            if (equip.vicious() > 0) {
               getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
            }
            getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
         }

         if (equip.upgradeable()) {
            while (stats.isEmpty()) {
               improveDefaultStats(equip, stats);
               if (ServerConstants.USE_EQUIPMNT_LVLUP_SLOTS) {
                  if (equip.vicious() > 0) {
                     getUnitSlotUpgrade(stats, StatUpgrade.incVicious);
                  }
                  getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
               }
            }
         }
      }

      equip.itemLevel_$eq((byte) (equip.itemLevel() + 1));

      String lvupStr = "'" + MapleItemInformationProvider.getInstance().getName(equip.id()) + "' is now level " + equip.itemLevel() + "! ";
      String showStr = "#e'" + MapleItemInformationProvider.getInstance().getName(equip.id()) + "'#b is now #elevel #r" + equip.itemLevel() + "#k#b!";

      Pair<String, Pair<Boolean, Boolean>> res = equip.gainStats(stats);
      lvupStr += res.getLeft();
      boolean gotSlot = res.getRight().getLeft();
      boolean gotVicious = res.getRight().getRight();

      if (gotVicious) {
         //c.getPlayer().dropMessage(6, "A new Vicious Hammer opportunity has been found on the '" + MapleItemInformationProvider.getInstance().getName(getItemId()) + "'!");
         lvupStr += "+VICIOUS ";
      }
      if (gotSlot) {
         //c.getPlayer().dropMessage(6, "A new upgrade slot has been found on the '" + MapleItemInformationProvider.getInstance().getName(getItemId()) + "'!");
         lvupStr += "+UPGSLOT ";
      }

      c.getPlayer().equipChanged();

      showLevelupMessage(showStr, c); // thanks to Polaris dev team !
      MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.LIGHT_BLUE, lvupStr);

      c.announce(MaplePacketCreator.showEquipmentLevelUp());
      c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.create(new ShowForeignEffect(c.getPlayer().getId(), 15)));
      c.getPlayer().forceUpdateItem(equip);
   }

   private void getUnitStatUpgrade(Equip equip, List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name, int curStat, boolean isAttribute) {
      equip.upgradeable_$eq(true);

      int maxUpgrade = randomizeStatUpgrade((int) (1 + (curStat / (getStatModifier(isAttribute) * (isNotWeaponAffinity(equip, name) ? 2.7 : 1)))));
      if (maxUpgrade == 0) {
         return;
      }

      stats.add(new Pair<>(name, maxUpgrade));
   }

   private void improveDefaultStats(Equip equip, List<Pair<StatUpgrade, Integer>> stats) {
      if (equip.dex() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incDEX, equip.dex(), true);
      }
      if (equip.str() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incSTR, equip.str(), true);
      }
      if (equip._int() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incINT, equip._int(), true);
      }
      if (equip.luk() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incLUK, equip.luk(), true);
      }
      if (equip.hp() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incMHP, equip.hp(), false);
      }
      if (equip.mp() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incMMP, equip.mp(), false);
      }
      if (equip.watk() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incPAD, equip.watk(), false);
      }
      if (equip.matk() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incMAD, equip.matk(), false);
      }
      if (equip.wdef() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incPDD, equip.wdef(), false);
      }
      if (equip.mdef() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incMDD, equip.mdef(), false);
      }
      if (equip.avoid() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incEVA, equip.avoid(), false);
      }
      if (equip.acc() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incACC, equip.acc(), false);
      }
      if (equip.speed() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incSpeed, equip.speed(), false);
      }
      if (equip.jump() > 0) {
         getUnitStatUpgrade(equip, stats, StatUpgrade.incJump, equip.jump(), false);
      }
   }

   private boolean isNotWeaponAffinity(Equip equip, StatUpgrade name) {
      // WATK/MATK expected gains lessens outside of weapon affinity (physical/magic): Vcoc's idea

      if (ItemConstants.isWeapon(equip.id())) {
         if (name.equals(StatUpgrade.incPAD)) {
            return !isPhysicalWeapon(equip.id());
         } else if (name.equals(StatUpgrade.incMAD)) {
            return isPhysicalWeapon(equip.id());
         }
      }

      return false;
   }

   private boolean isPhysicalWeapon(int itemid) {
      Equip eqp = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
      return eqp.watk() >= eqp.matk();
   }
}