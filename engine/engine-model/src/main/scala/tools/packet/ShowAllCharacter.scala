package tools.packet

import net.opcodes.SendOpcode

class ShowAllCharacter(private var _chars: Int, private var _unk: Int) extends PacketInput {
  def chars: Int = _chars

  def unk: Int = _unk

  override def opcode(): SendOpcode = SendOpcode.VIEW_ALL_CHAR
}