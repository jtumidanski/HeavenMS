package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveKite(private var _objectId: Int, private var _animationType: Int) extends PacketInput {
  def objectId: Int = _objectId

  def animationType: Int = _animationType

  override def opcode(): SendOpcode = SendOpcode.REMOVE_KITE
}