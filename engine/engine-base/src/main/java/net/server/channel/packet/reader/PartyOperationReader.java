package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.party.BasePartyOperationPacket;
import net.server.channel.packet.party.ChangeLeaderPartyPacket;
import net.server.channel.packet.party.CreatePartyPacket;
import net.server.channel.packet.party.ExpelPartyPacket;
import net.server.channel.packet.party.InvitePartyPacket;
import net.server.channel.packet.party.JoinPartyPacket;
import net.server.channel.packet.party.LeavePartyPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PartyOperationReader implements PacketReader<BasePartyOperationPacket> {
   @Override
   public BasePartyOperationPacket read(SeekableLittleEndianAccessor accessor) {
      int operation = accessor.readByte();
      switch (operation) {
         case 1: {
            return new CreatePartyPacket(operation);
         }
         case 2: {
            return new LeavePartyPacket(operation);
         }
         case 3: { // join
            int partyid = accessor.readInt();
            return new JoinPartyPacket(operation, partyid);
         }
         case 4: { // invite
            String name = accessor.readMapleAsciiString();
            return new InvitePartyPacket(operation, name);
         }
         case 5: { // expel
            int cid = accessor.readInt();
            return new ExpelPartyPacket(operation, cid);
         }
         case 6: { // change leader
            int newLeader = accessor.readInt();
            return new ChangeLeaderPartyPacket(operation, newLeader);
         }
      }
      return new BasePartyOperationPacket(operation);
   }
}
