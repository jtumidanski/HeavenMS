package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GiveFameResponse(private var _mode: Int, private var _characterName: String, private var _newFame: Int) extends PacketInput {
  def mode: Int = _mode

  def characterName: String = _characterName

  def newFame: Int = _newFame

  override def opcode(): SendOpcode = SendOpcode.FAME_RESPONSE
}