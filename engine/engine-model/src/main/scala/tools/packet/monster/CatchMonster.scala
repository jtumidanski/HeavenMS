package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CatchMonster(private var _objectId: Int, private var _success: Byte) extends PacketInput {
  def objectId: Int = _objectId

  def success: Byte = _success

  override def opcode(): SendOpcode = SendOpcode.CATCH_MONSTER
}