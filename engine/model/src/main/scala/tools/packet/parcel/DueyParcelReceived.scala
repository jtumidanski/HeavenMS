package tools.packet.parcel

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DueyParcelReceived(private var _from: String, private var _quick: Boolean) extends PacketInput {
  def from: String = _from

  def quick: Boolean = _quick

  override def opcode(): SendOpcode = SendOpcode.PARCEL
}