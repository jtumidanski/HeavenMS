package tools.packet

import net.opcodes.SendOpcode
import server.WorldRecommendation

class RecommendedWorldMessage(private var _worlds: java.util.List[WorldRecommendation]) extends PacketInput {
  def worlds: java.util.List[WorldRecommendation] = _worlds

  override def opcode(): SendOpcode = SendOpcode.RECOMMENDED_WORLD_MESSAGE
}