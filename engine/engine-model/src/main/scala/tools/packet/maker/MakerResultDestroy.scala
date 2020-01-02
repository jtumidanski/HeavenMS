package tools.packet.maker

import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class MakerResultDestroy(private var _itemId: Int, private var _mesos: Int,
                         private var _itemsGained: java.util.List[Pair[java.lang.Integer, java.lang.Integer]]) extends PacketInput {
  def itemId: Int = _itemId

  def mesos: Int = _mesos

  def itemsGained: java.util.List[Pair[java.lang.Integer, java.lang.Integer]] = _itemsGained

  override def opcode(): SendOpcode = SendOpcode.MAKER_RESULT
}