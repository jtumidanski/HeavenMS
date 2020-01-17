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
import client.MapleAbnormalStatus;
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
import constants.skills.BowMaster;
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
import constants.skills.FirePoisonArchMage;
import constants.skills.FirePoisonMagician;
import constants.skills.FPWizard;
import constants.skills.GM;
import constants.skills.Hermit;
import constants.skills.Hero;
import constants.skills.Hunter;
import constants.skills.IceLighteningArchMagician;
import constants.skills.IceLighteningMagician;
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
import tools.I18nMessage;
import tools.packet.PacketInput;
import tools.packet.buff.GiveBuff;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.buff.GiveForeignPirateBuff;
import tools.packet.buff.GivePirateBuff;
import tools.packet.buff.ShowMonsterRiding;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.stat.EnableActions;

public class MapleStatEffect {

   private short weaponAttack, magicAttack, weaponDefense, magicDefense, acc, avoid, speed, jump;
   private short hp, mp;
   private double hpR, mpR;
   private short mhpRRate, mmpRRate, mobSkill, mobSkillLevel;
   private byte mhpR, mmpR;
   private short mpCon, hpCon;
   private int duration, target, barrier, mob;
   private boolean overTime, repeatEffect;
   private int sourceId;
   private int moveTo;
   private int cp, nuffSkill;
   private List<MapleAbnormalStatus> cureAbnormalStatuses;
   private boolean skill;
   private List<Pair<MapleBuffStat, Integer>> statups;
   private Map<MonsterStatus, Integer> monsterStatus;
   private int x, y, mobCount, moneyCon, coolDown, morphId = 0, ghost, fatigue, berserk, booster;
   private double prop;
   private int itemCon, itemConNo;
   private int damage, attackCount, fixDamage;
   private Point lt, rb;
   private short bulletCount, bulletConsume;
   private byte mapProtection;
   private CardItemUpStats cardStats;

   private boolean isEffectActive(int mapId, boolean partyHunting) {
      if (cardStats == null) {
         return true;
      }

      if (!cardStats.isInArea(mapId)) {
         return false;
      }

      return !cardStats.inParty() || partyHunting;
   }

   public boolean isActive(MapleCharacter character) {
      return isEffectActive(character.getMapId(), character.getPartyMembersOnSameMap().size() > 1);
   }

   public int getCardRate(int mapId, int itemId) {
      if (cardStats != null) {
         if (cardStats.itemCode() == Integer.MAX_VALUE) {
            return cardStats.probability();
         } else if (cardStats.itemCode() < 1000) {
            if (itemId / 10000 == cardStats.itemCode()) {
               return cardStats.probability();
            }
         } else {
            if (itemId == cardStats.itemCode()) {
               return cardStats.probability();
            }
         }
      }

      return 0;
   }

