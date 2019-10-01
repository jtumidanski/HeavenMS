package tools.packet.family;

import client.MapleFamilyEntry;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class ShowPedigree implements PacketInput {
   private MapleFamilyEntry familyEntry;

   public ShowPedigree(MapleFamilyEntry familyEntry) {
      this.familyEntry = familyEntry;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_CHART_RESULT;
   }

   public MapleFamilyEntry getFamilyEntry() {
      return familyEntry;
   }
}
