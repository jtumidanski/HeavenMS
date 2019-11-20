package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MiniGameClose(private var _visitor: Boolean, private var _type: Int) extends PacketInput {
  def visitor: Boolean = _visitor

  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}