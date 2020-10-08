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
import server.processor.QuestProcessor;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketFactory;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;

public abstract class AbstractPacketFactory implements PacketFactory {
   protected final long ZERO_TIME = 94354848000000000L;//00 40 E0 FD 3B 37 4F 01
   protected final long FT_UT_OFFSET = 116444736010800000L + (10000L * TimeZone.getDefault().getOffset(System.currentTimeMillis()));
   // normalize with timezone offset suggested by Ari
   protected final long DEFAULT_TIME = 150842304000000000L;//00 80 05 BB 46 E6 17 02
   protected final long PERMANENT = 150841440000000000L; // 00 C0 9B 90 7D E5 17 02

   protected HandlerRegistry registry = new HandlerRegistry();

   @Override
   public byte[] create(PacketInput packetInput) {
      Optional<Function<PacketInput, byte[]>> handler = registry.getHandler(packetInput.getClass());
      if (handler.isPresent()) {
         return handler.get().apply(packetInput);
      }
      LoggerUtil
            .printError(LoggerOriginator.ENGINE, LogType.PACKET_LOG, "Trying to handle invalid input " + packetInput.toString());
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

   protected void addCharEntry(final MaplePacketLittleEndianWriter writer, MapleCharacter character, boolean viewAll) {
      addCharStats(writer, character);
      addCharLook(writer, character, false);
      if (!viewAll) {
         writer.write(0);
      }
      if (character.isGM() || character.isGmJob()) {
         writer.write(0);
         return;
      }
      writer.write(1); // world rank enabled (next 4 ints are not sent if disabled) Short??
      writer.writeInt(character.getRank()); // world rank
      writer.writeInt(character.getRankMove()); // move (negative is downwards)
      writer.writeInt(character.getJobRank()); // job rank
      writer.writeInt(character.getJobRankMove()); // move (negative is downwards)
   }

   protected void addCharStats(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.writeInt(character.getId()); // character id
      writer.writeAsciiString(StringUtil.getRightPaddedStr(character.getName(), '\0', 13));
      writer.write(character.getGender()); // gender (0 = male, 1 = female)
      writer.write(character.getSkinColor().getId()); // skin color
      writer.writeInt(character.getFace()); // face
      writer.writeInt(character.getHair()); // hair

      for (int i = 0; i < 3; i++) {
         MaplePet pet = character.getPet(i);
         //Checked GMS.. and your pets stay when going into the cash shop.
         if (pet != null) {
            writer.writeLong(pet.uniqueId());
         } else {
            writer.writeLong(0);
         }
      }

      writer.write(character.getLevel()); // level
      writer.writeShort(character.getJob().getId()); // job
      writer.writeShort(character.getStr()); // str
      writer.writeShort(character.getDex()); // dex
      writer.writeShort(character.getInt()); // int
      writer.writeShort(character.getLuk()); // luk
      writer.writeShort(character.getHp()); // hp (?)
      writer.writeShort(character.getClientMaxHp()); // max hp
      writer.writeShort(character.getMp()); // mp (?)
      writer.writeShort(character.getClientMaxMp()); // max mp
      writer.writeShort(character.getRemainingAp()); // remaining ap
      if (GameConstants.hasSPTable(character.getJob())) {
         addRemainingSkillInfo(writer, character);
      } else {
         writer.writeShort(character.getRemainingSp()); // remaining sp
      }
      writer.writeInt(character.getExp()); // current exp
      writer.writeShort(character.getFame()); // fame
      writer.writeInt(character.getGachaponExperience()); //Gachapon Exp
      writer.writeInt(character.getMapId()); // current map id
      writer.write(character.getInitialSpawnPoint()); // spawn point
      writer.writeInt(0);
   }

   protected void addCharLook(final MaplePacketLittleEndianWriter writer, MapleCharacter character, boolean mega) {
      writer.write(character.getGender());
      writer.write(character.getSkinColor().getId()); // skin color
      writer.writeInt(character.getFace()); // face
      writer.write(mega ? 0 : 1);
      writer.writeInt(character.getHair()); // hair
      addCharEquips(writer, character);
   }

   protected void addRemainingSkillInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      int[] remainingSp = character.getRemainingSps();
      int effectiveLength = 0;
      for (int value : remainingSp) {
         if (value > 0) {
            effectiveLength++;
         }
      }

      writer.write(effectiveLength);
      for (int i = 0; i < remainingSp.length; i++) {
         if (remainingSp[i] > 0) {
            writer.write(i + 1);
            writer.write(remainingSp[i]);
         }
      }
   }

