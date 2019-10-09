package tools.packet

import net.opcodes.SendOpcode

class GetEnergy(private var _info: String, private var _amount: Int) extends PacketInput {
  def info: String = _info

  def amount: Int = _amount

  override def opcode(): SendOpcode = SendOpcode.SESSION_VALUE
}