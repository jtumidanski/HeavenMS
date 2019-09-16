package net.server.channel.packet.reader;

import net.server.channel.packet.alliance.AllianceAlreadyRegisteredPacket;
import net.server.channel.packet.alliance.AllianceInvitePacket;
import net.server.channel.packet.alliance.AllianceMessagePacket;
import net.server.channel.packet.alliance.AllianceNoticePacket;
import net.server.channel.packet.alliance.AllianceOperationPacket;
import net.server.channel.packet.alliance.AlliancePlayerRankDataPacket;
import net.server.channel.packet.alliance.AllianceRankDataPacket;
import net.server.channel.packet.alliance.ChangeAllianceLeaderPacket;
import net.server.channel.packet.alliance.ExpelGuildPacket;
import net.server.channel.packet.alliance.LeaveAlliancePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ExistingAllianceOperationReader extends AllianceOperationReader {
   @Override
   public AllianceOperationPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();

      switch (mode) {
         case 0x01:
            return new AllianceMessagePacket();
         case 0x02:
            return new LeaveAlliancePacket();
         case 0x03:
            String guildName = accessor.readMapleAsciiString();
            return new AllianceInvitePacket(guildName);
         case 0x04:
            return new AllianceAlreadyRegisteredPacket();
         case 0x06:
            return readExpelGuild(accessor);
         case 0x07:
            return readAllianceChangeLeader(accessor);
         case 0x08:
            return readAllianceRanks(accessor);
         case 0x09:
            return readPlayerRankData(accessor);
         case 0x0A:
            return readAllianceNotice(accessor);
      }

      return new AllianceOperationPacket();
   }

   private AllianceOperationPacket readExpelGuild(SeekableLittleEndianAccessor accessor) {
      int guildId = accessor.readInt();
      int allianceId = accessor.readInt();
      return new ExpelGuildPacket(guildId, allianceId);
   }

   private AllianceOperationPacket readAllianceChangeLeader(SeekableLittleEndianAccessor accessor) {
      int playerId = accessor.readInt();
      return new ChangeAllianceLeaderPacket(playerId);
   }

   private AllianceOperationPacket readAllianceRanks(SeekableLittleEndianAccessor accessor) {
      String[] ranks = new String[5];
      for (int i = 0; i < 5; i++) {
         ranks[i] = accessor.readMapleAsciiString();
      }
      return new AllianceRankDataPacket(ranks);
   }

   private AllianceOperationPacket readPlayerRankData(SeekableLittleEndianAccessor accessor) {
      int playerId = accessor.readInt();
      boolean rankRaised = accessor.readByte() > 0;
      return new AlliancePlayerRankDataPacket(playerId, rankRaised);
   }

   private AllianceOperationPacket readAllianceNotice(SeekableLittleEndianAccessor accessor) {
      String notice = accessor.readMapleAsciiString();
      return new AllianceNoticePacket(notice);
   }
}