   protected void addCharEquips(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      MapleInventory equip = character.getInventory(MapleInventoryType.EQUIPPED);
      Collection<Item> ii = MapleItemInformationProvider.getInstance().canWearEquipment(character, equip.list());
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
      writeEquips(writer, myEquip, maskedEquip);
      Item cWeapon = equip.getItem((short) -111);
      writer.writeInt(cWeapon != null ? cWeapon.id() : 0);
      for (int i = 0; i < 3; i++) {
         if (character.getPet(i) != null) {
            writer.writeInt(character.getPet(i).id());
         } else {
            writer.writeInt(0);
         }
      }
   }

   protected void writeEquips(MaplePacketLittleEndianWriter writer, Map<Short, Integer> myEquip, Map<Short, Integer> maskedEquip) {
      for (Map.Entry<Short, Integer> entry : myEquip.entrySet()) {
         writer.write(entry.getKey());
         writer.writeInt(entry.getValue());
      }
      writer.write(0xFF);
      for (Map.Entry<Short, Integer> entry : maskedEquip.entrySet()) {
         writer.write(entry.getKey());
         writer.writeInt(entry.getValue());
      }
      writer.write(0xFF);
   }

   protected void addExpirationTime(final MaplePacketLittleEndianWriter writer, long time) {
      writer.writeLong(getTime(time));
   }

   protected void addItemInfo(final MaplePacketLittleEndianWriter writer, Item item) {
      addItemInfo(writer, item, false);
   }

