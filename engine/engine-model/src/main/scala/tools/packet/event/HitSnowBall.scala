package tools.packet.event

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class HitSnowBall(private var _what: Int, private var _damage: Int) extends PacketInput {
  def what: Int = _what

  def damage: Int = _damage

  override def opcode(): SendOpcode = SendOpcode.HIT_SNOWBALL
}