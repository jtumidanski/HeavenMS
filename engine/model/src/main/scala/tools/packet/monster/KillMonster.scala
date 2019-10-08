package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class KillMonster(private var _objectId: Int, private var _animation: Int) extends PacketInput {
  def objectId: Int = _objectId

  def animation: Int = _animation

  def this(_objectId: Int, _animation: Boolean) = {
    this(_objectId, if (_animation) 1 else 0)
  }

  override def opcode(): SendOpcode = SendOpcode.KILL_MONSTER
}