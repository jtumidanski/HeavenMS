package tools.packet.maker; 
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MakerEnableActions() implements PacketInput {
  @Override
  public SendOpcode opcode() {
    return SendOpcode.MAKER_RESULT;
  }
}