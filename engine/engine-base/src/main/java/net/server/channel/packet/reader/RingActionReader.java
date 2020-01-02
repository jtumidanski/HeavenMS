package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ring.BaseRingPacket;
import net.server.channel.packet.ring.BreakEngagementPacket;
import net.server.channel.packet.ring.CancelProposal;
import net.server.channel.packet.ring.EngagementProposalPacket;
import net.server.channel.packet.ring.HandleWishListPacket;
import net.server.channel.packet.ring.InviteToWeddingPacket;
import net.server.channel.packet.ring.OpenWeddingInvitationPacket;
import net.server.channel.packet.ring.RespondToProposalPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class RingActionReader implements PacketReader<BaseRingPacket> {
   @Override
   public BaseRingPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      switch (mode) {
         case 0:
            return readEngagementProposal(accessor, mode);
         case 1:
            return readCancelProposal(mode);
         case 2:
            return readRespondToProposal(accessor, mode);
         case 3:
            return readBreakEngagement(accessor, mode);
         case 5:
            return readInviteToWedding(accessor, mode);
         case 6:
            return readOpenWeddingInvitation(accessor, mode);
         case 9:
            return readHandleWishList(accessor, mode);
      }
      return new BaseRingPacket(mode);
   }

   private BaseRingPacket readHandleWishList(SeekableLittleEndianAccessor accessor, byte mode) {
      int amount = accessor.readShort();
      if (amount > 10) {
         amount = 10;
      }
      String[] items = new String[amount];
      for (int i = 0; i < amount; i++) {
         items[i] = accessor.readMapleAsciiString();
      }
      return new HandleWishListPacket(mode, amount, items);
   }

   private BaseRingPacket readOpenWeddingInvitation(SeekableLittleEndianAccessor accessor, byte mode) {
      byte slot = (byte) accessor.readInt();
      int invitationId = accessor.readInt();
      return new OpenWeddingInvitationPacket(mode, slot, invitationId);
   }

   private BaseRingPacket readInviteToWedding(SeekableLittleEndianAccessor accessor, byte mode) {
      String name = accessor.readMapleAsciiString();
      int marriageId = accessor.readInt();
      byte slot = accessor.readByte();
      return new InviteToWeddingPacket(mode, name, marriageId, slot);
   }

   private BaseRingPacket readBreakEngagement(SeekableLittleEndianAccessor accessor, byte mode) {
      return new BreakEngagementPacket(mode, accessor.readInt());
   }

   private BaseRingPacket readRespondToProposal(SeekableLittleEndianAccessor accessor, byte mode) {
      final boolean accepted = accessor.readByte() > 0;
      String name = accessor.readMapleAsciiString();
      final int id = accessor.readInt();
      return new RespondToProposalPacket(mode, accepted, name, id);
   }

   private BaseRingPacket readCancelProposal(byte mode) {
      return new CancelProposal(mode);
   }

   private BaseRingPacket readEngagementProposal(SeekableLittleEndianAccessor accessor, byte mode) {
      String name = accessor.readMapleAsciiString();
      int itemId = accessor.readInt();
      return new EngagementProposalPacket(mode, name, itemId);
   }
}
