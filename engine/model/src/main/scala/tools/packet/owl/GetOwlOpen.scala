package tools.packet.owl

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetOwlOpen(private var _leaderboards: java.util.List[java.lang.Integer]) extends PacketInput {
  def leaderboards: java.util.List[java.lang.Integer] = _leaderboards

  override def opcode(): SendOpcode = SendOpcode.SHOP_SCANNER_RESULT
}