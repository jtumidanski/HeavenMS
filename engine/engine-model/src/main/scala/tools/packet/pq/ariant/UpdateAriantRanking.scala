package tools.packet.pq.ariant

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateAriantRanking(private var _scores: java.util.List[AriantScore]) extends PacketInput {
  def scores: java.util.List[AriantScore] = _scores

  override def opcode(): SendOpcode = SendOpcode.ARIANT_ARENA_USER_SCORE
}