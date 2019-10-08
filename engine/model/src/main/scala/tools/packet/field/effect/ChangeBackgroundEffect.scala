package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ChangeBackgroundEffect(private var _remove: Boolean, private var _layer: Int, private var _transition: Int) extends PacketInput {
  def remove: Boolean = _remove

  def layer: Int = _layer

  def transition: Int = _transition

  override def opcode(): SendOpcode = SendOpcode.SET_BACK_EFFECT
}