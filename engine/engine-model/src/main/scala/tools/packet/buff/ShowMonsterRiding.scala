package tools.packet.buff

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowMonsterRiding(private var _characterId: Int, private var _mountId: Int, private var _skillId: Int) extends PacketInput {
  def characterId: Int = _characterId

  def mountId: Int = _mountId

  def skillId: Int = _skillId

  override def opcode(): SendOpcode = SendOpcode.GIVE_FOREIGN_BUFF
}