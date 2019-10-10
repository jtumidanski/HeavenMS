package tools.packet.factory;

import java.util.Collection;

import client.MapleCharacter;
import client.database.data.GlobalUserRank;
import client.database.data.GuildData;
import net.opcodes.SendOpcode;
import net.server.guild.MapleGuildCharacter;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.guild.CreateGuildMessage;
import tools.packet.guild.GenericGuildMessage;
import tools.packet.guild.GuildCapacityChange;
import tools.packet.guild.GuildDisband;
import tools.packet.guild.GuildEmblemChange;
import tools.packet.guild.GuildInvite;
import tools.packet.guild.GuildMarkChanged;
import tools.packet.guild.GuildMemberChangeRank;
import tools.packet.guild.GuildMemberLeft;
import tools.packet.guild.GuildMemberLevelJobUpdate;
import tools.packet.guild.GuildMemberOnline;
import tools.packet.guild.GuildNameChange;
import tools.packet.guild.GuildNotice;
import tools.packet.guild.GuildQuestWaitingNotice;
import tools.packet.guild.GuildRankTitleChange;
import tools.packet.guild.NewGuildMember;
import tools.packet.guild.ResponseGuildMessage;
import tools.packet.guild.ShowGuildInfo;
import tools.packet.guild.ShowGuildRanks;
import tools.packet.guild.ShowPlayerRanks;
import tools.packet.guild.UpdateGuildPoints;

public class GuildPacketFactory extends AbstractPacketFactory {
   private static GuildPacketFactory instance;

   public static GuildPacketFactory getInstance() {
      if (instance == null) {
         instance = new GuildPacketFactory();
      }
      return instance;
   }

