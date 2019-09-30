package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowRecovery(private var _characterId: Int, private var _amount: Byte) extends PacketInput {
  def characterId: Int = _characterId

  def amount: Byte = _amount

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}