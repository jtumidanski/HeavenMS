package tools.packet.npctalk

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetDimensionalMirror(private var _talk: String) extends PacketInput {
  def talk: String = _talk

  override def opcode(): SendOpcode = SendOpcode.NPC_TALK
}