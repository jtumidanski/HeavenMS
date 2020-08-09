package tools.packet.parcel;

import java.util.ArrayList;
import java.util.List;

import client.DueyAction;
import net.opcodes.SendOpcode;
import server.DueyPackage;
import tools.packet.PacketInput;

public record SendDuey(DueyAction operation, List<DueyPackage> packages) implements PacketInput {
   public SendDuey(DueyAction operation) {
      this(operation, new ArrayList<>());
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARCEL;
   }
}