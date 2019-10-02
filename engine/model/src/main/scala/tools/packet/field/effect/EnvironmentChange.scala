package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class EnvironmentChange(private var _env: String, private var _mode: Int) extends PacketInput {
  def env: String = _env

  def mode: Int = _mode

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}