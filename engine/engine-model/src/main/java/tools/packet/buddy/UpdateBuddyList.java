package tools.packet.buddy;

import java.util.Collection;

import client.BuddyListEntry;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateBuddyList(Collection<BuddyListEntry> buddies) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BUDDY_LIST;
   }
}
