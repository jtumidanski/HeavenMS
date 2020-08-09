package tools.packet.monster; 
import java.util.Map;

import client.status.MonsterStatus;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelMonsterStatus( Integer objectId,  Map<MonsterStatus, Integer> stats) implements PacketInput {
  @Override
  public SendOpcode opcode() {
    return SendOpcode.CANCEL_MONSTER_STATUS;
  }
}