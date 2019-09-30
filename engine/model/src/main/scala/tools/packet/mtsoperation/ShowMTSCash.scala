package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowMTSCash(private var _maplePoint: Int, private var _nxPrepaid: Int) extends PacketInput {
  def maplePoint: Int = _maplePoint

  def nxPrepaid: Int = _nxPrepaid

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION2
}