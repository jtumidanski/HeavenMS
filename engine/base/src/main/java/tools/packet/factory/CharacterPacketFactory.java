package tools.packet.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleMount;
import client.MapleQuestStatus;
import client.MonsterBook;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.guild.MapleAlliance;
import server.MapleItemInformationProvider;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.CharacterKnockBack;
import tools.packet.character.CharacterLook;
import tools.packet.character.DamageCharacter;
import tools.packet.character.FacialExpression;
import tools.packet.character.GetCharacterInfo;
import tools.packet.character.SetAutoHpPot;
import tools.packet.character.SetAutoMpPot;
import tools.packet.character.SkillCooldown;
import tools.packet.character.SummonSkill;
import tools.packet.character.UpdateGender;
import tools.packet.character.UpdateMount;
import tools.packet.character.UpdateSkill;

public class CharacterPacketFactory extends AbstractPacketFactory {
   private static CharacterPacketFactory instance;

   public static CharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterPacketFactory();
      }
      return instance;
   }

   private CharacterPacketFactory() {
      registry.setHandler(CharacterLook.class, packet -> create(SendOpcode.UPDATE_CHAR_LOOK, this::updateCharLook, packet));
      registry.setHandler(GetCharacterInfo.class, packet -> create(SendOpcode.CHAR_INFO, this::charInfo, packet));
      registry.setHandler(FacialExpression.class, packet -> create(SendOpcode.FACIAL_EXPRESSION, this::facialExpression, packet, 10));
      registry.setHandler(UpdateGender.class, packet -> create(SendOpcode.SET_GENDER, this::updateGender, packet, 3));
      registry.setHandler(SetAutoMpPot.class, packet -> create(SendOpcode.AUTO_MP_POT, this::sendAutoMpPot, packet, 6));
      registry.setHandler(SetAutoHpPot.class, packet -> create(SendOpcode.AUTO_HP_POT, this::sendAutoHpPot, packet));
      registry.setHandler(UpdateSkill.class, packet -> create(SendOpcode.UPDATE_SKILLS, this::updateSkill, packet));
      registry.setHandler(SummonSkill.class, packet -> create(SendOpcode.SUMMON_SKILL, this::summonSkill, packet));
      registry.setHandler(SkillCooldown.class, packet -> create(SendOpcode.COOLDOWN, this::skillCooldown, packet));
      registry.setHandler(DamageCharacter.class, packet -> create(SendOpcode.DAMAGE_PLAYER, this::damagePlayer, packet));
      registry.setHandler(UpdateMount.class, packet -> create(SendOpcode.SET_TAMING_MOB_INFO, this::updateMount, packet));
      registry.setHandler(CharacterKnockBack.class, packet -> create(SendOpcode.LEFT_KNOCK_BACK, this::leftKnockBack, packet, 2));
   }

   protected void updateCharLook(MaplePacketLittleEndianWriter writer, CharacterLook packet) {
      writer.writeInt(packet.getReference().getId());
      writer.write(1);
      addCharLook(writer, packet.getReference(), false);
      addRingLook(writer, packet.getReference(), true);
      addRingLook(writer, packet.getReference(), false);
      addMarriageRingLook(packet.getTarget(), writer, packet.getReference());
      writer.writeInt(0);
   }

   protected void charInfo(MaplePacketLittleEndianWriter writer, GetCharacterInfo packet) {
      MapleCharacter chr = packet.getCharacter();
      //3D 00 0A 43 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
      writer.writeInt(chr.getId());
      writer.write(chr.getLevel());
      writer.writeShort(chr.getJob().getId());
      writer.writeShort(chr.getFame());
      writer.write(chr.getMarriageRing() != null ? 1 : 0);
      writeGuildInfo(chr, writer);
      writer.write(0); // pMedalInfo, thanks to Arnah (Vertisy)

      MaplePet[] pets = chr.getPets();
      Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -114);
      for (int i = 0; i < 3; i++) {
         if (pets[i] != null) {
            writer.write(pets[i].uniqueId());
            writer.writeInt(pets[i].id()); // petid
            writer.writeMapleAsciiString(pets[i].name());
            writer.write(pets[i].level()); // pet level
            writer.writeShort(pets[i].closeness()); // pet closeness
            writer.write(pets[i].fullness()); // pet fullness
            writer.writeShort(0);
            writer.writeInt(inv != null ? inv.id() : 0);
         }
      }
      writer.write(0); //end of pets

      Item mount;     //mounts can potentially crash the client if the player's level is not properly checked
      if (chr.getMount() != null && (mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18)) != null && MapleItemInformationProvider.getInstance().getEquipLevelReq(mount.id()) <= chr.getLevel()) {
         MapleMount mmount = chr.getMount();
         writer.write(mmount.getId()); //mount
         writer.writeInt(mmount.getLevel()); //level
         writer.writeInt(mmount.getExp()); //exp
         writer.writeInt(mmount.getTiredness()); //tiredness
      } else {
         writer.write(0);
      }
      writer.write(chr.getCashShop().getWishList().size());
      for (int sn : chr.getCashShop().getWishList()) {
         writer.writeInt(sn);
      }

      MonsterBook book = chr.getMonsterBook();
      writer.writeInt(book.getBookLevel());
      writer.writeInt(book.getNormalCard());
      writer.writeInt(book.getSpecialCard());
      writer.writeInt(book.getTotalCards());
      writer.writeInt(chr.getMonsterBookCover() > 0 ? MapleItemInformationProvider.getInstance().getCardMobId(chr.getMonsterBookCover()) : 0);
      Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
      if (medal != null) {
         writer.writeInt(medal.id());
      } else {
         writer.writeInt(0);
      }
      ArrayList<Short> medalQuests = new ArrayList<>();
      List<MapleQuestStatus> completed = chr.getCompletedQuests();
      for (MapleQuestStatus q : completed) {
         if (q.getQuest().getId() >= 29000) { // && q.getQuest().getId() <= 29923
            medalQuests.add(q.getQuest().getId());
         }
      }

      Collections.sort(medalQuests);
      writer.writeShort(medalQuests.size());
      for (Short s : medalQuests) {
         writer.writeShort(s);
      }
   }

   protected void writeGuildInfo(MapleCharacter chr, MaplePacketLittleEndianWriter writer) {
      if (chr.getGuildId() > 0) {
         Server.getInstance().getGuild(chr.getGuildId()).ifPresentOrElse(guild -> {
            writer.writeMapleAsciiString(guild.getName());
            String allianceName = Server.getInstance().getAlliance(guild.getAllianceId()).map(MapleAlliance::name).orElse("");
            writer.writeMapleAsciiString(allianceName);
         }, () -> {
            writer.writeMapleAsciiString("");
            writer.writeMapleAsciiString("");  // does not seem to work
         });
      } else {
         writer.writeMapleAsciiString("");
         writer.writeMapleAsciiString("");  // does not seem to work
      }
   }

   protected void facialExpression(MaplePacketLittleEndianWriter writer, FacialExpression packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.expression());
   }

   protected void updateGender(MaplePacketLittleEndianWriter writer, UpdateGender packet) {
      writer.write(packet.gender());
   }

   protected void sendAutoMpPot(MaplePacketLittleEndianWriter writer, SetAutoMpPot packet) {
      writer.writeInt(packet.itemId());
   }

   protected void sendAutoHpPot(MaplePacketLittleEndianWriter writer, SetAutoHpPot packet) {
      writer.writeInt(packet.itemId());
   }

   protected void updateSkill(MaplePacketLittleEndianWriter writer, UpdateSkill packet) {
      writer.write(1);
      writer.writeShort(1);
      writer.writeInt(packet.skillId());
      writer.writeInt(packet.level());
      writer.writeInt(packet.masterLevel());
      addExpirationTime(writer, packet.expiration());
      writer.write(4);
   }

   protected void summonSkill(MaplePacketLittleEndianWriter writer, SummonSkill packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.summonSkillId());
      writer.write(packet.newStance());
   }

   protected void skillCooldown(MaplePacketLittleEndianWriter writer, SkillCooldown packet) {
      writer.writeInt(packet.skillId());
      writer.writeShort(packet.time());//Int in v97
   }

   protected void damagePlayer(MaplePacketLittleEndianWriter writer, DamageCharacter packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.skill());
      writer.writeInt(packet.damage());
      if (packet.skill() != -4) {
         writer.writeInt(packet.monsterIdFrom());
         writer.write(packet.direction());
         if (packet.pgmr()) {
            writer.write(packet.pgmr_1());
            writer.write(packet.is_pg() ? 1 : 0);
            writer.writeInt(packet.objectId());
            writer.write(6);
            writer.writeShort(packet.xPosition());
            writer.writeShort(packet.yPosition());
            writer.write(0);
         } else {
            writer.writeShort(0);
         }
         writer.writeInt(packet.damage());
         if (packet.fake() > 0) {
            writer.writeInt(packet.fake());
         }
      } else {
         writer.writeInt(packet.damage());
      }
   }

   protected void updateMount(MaplePacketLittleEndianWriter writer, UpdateMount packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.mountLevel());
      writer.writeInt(packet.mountExp());
      writer.writeInt(packet.mountTiredness());
      writer.write(packet.levelUp() ? (byte) 1 : (byte) 0);
   }

   protected void leftKnockBack(MaplePacketLittleEndianWriter writer, CharacterKnockBack packet) {
   }
}