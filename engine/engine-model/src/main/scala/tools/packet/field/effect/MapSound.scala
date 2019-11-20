package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MapSound(private var _path: String) extends PacketInput {
  def path: String = _path

  override def opcode(): SendOpcode = SendOpcode.FIELD_EFFECT
}