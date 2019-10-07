package tools.packet.buff

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GiveForeignChairSkillEffect(private var _characterId: Int) extends PacketInput {
  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.GIVE_FOREIGN_BUFF
}