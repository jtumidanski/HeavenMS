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
package tools;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import client.KeyBinding;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleMount;
import client.MapleStat;
import client.database.data.NoteData;
import client.inventory.ScrollResult;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.SkillMacro;
import net.server.world.World;
import server.life.MapleMonster;
import server.life.MaplePlayerNPC;
import server.life.MobSkill;
import server.maps.MapleMapItem;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.statusinfo.ShowItemGain;

/**
 * @author Frz
 */
public class MaplePacketCreator {

   public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();
   public final static long ZERO_TIME = 94354848000000000L;//00 40 E0 FD 3B 37 4F 01
   private final static long FT_UT_OFFSET = 116444736010800000L + (10000L * TimeZone.getDefault().getOffset(System.currentTimeMillis())); // normalize with timezone offset suggested by Ari
   private final static long DEFAULT_TIME = 150842304000000000L;//00 80 05 BB 46 E6 17 02
   private final static long PERMANENT = 150841440000000000L; // 00 C0 9B 90 7D E5 17 02

   private static long getTime(long utcTimestamp) {
      if (utcTimestamp < 0 && utcTimestamp >= -3) {
         if (utcTimestamp == -1) {
            return DEFAULT_TIME;    //high number ll
         } else if (utcTimestamp == -2) {
            return ZERO_TIME;
         } else {
            return PERMANENT;
         }
      }

      return utcTimestamp * 10000 + FT_UT_OFFSET;
   }

