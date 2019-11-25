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
package server;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleDisease;
import client.MapleJob;
import client.MapleMount;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import constants.skills.Aran;
import constants.skills.Beginner;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.Bowmaster;
import constants.skills.Buccaneer;
import constants.skills.ChiefBandit;
import constants.skills.Cleric;
import constants.skills.Corsair;
import constants.skills.Crossbowman;
import constants.skills.Crusader;
import constants.skills.DarkKnight;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.FPMage;
import constants.skills.FPWizard;
import constants.skills.GM;
import constants.skills.Hermit;
import constants.skills.Hero;
import constants.skills.Hunter;
import constants.skills.ILArchMage;
import constants.skills.ILMage;
import constants.skills.ILWizard;
import constants.skills.Legend;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Noblesse;
import constants.skills.Outlaw;
import constants.skills.Page;
import constants.skills.Paladin;
import constants.skills.Pirate;
import constants.skills.Priest;
import constants.skills.Ranger;
import constants.skills.Rogue;
import constants.skills.Shadower;
import constants.skills.Sniper;
import constants.skills.Spearman;
import constants.skills.SuperGM;
import constants.skills.ThunderBreaker;
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import net.server.Server;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.FieldLimit;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MaplePortal;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import server.partyquest.MapleCarnivalFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.processor.StatEffectProcessor;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.PacketInput;
import tools.packet.buff.GiveBuff;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.buff.GiveForeignPirateBuff;
import tools.packet.buff.GivePirateBuff;
import tools.packet.buff.ShowMonsterRiding;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.stat.EnableActions;

/**
 * @author Matze
 * @author Frz
 * @author Ronan
 */
public class MapleStatEffect {

   private short watk, matk, wdef, mdef, acc, avoid, speed, jump;
   private short hp, mp;
   private double hpR, mpR;
   private short mhpRRate, mmpRRate, mobSkill, mobSkillLevel;
   private byte mhpR, mmpR;
   private short mpCon, hpCon;
   private int duration, target, barrier, mob;
   private boolean overTime, repeatEffect;
   private int sourceid;
   private int moveTo;
   private int cp, nuffSkill;
   private List<MapleDisease> cureDebuffs;
   private boolean skill;
   private List<Pair<MapleBuffStat, Integer>> statups;
   private Map<MonsterStatus, Integer> monsterStatus;
   private int x, y, mobCount, moneyCon, cooldown, morphId = 0, ghost, fatigue, berserk, booster;
   private double prop;
   private int itemCon, itemConNo;
   private int damage, attackCount, fixdamage;
   private Point lt, rb;
   private byte bulletCount, bulletConsume;
   private byte mapProtection;
   private CardItemupStats cardStats;

   private boolean isEffectActive(int mapid, boolean partyHunting) {
      if (cardStats == null) {
         return true;
      }

      if (!cardStats.isInArea(mapid)) {
         return false;
      }

      return !cardStats.inParty() || partyHunting;
   }

   public boolean isActive(MapleCharacter applyto) {
      return isEffectActive(applyto.getMapId(), applyto.getPartyMembersOnSameMap().size() > 1);
   }

   public int getCardRate(int mapid, int itemid) {
      if (cardStats != null) {
         if (cardStats.itemCode() == Integer.MAX_VALUE) {
            return cardStats.probability();
         } else if (cardStats.itemCode() < 1000) {
            if (itemid / 10000 == cardStats.itemCode()) {
               return cardStats.probability();
            }
         } else {
            if (itemid == cardStats.itemCode()) {
               return cardStats.probability();
            }
         }
      }

      return 0;
   }

   /**
    * @param applyto
    * @param obj
    * @param attack  damage done by the skill
    */
   public void applyPassive(MapleCharacter applyto, MapleMapObject obj, int attack) {
      if (makeChanceResult()) {
         switch (sourceid) { // MP eater
            case FPWizard.MP_EATER:
            case ILWizard.MP_EATER:
            case Cleric.MP_EATER:
               if (obj == null || obj.type() != MapleMapObjectType.MONSTER) {
                  return;
               }
               MapleMonster mob = (MapleMonster) obj; // x is absorb percentage
               if (!mob.isBoss()) {
                  int absorbMp = Math.min((int) (mob.getMaxMp() * (getX() / 100.0)), mob.getMp());
                  if (absorbMp > 0) {
                     mob.setMp(mob.getMp() - absorbMp);
                     applyto.addMP(absorbMp);
                     PacketCreator.announce(applyto, new ShowOwnBuffEffect(sourceid, 1));
                     MasterBroadcaster.getInstance().sendToAllInMap(applyto.getMap(), new ShowBuffEffect(applyto.getId(), sourceid, 1, (byte) 3), false, applyto);
                  }
               }
               break;
         }
      }
   }

   public boolean applyEchoOfHero(MapleCharacter applyfrom) {
      Map<Integer, MapleCharacter> mapPlayers = applyfrom.getMap().getMapPlayers();
      mapPlayers.remove(applyfrom.getId());

      boolean hwResult = applyTo(applyfrom);
      // Echo of Hero not buffing players in the map detected thanks to Masterrulax
      mapPlayers.values().forEach(character -> applyTo(applyfrom, character, false, null, false, 1));
      return hwResult;
   }

   public boolean applyTo(MapleCharacter chr) {
      return applyTo(chr, chr, true, null, false, 1);
   }

   public boolean applyTo(MapleCharacter chr, boolean useMaxRange) {
      return applyTo(chr, chr, true, null, useMaxRange, 1);
   }

   public boolean applyTo(MapleCharacter chr, Point pos) {
      return applyTo(chr, chr, true, pos, false, 1);
   }

