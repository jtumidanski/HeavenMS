package tools.packet.field.obstacle

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class EnvironmentMove(private var _environment: String, private var _mode: Int) extends PacketInput {
  def environment: String = _environment

  def mode: Int = _mode

  override def opcode(): SendOpcode = SendOpcode.FIELD_OBSTACLE_ONOFF
}