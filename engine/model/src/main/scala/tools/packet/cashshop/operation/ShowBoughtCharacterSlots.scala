package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtCharacterSlots(private var _slots: Short) extends PacketInput {
  def slots: Short = _slots

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}