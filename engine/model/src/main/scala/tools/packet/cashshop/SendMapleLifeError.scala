package tools.packet.cashshop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendMapleLifeError(private var _code: Int) extends PacketInput {
  def code: Int = _code

  override def opcode(): SendOpcode = SendOpcode.MAPLELIFE_ERROR
}