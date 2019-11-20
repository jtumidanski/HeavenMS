package tools.packet.maker

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MakerCrystalResult(private var _itemIdGained: Int, private var _itemIdLost: Int) extends PacketInput {
  def itemIdGained: Int = _itemIdGained

  def itemIdLost: Int = _itemIdLost

  override def opcode(): SendOpcode = SendOpcode.MAKER_RESULT
}