   // primary: the player caster of the buff
   private boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, boolean useMaxRange, int affectedPlayers) {
      if (skill && (sourceid == GM.HIDE || sourceid == SuperGM.HIDE)) {
         applyto.toggleHide(false);
         return true;
      }

      if (primary && isHeal()) {
         affectedPlayers = applyBuff(applyfrom, useMaxRange);
      }

      int hpchange = calcHPChange(applyfrom, primary, affectedPlayers);
      int mpchange = calcMPChange(applyfrom, primary);
      if (primary) {
         if (itemConNo != 0) {
            if (!applyto.getAbstractPlayerInteraction().hasItem(itemCon, itemConNo)) {
               PacketCreator.announce(applyto, new EnableActions());
               return false;
            }
            MapleInventoryManipulator.removeById(applyto.getClient(), ItemConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
         }
      } else {
         if (isResurrection()) {
            hpchange = applyto.getCurrentMaxHp();
            applyto.broadcastStance(applyto.isFacingLeft() ? 5 : 4);
         }
      }

      if (isDispel() && makeChanceResult()) {
         applyto.dispelDebuffs();
      } else if (isCureAllAbnormalStatus()) {
         applyto.dispelDebuff(MapleDisease.SEDUCE);
         applyto.dispelDebuff(MapleDisease.ZOMBIFY);
         applyto.dispelDebuffs();
      } else if (isComboReset()) {
         applyto.setCombo((short) 0);
      }
        /*if (applyfrom.getMp() < getMpCon()) {
         AutobanFactory.MPCON.addPoint(applyfrom.getAutobanManager(), "mpCon hack for skill:" + sourceid + "; Player MP: " + applyto.getMp() + " MP Needed: " + getMpCon());
         } */

      if (!applyto.applyHpMpChange(hpCon, hpchange, mpchange)) {
         PacketCreator.announce(applyto, new EnableActions());
         return false;
      }

      if (moveTo != -1) {
         if (moveTo != applyto.getMapId()) {
            MapleMap target;
            MaplePortal pt;

            if (moveTo == 999999999) {
               if (sourceid != 2030100) {
                  target = applyto.getMap().getReturnMap();
                  pt = target.getRandomPlayerSpawnpoint();
               } else {
                  if (!applyto.canRecoverLastBanish()) {
                     return false;
                  }

                  Pair<Integer, Integer> lastBanishInfo = applyto.getLastBanishData();
                  target = applyto.getWarpMap(lastBanishInfo.getLeft());
                  pt = target.getPortal(lastBanishInfo.getRight());
               }
            } else {
               target = applyto.getClient().getWorldServer().getChannel(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
               int targetid = target.getId() / 10000000;
               if (targetid != 60 && applyto.getMapId() / 10000000 != 61 && targetid != applyto.getMapId() / 10000000 && targetid != 21 && targetid != 20 && targetid != 12 && (applyto.getMapId() / 10000000 != 10 && applyto.getMapId() / 10000000 != 12)) {
                  return false;
               }

               pt = target.getRandomPlayerSpawnpoint();
            }

            applyto.changeMap(target, pt);
         } else {
            return false;
         }
      }
      if (isShadowClaw()) {
         MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
         use.lockInventory();
         try {
            Item projectile = null;
            for (int i = 1; i <= use.getSlotLimit(); i++) { // impose order...
               Item item = use.getItem((short) i);
               if (item != null) {
                  if (ItemConstants.isThrowingStar(item.id()) && item.quantity() >= 200) {
                     projectile = item;
                     break;
                  }
               }
            }
            if (projectile == null) {
               return false;
            } else {
               MapleInventoryManipulator.removeFromSlot(applyto.getClient(), MapleInventoryType.USE, projectile.position(), (short) 200, false, true);
            }
         } finally {
            use.unlockInventory();
         }
      }
      SummonMovementType summonMovementType = getSummonMovementType();
      if (overTime || isCygnusFA() || summonMovementType != null) {
         if (summonMovementType != null && pos != null) {
            if (summonMovementType.getValue() == SummonMovementType.STATIONARY.getValue()) {
               applyto.cancelBuffStats(MapleBuffStat.PUPPET);
            } else {
               applyto.cancelBuffStats(MapleBuffStat.SUMMON);
            }

            PacketCreator.announce(applyto, new EnableActions());
         }

         applyBuffEffect(applyfrom, applyto, primary);
      }

      if (primary) {
         if (overTime) {
            applyBuff(applyfrom, useMaxRange);
         }

         if (isMonsterBuff()) {
            applyMonsterBuff(applyfrom);
         }
      }

      if (this.getFatigue() != 0) {
         applyto.getMount().tiredness_$eq(applyto.getMount().tiredness() + this.getFatigue());
      }

      if (summonMovementType != null && pos != null) {
         final MapleSummon tosummon = new MapleSummon(applyfrom, sourceid, pos, summonMovementType);
         applyfrom.getMap().spawnSummon(tosummon);
         applyfrom.addSummon(sourceid, tosummon);
         tosummon.addHP(x);
         if (isBeholder()) {
            tosummon.addHP(1);
         }
      }
      if (isMagicDoor() && !FieldLimit.DOOR.check(applyto.getMap().getFieldLimit())) { // Magic Door
         int y = applyto.getFh();
         if (y == 0) {
            y = applyto.getMap().getGroundBelow(applyto.position()).y;    // thanks Lame for pointing out unusual cases of doors sending players on ground below
         }
         Point doorPosition = new Point(applyto.position().x, y);
         MapleDoor door = new MapleDoor(applyto, doorPosition);

         if (door.getOwnerId() >= 0) {
            applyto.applyPartyDoor(door, false);

            door.getTarget().spawnDoor(door.getAreaDoor());
            door.getTown().spawnDoor(door.getTownDoor());
         } else {
            MapleInventoryManipulator.addFromDrop(applyto.getClient(), new Item(4006000, (short) 0, (short) 1), false);

            if (door.getOwnerId() == -3) {
               MessageBroadcaster.getInstance().sendServerNotice(applyto, ServerNoticeType.PINK_TEXT, "Mystic Door cannot be cast far from a spawn point. Nearest one is at " + door.getDoorStatus().getRight() + "pts " + door.getDoorStatus().getLeft());
            } else if (door.getOwnerId() == -2) {
               MessageBroadcaster.getInstance().sendServerNotice(applyto, ServerNoticeType.PINK_TEXT, "Mystic Door cannot be cast on a slope, try elsewhere.");
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(applyto, ServerNoticeType.PINK_TEXT, "There are no door portals available for the town at this moment. Try again later.");
            }

            applyto.cancelBuffStats(MapleBuffStat.SOULARROW);  // cancel door buff
         }
      } else if (isMist()) {
         Rectangle bounds = calculateBoundingBox(sourceid == NightWalker.POISON_BOMB ? pos : applyfrom.position(), applyfrom.isFacingLeft());
         MapleMist mist = new MapleMist(bounds, applyfrom, this);
         applyfrom.getMap().spawnMist(mist, getDuration(), mist.isPoisonMist(), false, mist.isRecoveryMist());
      } else if (isTimeLeap()) {
         applyto.removeAllCooldownsExcept(Buccaneer.TIME_LEAP, true);
      } else if (cp != 0 && applyto.getMonsterCarnival() != null) {
         applyto.gainCP(cp);
      } else if (nuffSkill != 0 && applyto.getParty().isPresent() && applyto.getMap().isCPQMap()) { // by Drago-Dragohe4rt
         final MCSkill skill = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
         if (skill != null) {
            final MapleDisease dis = skill.getDisease();
            MapleParty opposition = applyfrom.getParty().orElseThrow().getEnemy();
            if (skill.targetsAll) {
               opposition.getPartyMembers().parallelStream()
                     .map(MaplePartyCharacter::getPlayer)
                     .flatMap(Optional::stream)
                     .filter(character -> character.getMap().isCPQMap())
                     .forEach(character -> {
                        if (dis == null) {
                           character.dispel();
                        } else {
                           character.giveDebuff(dis, MCSkill.getMobSkill(dis.getDisease(), skill.level));
                        }
                     });
            } else {
               int amount = opposition.getMembers().size();
               int randd = (int) Math.floor(Math.random() * amount);
               MapleCharacter chrApp = applyfrom.getMap().getCharacterById(opposition.getMemberByPos(randd).getId());
               if (chrApp != null && chrApp.getMap().isCPQMap()) {
                  if (dis == null) {
                     chrApp.dispel();
                  } else {
                     chrApp.giveDebuff(dis, MCSkill.getMobSkill(dis.getDisease(), skill.level));
                  }
               }
            }
         }
      } else if (cureDebuffs.size() > 0) { // by Drago-Dragohe4rt
         for (final MapleDisease debuff : cureDebuffs) {
            applyfrom.dispelDebuff(debuff);
         }
      } else if (mobSkill > 0 && mobSkillLevel > 0) {
         MobSkill ms = MobSkillFactory.getMobSkill(mobSkill, mobSkillLevel);
         MapleDisease dis = MapleDisease.getBySkill(mobSkill);

         if (target > 0) {
            for (MapleCharacter chr : applyto.getMap().getAllPlayers()) {
               if (chr.getId() != applyto.getId()) {
                  chr.giveDebuff(dis, ms);
               }
            }
         } else {
            applyto.giveDebuff(dis, ms);
         }
      }
      return true;
   }

   private int applyBuff(MapleCharacter applyfrom, boolean useMaxRange) {
      int affectedc = 1;

      if (isPartyBuff() && (applyfrom.getParty().isPresent() || isGmBuff())) {
         Rectangle bounds = (!useMaxRange) ? calculateBoundingBox(applyfrom.position(), applyfrom.isFacingLeft()) : new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE);
         List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.PLAYER));
         List<MapleCharacter> affectedp = new ArrayList<>(affecteds.size());
         for (MapleMapObject affectedmo : affecteds) {
            MapleCharacter affected = (MapleCharacter) affectedmo;
            if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
               if (!isResurrection()) {
                  if (affected.isAlive()) {
                     affectedp.add(affected);
                  }
               } else {
                  if (!affected.isAlive()) {
                     affectedp.add(affected);
                  }
               }
            }
         }

         affectedc += affectedp.size();   // used for heal
         for (MapleCharacter affected : affectedp) {
            applyTo(applyfrom, affected, false, null, useMaxRange, affectedc);
            PacketCreator.announce(affected, new ShowOwnBuffEffect(sourceid, 2));
            MasterBroadcaster.getInstance().sendToAllInMap(affected.getMap(), new ShowBuffEffect(affected.getId(), sourceid, 2, (byte) 3), false, affected);
         }
      }

      return affectedc;
   }

   private void applyMonsterBuff(MapleCharacter applyfrom) {
      Rectangle bounds = calculateBoundingBox(applyfrom.position(), applyfrom.isFacingLeft());
      List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.MONSTER));
      SkillFactory.getSkill(sourceid).ifPresent(skill_ -> {
         int i = 0;
         for (MapleMapObject mo : affected) {
            MapleMonster monster = (MapleMonster) mo;
            if (isDispel()) {
               monster.debuffMob(skill_.getId());
            } else if (isSeal() && monster.isBoss()) {
               //do nothing, seal shouldn't work on bosses
            } else {
               if (makeChanceResult()) {
                  monster.applyStatus(applyfrom, new MonsterStatusEffect(getMonsterStati(), skill_, null, false), isPoison(), getDuration());
                  if (isCrash()) {
                     monster.debuffMob(skill_.getId());
                  }
               }
            }
            i++;
            if (i >= mobCount) {
               break;
            }
         }
      });
   }

   private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
      Point mylt;
      Point myrb;
      if (facingLeft) {
         mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
         myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
      } else {
         myrb = new Point(-lt.x + posFrom.x, rb.y + posFrom.y);  // thanks Conrad, April for noticing a disturbance in AoE skill behavior after a hitched refactor here
         mylt = new Point(-rb.x + posFrom.x, lt.y + posFrom.y);
      }
      return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
   }

   public int getBuffLocalDuration() {
      return !YamlConfig.config.server.USE_BUFF_EVERLASTING ? duration : Integer.MAX_VALUE;
   }

   public void silentApplyBuff(MapleCharacter chr, long localStartTime) {
      int localDuration = getBuffLocalDuration();
      localDuration = alchemistModifyVal(chr, localDuration, false);
      //CancelEffectAction cancelAction = new CancelEffectAction(chr, this, starttime);
      //ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + localDuration) - Server.getInstance().getCurrentTime()));

      chr.registerEffect(this, localStartTime, localStartTime + localDuration, true);
      SummonMovementType summonMovementType = getSummonMovementType();
      if (summonMovementType != null) {
         final MapleSummon tosummon = new MapleSummon(chr, sourceid, chr.position(), summonMovementType);
         if (!tosummon.isStationary()) {
            chr.addSummon(sourceid, tosummon);
            tosummon.addHP(x);
         }
      }
      if (sourceid == Corsair.BATTLE_SHIP) {
         chr.announceBattleshipHp();
      }
   }

   public final void applyComboBuff(final MapleCharacter applyto, int combo) {
      final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ARAN_COMBO, combo));
      PacketCreator.announce(applyto, new GiveBuff(sourceid, 99999, stat));

      final long starttime = Server.getInstance().getCurrentTime();