   protected void addItemInfo(final MaplePacketLittleEndianWriter writer, Item item, boolean zeroPosition) {
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
            writer.writeShort(pos > 100 ? pos - 100 : pos);
         } else {
            writer.write(pos);
         }
      }
      writer.write(itemType);
      writer.writeInt(item.id());
      writer.writeBool(isCash);
      if (isCash) {
         writer.writeLong(isPet ? item.petId() : isRing ? equip.ringId() : item.cashId());
      }
      addExpirationTime(writer, item.expiration());
      if (isPet) {
         MaplePet pet = item.pet();
         writer.writeAsciiString(StringUtil.getRightPaddedStr(pet.name(), '\0', 13));
         writer.write(pet.level());
         writer.writeShort(pet.closeness());
         writer.write(pet.fullness());
         addExpirationTime(writer, item.expiration());
         writer.writeInt(pet.petFlag());

         writer.write(new byte[]{(byte) 0x50, (byte) 0x46}); //wonder what this is
         writer.writeInt(0);
         return;
      }
      if (equip == null) {
         writer.writeShort(item.quantity());
         writer.writeMapleAsciiString(item.owner());
         writer.writeShort(item.flag()); // flag

         if (ItemConstants.isRechargeable(item.id())) {
            writer.writeInt(2);
            writer.write(new byte[]{(byte) 0x54, 0, 0, (byte) 0x34});
         }
         return;
      }
      writer.write(equip.slots()); // upgrade slots
      writer.write(equip.level()); // level
      writer.writeShort(equip.str()); // str
      writer.writeShort(equip.dex()); // dex
      writer.writeShort(equip.intelligence()); // int
      writer.writeShort(equip.luk()); // luk
      writer.writeShort(equip.hp()); // hp
      writer.writeShort(equip.mp()); // mp
      writer.writeShort(equip.watk()); // weapon attack
      writer.writeShort(equip.matk()); // magic attack
      writer.writeShort(equip.wdef()); // weapon defense
      writer.writeShort(equip.mdef()); // magic defense
      writer.writeShort(equip.acc()); // accuracy
      writer.writeShort(equip.avoid()); // avoid
      writer.writeShort(equip.hands()); // hands
      writer.writeShort(equip.speed()); // speed
      writer.writeShort(equip.jump()); // jump
      writer.writeMapleAsciiString(equip.owner()); // owner name
      writer.writeShort(equip.flag()); //Item Flags

      if (isCash) {
         for (int i = 0; i < 10; i++) {
            writer.write(0x40);
         }
      } else {
         int itemLevel = equip.itemLevel();

         long expNibble = (long) (ExpTable.getExpNeededForLevel(ii.getEquipLevelReq(item.id())) * equip.itemExp());
         expNibble /= ExpTable.getEquipExpNeededForLevel(itemLevel);

         writer.write(0);
         writer.write(itemLevel); //Item Level
         writer.writeInt((int) expNibble);
         writer.writeInt(equip.vicious()); //WTF NEXON ARE YOU SERIOUS?
         writer.writeLong(0);
      }
      writer.writeLong(getTime(-2));
      writer.writeInt(-1);
   }

   protected void addAnnounceBox(final MaplePacketLittleEndianWriter writer, MapleMiniGame game, int amount, int joinable) {
      writer.write(game.getGameType().getValue());
      writer.writeInt(game.objectId()); // game id / shop id
      writer.writeMapleAsciiString(game.getDescription()); // desc
      writer.writeBool(!game.getPassword().isEmpty());
      writer.write(game.getPieceType());
      writer.write(amount);
      writer.write(2);         //player capacity
      writer.write(joinable);
   }

   protected void addRingLook(final MaplePacketLittleEndianWriter writer, MapleCharacter character, boolean crush) {
      List<Ring> rings;
      if (crush) {
         rings = character.getCrushRings();
      } else {
         rings = character.getFriendshipRings();
      }
      boolean yes = false;
      for (Ring ring : rings) {
         if (ring.equipped()) {
            if (!yes) {
               yes = true;
               writer.write(1);
            }
            writer.writeInt(ring.ringId());
            writer.writeInt(0);
            writer.writeInt(ring.partnerRingId());
            writer.writeInt(0);
            writer.writeInt(ring.itemId());
         }
      }
      if (!yes) {
         writer.write(0);
      }
   }

   protected void addMarriageRingLook(MapleClient target, final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      Ring ring = character.getMarriageRing();

      if (ring == null || !ring.equipped()) {
         writer.write(0);
      } else {
         writer.write(1);

         MapleCharacter targetPlayer = target.getPlayer();
         if (targetPlayer != null && targetPlayer.getPartnerId() == character.getId()) {
            writer.writeInt(0);
            writer.writeInt(0);
         } else {
            writer.writeInt(character.getId());
            writer.writeInt(ring.partnerId());
         }

         writer.writeInt(ring.itemId());
      }
   }

   protected void addCharacterInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.writeLong(-1);
      writer.write(0);
      addCharStats(writer, character);
      writer.write(character.getBuddyList().capacity());

      if (character.getLinkedName() == null) {
         writer.write(0);
      } else {
         writer.write(1);
         writer.writeMapleAsciiString(character.getLinkedName());
      }

      writer.writeInt(character.getMeso());
      addInventoryInfo(writer, character);
      addSkillInfo(writer, character);
      addQuestInfo(writer, character);
      addMiniGameInfo(writer, character);
      addRingInfo(writer, character);
      addTeleportInfo(writer, character);
      addMonsterBookInfo(writer, character);
      addNewYearInfo(writer, character);
      addAreaInfo(writer, character);//assuming it stayed here xd
      writer.writeShort(0);
   }

   protected void addInventoryInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      for (byte i = 1; i <= 5; i++) {
         writer.write(character.getInventory(MapleInventoryType.getByType(i)).getSlotLimit());
      }
      writer.writeLong(getTime(-2));
      MapleInventory iv = character.getInventory(MapleInventoryType.EQUIPPED);
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
      for (Item item : equipped) {
         addItemInfo(writer, item);
      }
      writer.writeShort(0); // start of equip cash
      for (Item item : equippedCash) {
         addItemInfo(writer, item);
      }
      writer.writeShort(0); // start of equip inventory
      for (Item item : character.getInventory(MapleInventoryType.EQUIP).list()) {
         addItemInfo(writer, item);
      }
      writer.writeInt(0);
      for (Item item : character.getInventory(MapleInventoryType.USE).list()) {
         addItemInfo(writer, item);
      }
      writer.write(0);
      for (Item item : character.getInventory(MapleInventoryType.SETUP).list()) {
         addItemInfo(writer, item);
      }
      writer.write(0);
      for (Item item : character.getInventory(MapleInventoryType.ETC).list()) {
         addItemInfo(writer, item);
      }
      writer.write(0);
      for (Item item : character.getInventory(MapleInventoryType.CASH).list()) {
         addItemInfo(writer, item);
      }
   }

   protected void addSkillInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.write(0); // start of skills
      Map<Skill, SkillEntry> skills = character.getSkills();
      int skillsSize = skills.size();
      // We don't want to include any hidden skill in this, so subtract them from the size list and ignore them.
      for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         if (GameConstants.isHiddenSkills(skill.getKey().getId())) {
            skillsSize--;
         }
      }
      writer.writeShort(skillsSize);
      for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         if (GameConstants.isHiddenSkills(skill.getKey().getId())) {
            continue;
         }
         writer.writeInt(skill.getKey().getId());
         writer.writeInt(skill.getValue().skillLevel());
         addExpirationTime(writer, skill.getValue().expiration());
         if (skill.getKey().isFourthJob()) {
            writer.writeInt(skill.getValue().masterLevel());
         }
      }
      writer.writeShort(character.getAllCoolDowns().size());
      for (PlayerCoolDownValueHolder cooling : character.getAllCoolDowns()) {
         writer.writeInt(cooling.skillId);
         int timeLeft = (int) (cooling.length + cooling.startTime - System.currentTimeMillis());
         writer.writeShort(timeLeft / 1000);
      }
   }

   protected void addQuestInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      List<MapleQuestStatus> started = character.getStartedQuests();
      int startedSize = 0;
      for (MapleQuestStatus qs : started) {
         if (QuestProcessor.getInstance().getInfoNumber(qs) > 0) {
            startedSize++;
         }
         startedSize++;
      }
      writer.writeShort(startedSize);
      for (MapleQuestStatus qs : started) {
         writer.writeShort(qs.questId());
         writer.writeMapleAsciiString(qs.getProgressData());

         short infoNumber = QuestProcessor.getInstance().getInfoNumber(qs);
         if (infoNumber > 0) {
            MapleQuestStatus iqs = QuestProcessor.getInstance().getQuestStatus(character, infoNumber);
            writer.writeShort(infoNumber);
            writer.writeMapleAsciiString(iqs.getProgressData());
         }
      }
      List<MapleQuestStatus> completed = character.getCompletedQuests();
      writer.writeShort(completed.size());
      for (MapleQuestStatus qs : completed) {
         writer.writeShort(qs.questId());
         writer.writeLong(getTime(qs.completionTime()));
      }
   }

   protected void addMiniGameInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.writeShort(0);
                /*for (int m = size; m > 0; m--) {//nexon does this :P
                 writer.writeInt(0);
                 writer.writeInt(0);
                 writer.writeInt(0);
                 writer.writeInt(0);
                 writer.writeInt(0);
                 }*/
   }

   protected void addRingInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.writeShort(character.getCrushRings().size());
      for (Ring ring : character.getCrushRings()) {
         writer.writeInt(ring.partnerId());
         writer.writeAsciiString(StringUtil.getRightPaddedStr(ring.partnerName(), '\0', 13));
         writer.writeInt(ring.ringId());
         writer.writeInt(0);
         writer.writeInt(ring.partnerRingId());
         writer.writeInt(0);
      }
      writer.writeShort(character.getFriendshipRings().size());
      for (Ring ring : character.getFriendshipRings()) {
         writer.writeInt(ring.partnerId());
         writer.writeAsciiString(StringUtil.getRightPaddedStr(ring.partnerName(), '\0', 13));
         writer.writeInt(ring.ringId());
         writer.writeInt(0);
         writer.writeInt(ring.partnerRingId());
         writer.writeInt(0);
         writer.writeInt(ring.itemId());
      }

      if (character.getPartnerId() > 0) {
         Ring marriageRing = character.getMarriageRing();

         writer.writeShort(1);
         writer.writeInt(character.getRelationshipId());
         writer.writeInt(character.getGender() == 0 ? character.getId() : character.getPartnerId());
         writer.writeInt(character.getGender() == 0 ? character.getPartnerId() : character.getId());
         writer.writeShort((marriageRing != null) ? 3 : 1);
         if (marriageRing != null) {
            writer.writeInt(marriageRing.itemId());
            writer.writeInt(marriageRing.itemId());
         } else {
            writer.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
            writer.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
         }
         writer.writeAsciiString(StringUtil.getRightPaddedStr(character.getGender() == 0 ? character.getName() :
               CharacterProcessor.getInstance().getNameById(character.getPartnerId()), '\0', 13));
         writer.writeAsciiString(StringUtil.getRightPaddedStr(
               character.getGender() == 0 ? CharacterProcessor.getInstance().getNameById(character.getPartnerId()) :
                     character.getName(), '\0', 13));
      } else {
         writer.writeShort(0);
      }
   }

   protected void addTeleportInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      final List<Integer> teleportRockMaps = character.getTeleportRockMaps();
      final List<Integer> vipTeleportRockMaps = character.getVipTeleportRockMaps();
      for (int i = 0; i < 5; i++) {
         writer.writeInt(teleportRockMaps.get(i));
      }
      for (int i = 0; i < 10; i++) {
         writer.writeInt(vipTeleportRockMaps.get(i));
      }
   }

   protected void addMonsterBookInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      writer.writeInt(character.getMonsterBookCover()); // cover
      writer.write(0);
      Map<Integer, Integer> cards = character.getMonsterBook().getCards();
      writer.writeShort(cards.size());
      for (Map.Entry<Integer, Integer> all : cards.entrySet()) {
         writer.writeShort(all.getKey() % 10000); // Id
         writer.write(all.getValue()); // Level
      }
   }

   protected void addNewYearInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      Set<NewYearCardRecord> received = character.getReceivedNewYearRecords();

      writer.writeShort(received.size());
      for (NewYearCardRecord nyc : received) {
         encodeNewYearCard(nyc, writer);
      }
   }

   protected void encodeNewYearCard(NewYearCardRecord newYearCardRecord, MaplePacketLittleEndianWriter writer) {
      writer.writeInt(newYearCardRecord.id());
      writer.writeInt(newYearCardRecord.senderId());
      writer.writeMapleAsciiString(newYearCardRecord.senderName());
      writer.writeBool(newYearCardRecord.senderDiscardCard());
      writer.writeLong(newYearCardRecord.dateSent());
      writer.writeInt(newYearCardRecord.receiverId());
      writer.writeMapleAsciiString(newYearCardRecord.receiverName());
      writer.writeBool(newYearCardRecord.receiverDiscardCard());
      writer.writeBool(newYearCardRecord.receiverReceivedCard());
      writer.writeLong(newYearCardRecord.dateReceived());
      writer.writeMapleAsciiString(newYearCardRecord.message());
   }

   protected void addAreaInfo(final MaplePacketLittleEndianWriter writer, MapleCharacter character) {
      Map<Short, String> areaInfos = character.getAreaInfos();
      writer.writeShort(areaInfos.size());
      for (Short area : areaInfos.keySet()) {
         writer.writeShort(area);
         writer.writeMapleAsciiString(areaInfos.get(area));
      }
   }
}
