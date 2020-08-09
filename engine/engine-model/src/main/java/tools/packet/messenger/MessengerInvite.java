package tools.packet.messenger; 
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MessengerInvite(String characterNameFrom, Integer messengerId) implements PacketInput {
  @Override
  public SendOpcode opcode() {
    return SendOpcode.MESSENGER;
  }
}