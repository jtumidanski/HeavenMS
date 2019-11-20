package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CatchMonsterFailure(private var _message: Int) extends PacketInput {
  def message: Int = _message

  override def opcode(): SendOpcode = SendOpcode.BRIDLE_MOB_CATCH_FAIL
}