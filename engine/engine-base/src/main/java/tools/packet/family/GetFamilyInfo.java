package tools.packet.family;

import client.MapleFamilyEntry;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class GetFamilyInfo implements PacketInput {
   private MapleFamilyEntry familyEntry;

   public GetFamilyInfo(MapleFamilyEntry familyEntry) {
      this.familyEntry = familyEntry;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_INFO_RESULT;
   }

   public MapleFamilyEntry getFamilyEntry() {
      return familyEntry;
   }
}
