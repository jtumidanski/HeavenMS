package tools.packet.cashshop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowCash(private var _nxCredit: Int, private var _maplePoint: Int, private var _nxPrepaid: Int) extends PacketInput {
  def nxCredit: Int = _nxCredit

  def maplePoint: Int = _maplePoint

  def nxPrepaid: Int = _nxPrepaid

  override def opcode(): SendOpcode = SendOpcode.QUERY_CASH_RESULT
}