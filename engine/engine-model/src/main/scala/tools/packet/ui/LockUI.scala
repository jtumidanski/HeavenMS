package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class LockUI(private var _enable: Boolean) extends PacketInput {
  def enable: Boolean = _enable

  override def opcode(): SendOpcode = SendOpcode.LOCK_UI
}