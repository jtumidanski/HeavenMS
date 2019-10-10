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
      registry.setHandler(GuildMemberOnline.class, packet -> this.guildMemberOnline((GuildMemberOnline) packet));
      registry.setHandler(GuildInvite.class, packet -> this.guildInvite((GuildInvite) packet));
      registry.setHandler(CreateGuildMessage.class, packet -> this.createGuildMessage((CreateGuildMessage) packet));
      registry.setHandler(GenericGuildMessage.class, packet -> this.genericGuildMessage((GenericGuildMessage) packet));
      registry.setHandler(ResponseGuildMessage.class, packet -> this.responseGuildMessage((ResponseGuildMessage) packet));
      registry.setHandler(NewGuildMember.class, packet -> this.newGuildMember((NewGuildMember) packet));
      registry.setHandler(GuildMemberLeft.class, packet -> this.memberLeft((GuildMemberLeft) packet));
      registry.setHandler(GuildMemberChangeRank.class, packet -> this.changeRank((GuildMemberChangeRank) packet));
      registry.setHandler(GuildNotice.class, packet -> this.guildNotice((GuildNotice) packet));
      registry.setHandler(GuildMemberLevelJobUpdate.class, packet -> this.guildMemberLevelJobUpdate((GuildMemberLevelJobUpdate) packet));
      registry.setHandler(GuildRankTitleChange.class, packet -> this.rankTitleChange((GuildRankTitleChange) packet));
      registry.setHandler(GuildDisband.class, packet -> this.guildDisband((GuildDisband) packet));
      registry.setHandler(GuildQuestWaitingNotice.class, packet -> this.guildQuestWaitingNotice((GuildQuestWaitingNotice) packet));
      registry.setHandler(GuildEmblemChange.class, packet -> this.guildEmblemChange((GuildEmblemChange) packet));
      registry.setHandler(GuildCapacityChange.class, packet -> this.guildCapacityChange((GuildCapacityChange) packet));
      registry.setHandler(ShowGuildRanks.class, packet -> this.showGuildRanks((ShowGuildRanks) packet));
      registry.setHandler(ShowPlayerRanks.class, packet -> this.showPlayerRanks((ShowPlayerRanks) packet));
      registry.setHandler(UpdateGuildPoints.class, packet -> this.updateGP((UpdateGuildPoints) packet));
      registry.setHandler(ShowGuildInfo.class, packet -> this.showGuildInfo((ShowGuildInfo) packet));
      registry.setHandler(GuildNameChange.class, packet -> this.guildNameChanged((GuildNameChange) packet));
      registry.setHandler(GuildMarkChanged.class, packet -> this.guildMarkChanged((GuildMarkChanged) packet));
   }

   protected byte[] guildMemberOnline(GuildMemberOnline packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x3d);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.online() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] guildInvite(GuildInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x05);
      mplew.writeInt(packet.guildId());
      mplew.writeMapleAsciiString(packet.characterName());
      return mplew.getPacket();
   }

   protected byte[] createGuildMessage(CreateGuildMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x3);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString(packet.masterCharacterName());
      mplew.writeMapleAsciiString(packet.guildName());
      return mplew.getPacket();
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
   protected byte[] genericGuildMessage(GenericGuildMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(packet.code());
      return mplew.getPacket();
   }

   /**
    * Gets a guild message packet appended with target name.
    * <p>
    * 53: player not accepting guild invites<br>
    * 54: player already managing an invite<br> 55: player denied an invite<br>
    *
    * @return The guild message packet.
    */
   protected byte[] responseGuildMessage(ResponseGuildMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(packet.code());
      mplew.writeMapleAsciiString(packet.targetName());
      return mplew.getPacket();
   }

   protected byte[] newGuildMember(NewGuildMember packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x27);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.writeAsciiString(StringUtil.getRightPaddedStr(packet.name(), '\0', 13));
      mplew.writeInt(packet.jobId());
      mplew.writeInt(packet.level());
      mplew.writeInt(packet.guildRank()); //should be always 5 but whatevs
      mplew.writeInt(packet.online() ? 1 : 0); //should always be 1 too
      mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
      mplew.writeInt(3);
      return mplew.getPacket();
   }

   //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
   protected byte[] memberLeft(GuildMemberLeft packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(packet.expelled() ? 0x2f : 0x2c);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.writeMapleAsciiString(packet.characterName());
      return mplew.getPacket();
   }

   //rank change
   protected byte[] changeRank(GuildMemberChangeRank packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x40);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.guildRank());
      return mplew.getPacket();
   }

   protected byte[] guildNotice(GuildNotice packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x44);
      mplew.writeInt(packet.guildId());
      mplew.writeMapleAsciiString(packet.notice());
      return mplew.getPacket();
   }

   protected byte[] guildMemberLevelJobUpdate(GuildMemberLevelJobUpdate packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x3C);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.level());
      mplew.writeInt(packet.jobId());
      return mplew.getPacket();
   }

   protected byte[] rankTitleChange(GuildRankTitleChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x3E);
      mplew.writeInt(packet.guildId());
      for (int i = 0; i < 5; i++) {
         mplew.writeMapleAsciiString(packet.ranks()[i]);
      }
      return mplew.getPacket();
   }

   protected byte[] guildDisband(GuildDisband packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x32);
      mplew.writeInt(packet.guildId());
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] guildQuestWaitingNotice(GuildQuestWaitingNotice packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x4C);
      mplew.write(packet.channel() - 1);
      mplew.write(packet.position());
      return mplew.getPacket();
   }

   protected byte[] guildEmblemChange(GuildEmblemChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x42);
      mplew.writeInt(packet.guildId());
      mplew.writeShort(packet.background());
      mplew.write(packet.backgroundColor());
      mplew.writeShort(packet.logo());
      mplew.write(packet.logoColor());
      return mplew.getPacket();
   }

   protected byte[] guildCapacityChange(GuildCapacityChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x3A);
      mplew.writeInt(packet.guildId());
      mplew.write(packet.capacity());
      return mplew.getPacket();
   }

   protected byte[] showGuildRanks(ShowGuildRanks packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x49);
      mplew.writeInt(packet.npcId());
      if (packet.ranks().size() == 0) { //no guilds o.o
         mplew.writeInt(0);
         return mplew.getPacket();
      }
      mplew.writeInt(packet.ranks().size()); //number of entries

      for (GuildData guildData : packet.ranks()) {
         mplew.writeMapleAsciiString(guildData.name());
         mplew.writeInt(guildData.gp());
         mplew.writeInt(guildData.logo());
         mplew.writeInt(guildData.logoColor());
         mplew.writeInt(guildData.logoBackground());
         mplew.writeInt(guildData.logoBackgroundColor());
      }
      return mplew.getPacket();
   }

   protected byte[] showPlayerRanks(ShowPlayerRanks packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x49);
      mplew.writeInt(packet.npcId());
      if (packet.ranks().isEmpty()) {
         mplew.writeInt(0);
         return mplew.getPacket();
      }
      mplew.writeInt(packet.ranks().size());
      for (GlobalUserRank wr : packet.ranks()) {
         mplew.writeMapleAsciiString(wr.name());
         mplew.writeInt(wr.level());
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   protected byte[] updateGP(UpdateGuildPoints packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x48);
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.points());
      return mplew.getPacket();
   }

   protected byte[] showGuildInfo(ShowGuildInfo packet) {
      MapleCharacter character = packet.getCharacter();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
      mplew.write(0x1A); //signature for showing guild info
      if (character == null) { //show empty guild (used for leaving, expelled)
         mplew.write(0);
         return mplew.getPacket();
      }
      character.getClient().getWorldServer().getGuild(character.getMGC()).ifPresentOrElse(guild -> {
         mplew.write(1); //bInGuild
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString(guild.getName());
         for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
         }
         Collection<MapleGuildCharacter> members = guild.getMembers();
         mplew.write(members.size()); //then it is the size of all the members
         for (MapleGuildCharacter mgc : members) {//and each of their character ids o_O
            mplew.writeInt(mgc.getId());
         }
         for (MapleGuildCharacter mgc : members) {
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(guild.getSignature());
            mplew.writeInt(mgc.getAllianceRank());
         }
         mplew.writeInt(guild.getCapacity());
         mplew.writeShort(guild.getLogoBG());
         mplew.write(guild.getLogoBGColor());
         mplew.writeShort(guild.getLogo());
         mplew.write(guild.getLogoColor());
         mplew.writeMapleAsciiString(guild.getNotice());
         mplew.writeInt(guild.getGP());
         mplew.writeInt(guild.getAllianceId());

      }, () -> {
         //failed to read from DB - don't show a guild
         mplew.write(0);
      });
      return mplew.getPacket();
   }

   /**
    * Guild Name & Mark update packet, thanks to Arnah (Vertisy)
    */
   protected byte[] guildNameChanged(GuildNameChange packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_NAME_CHANGED.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeMapleAsciiString(packet.guildName());
      return mplew.getPacket();
   }

   protected byte[] guildMarkChanged(GuildMarkChanged packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_MARK_CHANGED.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeShort(packet.logoBackground());
      mplew.write(packet.logoBackgroundColor());
      mplew.writeShort(packet.logo());
      mplew.write(packet.logoColor());
      return mplew.getPacket();
   }
}