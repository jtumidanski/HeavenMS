package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateMount(private var _characterId: Int, private var _mountLevel: Int, private var _mountExp: Int,
                  private var _mountTiredness: Int, private var _levelUp: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def mountLevel: Int = _mountLevel

  def mountExp: Int = _mountExp

  def mountTiredness: Int = _mountTiredness

  def levelUp: Boolean = _levelUp

  override def opcode(): SendOpcode = SendOpcode.SET_TAMING_MOB_INFO
}