package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowTitleEarned(private var _message: String) extends PacketInput {
  def message: String = _message

  override def opcode(): SendOpcode = SendOpcode.SCRIPT_PROGRESS_MESSAGE
}