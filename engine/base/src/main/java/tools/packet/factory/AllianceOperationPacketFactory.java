package tools.packet.factory;

import java.util.Collection;
import java.util.Optional;

import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import tools.FilePrinter;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GetAllianceInfo) {
         return create(this::getAllianceInfo, packetInput);
      } else if (packetInput instanceof UpdateAllianceInfo) {
         return create(this::updateAllianceInfo, packetInput);
      } else if (packetInput instanceof GetGuildAlliances) {
         return create(this::getGuildAlliances, packetInput);
      } else if (packetInput instanceof AddGuildToAlliance) {
         return create(this::addGuildToAlliance, packetInput);
      } else if (packetInput instanceof AllianceMemberOnline) {
         return create(this::allianceMemberOnline, packetInput);
      } else if (packetInput instanceof AllianceNotice) {
         return create(this::allianceNotice, packetInput);
      } else if (packetInput instanceof ChangeAllianceRankTitles) {
         return create(this::changeAllianceRankTitle, packetInput);
      } else if (packetInput instanceof UpdateAllianceJobLevel) {
         return create(this::updateAllianceJobLevel, packetInput);
      } else if (packetInput instanceof RemoveGuildFromAlliance) {
         return create(this::removeGuildFromAlliance, packetInput);
      } else if (packetInput instanceof DisbandAlliance) {
         return create(this::disbandAlliance, packetInput);
      } else if (packetInput instanceof AllianceInvite) {
         return create(this::allianceInvite, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] getAllianceInfo(GetAllianceInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x0C);
      mplew.write(1);
      mplew.writeInt(packet.alliance().id());
      mplew.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         mplew.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      mplew.write(packet.alliance().guilds().size());
      mplew.writeInt(packet.alliance().capacity()); // probably capacity
      for (Integer guild : packet.alliance().guilds()) {
         mplew.writeInt(guild);
      }
      mplew.writeMapleAsciiString(packet.alliance().notice());
      return mplew.getPacket();
   }

   protected byte[] updateAllianceInfo(UpdateAllianceInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x0F);
      mplew.writeInt(packet.alliance().id());
      mplew.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         mplew.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      mplew.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         mplew.writeInt(guild);
      }
      mplew.writeInt(packet.alliance().capacity()); // probably capacity
      mplew.writeShort(0);
      packet.alliance().guilds().stream()
            .map(guildId -> Server.getInstance().getGuild(guildId, packet.worldId()))
            .flatMap(Optional::stream)
            .forEach(guild -> getGuildInfo(mplew, guild));
      return mplew.getPacket();
   }

   private void getGuildInfo(final MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
      mplew.writeInt(guild.getId());
      mplew.writeMapleAsciiString(guild.getName());
      for (int i = 1; i <= 5; i++) {
         mplew.writeMapleAsciiString(guild.getRankTitle(i));
      }
      Collection<MapleGuildCharacter> members = guild.getMembers();
      mplew.write(members.size());
      for (MapleGuildCharacter mgc : members) {
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
   }

   protected byte[] getGuildAlliances(GetGuildAlliances packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x0D);
      mplew.writeInt(packet.alliance().guilds().size());
      packet.alliance().guilds().stream()
            .map(guildId -> Server.getInstance().getGuild(guildId, packet.worldId()))
            .flatMap(Optional::stream)
            .forEach(guild -> getGuildInfo(mplew, guild));
      return mplew.getPacket();
   }

   protected byte[] addGuildToAlliance(AddGuildToAlliance packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x12);
      mplew.writeInt(packet.alliance().id());
      mplew.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         mplew.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      mplew.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         mplew.writeInt(guild);
      }
      mplew.writeInt(packet.alliance().capacity());
      mplew.writeMapleAsciiString(packet.alliance().notice());
      mplew.writeInt(packet.newGuildId());
      Server.getInstance().getGuild(packet.newGuildId(), packet.worldId(), null).ifPresent(guild -> getGuildInfo(mplew, guild));
      return mplew.getPacket();
   }

   protected byte[] allianceMemberOnline(AllianceMemberOnline packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x0E);
      mplew.writeInt(packet.allianceId());
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.online() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] allianceNotice(AllianceNotice packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x1C);
      mplew.writeInt(packet.id());
      mplew.writeMapleAsciiString(packet.notice());
      return mplew.getPacket();
   }

   protected byte[] changeAllianceRankTitle(ChangeAllianceRankTitles packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x1A);
      mplew.writeInt(packet.allianceId());
      for (int i = 0; i < 5; i++) {
         mplew.writeMapleAsciiString(packet.ranks()[i]);
      }
      return mplew.getPacket();
   }

   protected byte[] updateAllianceJobLevel(UpdateAllianceJobLevel packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x18);
      mplew.writeInt(packet.allianceId());
      mplew.writeInt(packet.guildId());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.level());
      mplew.writeInt(packet.jobId());
      return mplew.getPacket();
   }

   protected byte[] removeGuildFromAlliance(RemoveGuildFromAlliance packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x10);
      mplew.writeInt(packet.alliance().id());
      mplew.writeMapleAsciiString(packet.alliance().name());
      for (int i = 1; i <= 5; i++) {
         mplew.writeMapleAsciiString(packet.alliance().rankTitle(i));
      }
      mplew.write(packet.alliance().guilds().size());
      for (Integer guild : packet.alliance().guilds()) {
         mplew.writeInt(guild);
      }
      mplew.writeInt(packet.alliance().capacity());
      mplew.writeMapleAsciiString(packet.alliance().notice());
      mplew.writeInt(packet.expelledGuildId());
      Server.getInstance().getGuild(packet.expelledGuildId(), packet.worldId(), null).ifPresent(guild -> getGuildInfo(mplew, guild));
      mplew.write(0x01);
      return mplew.getPacket();
   }

   protected byte[] disbandAlliance(DisbandAlliance packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x1D);
      mplew.writeInt(packet.allianceId());
      return mplew.getPacket();
   }

   protected byte[] allianceInvite(AllianceInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      mplew.write(0x03);
      mplew.writeInt(packet.allianceId());
      mplew.writeMapleAsciiString(packet.characterName());
      mplew.writeShort(0);
      return mplew.getPacket();
   }
}