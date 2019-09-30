package tools.packet.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.ExpTable;
import constants.GameConstants;
import constants.ItemConstants;
import server.MapleItemInformationProvider;
import tools.PacketFactory;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;

public abstract class AbstractPacketFactory implements PacketFactory {
   protected final long ZERO_TIME = 94354848000000000L;//00 40 E0 FD 3B 37 4F 01
   protected final long FT_UT_OFFSET = 116444736010800000L + (10000L * TimeZone.getDefault().getOffset(System.currentTimeMillis())); // normalize with timezone offset suggested by Ari
   protected final long DEFAULT_TIME = 150842304000000000L;//00 80 05 BB 46 E6 17 02
   protected final long PERMANENT = 150841440000000000L; // 00 C0 9B 90 7D E5 17 02

   public void announce(MapleClient client, PacketInput packetInput) {
      client.announce(create(packetInput));
   }

   public <T extends PacketInput> byte[] create(Function<T, byte[]> creator, PacketInput packetInput) {
      return creator.apply((T) packetInput);
   }

   protected MaplePacketLittleEndianWriter newWriter(int size) {
      return new MaplePacketLittleEndianWriter(size);
   }

   protected long getTime(long utcTimestamp) {
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

   protected void addCharEntry(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean viewall) {
      addCharStats(mplew, chr);
      addCharLook(mplew, chr, false);
      if (!viewall) {
         mplew.write(0);
      }
      if (chr.isGM() || chr.isGmJob()) {  // thanks Egg Daddy (Ubaware), resinate for noticing GM jobs crashing on non-GM players account
         mplew.write(0);
         return;
      }
      mplew.write(1); // world rank enabled (next 4 ints are not sent if disabled) Short??
      mplew.writeInt(chr.getRank()); // world rank
      mplew.writeInt(chr.getRankMove()); // move (negative is downwards)
      mplew.writeInt(chr.getJobRank()); // job rank
      mplew.writeInt(chr.getJobRankMove()); // move (negative is downwards)
   }

   protected void addCharStats(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeInt(chr.getId()); // character id
      mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getName(), '\0', 13));
      mplew.write(chr.getGender()); // gender (0 = male, 1 = female)
      mplew.write(chr.getSkinColor().getId()); // skin color
      mplew.writeInt(chr.getFace()); // face
      mplew.writeInt(chr.getHair()); // hair

      for (int i = 0; i < 3; i++) {
         MaplePet pet = chr.getPet(i);
         if (pet != null) //Checked GMS.. and your pets stay when going into the cash shop.
         {
            mplew.writeLong(pet.uniqueId());
         } else {
            mplew.writeLong(0);
         }
      }