//	final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
//	final ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + 99999) - Server.getInstance().getCurrentTime()));
      applyto.registerEffect(this, starttime, Long.MAX_VALUE, false);
   }

   public final void applyBeaconBuff(final MapleCharacter applyto, int objectid) { // thanks Thora & Hyun for reporting an issue with homing beacon autoflagging mobs when changing maps
      final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.HOMING_BEACON, objectid));
      PacketCreator.announce(applyto, new GiveBuff(1, sourceid, stat));

      final long starttime = Server.getInstance().getCurrentTime();
      applyto.registerEffect(this, starttime, Long.MAX_VALUE, false);
   }

   public void updateBuffEffect(MapleCharacter target, List<Pair<MapleBuffStat, Integer>> activeStats, long starttime) {
      int localDuration = getBuffLocalDuration();
      localDuration = alchemistModifyVal(target, localDuration, false);

      long leftDuration = (starttime + localDuration) - Server.getInstance().getCurrentTime();
      if (leftDuration > 0) {
         if (isDash() || isInfusion()) {
            PacketCreator.announce(target, new GivePirateBuff(activeStats, (skill ? sourceid : -sourceid), (int) leftDuration));
         } else {
            PacketCreator.announce(target, new GiveBuff((skill ? sourceid : -sourceid), (int) leftDuration, activeStats));
         }
      }
   }

   private void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary) {
      if (!isMonsterRiding() && !isCouponBuff() && !isMysticDoor() && !isHyperBody() && !isCombo()) {     // last mystic door already dispelled if it has been used before.
         applyto.cancelEffect(this, true, -1);
      }

      List<Pair<MapleBuffStat, Integer>> localstatups = statups;
      int localDuration = getBuffLocalDuration();
      int localsourceid = sourceid;
      int seconds = localDuration / 1000;
      MapleMount givemount = null;
      if (isMonsterRiding()) {
         int ridingMountId = 0;
         Item mount = applyfrom.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
         if (mount != null) {
            ridingMountId = mount.id();
         }

         if (sourceid == Corsair.BATTLE_SHIP) {
            ridingMountId = 1932000;
         } else if (sourceid == Beginner.SPACESHIP || sourceid == Noblesse.SPACESHIP) {
            ridingMountId = 1932000 + applyto.getSkillLevel(sourceid);
         } else if (sourceid == Beginner.YETI_MOUNT1 || sourceid == Noblesse.YETI_MOUNT1 || sourceid == Legend.YETI_MOUNT1) {
            ridingMountId = 1932003;
         } else if (sourceid == Beginner.YETI_MOUNT2 || sourceid == Noblesse.YETI_MOUNT2 || sourceid == Legend.YETI_MOUNT2) {
            ridingMountId = 1932004;
         } else if (sourceid == Beginner.WITCH_BROOMSTICK || sourceid == Noblesse.WITCH_BROOMSTICK || sourceid == Legend.WITCH_BROOMSTICK) {
            ridingMountId = 1932005;
         } else if (sourceid == Beginner.BALROG_MOUNT || sourceid == Noblesse.BALROG_MOUNT || sourceid == Legend.BALROG_MOUNT) {
            ridingMountId = 1932010;
         }

         // thanks inhyuk for noticing some skill mounts not acting properly for other players when changing maps
         givemount = applyto.mount(ridingMountId, sourceid);
         applyto.getClient().getWorldServer().registerMountHunger(applyto.getId(), applyto.isGM());

         localDuration = sourceid;
         localsourceid = ridingMountId;
         localstatups = Collections.singletonList(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
      } else if (isSkillMorph()) {
         for (int i = 0; i < localstatups.size(); i++) {
            if (localstatups.get(i).getLeft().equals(MapleBuffStat.MORPH)) {
               localstatups.set(i, new Pair<>(MapleBuffStat.MORPH, getMorph(applyto)));
               break;
            }
         }
      }
      if (primary) {
         localDuration = alchemistModifyVal(applyfrom, localDuration, false);
         MasterBroadcaster.getInstance().sendToAllInMap(applyto.getMap(), new ShowBuffEffect(applyto.getId(), sourceid, 1, (byte) 3), false, applyto);
      }
      if (localstatups.size() > 0) {
         PacketInput buff = null;
         PacketInput mbuff = null;
         if (this.isActive(applyto)) {
            buff = new GiveBuff((skill ? sourceid : -sourceid), localDuration, localstatups);
         }
         if (isDash()) {
            buff = new GivePirateBuff(statups, sourceid, seconds);
            mbuff = new GiveForeignPirateBuff(applyto.getId(), sourceid, seconds, localstatups);
         } else if (isInfusion()) {
            buff = new GivePirateBuff(localstatups, sourceid, seconds);
            mbuff = new GiveForeignPirateBuff(applyto.getId(), sourceid, seconds, localstatups);
         } else if (isDs()) {
            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.DARKSIGHT, 0));
            mbuff = new GiveForeignBuff(applyto.getId(), dsstat);
         } else if (isWw()) {
            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.WIND_WALK, 0));
            mbuff = new GiveForeignBuff(applyto.getId(), dsstat);
         } else if (isCombo()) {
            Integer comboCount = applyto.getBuffedValue(MapleBuffStat.COMBO);
            if (comboCount == null) {
               comboCount = 0;
            }

            List<Pair<MapleBuffStat, Integer>> cbstat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, comboCount));
            buff = new GiveBuff((skill ? sourceid : -sourceid), localDuration, cbstat);
            mbuff = new GiveForeignBuff(applyto.getId(), cbstat);
         } else if (isMonsterRiding()) {
            if (sourceid == Corsair.BATTLE_SHIP) {//hp
               if (applyto.getBattleshipHp() <= 0) {
                  applyto.resetBattleshipHp();
               }

               localstatups = statups;
            }
            buff = new GiveBuff(localsourceid, localDuration, localstatups);
            mbuff = new ShowMonsterRiding(applyto.getId(), givemount.itemId(), givemount.skillId());
            localDuration = duration;
         } else if (isShadowPartner()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SHADOWPARTNER, 0));
            mbuff = new GiveForeignBuff(applyto.getId(), stat);
         } else if (isSoulArrow()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SOULARROW, 0));
            mbuff = new GiveForeignBuff(applyto.getId(), stat);
         } else if (isEnrage()) {
            applyto.handleOrbconsume();
         } else if (isMorph()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MORPH, getMorph(applyto)));
            mbuff = new GiveForeignBuff(applyto.getId(), stat);
         } else if (isAriantShield()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.AURA, 1));
            mbuff = new GiveForeignBuff(applyto.getId(), stat);
         }

         if (buff != null) {
            //Thanks flav for such a simple release! :)
            //Thanks Conrad, Atoot for noticing summons not using buff icon
            PacketCreator.announce(applyto, buff);
         }

         long starttime = Server.getInstance().getCurrentTime();
         //CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
         //ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, localDuration);
         applyto.registerEffect(this, starttime, starttime + localDuration, false);

         if (mbuff != null) {
            MasterBroadcaster.getInstance().sendToAllInMap(applyto.getMap(), mbuff, false, applyto);
         }
         if (sourceid == Corsair.BATTLE_SHIP) {
            applyto.announceBattleshipHp();
         }
      }
   }

   private int calcHPChange(MapleCharacter applyfrom, boolean primary, int affectedPlayers) {
      int hpchange = 0;
      if (hp != 0) {
         if (!skill) {
            if (primary) {
               hpchange += alchemistModifyVal(applyfrom, hp, true);
            } else {
               hpchange += hp;
            }
            if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
               hpchange /= 2;
            }
         } else { // assumption: this is heal
            float hpHeal = (applyfrom.getCurrentMaxHp() * (float) hp / (100.0f * affectedPlayers));
            hpchange += hpHeal;
            if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
               hpchange = -hpchange;
               hpCon = 0;
            }
         }
      }
      if (hpR != 0) {
         hpchange += (int) (applyfrom.getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(MapleDisease.ZOMBIFY) ? 2 : 1);
      }
      if (primary) {
         if (hpCon != 0) {
            hpchange -= hpCon;
         }
      }
      if (isChakra()) {
         hpchange += makeHealHP(getY() / 100.0, applyfrom.getTotalLuk(), 2.3, 3.5);
      } else if (sourceid == SuperGM.HEAL_PLUS_DISPEL) {
         hpchange += applyfrom.getCurrentMaxHp();
      }

      return hpchange;
   }

   private int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
      return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
   }

   private int calcMPChange(MapleCharacter applyfrom, boolean primary) {
      int mpchange = 0;
      if (mp != 0) {
         if (primary) {
            mpchange += alchemistModifyVal(applyfrom, mp, true);
         } else {
            mpchange += mp;
         }
      }
      if (mpR != 0) {
         mpchange += (int) (applyfrom.getCurrentMaxMp() * mpR);
      }
      if (primary) {
         if (mpCon != 0) {
            double mod = 1.0;
            boolean isAFpMage = applyfrom.getJob().isA(MapleJob.FP_MAGE);
            boolean isCygnus = applyfrom.getJob().isA(MapleJob.BLAZEWIZARD2);
            boolean isEvan = applyfrom.getJob().isA(MapleJob.EVAN7);
            if (isAFpMage || isCygnus || isEvan || applyfrom.getJob().isA(MapleJob.IL_MAGE)) {
               int skillId = isAFpMage ? FPMage.ELEMENT_AMPLIFICATION : (isCygnus ? BlazeWizard.ELEMENT_AMPLIFICATION : (isEvan ? Evan.MAGIC_AMPLIFICATION : ILMage.ELEMENT_AMPLIFICATION));
               mod = SkillFactory.applyIfHasSkill(applyfrom, skillId, (skill, skillLevel) -> skill.getEffect(skillLevel).getX() / 100.0, 0.0);
            }
            mpchange -= mpCon * mod;
            if (applyfrom.getBuffedValue(MapleBuffStat.INFINITY) != null) {
               mpchange = 0;
            } else if (applyfrom.getBuffedValue(MapleBuffStat.CONCENTRATE) != null) {
               mpchange -= (int) (mpchange * (applyfrom.getBuffedValue(MapleBuffStat.CONCENTRATE).doubleValue() / 100));
            }
         }
      }
      if (sourceid == SuperGM.HEAL_PLUS_DISPEL) {
         mpchange += applyfrom.getCurrentMaxMp();
      }

      return mpchange;
   }

   private int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
      if (!skill && (chr.getJob().isA(MapleJob.HERMIT) || chr.getJob().isA(MapleJob.NIGHTWALKER3))) {
         MapleStatEffect alchemistEffect = StatEffectProcessor.getInstance().getAlchemistEffect(chr);
         if (alchemistEffect != null) {
            return (int) (val * ((withX ? alchemistEffect.getX() : alchemistEffect.getY()) / 100.0));
         }
      }
      return val;
   }

   private boolean isGmBuff() {
      switch (sourceid) {
         case Beginner.ECHO_OF_HERO:
         case Noblesse.ECHO_OF_HERO:
         case Legend.ECHO_OF_HERO:
         case Evan.ECHO_OF_HERO:
         case SuperGM.HEAL_PLUS_DISPEL:
         case SuperGM.HASTE:
         case SuperGM.HOLY_SYMBOL:
         case SuperGM.BLESS:
         case SuperGM.RESURRECTION:
         case SuperGM.HYPER_BODY:
            return true;
         default:
            return false;
      }
   }

   private boolean isMonsterBuff() {
      if (!skill) {
         return false;
      }
      switch (sourceid) {
         case Page.THREATEN:
         case FPWizard.SLOW:
         case ILWizard.SLOW:
         case FPMage.SEAL:
         case ILMage.SEAL:
         case Priest.DOOM:
         case Hermit.SHADOW_WEB:
         case NightLord.NINJA_AMBUSH:
         case Shadower.NINJA_AMBUSH:
         case BlazeWizard.SLOW:
         case BlazeWizard.SEAL:
         case NightWalker.SHADOW_WEB:
         case Crusader.ARMOR_CRASH:
         case DragonKnight.POWER_CRASH:
         case WhiteKnight.MAGIC_CRASH:
         case Priest.DISPEL:
         case SuperGM.HEAL_PLUS_DISPEL:
            return true;
      }
      return false;
   }

   private boolean isPartyBuff() {
      if (lt == null || rb == null) {
         return false;
      }
      // wk charges have lt and rb set but are neither player nor monster buffs
      return (sourceid < 1211003 || sourceid > 1211008) && sourceid != Paladin.SWORD_HOLY_CHARGE && sourceid != Paladin.BW_HOLY_CHARGE && sourceid != DawnWarrior.SOUL_CHARGE;
   }

   private boolean isHeal() {
      return sourceid == Cleric.HEAL || sourceid == SuperGM.HEAL_PLUS_DISPEL;
   }

   private boolean isResurrection() {
      return sourceid == Bishop.RESURRECTION || sourceid == GM.RESURRECTION || sourceid == SuperGM.RESURRECTION;
   }

   private boolean isTimeLeap() {
      return sourceid == Buccaneer.TIME_LEAP;
   }

   public boolean isDragonBlood() {
      return skill && sourceid == DragonKnight.DRAGON_BLOOD;
   }

   public boolean isBerserk() {
      return skill && sourceid == DarkKnight.BERSERK;
   }

   public boolean isRecovery() {
      return sourceid == Beginner.RECOVERY || sourceid == Noblesse.RECOVERY || sourceid == Legend.RECOVERY || sourceid == Evan.RECOVERY;
   }

   public boolean isMapChair() {
      return sourceid == Beginner.MAP_CHAIR || sourceid == Noblesse.MAP_CHAIR || sourceid == Legend.MAP_CHAIR;
   }

   public boolean isDojoBuff() {
      return sourceid >= 2022359 && sourceid <= 2022421;
   }

   private boolean isDs() {
      return skill && (sourceid == Rogue.DARK_SIGHT || sourceid == NightWalker.DARK_SIGHT);
   }

   private boolean isWw() {
      return skill && (sourceid == WindArcher.WIND_WALK);
   }

   private boolean isCombo() {
      return skill && (sourceid == Crusader.COMBO || sourceid == DawnWarrior.COMBO);
   }

   private boolean isEnrage() {
      return skill && sourceid == Hero.ENRAGE;
   }

   public boolean isBeholder() {
      return skill && sourceid == DarkKnight.BEHOLDER;
   }

   private boolean isShadowPartner() {
      return skill && (sourceid == Hermit.SHADOW_PARTNER || sourceid == NightWalker.SHADOW_PARTNER);
   }

   private boolean isChakra() {
      return skill && sourceid == ChiefBandit.CHAKRA;
   }

   private boolean isCouponBuff() {
      return StatEffectProcessor.getInstance().isRateCoupon(sourceid);
   }

   private boolean isAriantShield() {
      int itemid = sourceid;
      return StatEffectProcessor.getInstance().isAriantShield(itemid);
   }

   private boolean isMysticDoor() {
      return skill && sourceid == Priest.MYSTIC_DOOR;
   }

   public boolean isMonsterRiding() {
      return skill && (sourceid % 10000000 == 1004 || sourceid == Corsair.BATTLE_SHIP || sourceid == Beginner.SPACESHIP || sourceid == Noblesse.SPACESHIP
            || sourceid == Beginner.YETI_MOUNT1 || sourceid == Beginner.YETI_MOUNT2 || sourceid == Beginner.WITCH_BROOMSTICK || sourceid == Beginner.BALROG_MOUNT
            || sourceid == Noblesse.YETI_MOUNT1 || sourceid == Noblesse.YETI_MOUNT2 || sourceid == Noblesse.WITCH_BROOMSTICK || sourceid == Noblesse.BALROG_MOUNT
            || sourceid == Legend.YETI_MOUNT1 || sourceid == Legend.YETI_MOUNT2 || sourceid == Legend.WITCH_BROOMSTICK || sourceid == Legend.BALROG_MOUNT);
   }

   public boolean isMagicDoor() {
      return skill && sourceid == Priest.MYSTIC_DOOR;
   }

   public boolean isPoison() {
      return skill && (sourceid == FPMage.POISON_MIST || sourceid == FPWizard.POISON_BREATH || sourceid == FPMage.ELEMENT_COMPOSITION || sourceid == NightWalker.POISON_BOMB || sourceid == BlazeWizard.FLAME_GEAR);
   }

   public boolean isMorph() {
      return morphId > 0;
   }

   public boolean isMorphWithoutAttack() {
      return morphId > 0 && morphId < 100; // Every morph item I have found has been under 100, pirate skill transforms start at 1000.
   }

   private boolean isMist() {
      return skill && (sourceid == FPMage.POISON_MIST || sourceid == Shadower.SMOKE_SCREEN || sourceid == BlazeWizard.FLAME_GEAR || sourceid == NightWalker.POISON_BOMB || sourceid == Evan.RECOVERY_AURA);
   }

   private boolean isSoulArrow() {
      return skill && (sourceid == Hunter.SOUL_ARROW || sourceid == Crossbowman.SOUL_ARROW || sourceid == WindArcher.SOUL_ARROW);
   }

   private boolean isShadowClaw() {
      return skill && sourceid == NightLord.SHADOW_STARS;
   }

   private boolean isCrash() {
      return skill && (sourceid == DragonKnight.POWER_CRASH || sourceid == Crusader.ARMOR_CRASH || sourceid == WhiteKnight.MAGIC_CRASH);
   }

   private boolean isSeal() {
      return skill && (sourceid == ILMage.SEAL || sourceid == FPMage.SEAL || sourceid == BlazeWizard.SEAL);
   }

   private boolean isDispel() {
      return skill && (sourceid == Priest.DISPEL || sourceid == SuperGM.HEAL_PLUS_DISPEL);
   }

   private boolean isCureAllAbnormalStatus() {
      if (skill) {
         return StatEffectProcessor.getInstance().isHerosWill(sourceid);
      } else {
         return sourceid == 2022544;
      }

   }

   private boolean isDash() {
      return skill && (sourceid == Pirate.DASH || sourceid == ThunderBreaker.DASH || sourceid == Beginner.SPACE_DASH || sourceid == Noblesse.SPACE_DASH);
   }

   private boolean isSkillMorph() {
      return skill && (sourceid == Buccaneer.SUPER_TRANSFORMATION || sourceid == Marauder.TRANSFORMATION || sourceid == WindArcher.EAGLE_EYE || sourceid == ThunderBreaker.TRANSFORMATION);
   }

   private boolean isInfusion() {
      return skill && (sourceid == Buccaneer.SPEED_INFUSION || sourceid == Corsair.SPEED_INFUSION || sourceid == ThunderBreaker.SPEED_INFUSION);
   }

   private boolean isCygnusFA() {
      return skill && (sourceid == DawnWarrior.FINAL_ATTACK || sourceid == WindArcher.FINAL_ATTACK);
   }

   private boolean isHyperBody() {
      return skill && (sourceid == Spearman.HYPER_BODY || sourceid == GM.HYPER_BODY || sourceid == SuperGM.HYPER_BODY);
   }

   private boolean isComboReset() {
      return sourceid == Aran.COMBO_BARRIER || sourceid == Aran.COMBO_DRAIN;
   }

   private int getFatigue() {
      return fatigue;
   }

   public int getMorph() {
      return morphId;
   }

   private int getMorph(MapleCharacter chr) {
      if (morphId == 1000 || morphId == 1001 || morphId == 1003) { // morph skill
         return chr.getGender() == 0 ? morphId : morphId + 100;
      }
      return morphId;
   }

   public SummonMovementType getSummonMovementType() {
      if (!skill) {
         return null;
      }
      switch (sourceid) {
         case Ranger.PUPPET:
         case Sniper.PUPPET:
         case WindArcher.PUPPET:
         case Outlaw.OCTOPUS:
         case Corsair.WRATH_OF_THE_OCTOPI:
            return SummonMovementType.STATIONARY;
         case Ranger.SILVER_HAWK:
         case Sniper.GOLDEN_EAGLE:
         case Priest.SUMMON_DRAGON:
         case Marksman.FROST_PREY:
         case Bowmaster.PHOENIX:
         case Outlaw.GAVIOTA:
            return SummonMovementType.CIRCLE_FOLLOW;
         case DarkKnight.BEHOLDER:
         case FPArchMage.ELQUINES:
         case ILArchMage.IFRIT:
         case Bishop.BAHAMUT:
         case DawnWarrior.SOUL:
         case BlazeWizard.FLAME:
         case BlazeWizard.IFRIT:
         case WindArcher.STORM:
         case NightWalker.DARKNESS:
         case ThunderBreaker.LIGHTNING:
            return SummonMovementType.FOLLOW;
      }
      return null;
   }

   public boolean isSkill() {
      return skill;
   }

   public int getSourceId() {
      return sourceid;
   }

   public int getBuffSourceId() {
      return skill ? sourceid : -sourceid;
   }

   public boolean makeChanceResult() {
      return prop == 1.0 || Math.random() < prop;
   }

   public short getHp() {
      return hp;
   }

   public short getMp() {
      return mp;
   }

   public double getHpRate() {
      return hpR;
   }

   public double getMpRate() {
      return mpR;
   }

   public byte getHpR() {
      return mhpR;
   }

   public byte getMpR() {
      return mmpR;
   }

   public short getHpRRate() {
      return mhpRRate;
   }

   public short getMpRRate() {
      return mmpRRate;
   }

   public short getHpCon() {
      return hpCon;
   }

   public short getMpCon() {
      return mpCon;
   }

   public short getMatk() {
      return matk;
   }

   public short getWatk() {
      return watk;
   }

   public int getDuration() {
      return duration;
   }

   public List<Pair<MapleBuffStat, Integer>> getStatups() {
      return statups;
   }

   public boolean sameSource(MapleStatEffect effect) {
      return this.sourceid == effect.sourceid && this.skill == effect.skill;
   }

   public int getX() {
      return x;
   }

   public int getY() {
      return y;
   }

   public int getDamage() {
      return damage;
   }

   public int getAttackCount() {
      return attackCount;
   }

   public int getMobCount() {
      return mobCount;
   }

   public int getFixDamage() {
      return fixdamage;
   }

   public byte getBulletCount() {
      return bulletCount;
   }

   public byte getBulletConsume() {
      return bulletConsume;
   }

   public int getMoneyCon() {
      return moneyCon;
   }

   public int getCooldown() {
      return cooldown;
   }

   public Map<MonsterStatus, Integer> getMonsterStati() {
      return monsterStatus;
   }

   public short getWdef() {
      return wdef;
   }

   public short getMdef() {
      return mdef;
   }

   public short getAcc() {
      return acc;
   }

   public short getAvoid() {
      return avoid;
   }

   public short getSpeed() {
      return speed;
   }

   public short getJump() {
      return jump;
   }

   public short getMhpRRate() {
      return mhpRRate;
   }

   public short getMmpRRate() {
      return mmpRRate;
   }

   public short getMobSkill() {
      return mobSkill;
   }

   public short getMobSkillLevel() {
      return mobSkillLevel;
   }

   public byte getMhpR() {
      return mhpR;
   }

   public byte getMmpR() {
      return mmpR;
   }

   public int getTarget() {
      return target;
   }

   public int getBarrier() {
      return barrier;
   }

   public int getMob() {
      return mob;
   }

   public boolean isOverTime() {
      return overTime;
   }

   public boolean isRepeatEffect() {
      return repeatEffect;
   }

   public int getSourceid() {
      return sourceid;
   }

   public int getMoveTo() {
      return moveTo;
   }

   public int getCp() {
      return cp;
   }

   public int getNuffSkill() {
      return nuffSkill;
   }

   public List<MapleDisease> getCureDebuffs() {
      return cureDebuffs;
   }

   public Map<MonsterStatus, Integer> getMonsterStatus() {
      return monsterStatus;
   }

   public int getMorphId() {
      return morphId;
   }

   public int getGhost() {
      return ghost;
   }

   public int getBerserk() {
      return berserk;
   }

   public int getBooster() {
      return booster;
   }

   public double getProp() {
      return prop;
   }

   public int getItemCon() {
      return itemCon;
   }

   public int getItemConNo() {
      return itemConNo;
   }

   public int getFixdamage() {
      return fixdamage;
   }

   public Point getLt() {
      return lt;
   }

   public Point getRb() {
      return rb;
   }

   public byte getMapProtection() {
      return mapProtection;
   }

   public CardItemupStats getCardStats() {
      return cardStats;
   }

   public void setWatk(short watk) {
      this.watk = watk;
   }

   public void setMatk(short matk) {
      this.matk = matk;
   }

   public void setWdef(short wdef) {
      this.wdef = wdef;
   }

   public void setMdef(short mdef) {
      this.mdef = mdef;
   }

   public void setAcc(short acc) {
      this.acc = acc;
   }

   public void setAvoid(short avoid) {
      this.avoid = avoid;
   }

   public void setSpeed(short speed) {
      this.speed = speed;
   }

   public void setJump(short jump) {
      this.jump = jump;
   }

   public void setHp(short hp) {
      this.hp = hp;
   }

   public void setMp(short mp) {
      this.mp = mp;
   }

   public void setHpR(double hpR) {
      this.hpR = hpR;
   }

   public void setMpR(double mpR) {
      this.mpR = mpR;
   }

   public void setMhpRRate(short mhpRRate) {
      this.mhpRRate = mhpRRate;
   }

   public void setMmpRRate(short mmpRRate) {
      this.mmpRRate = mmpRRate;
   }

   public void setMobSkill(short mobSkill) {
      this.mobSkill = mobSkill;
   }

   public void setMobSkillLevel(short mobSkillLevel) {
      this.mobSkillLevel = mobSkillLevel;
   }

   public void setMhpR(byte mhpR) {
      this.mhpR = mhpR;
   }

   public void setMmpR(byte mmpR) {
      this.mmpR = mmpR;
   }

   public void setMpCon(short mpCon) {
      this.mpCon = mpCon;
   }

   public void setHpCon(short hpCon) {
      this.hpCon = hpCon;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public void setTarget(int target) {
      this.target = target;
   }

   public void setBarrier(int barrier) {
      this.barrier = barrier;
   }

   public void setMob(int mob) {
      this.mob = mob;
   }

   public void setOverTime(boolean overTime) {
      this.overTime = overTime;
   }

   public void setRepeatEffect(boolean repeatEffect) {
      this.repeatEffect = repeatEffect;
   }

   public void setSourceid(int sourceid) {
      this.sourceid = sourceid;
   }

   public void setMoveTo(int moveTo) {
      this.moveTo = moveTo;
   }

   public void setCp(int cp) {
      this.cp = cp;
   }

   public void setNuffSkill(int nuffSkill) {
      this.nuffSkill = nuffSkill;
   }

   public void setCureDebuffs(List<MapleDisease> cureDebuffs) {
      this.cureDebuffs = cureDebuffs;
   }

   public void setSkill(boolean skill) {
      this.skill = skill;
   }

   public void setStatups(List<Pair<MapleBuffStat, Integer>> statups) {
      this.statups = statups;
   }

   public void setMonsterStatus(Map<MonsterStatus, Integer> monsterStatus) {
      this.monsterStatus = monsterStatus;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public void setMobCount(int mobCount) {
      this.mobCount = mobCount;
   }

   public void setMoneyCon(int moneyCon) {
      this.moneyCon = moneyCon;
   }

   public void setCooldown(int cooldown) {
      this.cooldown = cooldown;
   }

   public void setMorphId(int morphId) {
      this.morphId = morphId;
   }

   public void setGhost(int ghost) {
      this.ghost = ghost;
   }

   public void setFatigue(int fatigue) {
      this.fatigue = fatigue;
   }

   public void setBerserk(int berserk) {
      this.berserk = berserk;
   }

   public void setBooster(int booster) {
      this.booster = booster;
   }

   public void setProp(double prop) {
      this.prop = prop;
   }

   public void setItemCon(int itemCon) {
      this.itemCon = itemCon;
   }

   public void setItemConNo(int itemConNo) {
      this.itemConNo = itemConNo;
   }

   public void setDamage(int damage) {
      this.damage = damage;
   }

   public void setAttackCount(int attackCount) {
      this.attackCount = attackCount;
   }

   public void setFixdamage(int fixdamage) {
      this.fixdamage = fixdamage;
   }

   public void setLt(Point lt) {
      this.lt = lt;
   }

   public void setRb(Point rb) {
      this.rb = rb;
   }

   public void setBulletCount(byte bulletCount) {
      this.bulletCount = bulletCount;
   }

   public void setBulletConsume(byte bulletConsume) {
      this.bulletConsume = bulletConsume;
   }

   public void setMapProtection(byte mapProtection) {
      this.mapProtection = mapProtection;
   }

   public void setCardStats(CardItemupStats cardStats) {
      this.cardStats = cardStats;
   }
}