   public void applyPassive(MapleCharacter applyTo, MapleMapObject obj, int attack) {
      if (makeChanceResult()) {
         switch (sourceId) { // MP eater
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
                     applyTo.addMP(absorbMp);
                     PacketCreator.announce(applyTo, new ShowOwnBuffEffect(sourceId, 1));
                     MasterBroadcaster.getInstance().sendToAllInMap(applyTo.getMap(), new ShowBuffEffect(applyTo.getId(), sourceId, 1, (byte) 3), false, applyTo);
                  }
               }
               break;
         }
      }
   }

   public boolean applyEchoOfHero(MapleCharacter applyFrom) {
      Map<Integer, MapleCharacter> mapPlayers = applyFrom.getMap().getMapPlayers();
      mapPlayers.remove(applyFrom.getId());

      boolean hwResult = applyTo(applyFrom);
      mapPlayers.values().forEach(character -> applyTo(applyFrom, character, false, null, false, 1));
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
   private boolean applyTo(MapleCharacter applyFrom, MapleCharacter applyTo, boolean primary, Point pos, boolean useMaxRange, int affectedPlayers) {
      if (skill && (sourceId == GM.HIDE || sourceId == SuperGM.HIDE)) {
         applyTo.toggleHide(false);
         return true;
      }

      if (primary && isHeal()) {
         affectedPlayers = applyBuff(applyFrom, useMaxRange);
      }

      int hpChange = calcHPChange(applyFrom, primary, affectedPlayers);
      int mpChange = calcMPChange(applyFrom, primary);
      if (primary) {
         if (itemConNo != 0) {
            if (!applyTo.getAbstractPlayerInteraction().hasItem(itemCon, itemConNo)) {
               PacketCreator.announce(applyTo, new EnableActions());
               return false;
            }
            MapleInventoryManipulator.removeById(applyTo.getClient(), ItemConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
         }
      } else {
         if (isResurrection()) {
            hpChange = applyTo.getCurrentMaxHp();
            applyTo.broadcastStance(applyTo.isFacingLeft() ? 5 : 4);
         }
      }

      if (isDispel() && makeChanceResult()) {
         applyTo.dispelAbnormalStatuses();
      } else if (isCureAllAbnormalStatus()) {
         applyTo.dispelAbnormalStatus(MapleAbnormalStatus.SEDUCE);
         applyTo.dispelAbnormalStatus(MapleAbnormalStatus.ZOMBIFY);
         applyTo.dispelAbnormalStatus(MapleAbnormalStatus.CONFUSE);
         applyTo.dispelAbnormalStatuses();
      } else if (isComboReset()) {
         applyTo.setCombo((short) 0);
      }

      if (!applyTo.applyHpMpChange(hpCon, hpChange, mpChange)) {
         PacketCreator.announce(applyTo, new EnableActions());
         return false;
      }

      if (moveTo != -1) {
         if (moveTo != applyTo.getMapId()) {
            MapleMap target;
            MaplePortal pt;

            if (moveTo == 999999999) {
               if (sourceId != 2030100) {
                  target = applyTo.getMap().getReturnMap();
                  pt = target.getRandomPlayerSpawnPoint();
               } else {
                  if (!applyTo.canRecoverLastBanish()) {
                     return false;
                  }

                  Pair<Integer, Integer> lastBanishInfo = applyTo.getLastBanishData();
                  target = applyTo.getWarpMap(lastBanishInfo.getLeft());
                  pt = target.getPortal(lastBanishInfo.getRight());
               }
            } else {
               target = applyTo.getClient().getWorldServer().getChannel(applyTo.getClient().getChannel()).getMapFactory().getMap(moveTo);
               int targetId = target.getId() / 10000000;
               if (targetId != 60 && applyTo.getMapId() / 10000000 != 61 && targetId != applyTo.getMapId() / 10000000 && targetId != 21 && targetId != 20 && targetId != 12 && (applyTo.getMapId() / 10000000 != 10 && applyTo.getMapId() / 10000000 != 12)) {
                  return false;
               }

               pt = target.getRandomPlayerSpawnPoint();
            }

            applyTo.changeMap(target, pt);
         } else {
            return false;
         }
      }
      if (isShadowClaw()) {
         short projectileConsume = this.getBulletConsume();
         MapleInventory use = applyTo.getInventory(MapleInventoryType.USE);
         use.lockInventory();
         try {
            Item projectile = null;
            for (int i = 1; i <= use.getSlotLimit(); i++) { // impose order...
               Item item = use.getItem((short) i);
               if (item != null) {
                  if (ItemConstants.isThrowingStar(item.id()) && item.quantity() >= projectileConsume) {
                     projectile = item;
                     break;
                  }
               }
            }
            if (projectile == null) {
               return false;
            } else {
               MapleInventoryManipulator.removeFromSlot(applyTo.getClient(), MapleInventoryType.USE, projectile.position(), projectileConsume, false, true);
            }
         } finally {
            use.unlockInventory();
         }
      }
      SummonMovementType summonMovementType = getSummonMovementType();
      if (overTime || isCygnusFA() || summonMovementType != null) {
         if (summonMovementType != null && pos != null) {
            if (summonMovementType.getValue() == SummonMovementType.STATIONARY.getValue()) {
               applyTo.cancelBuffStats(MapleBuffStat.PUPPET);
            } else {
               applyTo.cancelBuffStats(MapleBuffStat.SUMMON);
            }

            PacketCreator.announce(applyTo, new EnableActions());
         }

         applyBuffEffect(applyFrom, applyTo, primary);
      }

      if (primary) {
         if (overTime) {
            applyBuff(applyFrom, useMaxRange);
         }

         if (isMonsterBuff()) {
            applyMonsterBuff(applyFrom);
         }
      }

      if (this.getFatigue() != 0) {
         applyTo.getMount().tiredness_$eq(applyTo.getMount().tiredness() + this.getFatigue());
      }

      if (summonMovementType != null && pos != null) {
         final MapleSummon toSummon = new MapleSummon(applyFrom, sourceId, pos, summonMovementType);
         applyFrom.getMap().spawnSummon(toSummon);
         applyFrom.addSummon(sourceId, toSummon);
         toSummon.addHP(x);
         if (isBeholder()) {
            toSummon.addHP(1);
         }
      }
      if (isMagicDoor() && !FieldLimit.DOOR.check(applyTo.getMap().getFieldLimit())) { // Magic Door
         int y = applyTo.getFh();
         if (y == 0) {
            y = applyTo.getMap().getGroundBelow(applyTo.position()).y;
         }
         Point doorPosition = new Point(applyTo.position().x, y);
         MapleDoor door = new MapleDoor(applyTo, doorPosition);

         if (door.getOwnerId() >= 0) {
            applyTo.applyPartyDoor(door, false);

            door.getTarget().spawnDoor(door.getAreaDoor());
            door.getTown().spawnDoor(door.getTownDoor());
         } else {
            MapleInventoryManipulator.addFromDrop(applyTo.getClient(), new Item(4006000, (short) 0, (short) 1), false);

            if (door.getOwnerId() == -3) {
               MessageBroadcaster.getInstance().sendServerNotice(applyTo, ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTIC_DOOR_CLOSENESS").with(door.getDoorStatus().getRight(), door.getDoorStatus().getLeft()));
            } else if (door.getOwnerId() == -2) {
               MessageBroadcaster.getInstance().sendServerNotice(applyTo, ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTIC_DOOR_SLOPE"));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(applyTo, ServerNoticeType.PINK_TEXT, I18nMessage.from("NO_PORTALS_AVAILABLE"));
            }

            applyTo.cancelBuffStats(MapleBuffStat.SOUL_ARROW);  // cancel door buff
         }
      } else if (isMist()) {
         Rectangle bounds = calculateBoundingBox(sourceId == NightWalker.POISON_BOMB ? pos : applyFrom.position(), applyFrom.isFacingLeft());
         MapleMist mist = new MapleMist(bounds, applyFrom, this);
         applyFrom.getMap().spawnMist(mist, getDuration(), mist.isPoisonMist(), false, mist.isRecoveryMist());
      } else if (isTimeLeap()) {
         applyTo.removeAllCoolDownsExcept(Buccaneer.TIME_LEAP, true);
      } else if (cp != 0 && applyTo.getMonsterCarnival() != null) {
         applyTo.gainCP(cp);
      } else if (nuffSkill != 0 && applyTo.getParty().isPresent() && applyTo.getMap().isCPQMap()) {
         final MCSkill skill = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
         if (skill != null) {
            final MapleAbnormalStatus dis = skill.getDisease();
            MapleParty opposition = applyFrom.getParty().orElseThrow().getEnemy();
            if (skill.targetsAll) {
               opposition.getPartyMembers().parallelStream()
                     .map(MaplePartyCharacter::getPlayer)
                     .flatMap(Optional::stream)
                     .filter(character -> character.getMap().isCPQMap())
                     .forEach(character -> {
                        if (dis == null) {
                           character.dispel();
                        } else {
                           character.giveAbnormalStatus(dis, MCSkill.getMobSkill(dis.getDisease(), skill.level));
                        }
                     });
            } else {
               int amount = opposition.getMembers().size();
               int random = (int) Math.floor(Math.random() * amount);
               MapleCharacter chrApp = applyFrom.getMap().getCharacterById(opposition.getMemberByPos(random).getId());
               if (chrApp != null && chrApp.getMap().isCPQMap()) {
                  if (dis == null) {
                     chrApp.dispel();
                  } else {
                     chrApp.giveAbnormalStatus(dis, MCSkill.getMobSkill(dis.getDisease(), skill.level));
                  }
               }
            }
         }
      } else if (cureAbnormalStatuses.size() > 0) {
         cureAbnormalStatuses.forEach(applyFrom::dispelAbnormalStatus);
      } else if (mobSkill > 0 && mobSkillLevel > 0) {
         MobSkill ms = MobSkillFactory.getMobSkill(mobSkill, mobSkillLevel);
         MapleAbnormalStatus dis = MapleAbnormalStatus.getBySkill(mobSkill);

         if (target > 0) {
            for (MapleCharacter chr : applyTo.getMap().getAllPlayers()) {
               if (chr.getId() != applyTo.getId()) {
                  chr.giveAbnormalStatus(dis, ms);
               }
            }
         } else {
            applyTo.giveAbnormalStatus(dis, ms);
         }
      }
      return true;
   }

   private int applyBuff(MapleCharacter applyFrom, boolean useMaxRange) {
      int affectedc = 1;

      if (isPartyBuff() && (applyFrom.getParty().isPresent() || isGmBuff())) {
         Rectangle bounds = (!useMaxRange) ? calculateBoundingBox(applyFrom.position(), applyFrom.isFacingLeft()) : new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE);
         List<MapleMapObject> affecteds = applyFrom.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.PLAYER));
         List<MapleCharacter> affectedp = new ArrayList<>(affecteds.size());
         for (MapleMapObject affectedmo : affecteds) {
            MapleCharacter affected = (MapleCharacter) affectedmo;
            if (affected != applyFrom && (isGmBuff() || applyFrom.getParty().equals(affected.getParty()))) {
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
            applyTo(applyFrom, affected, false, null, useMaxRange, affectedc);
            PacketCreator.announce(affected, new ShowOwnBuffEffect(sourceId, 2));
            MasterBroadcaster.getInstance().sendToAllInMap(affected.getMap(), new ShowBuffEffect(affected.getId(), sourceId, 2, (byte) 3), false, affected);
         }
      }

      return affectedc;
   }

   private void applyMonsterBuff(MapleCharacter applyFrom) {
      Rectangle bounds = calculateBoundingBox(applyFrom.position(), applyFrom.isFacingLeft());
      List<MapleMapObject> affected = applyFrom.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.MONSTER));
      SkillFactory.getSkill(sourceId).ifPresent(skill_ -> {
         int i = 0;
         for (MapleMapObject mo : affected) {
            MapleMonster monster = (MapleMonster) mo;
            if (isDispel()) {
               monster.removeMobStatus(skill_.getId());
            } else if (isSeal() && monster.isBoss()) {
               //do nothing, seal shouldn't work on bosses
            } else {
               if (makeChanceResult()) {
                  monster.applyStatus(applyFrom, new MonsterStatusEffect(getMonsterStati(), skill_, null, false), isPoison(), getDuration());
                  if (isCrash()) {
                     monster.removeMobStatus(skill_.getId());
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
         myrb = new Point(-lt.x + posFrom.x, rb.y + posFrom.y);
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

      chr.registerEffect(this, localStartTime, localStartTime + localDuration, true);
      SummonMovementType summonMovementType = getSummonMovementType();
      if (summonMovementType != null) {
         final MapleSummon toSummon = new MapleSummon(chr, sourceId, chr.position(), summonMovementType);
         if (!toSummon.isStationary()) {
            chr.addSummon(sourceId, toSummon);
            toSummon.addHP(x);
         }
      }
      if (sourceId == Corsair.BATTLE_SHIP) {
         chr.announceBattleshipHp();
      }
   }

   public final void applyComboBuff(final MapleCharacter applyTo, int combo) {
      final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ARAN_COMBO, combo));
      PacketCreator.announce(applyTo, new GiveBuff(sourceId, 99999, stat));

      final long startTime = Server.getInstance().getCurrentTime();
      applyTo.registerEffect(this, startTime, Long.MAX_VALUE, false);
   }

   public final void applyBeaconBuff(final MapleCharacter applyTo, int objectId) {
      final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.HOMING_BEACON, objectId));
      PacketCreator.announce(applyTo, new GiveBuff(1, sourceId, stat));

      final long startTime = Server.getInstance().getCurrentTime();
      applyTo.registerEffect(this, startTime, Long.MAX_VALUE, false);
   }

   public void updateBuffEffect(MapleCharacter target, List<Pair<MapleBuffStat, Integer>> activeStats, long startTime) {
      int localDuration = getBuffLocalDuration();
      localDuration = alchemistModifyVal(target, localDuration, false);

      long leftDuration = (startTime + localDuration) - Server.getInstance().getCurrentTime();
      if (leftDuration > 0) {
         if (isDash() || isInfusion()) {
            PacketCreator.announce(target, new GivePirateBuff(activeStats, (skill ? sourceId : -sourceId), (int) leftDuration));
         } else {
            PacketCreator.announce(target, new GiveBuff((skill ? sourceId : -sourceId), (int) leftDuration, activeStats));
         }
      }
   }

   private void applyBuffEffect(MapleCharacter applyFrom, MapleCharacter applyTo, boolean primary) {
      if (!isMonsterRiding() && !isCouponBuff() && !isMysticDoor() && !isHyperBody() && !isCombo()) {     // last mystic door already dispelled if it has been used before.
         applyTo.cancelEffect(this, true, -1);
      }

      List<Pair<MapleBuffStat, Integer>> localstatups = statups;
      int localDuration = getBuffLocalDuration();
      int localSourceId = sourceId;
      int seconds = localDuration / 1000;
      MapleMount givenMount = null;
      if (isMonsterRiding()) {
         int ridingMountId = 0;
         Item mount = applyFrom.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
         if (mount != null) {
            ridingMountId = mount.id();
         }

         if (sourceId == Corsair.BATTLE_SHIP) {
            ridingMountId = 1932000;
         } else if (sourceId == Beginner.SPACESHIP || sourceId == Noblesse.SPACESHIP) {
            ridingMountId = 1932000 + applyTo.getSkillLevel(sourceId);
         } else if (sourceId == Beginner.YETI_MOUNT1 || sourceId == Noblesse.YETI_MOUNT1 || sourceId == Legend.YETI_MOUNT1) {
            ridingMountId = 1932003;
         } else if (sourceId == Beginner.YETI_MOUNT2 || sourceId == Noblesse.YETI_MOUNT2 || sourceId == Legend.YETI_MOUNT2) {
            ridingMountId = 1932004;
         } else if (sourceId == Beginner.WITCH_BROOMSTICK || sourceId == Noblesse.WITCH_BROOMSTICK || sourceId == Legend.WITCH_BROOMSTICK) {
            ridingMountId = 1932005;
         } else if (sourceId == Beginner.BALROG_MOUNT || sourceId == Noblesse.BALROG_MOUNT || sourceId == Legend.BALROG_MOUNT) {
            ridingMountId = 1932010;
         }

         givenMount = applyTo.mount(ridingMountId, sourceId);
         applyTo.getClient().getWorldServer().registerMountHunger(applyTo.getId(), applyTo.isGM());

         localDuration = sourceId;
         localSourceId = ridingMountId;
         localstatups = Collections.singletonList(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
      } else if (isSkillMorph()) {
         for (int i = 0; i < localstatups.size(); i++) {
            if (localstatups.get(i).getLeft().equals(MapleBuffStat.MORPH)) {
               localstatups.set(i, new Pair<>(MapleBuffStat.MORPH, getMorph(applyTo)));
               break;
            }
         }
      }
      if (primary) {
         localDuration = alchemistModifyVal(applyFrom, localDuration, false);
         MasterBroadcaster.getInstance().sendToAllInMap(applyTo.getMap(), new ShowBuffEffect(applyTo.getId(), sourceId, 1, (byte) 3), false, applyTo);
      }
      if (localstatups.size() > 0) {
         PacketInput buff = null;
         PacketInput mbuff = null;
         if (this.isActive(applyTo)) {
            buff = new GiveBuff((skill ? sourceId : -sourceId), localDuration, localstatups);
         }
         if (isDash()) {
            buff = new GivePirateBuff(statups, sourceId, seconds);
            mbuff = new GiveForeignPirateBuff(applyTo.getId(), sourceId, seconds, localstatups);
         } else if (isInfusion()) {
            buff = new GivePirateBuff(localstatups, sourceId, seconds);
            mbuff = new GiveForeignPirateBuff(applyTo.getId(), sourceId, seconds, localstatups);
         } else if (isDs()) {
            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.DARK_SIGHT, 0));
            mbuff = new GiveForeignBuff(applyTo.getId(), dsstat);
         } else if (isWw()) {
            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.WIND_WALK, 0));
            mbuff = new GiveForeignBuff(applyTo.getId(), dsstat);
         } else if (isCombo()) {
            Integer comboCount = applyTo.getBuffedValue(MapleBuffStat.COMBO);
            if (comboCount == null) {
               comboCount = 0;
            }

            List<Pair<MapleBuffStat, Integer>> cbstat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, comboCount));
            buff = new GiveBuff((skill ? sourceId : -sourceId), localDuration, cbstat);
            mbuff = new GiveForeignBuff(applyTo.getId(), cbstat);
         } else if (isMonsterRiding()) {
            if (sourceId == Corsair.BATTLE_SHIP) {//hp
               if (applyTo.getBattleshipHp() <= 0) {
                  applyTo.resetBattleshipHp();
               }

               localstatups = statups;
            }
            buff = new GiveBuff(localSourceId, localDuration, localstatups);
            mbuff = new ShowMonsterRiding(applyTo.getId(), givenMount.itemId(), givenMount.skillId());
            localDuration = duration;
         } else if (isShadowPartner()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SHADOW_PARTNER, 0));
            mbuff = new GiveForeignBuff(applyTo.getId(), stat);
         } else if (isSoulArrow()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SOUL_ARROW, 0));
            mbuff = new GiveForeignBuff(applyTo.getId(), stat);
         } else if (isEnrage()) {
            applyTo.handleOrbConsume();
         } else if (isMorph()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MORPH, getMorph(applyTo)));
            mbuff = new GiveForeignBuff(applyTo.getId(), stat);
         } else if (isAriantShield()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.AURA, 1));
            mbuff = new GiveForeignBuff(applyTo.getId(), stat);
         }

         if (buff != null) {
            PacketCreator.announce(applyTo, buff);
         }

         long startTime = Server.getInstance().getCurrentTime();
         applyTo.registerEffect(this, startTime, startTime + localDuration, false);

         if (mbuff != null) {
            MasterBroadcaster.getInstance().sendToAllInMap(applyTo.getMap(), mbuff, false, applyTo);
         }
         if (sourceId == Corsair.BATTLE_SHIP) {
            applyTo.announceBattleshipHp();
         }
      }
   }

   private int calcHPChange(MapleCharacter applyFrom, boolean primary, int affectedPlayers) {
      int hpChange = 0;
      if (hp != 0) {
         if (!skill) {
            if (primary) {
               hpChange += alchemistModifyVal(applyFrom, hp, true);
            } else {
               hpChange += hp;
            }
            if (applyFrom.hasDisease(MapleAbnormalStatus.ZOMBIFY)) {
               hpChange /= 2;
            }
         } else { // assumption: this is heal
            float hpHeal = (applyFrom.getCurrentMaxHp() * (float) hp / (100.0f * affectedPlayers));
            hpChange += hpHeal;
            if (applyFrom.hasDisease(MapleAbnormalStatus.ZOMBIFY)) {
               hpChange = -hpChange;
               hpCon = 0;
            }
         }
      }
      if (hpR != 0) {
         hpChange += (int) (applyFrom.getCurrentMaxHp() * hpR) / (applyFrom.hasDisease(MapleAbnormalStatus.ZOMBIFY) ? 2 : 1);
      }
      if (primary) {
         if (hpCon != 0) {
            hpChange -= hpCon;
         }
      }
      if (isChakra()) {
         hpChange += makeHealHP(getY() / 100.0, applyFrom.getTotalLuk(), 2.3, 3.5);
      } else if (sourceId == SuperGM.HEAL_PLUS_DISPEL) {
         hpChange += applyFrom.getCurrentMaxHp();
      }

      return hpChange;
   }

   private int makeHealHP(double rate, double stat, double lowerFactor, double upperFactor) {
      return (int) ((Math.random() * ((int) (stat * upperFactor * rate) - (int) (stat * lowerFactor * rate) + 1)) + (int) (stat * lowerFactor * rate));
   }

   private int calcMPChange(MapleCharacter applyFrom, boolean primary) {
      int mpChange = 0;
      if (mp != 0) {
         if (primary) {
            mpChange += alchemistModifyVal(applyFrom, mp, true);
         } else {
            mpChange += mp;
         }
      }
      if (mpR != 0) {
         mpChange += (int) (applyFrom.getCurrentMaxMp() * mpR);
      }
      if (primary) {
         if (mpCon != 0) {
            double mod = 1.0;
            boolean isAFpMage = applyFrom.getJob().isA(MapleJob.FIRE_POISON_MAGICIAN);
            boolean isCygnus = applyFrom.getJob().isA(MapleJob.BLAZE_WIZARD_2);
            boolean isEvan = applyFrom.getJob().isA(MapleJob.EVAN7);
            if (isAFpMage || isCygnus || isEvan || applyFrom.getJob().isA(MapleJob.ICE_LIGHTENING_MAGICIAN)) {
               int skillId = isAFpMage ? FirePoisonMagician.ELEMENT_AMPLIFICATION : (isCygnus ? BlazeWizard.ELEMENT_AMPLIFICATION : (isEvan ? Evan.MAGIC_AMPLIFICATION : IceLighteningMagician.ELEMENT_AMPLIFICATION));
               mod = SkillFactory.applyIfHasSkill(applyFrom, skillId, (skill, skillLevel) -> skill.getEffect(skillLevel).getX() / 100.0, 0.0);
            }
            mpChange -= mpCon * mod;
            if (applyFrom.getBuffedValue(MapleBuffStat.INFINITY) != null) {
               mpChange = 0;
            } else if (applyFrom.getBuffedValue(MapleBuffStat.CONCENTRATE) != null) {
               mpChange -= (int) (mpChange * (applyFrom.getBuffedValue(MapleBuffStat.CONCENTRATE).doubleValue() / 100));
            }
         }
      }
      if (sourceId == SuperGM.HEAL_PLUS_DISPEL) {
         mpChange += applyFrom.getCurrentMaxMp();
      }

      return mpChange;
   }

   private int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
      if (!skill && (chr.getJob().isA(MapleJob.HERMIT) || chr.getJob().isA(MapleJob.NIGHT_WALKER_3))) {
         MapleStatEffect alchemistEffect = StatEffectProcessor.getInstance().getAlchemistEffect(chr);
         if (alchemistEffect != null) {
            return (int) (val * ((withX ? alchemistEffect.getX() : alchemistEffect.getY()) / 100.0));
         }
      }
      return val;
   }

   private boolean isGmBuff() {
      switch (sourceId) {
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
      switch (sourceId) {
         case Page.THREATEN:
         case FPWizard.SLOW:
         case ILWizard.SLOW:
         case FirePoisonMagician.SEAL:
         case IceLighteningMagician.SEAL:
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
      return (sourceId < 1211003 || sourceId > 1211008) && sourceId != Paladin.SWORD_HOLY_CHARGE && sourceId != Paladin.BW_HOLY_CHARGE && sourceId != DawnWarrior.SOUL_CHARGE;
   }

   private boolean isHeal() {
      return sourceId == Cleric.HEAL || sourceId == SuperGM.HEAL_PLUS_DISPEL;
   }

   private boolean isResurrection() {
      return sourceId == Bishop.RESURRECTION || sourceId == GM.RESURRECTION || sourceId == SuperGM.RESURRECTION;
   }

   private boolean isTimeLeap() {
      return sourceId == Buccaneer.TIME_LEAP;
   }

   public boolean isDragonBlood() {
      return skill && sourceId == DragonKnight.DRAGON_BLOOD;
   }

   public boolean isBerserk() {
      return skill && sourceId == DarkKnight.BERSERK;
   }

   public boolean isRecovery() {
      return sourceId == Beginner.RECOVERY || sourceId == Noblesse.RECOVERY || sourceId == Legend.RECOVERY || sourceId == Evan.RECOVERY;
   }

   public boolean isMapChair() {
      return sourceId == Beginner.MAP_CHAIR || sourceId == Noblesse.MAP_CHAIR || sourceId == Legend.MAP_CHAIR;
   }

   public boolean isDojoBuff() {
      return sourceId >= 2022359 && sourceId <= 2022421;
   }

   private boolean isDs() {
      return skill && (sourceId == Rogue.DARK_SIGHT || sourceId == NightWalker.DARK_SIGHT);
   }

   private boolean isWw() {
      return skill && (sourceId == WindArcher.WIND_WALK);
   }

   private boolean isCombo() {
      return skill && (sourceId == Crusader.COMBO || sourceId == DawnWarrior.COMBO);
   }

   private boolean isEnrage() {
      return skill && sourceId == Hero.ENRAGE;
   }

   public boolean isBeholder() {
      return skill && sourceId == DarkKnight.BEHOLDER;
   }

   private boolean isShadowPartner() {
      return skill && (sourceId == Hermit.SHADOW_PARTNER || sourceId == NightWalker.SHADOW_PARTNER);
   }

   private boolean isChakra() {
      return skill && sourceId == ChiefBandit.CHAKRA;
   }

   private boolean isCouponBuff() {
      return StatEffectProcessor.getInstance().isRateCoupon(sourceId);
   }

   private boolean isAriantShield() {
      int itemId = sourceId;
      return StatEffectProcessor.getInstance().isAriantShield(itemId);
   }

   private boolean isMysticDoor() {
      return skill && sourceId == Priest.MYSTIC_DOOR;
   }

   public boolean isMonsterRiding() {
      return skill && (sourceId % 10000000 == 1004 || sourceId == Corsair.BATTLE_SHIP || sourceId == Beginner.SPACESHIP || sourceId == Noblesse.SPACESHIP
            || sourceId == Beginner.YETI_MOUNT1 || sourceId == Beginner.YETI_MOUNT2 || sourceId == Beginner.WITCH_BROOMSTICK || sourceId == Beginner.BALROG_MOUNT
            || sourceId == Noblesse.YETI_MOUNT1 || sourceId == Noblesse.YETI_MOUNT2 || sourceId == Noblesse.WITCH_BROOMSTICK || sourceId == Noblesse.BALROG_MOUNT
            || sourceId == Legend.YETI_MOUNT1 || sourceId == Legend.YETI_MOUNT2 || sourceId == Legend.WITCH_BROOMSTICK || sourceId == Legend.BALROG_MOUNT);
   }

   public boolean isMagicDoor() {
      return skill && sourceId == Priest.MYSTIC_DOOR;
   }

   public boolean isPoison() {
      return skill && (sourceId == FirePoisonMagician.POISON_MIST || sourceId == FPWizard.POISON_BREATH || sourceId == FirePoisonMagician.ELEMENT_COMPOSITION || sourceId == NightWalker.POISON_BOMB || sourceId == BlazeWizard.FLAME_GEAR);
   }

   public boolean isMorph() {
      return morphId > 0;
   }

   public boolean isMorphWithoutAttack() {
      return morphId > 0 && morphId < 100; // Every morph item I have found has been under 100, pirate skill transforms start at 1000.
   }

   private boolean isMist() {
      return skill && (sourceId == FirePoisonMagician.POISON_MIST || sourceId == Shadower.SMOKE_SCREEN || sourceId == BlazeWizard.FLAME_GEAR || sourceId == NightWalker.POISON_BOMB || sourceId == Evan.RECOVERY_AURA);
   }

   private boolean isSoulArrow() {
      return skill && (sourceId == Hunter.SOUL_ARROW || sourceId == Crossbowman.SOUL_ARROW || sourceId == WindArcher.SOUL_ARROW);
   }

   private boolean isShadowClaw() {
      return skill && sourceId == NightLord.SHADOW_STARS;
   }

   private boolean isCrash() {
      return skill && (sourceId == DragonKnight.POWER_CRASH || sourceId == Crusader.ARMOR_CRASH || sourceId == WhiteKnight.MAGIC_CRASH);
   }

   private boolean isSeal() {
      return skill && (sourceId == IceLighteningMagician.SEAL || sourceId == FirePoisonMagician.SEAL || sourceId == BlazeWizard.SEAL);
   }

   private boolean isDispel() {
      return skill && (sourceId == Priest.DISPEL || sourceId == SuperGM.HEAL_PLUS_DISPEL);
   }

   private boolean isCureAllAbnormalStatus() {
      if (skill) {
         return StatEffectProcessor.getInstance().isHerosWill(sourceId);
      } else {
         return sourceId == 2022544;
      }

   }

   private boolean isDash() {
      return skill && (sourceId == Pirate.DASH || sourceId == ThunderBreaker.DASH || sourceId == Beginner.SPACE_DASH || sourceId == Noblesse.SPACE_DASH);
   }

   private boolean isSkillMorph() {
      return skill && (sourceId == Buccaneer.SUPER_TRANSFORMATION || sourceId == Marauder.TRANSFORMATION || sourceId == WindArcher.EAGLE_EYE || sourceId == ThunderBreaker.TRANSFORMATION);
   }

   private boolean isInfusion() {
      return skill && (sourceId == Buccaneer.SPEED_INFUSION || sourceId == Corsair.SPEED_INFUSION || sourceId == ThunderBreaker.SPEED_INFUSION);
   }

   private boolean isCygnusFA() {
      return skill && (sourceId == DawnWarrior.FINAL_ATTACK || sourceId == WindArcher.FINAL_ATTACK);
   }

   private boolean isHyperBody() {
      return skill && (sourceId == Spearman.HYPER_BODY || sourceId == GM.HYPER_BODY || sourceId == SuperGM.HYPER_BODY);
   }

   private boolean isComboReset() {
      return sourceId == Aran.COMBO_BARRIER || sourceId == Aran.COMBO_DRAIN;
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
      switch (sourceId) {
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
         case BowMaster.PHOENIX:
         case Outlaw.GAVIOTA:
            return SummonMovementType.CIRCLE_FOLLOW;
         case DarkKnight.BEHOLDER:
         case FirePoisonArchMage.ELQUINES:
         case IceLighteningArchMagician.IFRIT:
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
      return sourceId;
   }

   public int getBuffSourceId() {
      return skill ? sourceId : -sourceId;
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

   public short getMagicAttack() {
      return magicAttack;
   }

   public short getWeaponAttack() {
      return weaponAttack;
   }

   public int getDuration() {
      return duration;
   }

   public List<Pair<MapleBuffStat, Integer>> getStatups() {
      return statups;
   }

   public boolean sameSource(MapleStatEffect effect) {
      return this.sourceId == effect.sourceId && this.skill == effect.skill;
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
      return fixDamage;
   }

   public short getBulletCount() {
      return bulletCount;
   }

   public short getBulletConsume() {
      return bulletConsume;
   }

   public int getMoneyCon() {
      return moneyCon;
   }

   public int getCoolDown() {
      return coolDown;
   }

   public Map<MonsterStatus, Integer> getMonsterStati() {
      return monsterStatus;
   }

   public short getWeaponDefense() {
      return weaponDefense;
   }

   public short getMagicDefense() {
      return magicDefense;
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

   public int getMoveTo() {
      return moveTo;
   }

   public int getCp() {
      return cp;
   }

   public int getNuffSkill() {
      return nuffSkill;
   }

   public List<MapleAbnormalStatus> getCureAbnormalStatuses() {
      return cureAbnormalStatuses;
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

   public Point getLt() {
      return lt;
   }

   public Point getRb() {
      return rb;
   }

   public byte getMapProtection() {
      return mapProtection;
   }

   public CardItemUpStats getCardStats() {
      return cardStats;
   }

   public void setWeaponAttack(short weaponAttack) {
      this.weaponAttack = weaponAttack;
   }

   public void setMagicAttack(short magicAttack) {
      this.magicAttack = magicAttack;
   }

   public void setWeaponDefense(short weaponDefense) {
      this.weaponDefense = weaponDefense;
   }

   public void setMagicDefense(short magicDefense) {
      this.magicDefense = magicDefense;
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

   public void setSourceId(int sourceId) {
      this.sourceId = sourceId;
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

   public void setCureAbnormalStatuses(List<MapleAbnormalStatus> cureAbnormalStatuses) {
      this.cureAbnormalStatuses = cureAbnormalStatuses;
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

   public void setCoolDown(int coolDown) {
      this.coolDown = coolDown;
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

   public void setFixDamage(int fixDamage) {
      this.fixDamage = fixDamage;
   }

   public void setLt(Point lt) {
      this.lt = lt;
   }

   public void setRb(Point rb) {
      this.rb = rb;
   }

   public void setBulletCount(short bulletCount) {
      this.bulletCount = bulletCount;
   }

   public void setBulletConsume(short bulletConsume) {
      this.bulletConsume = bulletConsume;
   }

   public void setMapProtection(byte mapProtection) {
      this.mapProtection = mapProtection;
   }

   public void setCardStats(CardItemUpStats cardStats) {
      this.cardStats = cardStats;
   }
}
