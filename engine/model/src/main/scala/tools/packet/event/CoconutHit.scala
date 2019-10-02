package tools.packet.event

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CoconutHit(private var _spawn: Boolean, private var _id: Int, private var _type: Int) extends PacketInput {
  def spawn: Boolean = _spawn

  def id: Int = _id

  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.COCONUT_HIT
}