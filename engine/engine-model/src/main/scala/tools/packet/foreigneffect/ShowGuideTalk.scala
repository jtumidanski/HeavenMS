package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowGuideTalk(private var _talk: String) extends PacketInput {
  def talk: String = _talk

  override def opcode(): SendOpcode = SendOpcode.TALK_GUIDE
}