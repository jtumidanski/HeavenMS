package net.server.channel.packet.reader;

import net.server.channel.packet.alliance.AcceptedInvitePacket;
import net.server.channel.packet.alliance.AllianceOperationPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AllianceRegisterOperationReader extends AllianceOperationReader {
   @Override
   public AllianceOperationPacket read(SeekableLittleEndianAccessor accessor) {
      byte b = accessor.readByte();
      if (b == 4) {
         return readAcceptedInvite(accessor);
      }
      return new AllianceOperationPacket();
   }

   private AllianceOperationPacket readAcceptedInvite(SeekableLittleEndianAccessor accessor) {
      int allianceId = accessor.readInt();
      String recruiterGuild = accessor.readMapleAsciiString();
      return new AcceptedInvitePacket(allianceId, recruiterGuild);
   }
}
