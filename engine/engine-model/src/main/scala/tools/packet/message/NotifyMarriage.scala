package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NotifyMarriage(private var _type: Int, private var _characterName: String) extends PacketInput {
  def theType: Int = _type

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.NOTIFY_MARRIAGE
}