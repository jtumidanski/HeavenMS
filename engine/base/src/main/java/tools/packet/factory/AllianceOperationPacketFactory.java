package tools.packet.factory;

import java.util.Collection;
import java.util.Optional;

import net.server.Server;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.alliance.AddGuildToAlliance;
import tools.packet.alliance.AllianceInvite;
import tools.packet.alliance.AllianceMemberOnline;
import tools.packet.alliance.AllianceNotice;
import tools.packet.alliance.ChangeAllianceRankTitles;
import tools.packet.alliance.DisbandAlliance;
import tools.packet.alliance.GetAllianceInfo;
import tools.packet.alliance.GetGuildAlliances;
import tools.packet.alliance.RemoveGuildFromAlliance;
import tools.packet.alliance.UpdateAllianceInfo;
import tools.packet.alliance.UpdateAllianceJobLevel;

public class AllianceOperationPacketFactory extends AbstractPacketFactory {
   private static AllianceOperationPacketFactory instance;

   public static AllianceOperationPacketFactory getInstance() {
      if (instance == null) {
         instance = new AllianceOperationPacketFactory();
      }
      return instance;
   }

   private AllianceOperationPacketFactory() {
      Handler.handle(GetAllianceInfo.class).decorate(this::getAllianceInfo).register(registry);
      Handler.handle(UpdateAllianceInfo.class).decorate(this::updateAllianceInfo).register(registry);
      Handler.handle(GetGuildAlliances.class).decorate(this::getGuildAlliances).register(registry);
      Handler.handle(AddGuildToAlliance.class).decorate(this::addGuildToAlliance).register(registry);
      Handler.handle(AllianceMemberOnline.class).decorate(this::allianceMemberOnline).register(registry);
      Handler.handle(AllianceNotice.class).decorate(this::allianceNotice).register(registry);
      Handler.handle(ChangeAllianceRankTitles.class).decorate(this::changeAllianceRankTitle).register(registry);
      Handler.handle(UpdateAllianceJobLevel.class).decorate(this::updateAllianceJobLevel).register(registry);
      Handler.handle(RemoveGuildFromAlliance.class).decorate(this::removeGuildFromAlliance).register(registry);
      Handler.handle(DisbandAlliance.class).decorate(this::disbandAlliance).register(registry);
      Handler.handle(AllianceInvite.class).decorate(this::allianceInvite).register(registry);
   }

   protected void getAllianceInfo(MaplePacketLittleEndianWriter writer, GetAllianceInfo packet) {
      writer.write(0x0C);
      writer.write(1);
      writer.writeInt(packet.alliance().id());
      writer.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         writer.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      writer.write(packet.alliance().guilds().size());
      writer.writeInt(packet.alliance().capacity()); // probably capacity
      for (Integer guild : packet.alliance().guilds()) {
         writer.writeInt(guild);
      }
      writer.writeMapleAsciiString(packet.alliance().notice());
   }

   protected void updateAllianceInfo(MaplePacketLittleEndianWriter writer, UpdateAllianceInfo packet) {
      writer.write(0x0F);
      writer.writeInt(packet.alliance().id());
      writer.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         writer.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      writer.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         writer.writeInt(guild);
      }
      writer.writeInt(packet.alliance().capacity()); // probably capacity
      writer.writeShort(0);
      packet.alliance().guilds().stream()
            .map(guildId -> Server.getInstance().getGuild(guildId, packet.worldId()))
            .flatMap(Optional::stream)
            .forEach(guild -> getGuildInfo(writer, guild));
   }

   protected void getGuildInfo(final MaplePacketLittleEndianWriter writer, MapleGuild guild) {
      writer.writeInt(guild.getId());
      writer.writeMapleAsciiString(guild.getName());
      for (int i = 1; i <= 5; i++) {
         writer.writeMapleAsciiString(guild.getRankTitle(i));
      }
      Collection<MapleGuildCharacter> members = guild.getMembers();
      writer.write(members.size());
      for (MapleGuildCharacter mgc : members) {
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
   }

   protected void getGuildAlliances(MaplePacketLittleEndianWriter writer, GetGuildAlliances packet) {
      writer.write(0x0D);
      writer.writeInt(packet.alliance().guilds().size());
      packet.alliance().guilds().stream()
            .map(guildId -> Server.getInstance().getGuild(guildId, packet.worldId()))
            .flatMap(Optional::stream)
            .forEach(guild -> getGuildInfo(writer, guild));
   }

   protected void addGuildToAlliance(MaplePacketLittleEndianWriter writer, AddGuildToAlliance packet) {
      writer.write(0x12);
      writer.writeInt(packet.alliance().id());
      writer.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         writer.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      writer.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         writer.writeInt(guild);
      }
      writer.writeInt(packet.alliance().capacity());
      writer.writeMapleAsciiString(packet.alliance().notice());
      writer.writeInt(packet.newGuildId());
      Server.getInstance().getGuild(packet.newGuildId(), packet.worldId(), null).ifPresent(guild -> getGuildInfo(writer, guild));
   }

   protected void allianceMemberOnline(MaplePacketLittleEndianWriter writer, AllianceMemberOnline packet) {
      writer.write(0x0E);
      writer.writeInt(packet.allianceId());
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.write(packet.online() ? 1 : 0);
   }

   protected void allianceNotice(MaplePacketLittleEndianWriter writer, AllianceNotice packet) {
      writer.write(0x1C);
      writer.writeInt(packet.id());
      writer.writeMapleAsciiString(packet.notice());
   }

   protected void changeAllianceRankTitle(MaplePacketLittleEndianWriter writer, ChangeAllianceRankTitles packet) {
      writer.write(0x1A);
      writer.writeInt(packet.allianceId());
      for (int i = 0; i < 5; i++) {
         writer.writeMapleAsciiString(packet.ranks()[i]);
      }
   }

   protected void updateAllianceJobLevel(MaplePacketLittleEndianWriter writer, UpdateAllianceJobLevel packet) {
      writer.write(0x18);
      writer.writeInt(packet.allianceId());
      writer.writeInt(packet.guildId());
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.level());
      writer.writeInt(packet.jobId());
   }

   protected void removeGuildFromAlliance(MaplePacketLittleEndianWriter writer, RemoveGuildFromAlliance packet) {
      writer.write(0x10);
      writer.writeInt(packet.alliance().id());
      writer.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         writer.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      writer.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         writer.writeInt(guild);
      }
      writer.writeInt(packet.alliance().capacity());
      writer.writeMapleAsciiString(packet.alliance().notice());
      writer.writeInt(packet.expelledGuildId());
      Server.getInstance().getGuild(packet.expelledGuildId(), packet.worldId(), null).ifPresent(guild -> getGuildInfo(writer, guild));
      writer.write(0x01);
   }

   protected void disbandAlliance(MaplePacketLittleEndianWriter writer, DisbandAlliance packet) {
      writer.write(0x1D);
      writer.writeInt(packet.allianceId());
   }

   protected void allianceInvite(MaplePacketLittleEndianWriter writer, AllianceInvite packet) {
      writer.write(0x03);
      writer.writeInt(packet.allianceId());
      writer.writeMapleAsciiString(packet.characterName());
      writer.writeShort(0);
   }
}