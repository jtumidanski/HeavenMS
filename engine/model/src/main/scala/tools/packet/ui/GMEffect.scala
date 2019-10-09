package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GMEffect(private var _type: Int, private var _mode: Byte) extends PacketInput {
  def theType: Int = _type

  def mode: Byte = _mode

  override def opcode(): SendOpcode = SendOpcode.ADMIN_RESULT
}