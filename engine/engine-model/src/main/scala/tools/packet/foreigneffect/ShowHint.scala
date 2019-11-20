package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowHint(private var _hint: String, private var _width: Int, private var _height: Int) extends PacketInput {
  def hint: String = _hint

  def width: Int = _width

  def height: Int = _height

  override def opcode(): SendOpcode = SendOpcode.PLAYER_HINT
}