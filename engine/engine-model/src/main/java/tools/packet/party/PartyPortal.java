package tools.packet.party;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PartyPortal(Integer townId, Integer targetId, Point position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }
}