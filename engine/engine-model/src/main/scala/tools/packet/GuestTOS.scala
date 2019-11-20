package tools.packet
import net.opcodes.SendOpcode

class GuestTOS() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.GUEST_ID_LOGIN
}
