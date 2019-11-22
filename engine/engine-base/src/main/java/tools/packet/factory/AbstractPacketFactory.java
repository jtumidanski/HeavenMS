package tools.packet.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.Ring;
import client.Skill;
import client.SkillEntry;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.newyear.NewYearCardRecord;
import client.processor.CharacterProcessor;
import constants.game.ExpTable;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import net.server.PlayerCoolDownValueHolder;
import server.MapleItemInformationProvider;
import server.maps.MapleMiniGame;
import tools.FilePrinter;
import tools.PacketFactory;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;

public abstract class AbstractPacketFactory implements PacketFactory {
   protected final long ZERO_TIME = 94354848000000000L;//00 40 E0 FD 3B 37 4F 01
   protected final long FT_UT_OFFSET = 116444736010800000L + (10000L * TimeZone.getDefault().getOffset(System.currentTimeMillis())); // normalize with timezone offset suggested by Ari
   protected final long DEFAULT_TIME = 150842304000000000L;//00 80 05 BB 46 E6 17 02
   protected final long PERMANENT = 150841440000000000L; // 00 C0 9B 90 7D E5 17 02

   protected HandlerRegistry registry = new HandlerRegistry();

   @Override
   public byte[] create(PacketInput packetInput) {
      Optional<Function<PacketInput, byte[]>> handler = registry.getHandler(packetInput.getClass());
      if (handler.isPresent()) {
         return handler.get().apply(packetInput);
      }

      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   public void announce(MapleClient client, PacketInput packetInput) {
      client.announce(create(packetInput));
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

   protected void addItemInfo(final MaplePacketLittleEndianWriter mplew, Item item) {
      addItemInfo(mplew, item, false);
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

   protected void addAnnounceBox(final MaplePacketLittleEndianWriter mplew, MapleMiniGame game, int ammount, int joinable) {
      mplew.write(game.getGameType().getValue());
      mplew.writeInt(game.objectId()); // gameid/shopid
      mplew.writeMapleAsciiString(game.getDescription()); // desc
      mplew.writeBool(!game.getPassword().isEmpty());    // password here, thanks GabrielSin!
      mplew.write(game.getPieceType());
      mplew.write(ammount);
      mplew.write(2);         //player capacity
      mplew.write(joinable);
   }

   protected void addRingLook(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean crush) {
      List<Ring> rings;
      if (crush) {
         rings = chr.getCrushRings();
      } else {
         rings = chr.getFriendshipRings();
      }
      boolean yes = false;
      for (Ring ring : rings) {
         if (ring.isEquipped()) {
            if (!yes) {
               yes = true;
               mplew.write(1);
            }
            mplew.writeInt(ring.ringId());
            mplew.writeInt(0);
            mplew.writeInt(ring.partnerRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.itemId());
         }
      }
      if (!yes) {
         mplew.write(0);
      }
   }

   protected void addMarriageRingLook(MapleClient target, final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      Ring ring = chr.getMarriageRing();

      if (ring == null || !ring.isEquipped()) {
         mplew.write(0);
      } else {
         mplew.write(1);

         MapleCharacter targetChr = target.getPlayer();
         if (targetChr != null && targetChr.getPartnerId() == chr.getId()) {
            mplew.writeInt(0);
            mplew.writeInt(0);
         } else {
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.partnerId());
         }

         mplew.writeInt(ring.itemId());
      }
   }

   protected void addCharacterInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeLong(-1);
      mplew.write(0);
      addCharStats(mplew, chr);
      mplew.write(chr.getBuddylist().capacity());

      if (chr.getLinkedName() == null) {
         mplew.write(0);
      } else {
         mplew.write(1);
         mplew.writeMapleAsciiString(chr.getLinkedName());
      }

      mplew.writeInt(chr.getMeso());
      addInventoryInfo(mplew, chr);
      addSkillInfo(mplew, chr);
      addQuestInfo(mplew, chr);
      addMiniGameInfo(mplew, chr);
      addRingInfo(mplew, chr);
      addTeleportInfo(mplew, chr);
      addMonsterBookInfo(mplew, chr);
      addNewYearInfo(mplew, chr);
      addAreaInfo(mplew, chr);//assuming it stayed here xd
      mplew.writeShort(0);
   }

   protected void addInventoryInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      for (byte i = 1; i <= 5; i++) {
         mplew.write(chr.getInventory(MapleInventoryType.getByType(i)).getSlotLimit());
      }
      mplew.writeLong(getTime(-2));
      MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
      Collection<Item> equippedC = iv.list();
      List<Item> equipped = new ArrayList<>(equippedC.size());
      List<Item> equippedCash = new ArrayList<>(equippedC.size());
      for (Item item : equippedC) {
         if (item.position() <= -100) {
            equippedCash.add(item);
         } else {
            equipped.add(item);
         }
      }
      for (Item item : equipped) {    // equipped doesn't actually need sorting, thanks Pllsz
         addItemInfo(mplew, item);
      }
      mplew.writeShort(0); // start of equip cash
      for (Item item : equippedCash) {
         addItemInfo(mplew, item);
      }
      mplew.writeShort(0); // start of equip inventory
      for (Item item : chr.getInventory(MapleInventoryType.EQUIP).list()) {
         addItemInfo(mplew, item);
      }
      mplew.writeInt(0);
      for (Item item : chr.getInventory(MapleInventoryType.USE).list()) {
         addItemInfo(mplew, item);
      }
      mplew.write(0);
      for (Item item : chr.getInventory(MapleInventoryType.SETUP).list()) {
         addItemInfo(mplew, item);
      }
      mplew.write(0);
      for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
         addItemInfo(mplew, item);
      }
      mplew.write(0);
      for (Item item : chr.getInventory(MapleInventoryType.CASH).list()) {
         addItemInfo(mplew, item);
      }
   }

   protected void addSkillInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.write(0); // start of skills
      Map<Skill, SkillEntry> skills = chr.getSkills();
      int skillsSize = skills.size();
      // We don't want to include any hidden skill in this, so subtract them from the size list and ignore them.
      for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         if (GameConstants.isHiddenSkills(skill.getKey().getId())) {
            skillsSize--;
         }
      }
      mplew.writeShort(skillsSize);
      for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         if (GameConstants.isHiddenSkills(skill.getKey().getId())) {
            continue;
         }
         mplew.writeInt(skill.getKey().getId());
         mplew.writeInt(skill.getValue().skillLevel());
         addExpirationTime(mplew, skill.getValue().expiration());
         if (skill.getKey().isFourthJob()) {
            mplew.writeInt(skill.getValue().masterLevel());
         }
      }
      mplew.writeShort(chr.getAllCooldowns().size());
      for (PlayerCoolDownValueHolder cooling : chr.getAllCooldowns()) {
         mplew.writeInt(cooling.skillId);
         int timeLeft = (int) (cooling.length + cooling.startTime - System.currentTimeMillis());
         mplew.writeShort(timeLeft / 1000);
      }
   }

   protected void addQuestInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      List<MapleQuestStatus> started = chr.getStartedQuests();
      int startedSize = 0;
      for (MapleQuestStatus qs : started) {
         if (qs.getInfoNumber() > 0) {
            startedSize++;
         }
         startedSize++;
      }
      mplew.writeShort(startedSize);
      for (MapleQuestStatus qs : started) {
         mplew.writeShort(qs.getQuest().getId());
         mplew.writeMapleAsciiString(qs.getProgressData());

         short infoNumber = qs.getInfoNumber();
         if (infoNumber > 0) {
            MapleQuestStatus iqs = chr.getQuest(infoNumber);
            mplew.writeShort(infoNumber);
            mplew.writeMapleAsciiString(iqs.getProgressData());
         }
      }
      List<MapleQuestStatus> completed = chr.getCompletedQuests();
      mplew.writeShort(completed.size());
      for (MapleQuestStatus qs : completed) {
         mplew.writeShort(qs.getQuest().getId());
         mplew.writeLong(getTime(qs.getCompletionTime()));
      }
   }

   protected void addMiniGameInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeShort(0);
                /*for (int m = size; m > 0; m--) {//nexon does this :P
                 mplew.writeInt(0);
                 mplew.writeInt(0);
                 mplew.writeInt(0);
                 mplew.writeInt(0);
                 mplew.writeInt(0);
                 }*/
   }

   protected void addRingInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeShort(chr.getCrushRings().size());
      for (Ring ring : chr.getCrushRings()) {
         mplew.writeInt(ring.partnerId());
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(ring.partnerName(), '\0', 13));
         mplew.writeInt(ring.ringId());
         mplew.writeInt(0);
         mplew.writeInt(ring.partnerRingId());
         mplew.writeInt(0);
      }
      mplew.writeShort(chr.getFriendshipRings().size());
      for (Ring ring : chr.getFriendshipRings()) {
         mplew.writeInt(ring.partnerId());
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(ring.partnerName(), '\0', 13));
         mplew.writeInt(ring.ringId());
         mplew.writeInt(0);
         mplew.writeInt(ring.partnerRingId());
         mplew.writeInt(0);
         mplew.writeInt(ring.itemId());
      }

      if (chr.getPartnerId() > 0) {
         Ring marriageRing = chr.getMarriageRing();

         mplew.writeShort(1);
         mplew.writeInt(chr.getRelationshipId());
         mplew.writeInt(chr.getGender() == 0 ? chr.getId() : chr.getPartnerId());
         mplew.writeInt(chr.getGender() == 0 ? chr.getPartnerId() : chr.getId());
         mplew.writeShort((marriageRing != null) ? 3 : 1);
         if (marriageRing != null) {
            mplew.writeInt(marriageRing.itemId());
            mplew.writeInt(marriageRing.itemId());
         } else {
            mplew.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
            mplew.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
         }
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? chr.getName() : CharacterProcessor.getInstance().getNameById(chr.getPartnerId()), '\0', 13));
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? CharacterProcessor.getInstance().getNameById(chr.getPartnerId()) : chr.getName(), '\0', 13));
      } else {
         mplew.writeShort(0);
      }
   }

   protected void addTeleportInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      final List<Integer> tele = chr.getTrockMaps();
      final List<Integer> viptele = chr.getVipTrockMaps();
      for (int i = 0; i < 5; i++) {
         mplew.writeInt(tele.get(i));
      }
      for (int i = 0; i < 10; i++) {
         mplew.writeInt(viptele.get(i));
      }
   }

   protected void addMonsterBookInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeInt(chr.getMonsterBookCover()); // cover
      mplew.write(0);
      Map<Integer, Integer> cards = chr.getMonsterBook().getCards();
      mplew.writeShort(cards.size());
      for (Map.Entry<Integer, Integer> all : cards.entrySet()) {
         mplew.writeShort(all.getKey() % 10000); // Id
         mplew.write(all.getValue()); // Level
      }
   }

   protected void addNewYearInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      Set<NewYearCardRecord> received = chr.getReceivedNewYearRecords();

      mplew.writeShort(received.size());
      for (NewYearCardRecord nyc : received) {
         encodeNewYearCard(nyc, mplew);
      }
   }

   protected void encodeNewYearCard(NewYearCardRecord newyear, MaplePacketLittleEndianWriter mplew) {
      mplew.writeInt(newyear.id());
      mplew.writeInt(newyear.senderId());
      mplew.writeMapleAsciiString(newyear.senderName());
      mplew.writeBool(newyear.senderDiscardCard());
      mplew.writeLong(newyear.dateSent());
      mplew.writeInt(newyear.receiverId());
      mplew.writeMapleAsciiString(newyear.receiverName());
      mplew.writeBool(newyear.receiverDiscardCard());
      mplew.writeBool(newyear.receiverReceivedCard());
      mplew.writeLong(newyear.dateReceived());
      mplew.writeMapleAsciiString(newyear.message());
   }

   protected void addAreaInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      Map<Short, String> areaInfos = chr.getAreaInfos();
      mplew.writeShort(areaInfos.size());
      for (Short area : areaInfos.keySet()) {
         mplew.writeShort(area);
         mplew.writeMapleAsciiString(areaInfos.get(area));
      }
   }
}
