package tools.packet.event

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SnowBallMessage(private var _team: Int, private var _message: Int) extends PacketInput {
  def team: Int = _team

  def message: Int = _message

  override def opcode(): SendOpcode = SendOpcode.SNOWBALL_MESSAGE
}