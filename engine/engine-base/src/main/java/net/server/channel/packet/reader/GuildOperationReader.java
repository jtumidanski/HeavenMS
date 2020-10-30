package net.server.channel.packet.reader;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.PacketReader;
import net.server.channel.packet.guild.BaseGuildOperationPacket;
import net.server.channel.packet.guild.ChangeGuildEmblemPacket;
import net.server.channel.packet.guild.ChangeGuildNoticePacket;
import net.server.channel.packet.guild.ChangeGuildRankAndTitlePacket;
import net.server.channel.packet.guild.ChangeGuildRankPacket;
import net.server.channel.packet.guild.CreateGuildPacket;
import net.server.channel.packet.guild.ExpelFromGuildPacket;
import net.server.channel.packet.guild.GuildMatchPacket;
import net.server.channel.packet.guild.InviteToGuildPacket;
import net.server.channel.packet.guild.JoinGuildPacket;
import net.server.channel.packet.guild.LeaveGuildPacket;
import net.server.channel.packet.guild.ShowGuildInformationPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class GuildOperationReader implements PacketReader<BaseGuildOperationPacket> {
   @Override
   public BaseGuildOperationPacket read(SeekableLittleEndianAccessor accessor) {
      byte type = accessor.readByte();
      switch (type) {
         case 0x00:
            return readShowGuildInformation(type);
         case 0x02:
            return readCreate(accessor, type);
         case 0x05:
            return readInvite(accessor, type);
         case 0x06:
            return readJoin(accessor, type);
         case 0x07:
            return readLeave(accessor, type);
         case 0x08:
            return readExpel(accessor, type);
         case 0x0d:
            return readChangeGuildRankAndTitle(accessor, type);
         case 0x0e:
            return readChangeRank(accessor, type);
         case 0x0f:
            return readChangeEmblem(accessor, type);
         case 0x10:
            return readChangeNotice(accessor, type);
         case 0x1E:
            return readMatch(accessor, type);
         default:
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.UNHANDLED_EVENT, "Unhandled GUILD_OPERATION packet: \n" + accessor.toString());
      }

      return new BaseGuildOperationPacket(type);
   }

   private BaseGuildOperationPacket readShowGuildInformation(byte type) {
      return new ShowGuildInformationPacket(type);
   }

   private BaseGuildOperationPacket readMatch(SeekableLittleEndianAccessor accessor, byte type) {
      accessor.readInt();
      boolean result = accessor.readByte() != 0;
      return new GuildMatchPacket(type, result);
   }

   private BaseGuildOperationPacket readChangeNotice(SeekableLittleEndianAccessor accessor, byte type) {
      String notice = accessor.readMapleAsciiString();
      return new ChangeGuildNoticePacket(type, notice);
   }

   private BaseGuildOperationPacket readChangeEmblem(SeekableLittleEndianAccessor accessor, byte type) {
      short background = accessor.readShort();
      byte backgroundColor = accessor.readByte();
      short logo = accessor.readShort();
      byte logoColor = accessor.readByte();
      return new ChangeGuildEmblemPacket(type, background, backgroundColor, logo, logoColor);
   }

   private BaseGuildOperationPacket readChangeRank(SeekableLittleEndianAccessor accessor, byte type) {
      int playerId = accessor.readInt();
      byte newRank = accessor.readByte();
      return new ChangeGuildRankPacket(type, playerId, newRank);
   }

   private BaseGuildOperationPacket readChangeGuildRankAndTitle(SeekableLittleEndianAccessor accessor, byte type) {
      String[] ranks = new String[5];
      for (int i = 0; i < 5; i++) {
         ranks[i] = accessor.readMapleAsciiString();
      }
      return new ChangeGuildRankAndTitlePacket(type, ranks);
   }

   private BaseGuildOperationPacket readExpel(SeekableLittleEndianAccessor accessor, byte type) {
      int playerId = accessor.readInt();
      String playerName = accessor.readMapleAsciiString();
      return new ExpelFromGuildPacket(type, playerId, playerName);
   }

   private BaseGuildOperationPacket readLeave(SeekableLittleEndianAccessor accessor, byte type) {
      int playerId = accessor.readInt();
      String playerName = accessor.readMapleAsciiString();
      return new LeaveGuildPacket(type, playerId, playerName);
   }

   private BaseGuildOperationPacket readJoin(SeekableLittleEndianAccessor accessor, byte type) {
      int guildId = accessor.readInt();
      int playerId = accessor.readInt();
      return new JoinGuildPacket(type, guildId, playerId);
   }

   private BaseGuildOperationPacket readInvite(SeekableLittleEndianAccessor accessor, byte type) {
      String playerName = accessor.readMapleAsciiString();
      return new InviteToGuildPacket(type, playerName);
   }

   private BaseGuildOperationPacket readCreate(SeekableLittleEndianAccessor accessor, byte type) {
      String guildName = accessor.readMapleAsciiString();
      return new CreateGuildPacket(type, guildName);
   }
}
