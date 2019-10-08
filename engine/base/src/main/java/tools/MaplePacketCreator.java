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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleMount;
import client.database.data.NoteData;
import constants.GameConstants;
import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.world.World;
import server.life.MaplePlayerNPC;
import server.maps.MapleMapItem;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.statusinfo.ShowItemGain;

/**
 * @author Frz
 */
public class MaplePacketCreator {
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

   public static byte[] skillCancel(MapleCharacter from, int skillId) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_SKILL_EFFECT.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(skillId);
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

   public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.OX_QUIZ.getValue());
      mplew.write(askQuestion ? 1 : 0);
      mplew.write(questionSet);
      mplew.writeShort(questionId);
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

   public static byte[] leftKnockBack() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.LEFT_KNOCK_BACK.getValue());
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

   public static byte[] earnTitleMessage(String msg) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SCRIPT_PROGRESS_MESSAGE.getValue());
      mplew.writeMapleAsciiString(msg);
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
}