   public static byte[] setExtraPendantSlot(boolean toggleExtraSlot) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_EXTRA_PENDANT_SLOT.getValue());
      mplew.writeBool(toggleExtraSlot);
      return mplew.getPacket();
   }

   private static void addExpirationTime(final MaplePacketLittleEndianWriter mplew, long time) {
      mplew.writeLong(getTime(time)); // offset expiration time issue found thanks to Thora
   }

   /**
    * Sends a hello packet.
    *
    * @param mapleVersion The maple client version.
    * @param sendIv       the IV used by the server for sending
    * @param recvIv       the IV used by the server for receiving
    * @return
    */
   public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(0x0E);
      mplew.writeShort(mapleVersion);
      mplew.writeShort(1);
      mplew.write(49);
      mplew.write(recvIv);
      mplew.write(sendIv);
      mplew.write(8);
      return mplew.getPacket();
   }

   public static byte[] sendPolice() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAKE_GM_NOTICE.getValue());
      mplew.write(0);//doesn't even matter what value
      return mplew.getPacket();
   }

   public static byte[] sendPolice(String text) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DATA_CRC_CHECK_FAILED.getValue());
      mplew.writeMapleAsciiString(text);
      return mplew.getPacket();
   }

   public static byte[] sendCannotSpawnKite() {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANNOT_SPAWN_KITE.getValue());
      return mplew.getPacket();
   }

   /**
    * Gets a packet telling the client to show a item gain.
    *
    * @param itemId   The ID of the item gained.
    * @param quantity How many items gained.
    * @return The item gain packet.
    */
   public static byte[] getShowItemGain(int itemId, short quantity) {
      return PacketCreator.create(new ShowItemGain(itemId, quantity));
   }

   public static byte[] killMonster(int oid, boolean animation) {
      return killMonster(oid, animation ? 1 : 0);
   }

   /**
    * Gets a packet telling the client that a monster was killed.
    *
    * @param oid       The objectID of the killed monster.
    * @param animation 0 = dissapear, 1 = fade out, 2+ = special
    * @return The kill monster packet.
    */
   public static byte[] killMonster(int oid, int animation) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.KILL_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(animation);
      mplew.write(animation);
      return mplew.getPacket();
   }

   public static byte[] updateMapItemObject(MapleMapItem drop, boolean giveOwnership) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
      mplew.write(2);
      mplew.writeInt(drop.getObjectId());
      mplew.writeBool(drop.getMeso() > 0);
      mplew.writeInt(drop.getItemId());
      mplew.writeInt(giveOwnership ? 0 : -1);
      mplew.write(drop.hasExpiredOwnershipTime() ? 2 : drop.getDropType());
      mplew.writePos(drop.getPosition());
      mplew.writeInt(giveOwnership ? 0 : -1);

      if (drop.getMeso() == 0) {
         addExpirationTime(mplew, drop.getItem().expiration());
      }
      mplew.write(drop.isPlayerDrop() ? 0 : 1);
      return mplew.getPacket();
   }

   public static byte[] dropItemFromMapObject(MapleCharacter player, MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
      int dropType = drop.getDropType();
      if (drop.hasClientsideOwnership(player) && dropType < 3) {
         dropType = 2;
      }

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
      mplew.write(mod);
      mplew.writeInt(drop.getObjectId());
      mplew.writeBool(drop.getMeso() > 0); // 1 mesos, 0 item, 2 and above all item meso bag,
      mplew.writeInt(drop.getItemId()); // drop object ID
      mplew.writeInt(drop.getClientsideOwnerId()); // owner charid/partyid :)
      mplew.write(dropType); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
      mplew.writePos(dropto);
      mplew.writeInt(drop.getDropper().getObjectId()); // dropper oid, found thanks to Li Jixue

      if (mod != 2) {
         mplew.writePos(dropfrom);
         mplew.writeShort(0);//Fh?
      }
      if (drop.getMeso() == 0) {
         addExpirationTime(mplew, drop.getItem().expiration());
      }
      mplew.write(drop.isPlayerDrop() ? 0 : 1); //pet EQP pickup
      return mplew.getPacket();
   }

   public static byte[] facialExpression(MapleCharacter from, int expression) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
      mplew.writeShort(SendOpcode.FACIAL_EXPRESSION.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(expression);
      return mplew.getPacket();
   }

   public static byte[] getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll) {   // thanks to Rien dev team
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_SCROLL_EFFECT.getValue());
      mplew.writeInt(chr);
      mplew.writeBool(scrollSuccess == ScrollResult.SUCCESS);
      mplew.writeBool(scrollSuccess == ScrollResult.CURSE);
      mplew.writeBool(legendarySpirit);
      mplew.writeBool(whiteScroll);
      return mplew.getPacket();
   }

   public static byte[] catchMessage(int message) { // not done, I guess
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BRIDLE_MOB_CATCH_FAIL.getValue());
      mplew.write(message); // 1 = too strong, 2 = Elemental Rock
      mplew.writeInt(0);//Maybe itemid?
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] showAriantScoreBoard() {   // thanks lrenex for pointing match's end scoreboard packet
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ARIANT_ARENA_SHOW_RESULT.getValue());
      return mplew.getPacket();
   }

   public static byte[] updateAriantPQRanking(final MapleCharacter chr, final int score) {
      return updateAriantPQRanking(new LinkedHashMap<>() {{
         put(chr, score);
      }});
   }

   public static byte[] updateAriantPQRanking(Map<MapleCharacter, Integer> playerScore) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ARIANT_ARENA_USER_SCORE.getValue());
      mplew.write(playerScore.size());
      for (Entry<MapleCharacter, Integer> e : playerScore.entrySet()) {
         mplew.writeMapleAsciiString(e.getKey().getName());
         mplew.writeInt(e.getValue());
      }
      return mplew.getPacket();
   }

   public static byte[] updateWitchTowerScore(int score) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WITCH_TOWER_SCORE_UPDATE.getValue());
      mplew.write(score);
      return mplew.getPacket();
   }

   public static byte[] damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake, int direction, boolean pgmr, int pgmr_1, boolean is_pg, int oid, int pos_x, int pos_y) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_PLAYER.getValue());
      mplew.writeInt(cid);
      mplew.write(skill);
      mplew.writeInt(damage);
      if (skill != -4) {
         mplew.writeInt(monsteridfrom);
         mplew.write(direction);
         if (pgmr) {
            mplew.write(pgmr_1);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);
         } else {
            mplew.writeShort(0);
         }
         mplew.writeInt(damage);
         if (fake > 0) {
            mplew.writeInt(fake);
         }
      } else {
         mplew.writeInt(damage);
      }

      return mplew.getPacket();
   }

   public static byte[] sendMapleLifeCharacterInfo() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_RESULT.getValue());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] sendMapleLifeNameError() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_RESULT.getValue());
      mplew.writeInt(2);
      mplew.writeInt(3);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] sendMapleLifeError(int code) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_ERROR.getValue());
      mplew.write(0);
      mplew.writeInt(code);
      return mplew.getPacket();
   }

   public static byte[] updateSkill(int skillid, int level, int masterlevel, long expiration) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_SKILLS.getValue());
      mplew.write(1);
      mplew.writeShort(1);
      mplew.writeInt(skillid);
      mplew.writeInt(level);
      mplew.writeInt(masterlevel);
      addExpirationTime(mplew, expiration);
      mplew.write(4);
      return mplew.getPacket();
   }

   public static byte[] getShowQuestCompletion(int id) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.QUEST_CLEAR.getValue());
      mplew.writeShort(id);
      return mplew.getPacket();
   }

   public static byte[] getKeymap(Map<Integer, KeyBinding> keybindings) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.KEYMAP.getValue());
      mplew.write(0);
      for (int x = 0; x < 90; x++) {
         KeyBinding binding = keybindings.get(x);
         if (binding != null) {
            mplew.write(binding.theType());
            mplew.writeInt(binding.action());
         } else {
            mplew.write(0);
            mplew.writeInt(0);
         }
      }
      return mplew.getPacket();
   }

   /**
    * @param oid
    * @param remhppercentage
    * @return
    */
   public static byte[] showMonsterHP(int oid, int remhppercentage) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_MONSTER_HP.getValue());
      mplew.writeInt(oid);
      mplew.write(remhppercentage);
      return mplew.getPacket();
   }

   public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_PARTYMEMBER_HP.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(curhp);
      mplew.writeInt(maxhp);
      return mplew.getPacket();
   }

   private static void writeIntMask(final MaplePacketLittleEndianWriter mplew, Map<MonsterStatus, Integer> stats) {
      int firstmask = 0;
      int secondmask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         if (stat.isFirst()) {
            firstmask |= stat.getValue();
         } else {
            secondmask |= stat.getValue();
         }
      }
      mplew.writeInt(firstmask);
      mplew.writeInt(secondmask);
   }

   public static byte[] applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill, boolean monsterSkill, int delay, MobSkill mobskill) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(oid);
      int mask = 0;
      for (MonsterStatus stat : stats.keySet()) {
         mask |= stat.getValue();
      }
      mplew.writeInt(mask);
      for (Integer val : stats.values()) {
         mplew.writeShort(val);
         if (monsterSkill) {
            mplew.writeShort(mobskill.getSkillId());
            mplew.writeShort(mobskill.getSkillLevel());
         } else {
            mplew.writeInt(skill);
         }
         mplew.writeShort(0); // as this looks similar to giveBuff this
      }
      mplew.writeShort(delay); // delay in ms
      mplew.write(1); // ?
      return mplew.getPacket();
   }

   public static byte[] applyMonsterStatus(final int oid, final MonsterStatusEffect mse, final List<Integer> reflection) {
      Map<MonsterStatus, Integer> stati = mse.getStati();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.APPLY_MONSTER_STATUS.getValue());
      mplew.writeInt(oid);
      mplew.writeLong(0);
      writeIntMask(mplew, stati);
      for (Map.Entry<MonsterStatus, Integer> stat : stati.entrySet()) {
         mplew.writeShort(stat.getValue());
         if (mse.isMonsterSkill()) {
            mplew.writeShort(mse.getMobSkill().getSkillId());
            mplew.writeShort(mse.getMobSkill().getSkillLevel());
         } else {
            mplew.writeInt(mse.getSkill().getId());
         }
         mplew.writeShort(-1); // might actually be the buffTime but it's not displayed anywhere
      }
      int size = stati.size(); // size
      if (reflection != null) {
         for (Integer ref : reflection) {
            mplew.writeInt(ref);
         }
         if (reflection.size() > 0) {
            size /= 2; // This gives 2 buffs per reflection but it's really one buff
         }
      }
      mplew.write(size); // size
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] cancelMonsterStatus(int oid, Map<MonsterStatus, Integer> stats) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_MONSTER_STATUS.getValue());
      mplew.writeInt(oid);
      mplew.writeLong(0);
      writeIntMask(mplew, stats);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] getClock(int time) { // time in seconds
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLOCK.getValue());
      mplew.write(2); // clock type. if you send 3 here you have to send another byte (which does not matter at all) before the timestamp
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] getClockTime(int hour, int min, int sec) { // Current Time
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLOCK.getValue());
      mplew.write(1); //Clock-Type
      mplew.write(hour);
      mplew.write(min);
      mplew.write(sec);
      return mplew.getPacket();
   }

   public static byte[] removeClock() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STOP_CLOCK.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] damageSummon(int cid, int oid, int damage, int monsterIdFrom) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_SUMMON.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(oid);
      mplew.write(12);
      mplew.writeInt(damage);         // damage display doesn't seem to work...
      mplew.writeInt(monsterIdFrom);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] damageMonster(int oid, int damage) {
      return damageMonster(oid, damage, 0, 0);
   }

   public static byte[] healMonster(int oid, int heal, int curhp, int maxhp) {
      return damageMonster(oid, -heal, curhp, maxhp);
   }

   private static byte[] damageMonster(int oid, int damage, int curhp, int maxhp) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(oid);
      mplew.write(0);
      mplew.writeInt(damage);
      mplew.writeInt(curhp);
      mplew.writeInt(maxhp);
      return mplew.getPacket();
   }

   public static byte[] itemEffect(int characterid, int itemid) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_EFFECT.getValue());
      mplew.writeInt(characterid);
      mplew.writeInt(itemid);
      return mplew.getPacket();
   }

   public static byte[] showChair(int characterid, int itemid) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_CHAIR.getValue());
      mplew.writeInt(characterid);
      mplew.writeInt(itemid);
      return mplew.getPacket();
   }

   public static byte[] cancelChair(int id) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_CHAIR.getValue());
      if (id < 0) {
         mplew.write(0);
      } else {
         mplew.write(1);
         mplew.writeShort(id);
      }
      return mplew.getPacket();
   }

   public static byte[] environmentMove(String env, int mode) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ONOFF.getValue());
      mplew.writeMapleAsciiString(env);
      mplew.writeInt(mode);   // 0: stop and back to start, 1: move

      return mplew.getPacket();
   }

   public static byte[] environmentMoveList(Set<Entry<String, Integer>> envList) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ONOFF_LIST.getValue());
      mplew.writeInt(envList.size());

      for (Entry<String, Integer> envMove : envList) {
         mplew.writeMapleAsciiString(envMove.getKey());
         mplew.writeInt(envMove.getValue());
      }

      return mplew.getPacket();
   }

   public static byte[] environmentMoveReset() {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ALL_RESET.getValue());
      return mplew.getPacket();
   }

   public static byte[] startMapEffect(String msg, int itemid, boolean active) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOW_WEATHER.getValue());
      mplew.write(active ? 0 : 1);
      mplew.writeInt(itemid);
      if (active) {
         mplew.writeMapleAsciiString(msg);
      }
      return mplew.getPacket();
   }

   public static byte[] removeMapEffect() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOW_WEATHER.getValue());
      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] skillEffect(MapleCharacter from, int skillId, int level, byte flags, int speed, byte direction) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SKILL_EFFECT.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(skillId);
      mplew.write(level);
      mplew.write(flags);
      mplew.write(speed);
      mplew.write(direction); //Mmmk
      return mplew.getPacket();
   }

   public static byte[] skillCancel(MapleCharacter from, int skillId) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_SKILL_EFFECT.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(skillId);
      return mplew.getPacket();
   }

   public static byte[] catchMonster(int mobOid, byte success) {   // updated packet structure found thanks to Rien dev team
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CATCH_MONSTER.getValue());
      mplew.writeInt(mobOid);
      mplew.write(success);
      return mplew.getPacket();
   }

   public static byte[] catchMonster(int mobOid, int itemid, byte success) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CATCH_MONSTER_WITH_ITEM.getValue());
      mplew.writeInt(mobOid);
      mplew.writeInt(itemid);
      mplew.write(success);
      return mplew.getPacket();
   }

   /**
    * Sends a player hint.
    *
    * @param hint   The hint it's going to send.
    * @param width  How tall the box is going to be.
    * @param height How long the box is going to be.
    * @return The player hint packet.
    */
   public static byte[] sendHint(String hint, int width, int height) {
      if (width < 1) {
         width = hint.length() * 10;
         if (width < 40) {
            width = 40;
         }
      }
      if (height < 5) {
         height = 5;
      }
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_HINT.getValue());
      mplew.writeMapleAsciiString(hint);
      mplew.writeShort(width);
      mplew.writeShort(height);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] showForcedEquip(int team) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FORCED_MAP_EQUIP.getValue());
      if (team > -1) {
         mplew.write(team);   // 00 = red, 01 = blue
      }
      return mplew.getPacket();
   }

   public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SUMMON_SKILL.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(summonSkillId);
      mplew.write(newStance);
      return mplew.getPacket();
   }

   public static byte[] skillCooldown(int sid, int time) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.COOLDOWN.getValue());
      mplew.writeInt(sid);
      mplew.writeShort(time);//Int in v97
      return mplew.getPacket();
   }

   public static byte[] skillBookResult(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SKILL_LEARN_ITEM_RESULT.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(1);
      mplew.writeInt(skillid);
      mplew.writeInt(maxlevel);
      mplew.write(canuse ? 1 : 0);
      mplew.write(success ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] getMacros(SkillMacro[] macros) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MACRO_SYS_DATA_INIT.getValue());
      int count = 0;
      for (int i = 0; i < 5; i++) {
         if (macros[i] != null) {
            count++;
         }
      }
      mplew.write(count);
      for (int i = 0; i < 5; i++) {
         SkillMacro macro = macros[i];
         if (macro != null) {
            mplew.writeMapleAsciiString(macro.name());
            mplew.write(macro.shout());
            mplew.writeInt(macro.skill1());
            mplew.writeInt(macro.skill2());
            mplew.writeInt(macro.skill3());
         }
      }
      return mplew.getPacket();
   }

   public static byte[] updateMount(int charid, MapleMount mount, boolean levelup) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_TAMING_MOB_INFO.getValue());
      mplew.writeInt(charid);
      mplew.writeInt(mount.getLevel());
      mplew.writeInt(mount.getExp());
      mplew.writeInt(mount.getTiredness());
      mplew.write(levelup ? (byte) 1 : (byte) 0);
      return mplew.getPacket();
   }

   public static byte[] crogBoatPacket(boolean type) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CONTI_MOVE.getValue());
      mplew.write(10);
      mplew.write(type ? 4 : 5);
      return mplew.getPacket();
   }

   public static byte[] boatPacket(boolean type) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CONTI_STATE.getValue());
      mplew.write(type ? 1 : 2);
      mplew.write(0);
      return mplew.getPacket();
   }

   // RPS_GAME packets thanks to Arnah (Vertisy)
   public static byte[] openRPSNPC() {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(8);// open npc
      mplew.writeInt(9000019);
      return mplew.getPacket();
   }

   public static byte[] rpsMesoError(int mesos) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(0x06);
      if (mesos != -1) {
         mplew.writeInt(mesos);
      }
      return mplew.getPacket();
   }

   public static byte[] rpsSelection(byte selection, byte answer) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(0x0B);// 11l
      mplew.write(selection);
      mplew.write(answer);
      return mplew.getPacket();
   }

   public static byte[] rpsMode(byte mode) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.RPS_GAME.getValue());
      mplew.write(mode);
      return mplew.getPacket();
   }

   public static byte[] getPlayerNPC(MaplePlayerNPC npc) {     // thanks to Arnah
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.IMITATED_NPC_DATA.getValue());
      mplew.write(0x01);
      mplew.writeInt(npc.getScriptId());
      mplew.writeMapleAsciiString(npc.getName());
      mplew.write(npc.getGender());
      mplew.write(npc.getSkin());
      mplew.writeInt(npc.getFace());
      mplew.write(0);
      mplew.writeInt(npc.getHair());
      Map<Short, Integer> equip = npc.getEquips();
      Map<Short, Integer> myEquip = new LinkedHashMap<>();
      Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
      for (short position : equip.keySet()) {
         short pos = (byte) (position * -1);
         if (pos < 100 && myEquip.get(pos) == null) {
            myEquip.put(pos, equip.get(position));
         } else if ((pos > 100 && pos != 111) || pos == -128) { // don't ask. o.o
            pos -= 100;
            if (myEquip.get(pos) != null) {
               maskedEquip.put(pos, myEquip.get(pos));
            }
            myEquip.put(pos, equip.get(position));
         } else if (myEquip.get(pos) != null) {
            maskedEquip.put(pos, equip.get(position));
         }
      }
      for (Entry<Short, Integer> entry : myEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      for (Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      Integer cWeapon = equip.get((byte) -111);
      mplew.writeInt(Objects.requireNonNullElse(cWeapon, 0));
      for (int i = 0; i < 3; i++) {
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   public static byte[] removePlayerNPC(int oid) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.IMITATED_NPC_DATA.getValue());
      mplew.write(0x00);
      mplew.writeInt(oid);

      return mplew.getPacket();
   }

   public static byte[] noteSendMsg() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.MEMO_RESULT.getValue());
      mplew.write(4);
      return mplew.getPacket();
   }

   /*
    *  0 = Player online, use whisper
    *  1 = Check player's name
    *  2 = Receiver inbox full
    */
   public static byte[] noteError(byte error) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.MEMO_RESULT.getValue());
      mplew.write(5);
      mplew.write(error);
      return mplew.getPacket();
   }

   public static byte[] showNotes(List<NoteData> notes, int count) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MEMO_RESULT.getValue());
      mplew.write(3);
      mplew.write(count);
      notes.forEach(note -> {
         mplew.writeInt(note.id());
         mplew.writeMapleAsciiString(note.from() + " "); //Stupid nexon forgot space lol
         mplew.writeMapleAsciiString(note.message());
         mplew.writeLong(getTime(note.timestamp()));
         mplew.write(note.fame()); //FAME :D
      });
      return mplew.getPacket();
   }

   public static byte[] useChalkboard(MapleCharacter chr, boolean close) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHALKBOARD.getValue());
      mplew.writeInt(chr.getId());
      if (close) {
         mplew.write(0);
      } else {
         mplew.write(1);
         mplew.writeMapleAsciiString(chr.getChalkboard());
      }
      return mplew.getPacket();
   }

   public static byte[] trockRefreshMapList(MapleCharacter chr, boolean delete, boolean vip) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAP_TRANSFER_RESULT.getValue());
      mplew.write(delete ? 2 : 3);
      if (vip) {
         mplew.write(1);
         List<Integer> map = chr.getVipTrockMaps();
         for (int i = 0; i < 10; i++) {
            mplew.writeInt(map.get(i));
         }
      } else {
         mplew.write(0);
         List<Integer> map = chr.getTrockMaps();
         for (int i = 0; i < 5; i++) {
            mplew.writeInt(map.get(i));
         }
      }
      return mplew.getPacket();
   }

   /*  1: cannot find char info,
            2: cannot transfer under 20,
            3: cannot send banned,
            4: cannot send married,
            5: cannot send guild leader,
            6: cannot send if account already requested transfer,
            7: cannot transfer within 30days,
            8: must quit family,
            9: unknown error
        */
   public static byte[] sendWorldTransferRules(int error, MapleClient c) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_TRANSFER_WORLD_POSSIBLE_RESULT.getValue());
      mplew.writeInt(0); //ignored
      mplew.write(error);
      mplew.writeInt(0);
      mplew.writeBool(error == 0); //0 = ?, otherwise list servers
      if (error == 0) {
         List<World> worlds = Server.getInstance().getWorlds();
         mplew.writeInt(worlds.size());
         for (World world : worlds) {
            mplew.writeMapleAsciiString(GameConstants.WORLD_NAMES[world.getId()]);
         }
      }
      return mplew.getPacket();
   }

   /*  1: name change already submitted
            2: name change within a month
            3: recently banned
            4: unknown error
        */
   public static byte[] sendNameTransferRules(int error) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT.getValue());
      mplew.writeInt(0);
      mplew.write(error);
      mplew.writeInt(0);

      return mplew.getPacket();
   }


   public static byte[] sendNameTransferCheck(String availableName, boolean canUseName) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CASHSHOP_CHECK_NAME_CHANGE.getValue());
      //Send provided name back to client to add to temporary cache of checked & accepted names
      mplew.writeMapleAsciiString(availableName);
      mplew.writeBool(!canUseName);
      return mplew.getPacket();
   }

   public static byte[] showNameChangeCancel(boolean success) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_NAME_CHANGE_RESULT.getValue());
      mplew.writeBool(success);
      if (!success) {
         mplew.write(0);
      }
      //mplew.writeMapleAsciiString("Custom message."); //only if ^ != 0
      return mplew.getPacket();
   }

   public static byte[] showWorldTransferCancel(boolean success) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_TRANSFER_WORLD_RESULT.getValue());
      mplew.writeBool(success);
      if (!success) {
         mplew.write(0);
      }
      //mplew.writeMapleAsciiString("Custom message."); //only if ^ != 0
      return mplew.getPacket();
   }

   public static byte[] showCash(MapleCharacter mc) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.QUERY_CASH_RESULT.getValue());

      mplew.writeInt(mc.getCashShop().getCash(1));
      mplew.writeInt(mc.getCashShop().getCash(2));
      mplew.writeInt(mc.getCashShop().getCash(4));

      return mplew.getPacket();
   }

   public static byte[] enableCSUse(MapleCharacter mc) {
      return showCash(mc);
   }

   public static byte[] sendAutoHpPot(int itemId) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.AUTO_HP_POT.getValue());
      mplew.writeInt(itemId);
      return mplew.getPacket();
   }

   public static byte[] sendAutoMpPot(int itemId) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.AUTO_MP_POT.getValue());
      mplew.writeInt(itemId);
      return mplew.getPacket();
   }

   public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.OX_QUIZ.getValue());
      mplew.write(askQuestion ? 1 : 0);
      mplew.write(questionSet);
      mplew.writeShort(questionId);
      return mplew.getPacket();
   }

   public static byte[] updateGender(MapleCharacter chr) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SET_GENDER.getValue());
      mplew.write(chr.getGender());
      return mplew.getPacket();
   }

   public static byte[] enableReport() { // thanks to snow
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CLAIM_STATUS_CHANGED.getValue());
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] aranGodlyStats() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FORCED_STAT_SET.getValue());
      mplew.write(new byte[]{(byte) 0x1F, (byte) 0x0F, 0, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xFF, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0x78, (byte) 0x8C});
      return mplew.getPacket();
   }

   /**
    * Sends a UI utility. 0x01 - Equipment Inventory. 0x02 - Stat Window. 0x03
    * - Skill Window. 0x05 - Keyboard Settings. 0x06 - Quest window. 0x09 -
    * Monsterbook Window. 0x0A - Char Info 0x0B - Guild BBS 0x12 - Monster
    * Carnival Window 0x16 - Party Search. 0x17 - Item Creation Window. 0x1A -
    * My Ranking O.O 0x1B - Family Window 0x1C - Family Pedigree 0x1D - GM
    * Story Board /funny shet 0x1E - Envelop saying you got mail from an admin.
    * lmfao 0x1F - Medal Window 0x20 - Maple Event (???) 0x21 - Invalid Pointer
    * Crash
    *
    * @param ui
    * @return
    */
   public static byte[] openUI(byte ui) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.OPEN_UI.getValue());
      mplew.write(ui);
      return mplew.getPacket();
   }

   public static byte[] lockUI(boolean enable) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.LOCK_UI.getValue());
      mplew.write(enable ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] disableUI(boolean enable) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DISABLE_UI.getValue());
      mplew.write(enable ? 1 : 0);
      return mplew.getPacket();
   }

   /**
    * Sends a report response
    * <p>
    * Possible values for <code>mode</code>:<br> 0: You have succesfully
    * reported the user.<br> 1: Unable to locate the user.<br> 2: You may only
    * report users 10 times a day.<br> 3: You have been reported to the GM's by
    * a user.<br> 4: Your request did not go through for unknown reasons.
    * Please try again later.<br>
    *
    * @param mode The mode
    * @return Report Reponse packet
    */
   public static byte[] reportResponse(byte mode) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SUE_CHARACTER_RESULT.getValue());
      mplew.write(mode);
      return mplew.getPacket();
   }

   /**
    * Gets a gm effect packet (ie. hide, banned, etc.)
    * <p>
    * Possible values for <code>type</code>:<br> 0x04: You have successfully
    * blocked access.<br>
    * 0x05: The unblocking has been successful.<br> 0x06 with Mode 0: You have
    * successfully removed the name from the ranks.<br> 0x06 with Mode 1: You
    * have entered an invalid character name.<br> 0x10: GM Hide, mode
    * determines whether or not it is on.<br> 0x1E: Mode 0: Failed to send
    * warning Mode 1: Sent warning<br> 0x13 with Mode 0: + mapid 0x13 with Mode
    * 1: + ch (FF = Unable to find merchant)
    *
    * @param type The type
    * @param mode The mode
    * @return The gm effect packet
    */
   public static byte[] getGMEffect(int type, byte mode) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADMIN_RESULT.getValue());
      mplew.write(type);
      mplew.write(mode);
      return mplew.getPacket();
   }

   public static byte[] findMerchantResponse(boolean map, int extra) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADMIN_RESULT.getValue());
      mplew.write(0x13);
      mplew.write(map ? 0 : 1); //00 = mapid, 01 = ch
      if (map) {
         mplew.writeInt(extra);
      } else {
         mplew.write(extra); //-1 = unable to find
      }
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] disableMinimap() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADMIN_RESULT.getValue());
      mplew.writeShort(0x1C);
      return mplew.getPacket();
   }

   public static byte[] sendMesoLimit() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TRADE_MONEY_LIMIT.getValue()); //Players under level 15 can only trade 1m per day
      return mplew.getPacket();
   }

   /**
    * Gets a "block" packet (ie. the cash shop is unavailable, etc)
    * <p>
    * Possible values for <code>type</code>:<br> 1: The portal is closed for
    * now.<br> 2: You cannot go to that place.<br> 3: Unable to approach due to
    * the force of the ground.<br> 4: You cannot teleport to or on this
    * map.<br> 5: Unable to approach due to the force of the ground.<br> 6:
    * This map can only be entered by party members.<br> 7: The Cash Shop is
    * currently not available. Stay tuned...<br>
    *
    * @param type The type
    * @return The "block" packet.
    */
   public static byte[] blockedMessage(int type) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOCKED_MAP.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   /**
    * Gets a "block" packet (ie. the cash shop is unavailable, etc)
    * <p>
    * Possible values for <code>type</code>:<br> 1: You cannot move that
    * channel. Please try again later.<br> 2: You cannot go into the cash shop.
    * Please try again later.<br> 3: The Item-Trading Shop is currently
    * unavailable. Please try again later.<br> 4: You cannot go into the trade
    * shop, due to limitation of user count.<br> 5: You do not meet the minimum
    * level requirement to access the Trade Shop.<br>
    *
    * @param type The type
    * @return The "block" packet.
    */
   public static byte[] blockedMessage2(int type) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOCKED_SERVER.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] getEnergy(String info, int amount) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SESSION_VALUE.getValue());
      mplew.writeMapleAsciiString(info);
      mplew.writeMapleAsciiString(Integer.toString(amount));
      return mplew.getPacket();
   }

   public static byte[] dojoWarpUp() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DOJO_WARP_UP.getValue());
      mplew.write(0);
      mplew.write(6);
      return mplew.getPacket();
   }

   public static byte[] MobDamageMobFriendly(MapleMonster mob, int damage, int remainingHp) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_MONSTER.getValue());
      mplew.writeInt(mob.getObjectId());
      mplew.write(1); // direction ?
      mplew.writeInt(damage);
      mplew.writeInt(remainingHp);
      mplew.writeInt(mob.getMaxHp());
      return mplew.getPacket();
   }

   public static byte[] finishedSort(int inv) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.GATHER_ITEM_RESULT.getValue());
      mplew.write(0);
      mplew.write(inv);
      return mplew.getPacket();
   }

   public static byte[] finishedSort2(int inv) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.SORT_ITEM_RESULT.getValue());
      mplew.write(0);
      mplew.write(inv);
      return mplew.getPacket();
   }

   public static byte[] hpqMessage(String text) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOW_WEATHER.getValue()); // not 100% sure
      mplew.write(0);
      mplew.writeInt(5120016);
      mplew.writeAsciiString(text);
      return mplew.getPacket();
   }

   public static byte[] showEventInstructions() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GMEVENT_INSTRUCTIONS.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] leftKnockBack() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.LEFT_KNOCK_BACK.getValue());
      return mplew.getPacket();
   }

   public static byte[] customPacket(String packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.write(HexTool.getByteArrayFromHexString(packet));
      return mplew.getPacket();
   }

   public static byte[] customPacket(byte[] packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(packet.length);
      mplew.write(packet);
      return mplew.getPacket();
   }

   public static byte[] talkGuide(String talk) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TALK_GUIDE.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString(talk);
      mplew.write(new byte[]{(byte) 0xC8, 0, 0, 0, (byte) 0xA0, (byte) 0x0F, 0, 0});
      return mplew.getPacket();
   }

   public static byte[] guideHint(int hint) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.TALK_GUIDE.getValue());
      mplew.write(1);
      mplew.writeInt(hint);
      mplew.writeInt(7000);
      return mplew.getPacket();
   }

   public static byte[] resetForcedStats() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.FORCED_STAT_RESET.getValue());
      return mplew.getPacket();
   }

   public static byte[] showCombo(int count) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.SHOW_COMBO.getValue());
      mplew.writeInt(count);
      return mplew.getPacket();
   }

   public static byte[] earnTitleMessage(String msg) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SCRIPT_PROGRESS_MESSAGE.getValue());
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] sheepRanchInfo(byte wolf, byte sheep) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHEEP_RANCH_INFO.getValue());
      mplew.write(wolf);
      mplew.write(sheep);
      return mplew.getPacket();
   }
   //Know what this is? ?? >=)

   public static byte[] sheepRanchClothes(int id, byte clothes) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHEEP_RANCH_CLOTHES.getValue());
      mplew.writeInt(id); //Character id
      mplew.write(clothes); //0 = sheep, 1 = wolf, 2 = Spectator (wolf without wool)
      return mplew.getPacket();
   }

   public static byte[] incubatorResult() {//lol
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(SendOpcode.INCUBATOR_RESULT.getValue());
      mplew.skip(6);
      return mplew.getPacket();
   }

   public static byte[] pyramidGauge(int gauge) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.PYRAMID_GAUGE.getValue());
      mplew.writeInt(gauge);
      return mplew.getPacket();
   }
   // f2

   public static byte[] pyramidScore(byte score, int exp) {//Type cannot be higher than 4 (Rank D), otherwise you'll crash
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.PYRAMID_SCORE.getValue());
      mplew.write(score);
      mplew.writeInt(exp);
      return mplew.getPacket();
   }

   /**
    * Changes the current background effect to either being rendered or not.
    * Data is still missing, so this is pretty binary at the moment in how it
    * behaves.
    *
    * @param remove     whether or not the remove or add the specified layer.
    * @param layer      the targeted layer for removal or addition.
    * @param transition the time it takes to transition the effect.
    * @return a packet to change the background effect of a specified layer.
    */
   public static byte[] changeBackgroundEffect(boolean remove, int layer, int transition) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_BACK_EFFECT.getValue());
      mplew.writeBool(remove);
      mplew.writeInt(0); // not sure what this int32 does yet
      mplew.write(layer);
      mplew.writeInt(transition);
      return mplew.getPacket();
   }

   public static byte[] setNPCScriptable(Set<Pair<Integer, String>> scriptNpcDescriptions) {  // thanks to GabrielSin
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_NPC_SCRIPTABLE.getValue());
      mplew.write(scriptNpcDescriptions.size());
      for (Pair<Integer, String> p : scriptNpcDescriptions) {
         mplew.writeInt(p.getLeft());
         mplew.writeMapleAsciiString(p.getRight());
         mplew.writeInt(0); // start time
         mplew.writeInt(Integer.MAX_VALUE); // end time
      }
      return mplew.getPacket();
   }

   private static byte[] MassacreResult(byte nRank, int nIncExp) {
      //CField_MassacreResult__OnMassacreResult @ 0x005617C5
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PYRAMID_SCORE.getValue()); //MASSACRERESULT | 0x009E
      mplew.write(nRank); //(0 - S) (1 - A) (2 - B) (3 - C) (4 - D) ( Else - Crash )
      mplew.writeInt(nIncExp);
      return mplew.getPacket();
   }

   private static byte[] GuildBoss_HealerMove(short nY) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BOSS_HEALER_MOVE.getValue());
      mplew.writeShort(nY); //New Y Position
      return mplew.getPacket();
   }


   private static byte[] GuildBoss_PulleyStateChange(byte nState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BOSS_PULLEY_STATE_CHANGE.getValue());
      mplew.write(nState);
      return mplew.getPacket();
   }

   private static byte[] Tournament__Tournament(byte nState, byte nSubState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT.getValue());
      mplew.write(nState);
      mplew.write(nSubState);
      return mplew.getPacket();
   }

   private static byte[] Tournament__MatchTable(byte nState, byte nSubState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_MATCH_TABLE.getValue()); //Prompts CMatchTableDlg Modal
      return mplew.getPacket();
   }

   private static byte[] Tournament__SetPrize(byte bSetPrize, byte bHasPrize, int nItemID1, int nItemID2) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_SET_PRIZE.getValue());

      //0 = "You have failed the set the prize. Please check the item number again."
      //1 = "You have successfully set the prize."
      mplew.write(bSetPrize);

      mplew.write(bHasPrize);

      if (bHasPrize != 0) {
         mplew.writeInt(nItemID1);
         mplew.writeInt(nItemID2);
      }

      return mplew.getPacket();
   }

   private static byte[] Tournament__UEW(byte nState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_UEW.getValue());

      //Is this a bitflag o.o ?
      //2 = "You have reached the finals by default."
      //4 = "You have reached the semifinals by default."
      //8 or 16 = "You have reached the round of %n by default." | Encodes nState as %n ?!
      mplew.write(nState);

      return mplew.getPacket();
   }
}