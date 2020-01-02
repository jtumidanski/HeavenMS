package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NotifyLevelUp(private var _type: Int, private var _level: Int, private var _characterName: String) extends PacketInput {
  def theType: Int = _type

  def level: Int = _level

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.NOTIFY_LEVEL_UP
}