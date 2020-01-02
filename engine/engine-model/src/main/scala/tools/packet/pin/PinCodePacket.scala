package tools.packet.pin

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PinCodePacket(private var _operation: PinOperation) extends PacketInput {
  def operation: PinOperation = _operation

  override def opcode(): SendOpcode = SendOpcode.CHECK_PIN_CODE
}
