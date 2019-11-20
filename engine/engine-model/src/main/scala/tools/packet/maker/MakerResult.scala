package tools.packet.maker

import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class MakerResult(private var _success: Boolean, private var _itemMade: Int, private var _itemCount: Int,
                  private var _mesos: Int, private var _itemsLost: java.util.List[Pair[java.lang.Integer, java.lang.Integer]],
                  private var _catalystId: Int, private var _incBuffGems: java.util.List[java.lang.Integer]) extends PacketInput {
  def success: Boolean = _success

  def itemMade: Int = _itemMade

  def itemCount: Int = _itemCount

  def mesos: Int = _mesos

  def itemsLost: java.util.List[Pair[java.lang.Integer, java.lang.Integer]] = _itemsLost

  def catalystId: Int = _catalystId

  def incBuffGems: java.util.List[java.lang.Integer] = _incBuffGems

  override def opcode(): SendOpcode = SendOpcode.MAKER_RESULT
}