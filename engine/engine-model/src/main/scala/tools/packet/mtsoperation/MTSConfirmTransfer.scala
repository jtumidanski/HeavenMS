package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MTSConfirmTransfer(private var _quantity: Int, private var _position: Int) extends PacketInput {
  def quantity: Int = _quantity

  def position: Int = _position

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION
}