      mplew.write(chr.getLevel()); // level
      mplew.writeShort(chr.getJob().getId()); // job
      mplew.writeShort(chr.getStr()); // str
      mplew.writeShort(chr.getDex()); // dex
      mplew.writeShort(chr.getInt()); // int
      mplew.writeShort(chr.getLuk()); // luk
      mplew.writeShort(chr.getHp()); // hp (?)
      mplew.writeShort(chr.getClientMaxHp()); // maxhp
      mplew.writeShort(chr.getMp()); // mp (?)
      mplew.writeShort(chr.getClientMaxMp()); // maxmp
      mplew.writeShort(chr.getRemainingAp()); // remaining ap
      if (GameConstants.hasSPTable(chr.getJob())) {
         addRemainingSkillInfo(mplew, chr);
      } else {
         mplew.writeShort(chr.getRemainingSp()); // remaining sp
      }
      mplew.writeInt(chr.getExp()); // current exp
      mplew.writeShort(chr.getFame()); // fame
      mplew.writeInt(chr.getGachaExp()); //Gacha Exp
      mplew.writeInt(chr.getMapId()); // current map id
      mplew.write(chr.getInitialSpawnPoint()); // spawnpoint
      mplew.writeInt(0);
   }

   protected void addCharLook(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega) {
      mplew.write(chr.getGender());
      mplew.write(chr.getSkinColor().getId()); // skin color
      mplew.writeInt(chr.getFace()); // face
      mplew.write(mega ? 0 : 1);
      mplew.writeInt(chr.getHair()); // hair
      addCharEquips(mplew, chr);
   }

   protected void addRemainingSkillInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      int[] remainingSp = chr.getRemainingSps();
      int effectiveLength = 0;
      for (int value : remainingSp) {
         if (value > 0) {
            effectiveLength++;
         }
      }

      mplew.write(effectiveLength);
      for (int i = 0; i < remainingSp.length; i++) {
         if (remainingSp[i] > 0) {
            mplew.write(i + 1);
            mplew.write(remainingSp[i]);
         }
      }
   }

   protected void addCharEquips(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
      Collection<Item> ii = MapleItemInformationProvider.getInstance().canWearEquipment(chr, equip.list());
      Map<Short, Integer> myEquip = new LinkedHashMap<>();
      Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
      for (Item item : ii) {
         short pos = (byte) (item.position() * -1);
         if (pos < 100 && myEquip.get(pos) == null) {
            myEquip.put(pos, item.id());
         } else if (pos > 100 && pos != 111) { // don't ask. o.o
            pos -= 100;
            if (myEquip.get(pos) != null) {
               maskedEquip.put(pos, myEquip.get(pos));
            }
            myEquip.put(pos, item.id());
         } else if (myEquip.get(pos) != null) {
            maskedEquip.put(pos, item.id());
         }
      }
      for (Map.Entry<Short, Integer> entry : myEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      for (Map.Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         mplew.write(entry.getKey());
         mplew.writeInt(entry.getValue());
      }
      mplew.write(0xFF);
      Item cWeapon = equip.getItem((short) -111);
      mplew.writeInt(cWeapon != null ? cWeapon.id() : 0);
      for (int i = 0; i < 3; i++) {
         if (chr.getPet(i) != null) {
            mplew.writeInt(chr.getPet(i).id());
         } else {
            mplew.writeInt(0);
         }
      }
   }

   protected void addExpirationTime(final MaplePacketLittleEndianWriter mplew, long time) {
      mplew.writeLong(getTime(time)); // offset expiration time issue found thanks to Thora
   }


   protected void addItemInfo(final MaplePacketLittleEndianWriter mplew, Item item, boolean zeroPosition) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      boolean isCash = ii.isCash(item.id());
      boolean isPet = item.petId() > -1;
      boolean isRing = false;
      Equip equip = null;
      short pos = item.position();
      byte itemType = item.itemType();
      if (itemType == 1) {
         equip = (Equip) item;
         isRing = equip.ringId() > -1;
      }
      if (!zeroPosition) {
         if (equip != null) {
            if (pos < 0) {
               pos *= -1;
            }
            mplew.writeShort(pos > 100 ? pos - 100 : pos);
         } else {
            mplew.write(pos);
         }
      }
      mplew.write(itemType);
      mplew.writeInt(item.id());
      mplew.writeBool(isCash);
      if (isCash) {
         mplew.writeLong(isPet ? item.petId() : isRing ? equip.ringId() : item.cashId());
      }
      addExpirationTime(mplew, item.expiration());
      if (isPet) {
         MaplePet pet = item.pet().get();
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(pet.name(), '\0', 13));
         mplew.write(pet.level());
         mplew.writeShort(pet.closeness());
         mplew.write(pet.fullness());
         addExpirationTime(mplew, item.expiration());
         mplew.writeInt(pet.petFlag());  /* pet flags found by -- lrenex & Spoon */

         mplew.write(new byte[]{(byte) 0x50, (byte) 0x46}); //wonder what this is
         mplew.writeInt(0);
         return;
      }
      if (equip == null) {
         mplew.writeShort(item.quantity());
         mplew.writeMapleAsciiString(item.owner());
         mplew.writeShort(item.flag()); // flag

         if (ItemConstants.isRechargeable(item.id())) {
            mplew.writeInt(2);
            mplew.write(new byte[]{(byte) 0x54, 0, 0, (byte) 0x34});
         }
         return;
      }
      mplew.write(equip.slots()); // upgrade slots
      mplew.write(equip.level()); // level
      mplew.writeShort(equip.str()); // str
      mplew.writeShort(equip.dex()); // dex
      mplew.writeShort(equip._int()); // int
      mplew.writeShort(equip.luk()); // luk
      mplew.writeShort(equip.hp()); // hp
      mplew.writeShort(equip.mp()); // mp
      mplew.writeShort(equip.watk()); // watk
      mplew.writeShort(equip.matk()); // matk
      mplew.writeShort(equip.wdef()); // wdef
      mplew.writeShort(equip.mdef()); // mdef
      mplew.writeShort(equip.acc()); // accuracy
      mplew.writeShort(equip.avoid()); // avoid
      mplew.writeShort(equip.hands()); // hands
      mplew.writeShort(equip.speed()); // speed
      mplew.writeShort(equip.jump()); // jump
      mplew.writeMapleAsciiString(equip.owner()); // owner name
      mplew.writeShort(equip.flag()); //Item Flags

      if (isCash) {
         for (int i = 0; i < 10; i++) {
            mplew.write(0x40);
         }
      } else {
         int itemLevel = equip.itemLevel();

         long expNibble = (long) (ExpTable.getExpNeededForLevel(ii.getEquipLevelReq(item.id())) * equip.itemExp());
         expNibble /= ExpTable.getEquipExpNeededForLevel(itemLevel);

         mplew.write(0);
         mplew.write(itemLevel); //Item Level
         mplew.writeInt((int) expNibble);
         mplew.writeInt(equip.vicious()); //WTF NEXON ARE YOU SERIOUS?
         mplew.writeLong(0);
      }
      mplew.writeLong(getTime(-2));
      mplew.writeInt(-1);

   }
}
