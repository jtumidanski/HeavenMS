package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ForcedEquip(private var _team: Int) extends PacketInput {
  def team: Int = _team

  override def opcode(): SendOpcode = SendOpcode.FORCED_MAP_EQUIP
}