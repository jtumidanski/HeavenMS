package tools.packet.factory;

import java.util.List;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleMount;
import client.MonsterBook;
import client.inventory.Item;
import client.inventory.MaplePet;
import constants.MapleInventoryType;
import net.server.Server;
import net.server.guild.MapleAlliance;
import rest.CharacterQuestAttributes;
import server.MapleItemInformationProvider;
import server.processor.QuestProcessor;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.CharacterKnockBack;
import tools.packet.character.CharacterLook;
import tools.packet.character.DamageCharacter;
import tools.packet.character.FacialExpression;
import tools.packet.character.GetCharacterInfo;
import tools.packet.character.SetAutoHpPot;
import tools.packet.character.SetAutoMpPot;
import tools.packet.character.SkillCoolDown;
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
      Handler.handle(CharacterLook.class).decorate(this::updateCharLook).register(registry);
      Handler.handle(GetCharacterInfo.class).decorate(this::charInfo).register(registry);
      Handler.handle(FacialExpression.class).decorate(this::facialExpression).size(10).register(registry);
      Handler.handle(UpdateGender.class).decorate(this::updateGender).size(3).register(registry);
      Handler.handle(SetAutoMpPot.class).decorate(this::sendAutoMpPot).size(6).register(registry);
      Handler.handle(SetAutoHpPot.class).decorate(this::sendAutoHpPot).register(registry);
      Handler.handle(UpdateSkill.class).decorate(this::updateSkill).register(registry);
      Handler.handle(SummonSkill.class).decorate(this::summonSkill).register(registry);
      Handler.handle(SkillCoolDown.class).decorate(this::skillCoolDown).register(registry);
      Handler.handle(DamageCharacter.class).decorate(this::damagePlayer).register(registry);
      Handler.handle(UpdateMount.class).decorate(this::updateMount).register(registry);
      Handler.handle(CharacterKnockBack.class).decorate(this::leftKnockBack).size(2).register(registry);
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
      writer.write(0);

      MaplePet[] pets = chr.getPets();
      Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -114);
      for (int i = 0; i < 3; i++) {
         if (pets[i] != null) {
            writer.write(pets[i].uniqueId());
            writer.writeInt(pets[i].id()); // pet id
            writer.writeMapleAsciiString(pets[i].name());
            writer.write(pets[i].level()); // pet level
            writer.writeShort(pets[i].closeness()); // pet closeness
            writer.write(pets[i].fullness()); // pet fullness
            writer.writeShort(0);
            writer.writeInt(inv != null ? inv.id() : 0);
         }
      }
      writer.write(0); //end of pets

      Item mountItem;     //mounts can potentially crash the client if the player's level is not properly checked
      if (chr.getMount() != null && (mountItem = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18)) != null
            && MapleItemInformationProvider.getInstance().getEquipLevelReq(mountItem.id()) <= chr.getLevel()) {
         MapleMount mount = chr.getMount();
         writer.write(mount.id()); //mount
         writer.writeInt(mount.level()); //level
         writer.writeInt(mount.exp()); //exp
         writer.writeInt(mount.tiredness()); //tiredness
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
      writer.writeInt(
            chr.getMonsterBookCover() > 0 ? MapleItemInformationProvider.getInstance().getCardMobId(chr.getMonsterBookCover()) : 0);
      Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
      if (medal != null) {
         writer.writeInt(medal.id());
      } else {
         writer.writeInt(0);
      }

      List<Short> medalQuests = QuestProcessor.getInstance().getCompletedQuests(chr).stream()
            .filter(questStatus -> questStatus.getId() >= 29000) // && q.getQuest().getId() <= 29923
            .map(CharacterQuestAttributes::getId)
            .map(Integer::shortValue)
            .sorted()
            .collect(Collectors.toList());

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

   protected void skillCoolDown(MaplePacketLittleEndianWriter writer, SkillCoolDown packet) {
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