package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class OpenUI(private var _ui: Byte) extends PacketInput {
  def ui: Byte = _ui

  override def opcode(): SendOpcode = SendOpcode.OPEN_UI
}