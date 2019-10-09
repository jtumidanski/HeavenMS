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
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CharacterLook) {
         return create(this::updateCharLook, packetInput);
      } else if (packetInput instanceof GetCharacterInfo) {
         return create(this::charInfo, packetInput);
      } else if (packetInput instanceof FacialExpression) {
         return create(this::facialExpression, packetInput);
      } else if (packetInput instanceof UpdateGender) {
         return create(this::updateGender, packetInput);
      } else if (packetInput instanceof SetAutoMpPot) {
         return create(this::sendAutoMpPot, packetInput);
      } else if (packetInput instanceof SetAutoHpPot) {
         return create(this::sendAutoHpPot, packetInput);
      } else if (packetInput instanceof UpdateSkill) {
         return create(this::updateSkill, packetInput);
      } else if (packetInput instanceof SummonSkill) {
         return create(this::summonSkill, packetInput);
      } else if (packetInput instanceof SkillCooldown) {
         return create(this::skillCooldown, packetInput);
      } else if (packetInput instanceof DamageCharacter) {
         return create(this::damagePlayer, packetInput);
      } else if (packetInput instanceof UpdateMount) {
         return create(this::updateMount, packetInput);
      } else if (packetInput instanceof CharacterKnockBack) {
         return create(this::leftKnockBack, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] updateCharLook(CharacterLook packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_CHAR_LOOK.getValue());
      mplew.writeInt(packet.getReference().getId());
      mplew.write(1);
      addCharLook(mplew, packet.getReference(), false);
      addRingLook(mplew, packet.getReference(), true);
      addRingLook(mplew, packet.getReference(), false);
      addMarriageRingLook(packet.getTarget(), mplew, packet.getReference());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] charInfo(GetCharacterInfo packet) {
      MapleCharacter chr = packet.getCharacter();
      //3D 00 0A 43 01 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHAR_INFO.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(chr.getLevel());
      mplew.writeShort(chr.getJob().getId());
      mplew.writeShort(chr.getFame());
      mplew.write(chr.getMarriageRing() != null ? 1 : 0);
      writeGuildInfo(chr, mplew);
      mplew.write(0); // pMedalInfo, thanks to Arnah (Vertisy)

      MaplePet[] pets = chr.getPets();
      Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -114);
      for (int i = 0; i < 3; i++) {
         if (pets[i] != null) {
            mplew.write(pets[i].uniqueId());
            mplew.writeInt(pets[i].id()); // petid
            mplew.writeMapleAsciiString(pets[i].name());
            mplew.write(pets[i].level()); // pet level
            mplew.writeShort(pets[i].closeness()); // pet closeness
            mplew.write(pets[i].fullness()); // pet fullness
            mplew.writeShort(0);
            mplew.writeInt(inv != null ? inv.id() : 0);
         }
      }
      mplew.write(0); //end of pets

      Item mount;     //mounts can potentially crash the client if the player's level is not properly checked
      if (chr.getMount() != null && (mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18)) != null && MapleItemInformationProvider.getInstance().getEquipLevelReq(mount.id()) <= chr.getLevel()) {
         MapleMount mmount = chr.getMount();
         mplew.write(mmount.getId()); //mount
         mplew.writeInt(mmount.getLevel()); //level
         mplew.writeInt(mmount.getExp()); //exp
         mplew.writeInt(mmount.getTiredness()); //tiredness
      } else {
         mplew.write(0);
      }
      mplew.write(chr.getCashShop().getWishList().size());
      for (int sn : chr.getCashShop().getWishList()) {
         mplew.writeInt(sn);
      }

      MonsterBook book = chr.getMonsterBook();
      mplew.writeInt(book.getBookLevel());
      mplew.writeInt(book.getNormalCard());
      mplew.writeInt(book.getSpecialCard());
      mplew.writeInt(book.getTotalCards());
      mplew.writeInt(chr.getMonsterBookCover() > 0 ? MapleItemInformationProvider.getInstance().getCardMobId(chr.getMonsterBookCover()) : 0);
      Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
      if (medal != null) {
         mplew.writeInt(medal.id());
      } else {
         mplew.writeInt(0);
      }
      ArrayList<Short> medalQuests = new ArrayList<>();
      List<MapleQuestStatus> completed = chr.getCompletedQuests();
      for (MapleQuestStatus q : completed) {
         if (q.getQuest().getId() >= 29000) { // && q.getQuest().getId() <= 29923
            medalQuests.add(q.getQuest().getId());
         }
      }

      Collections.sort(medalQuests);
      mplew.writeShort(medalQuests.size());
      for (Short s : medalQuests) {
         mplew.writeShort(s);
      }
      return mplew.getPacket();
   }

   protected void writeGuildInfo(MapleCharacter chr, MaplePacketLittleEndianWriter mplew) {
      if (chr.getGuildId() > 0) {
         Server.getInstance().getGuild(chr.getGuildId()).ifPresentOrElse(guild -> {
            mplew.writeMapleAsciiString(guild.getName());
            String allianceName = Server.getInstance().getAlliance(guild.getAllianceId()).map(MapleAlliance::name).orElse("");
            mplew.writeMapleAsciiString(allianceName);
         }, () -> {
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");  // does not seem to work
         });
      } else {
         mplew.writeMapleAsciiString("");
         mplew.writeMapleAsciiString("");  // does not seem to work
      }
   }

   protected byte[] facialExpression(FacialExpression packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
      mplew.writeShort(SendOpcode.FACIAL_EXPRESSION.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.expression());
      return mplew.getPacket();
   }

   protected byte[] updateGender(UpdateGender packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SET_GENDER.getValue());
      mplew.write(packet.gender());
      return mplew.getPacket();
   }

   protected byte[] sendAutoMpPot(SetAutoMpPot packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.AUTO_MP_POT.getValue());
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] sendAutoHpPot(SetAutoHpPot packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.AUTO_HP_POT.getValue());
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] updateSkill(UpdateSkill packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_SKILLS.getValue());
      mplew.write(1);
      mplew.writeShort(1);
      mplew.writeInt(packet.skillId());
      mplew.writeInt(packet.level());
      mplew.writeInt(packet.masterLevel());
      addExpirationTime(mplew, packet.expiration());
      mplew.write(4);
      return mplew.getPacket();
   }

   protected byte[] summonSkill(SummonSkill packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SUMMON_SKILL.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.summonSkillId());
      mplew.write(packet.newStance());
      return mplew.getPacket();
   }

   protected byte[] skillCooldown(SkillCooldown packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.COOLDOWN.getValue());
      mplew.writeInt(packet.skillId());
      mplew.writeShort(packet.time());//Int in v97
      return mplew.getPacket();
   }

   protected byte[] damagePlayer(DamageCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DAMAGE_PLAYER.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.skill());
      mplew.writeInt(packet.damage());
      if (packet.skill() != -4) {
         mplew.writeInt(packet.monsterIdFrom());
         mplew.write(packet.direction());
         if (packet.pgmr()) {
            mplew.write(packet.pgmr_1());
            mplew.write(packet.is_pg() ? 1 : 0);
            mplew.writeInt(packet.objectId());
            mplew.write(6);
            mplew.writeShort(packet.xPosition());
            mplew.writeShort(packet.yPosition());
            mplew.write(0);
         } else {
            mplew.writeShort(0);
         }
         mplew.writeInt(packet.damage());
         if (packet.fake() > 0) {
            mplew.writeInt(packet.fake());
         }
      } else {
         mplew.writeInt(packet.damage());
      }

      return mplew.getPacket();
   }

   protected byte[] updateMount(UpdateMount packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_TAMING_MOB_INFO.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.mountLevel());
      mplew.writeInt(packet.mountExp());
      mplew.writeInt(packet.mountTiredness());
      mplew.write(packet.levelUp() ? (byte) 1 : (byte) 0);
      return mplew.getPacket();
   }

   protected byte[] leftKnockBack(CharacterKnockBack packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.LEFT_KNOCK_BACK.getValue());
      return mplew.getPacket();
   }
}