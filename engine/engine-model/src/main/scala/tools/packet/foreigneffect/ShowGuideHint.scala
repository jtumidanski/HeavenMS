package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowGuideHint(private var _hint: Int) extends PacketInput {
  def hint: Int = _hint

  override def opcode(): SendOpcode = SendOpcode.TALK_GUIDE
}