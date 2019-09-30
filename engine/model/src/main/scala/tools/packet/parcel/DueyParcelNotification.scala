package tools.packet.parcel

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DueyParcelNotification(private var _quick: Boolean) extends PacketInput {
  def quick: Boolean = _quick

  override def opcode(): SendOpcode = SendOpcode.PARCEL
}