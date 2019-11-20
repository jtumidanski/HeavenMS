package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowForeignInfo(private var _characterId: Int, private var _path: String) extends PacketInput {
  def characterId: Int = _characterId

  def path: String = _path

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}