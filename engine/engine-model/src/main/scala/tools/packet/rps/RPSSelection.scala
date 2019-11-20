package tools.packet.rps

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RPSSelection(private var _selection: Byte, private var _answer: Byte) extends PacketInput {
  def selection: Byte = _selection

  def answer: Byte = _answer

  override def opcode(): SendOpcode = SendOpcode.RPS_GAME
}