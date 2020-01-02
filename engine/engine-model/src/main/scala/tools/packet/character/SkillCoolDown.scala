package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SkillCoolDown(private var _skillId: Int, private var _time: Int) extends PacketInput {
  def skillId: Int = _skillId

  def time: Int = _time

  override def opcode(): SendOpcode = SendOpcode.COOL_DOWN
}