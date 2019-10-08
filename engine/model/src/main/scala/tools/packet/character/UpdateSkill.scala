package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateSkill(private var _skillId: Int, private var _level: Int, private var _masterLevel: Int,
                  private var _expiration: Long) extends PacketInput {
  def skillId: Int = _skillId

  def level: Int = _level

  def masterLevel: Int = _masterLevel

  def expiration: Long = _expiration

  override def opcode(): SendOpcode = SendOpcode.UPDATE_SKILLS
}