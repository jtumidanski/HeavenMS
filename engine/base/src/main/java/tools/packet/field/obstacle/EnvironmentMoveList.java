package tools.packet.field.obstacle;

import java.util.Map;
import java.util.Set;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class EnvironmentMoveList implements PacketInput {
   private Set<Map.Entry<String, Integer>> environmentMoveList;

   public EnvironmentMoveList(Set<Map.Entry<String, Integer>> environmentMoveList) {
      this.environmentMoveList = environmentMoveList;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_OBSTACLE_ONOFF_LIST;
   }

   public Set<Map.Entry<String, Integer>> getEnvironmentMoveList() {
      return environmentMoveList;
   }
}