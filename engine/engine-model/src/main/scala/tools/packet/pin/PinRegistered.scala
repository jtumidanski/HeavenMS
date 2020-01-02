package tools.packet.pin

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PinRegistered() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.UPDATE_PIN_CODE
}
