package tools.packet.parcel

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveDueyItem(private var _remove: Boolean, private var _packageId: Int) extends PacketInput {
  def remove: Boolean = _remove

  def packageId: Int = _packageId

  override def opcode(): SendOpcode = SendOpcode.PARCEL
}