   private GuildPacketFactory() {
      registry.setHandler(GuildMemberOnline.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildMemberOnline, packet));
      registry.setHandler(GuildInvite.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildInvite, packet));
      registry.setHandler(CreateGuildMessage.class, packet -> create(SendOpcode.GUILD_OPERATION, this::createGuildMessage, packet));
      registry.setHandler(GenericGuildMessage.class, packet -> create(SendOpcode.GUILD_OPERATION, this::genericGuildMessage, packet));
      registry.setHandler(ResponseGuildMessage.class, packet -> create(SendOpcode.GUILD_OPERATION, this::responseGuildMessage, packet));
      registry.setHandler(NewGuildMember.class, packet -> create(SendOpcode.GUILD_OPERATION, this::newGuildMember, packet));
      registry.setHandler(GuildMemberLeft.class, packet -> create(SendOpcode.GUILD_OPERATION, this::memberLeft, packet));
      registry.setHandler(GuildMemberChangeRank.class, packet -> create(SendOpcode.GUILD_OPERATION, this::changeRank, packet));
      registry.setHandler(GuildNotice.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildNotice, packet));
      registry.setHandler(GuildMemberLevelJobUpdate.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildMemberLevelJobUpdate, packet));
      registry.setHandler(GuildRankTitleChange.class, packet -> create(SendOpcode.GUILD_OPERATION, this::rankTitleChange, packet));
      registry.setHandler(GuildDisband.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildDisband, packet));
      registry.setHandler(GuildQuestWaitingNotice.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildQuestWaitingNotice, packet));
      registry.setHandler(GuildEmblemChange.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildEmblemChange, packet));
      registry.setHandler(GuildCapacityChange.class, packet -> create(SendOpcode.GUILD_OPERATION, this::guildCapacityChange, packet));
      registry.setHandler(ShowGuildRanks.class, packet -> create(SendOpcode.GUILD_OPERATION, this::showGuildRanks, packet));
      registry.setHandler(ShowPlayerRanks.class, packet -> create(SendOpcode.GUILD_OPERATION, this::showPlayerRanks, packet));
      registry.setHandler(UpdateGuildPoints.class, packet -> create(SendOpcode.GUILD_OPERATION, this::updateGP, packet));
      registry.setHandler(ShowGuildInfo.class, packet -> create(SendOpcode.GUILD_OPERATION, this::showGuildInfo, packet));
      registry.setHandler(GuildNameChange.class, packet -> create(SendOpcode.GUILD_NAME_CHANGED, this::guildNameChanged, packet));
      registry.setHandler(GuildMarkChanged.class, packet -> create(SendOpcode.GUILD_MARK_CHANGED, this::guildMarkChanged, packet));
   }

   protected void guildMemberOnline(MaplePacketLittleEndianWriter writer, GuildMemberOnline packet) {
      writer.write(0x3d);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.write(packet.online() ? 1 : 0);
   }

   protected void guildInvite(MaplePacketLittleEndianWriter writer, GuildInvite packet) {
      writer.write(0x05);
      writer.writeInt(packet.guildId());
      writer.writeMapleAsciiString(packet.characterName());
   }

   protected void createGuildMessage(MaplePacketLittleEndianWriter writer, CreateGuildMessage packet) {
      writer.write(0x3);
      writer.writeInt(0);
      writer.writeMapleAsciiString(packet.masterCharacterName());
      writer.writeMapleAsciiString(packet.guildName());
   }

   /**
    * Gets a Heracle/guild message packet.
    * <p>
    * Possible values for <code>code</code>:<br> 28: guild name already in use<br>
    * 31: problem in locating players during agreement<br> 33/40: already joined a guild<br>
    * 35: Cannot make guild<br> 36: problem in player agreement<br> 38: problem during forming guild<br>
    * 41: max number of players in joining guild<br> 42: character can't be found this channel<br>
    * 45/48: character not in guild<br> 52: problem in disbanding guild<br> 56: admin cannot make guild<br>
    * 57: problem in increasing guild size<br>
    *
    * @return The guild message packet.
    */
   protected void genericGuildMessage(MaplePacketLittleEndianWriter writer, GenericGuildMessage packet) {
      writer.write(packet.code());
   }

   /**
    * Gets a guild message packet appended with target name.
    * <p>
    * 53: player not accepting guild invites<br>
    * 54: player already managing an invite<br> 55: player denied an invite<br>
    *
    * @return The guild message packet.
    */
   protected void responseGuildMessage(MaplePacketLittleEndianWriter writer, ResponseGuildMessage packet) {
      writer.write(packet.code());
      writer.writeMapleAsciiString(packet.targetName());
   }

   protected void newGuildMember(MaplePacketLittleEndianWriter writer, NewGuildMember packet) {
      writer.write(0x27);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.writeAsciiString(StringUtil.getRightPaddedStr(packet.name(), '\0', 13));
      writer.writeInt(packet.jobId());
      writer.writeInt(packet.level());
      writer.writeInt(packet.guildRank()); //should be always 5 but whatevs
      writer.writeInt(packet.online() ? 1 : 0); //should always be 1 too
      writer.writeInt(1); //? could be guild signature, but doesn't seem to matter
      writer.writeInt(3);
   }

   //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
   protected void memberLeft(MaplePacketLittleEndianWriter writer, GuildMemberLeft packet) {
      writer.write(packet.expelled() ? 0x2f : 0x2c);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.writeMapleAsciiString(packet.characterName());
   }

   //rank change
   protected void changeRank(MaplePacketLittleEndianWriter writer, GuildMemberChangeRank packet) {
      writer.write(0x40);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.write(packet.guildRank());
   }

   protected void guildNotice(MaplePacketLittleEndianWriter writer, GuildNotice packet) {
      writer.write(0x44);
      writer.writeInt(packet.guildId());
      writer.writeMapleAsciiString(packet.notice());
   }

   protected void guildMemberLevelJobUpdate(MaplePacketLittleEndianWriter writer, GuildMemberLevelJobUpdate packet) {
      writer.write(0x3C);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.level());
      writer.writeInt(packet.jobId());
   }

   protected void rankTitleChange(MaplePacketLittleEndianWriter writer, GuildRankTitleChange packet) {
      writer.write(0x3E);
      writer.writeInt(packet.guildId());
      for (int i = 0; i < 5; i++) {
         writer.writeMapleAsciiString(packet.ranks()[i]);
      }
   }

   protected void guildDisband(MaplePacketLittleEndianWriter writer, GuildDisband packet) {
      writer.write(0x32);
      writer.writeInt(packet.guildId());
      writer.write(1);
   }

   protected void guildQuestWaitingNotice(MaplePacketLittleEndianWriter writer, GuildQuestWaitingNotice packet) {
      writer.write(0x4C);
      writer.write(packet.channel() - 1);
      writer.write(packet.position());
   }

   protected void guildEmblemChange(MaplePacketLittleEndianWriter writer, GuildEmblemChange packet) {
      writer.write(0x42);
      writer.writeInt(packet.guildId());
      writer.writeShort(packet.background());
      writer.write(packet.backgroundColor());
      writer.writeShort(packet.logo());
      writer.write(packet.logoColor());
   }

   protected void guildCapacityChange(MaplePacketLittleEndianWriter writer, GuildCapacityChange packet) {
      writer.write(0x3A);
      writer.writeInt(packet.guildId());
      writer.write(packet.capacity());
   }

   protected void showGuildRanks(MaplePacketLittleEndianWriter writer, ShowGuildRanks packet) {
      writer.write(0x49);
      writer.writeInt(packet.npcId());
      if (packet.ranks().size() == 0) { //no guilds o.o
         writer.writeInt(0);
         return;
      }
      writer.writeInt(packet.ranks().size()); //number of entries

      for (GuildData guildData : packet.ranks()) {
         writer.writeMapleAsciiString(guildData.name());
         writer.writeInt(guildData.gp());
         writer.writeInt(guildData.logo());
         writer.writeInt(guildData.logoColor());
         writer.writeInt(guildData.logoBackground());
         writer.writeInt(guildData.logoBackgroundColor());
      }
   }

   protected void showPlayerRanks(MaplePacketLittleEndianWriter writer, ShowPlayerRanks packet) {
      writer.write(0x49);
      writer.writeInt(packet.npcId());
      if (packet.ranks().isEmpty()) {
         writer.writeInt(0);
         return;
      }
      writer.writeInt(packet.ranks().size());
      for (GlobalUserRank wr : packet.ranks()) {
         writer.writeMapleAsciiString(wr.name());
         writer.writeInt(wr.level());
         writer.writeInt(0);
         writer.writeInt(0);
         writer.writeInt(0);
         writer.writeInt(0);
      }
      return;
   }

   protected void updateGP(MaplePacketLittleEndianWriter writer, UpdateGuildPoints packet) {
      writer.write(0x48);
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.points());
   }

   protected void showGuildInfo(MaplePacketLittleEndianWriter writer, ShowGuildInfo packet) {
      MapleCharacter character = packet.getCharacter();
      writer.write(0x1A); //signature for showing guild info
      if (character == null) { //show empty guild (used for leaving, expelled)
         writer.write(0);
         return;
      }
      character.getClient().getWorldServer().getGuild(character.getMGC()).ifPresentOrElse(guild -> {
         writer.write(1); //bInGuild
         writer.writeInt(guild.getId());
         writer.writeMapleAsciiString(guild.getName());
         for (int i = 1; i <= 5; i++) {
            writer.writeMapleAsciiString(guild.getRankTitle(i));
         }
         Collection<MapleGuildCharacter> members = guild.getMembers();
         writer.write(members.size()); //then it is the size of all the members
         for (MapleGuildCharacter mgc : members) {//and each of their character ids o_O
            writer.writeInt(mgc.getId());
         }
         for (MapleGuildCharacter mgc : members) {
            writer.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            writer.writeInt(mgc.getJobId());
            writer.writeInt(mgc.getLevel());
            writer.writeInt(mgc.getGuildRank());
            writer.writeInt(mgc.isOnline() ? 1 : 0);
            writer.writeInt(guild.getSignature());
            writer.writeInt(mgc.getAllianceRank());
         }
         writer.writeInt(guild.getCapacity());
         writer.writeShort(guild.getLogoBG());
         writer.write(guild.getLogoBGColor());
         writer.writeShort(guild.getLogo());
         writer.write(guild.getLogoColor());
         writer.writeMapleAsciiString(guild.getNotice());
         writer.writeInt(guild.getGP());
         writer.writeInt(guild.getAllianceId());

      }, () -> {
         //failed to read from DB - don't show a guild
         writer.write(0);
      });
   }

   /**
    * Guild Name & Mark update packet, thanks to Arnah (Vertisy)
    */
   protected void guildNameChanged(MaplePacketLittleEndianWriter writer, GuildNameChange packet) {
      writer.writeInt(packet.characterId());
      writer.writeMapleAsciiString(packet.guildName());
   }

   protected void guildMarkChanged(MaplePacketLittleEndianWriter writer, GuildMarkChanged packet) {
      writer.writeInt(packet.characterId());
      writer.writeShort(packet.logoBackground());
      writer.write(packet.logoBackgroundColor());
      writer.writeShort(packet.logo());
      writer.write(packet.logoColor());
   }
}