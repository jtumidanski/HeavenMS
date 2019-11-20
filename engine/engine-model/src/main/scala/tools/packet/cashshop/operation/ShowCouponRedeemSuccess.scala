package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.Pair
import tools.packet.PacketInput

class ShowCouponRedeemSuccess(private var _accountId: Int, private var _maplePoints: Int, private var _mesos: Int,
                              private var _cashItems: java.util.List[Item],
                              private var _items: java.util.List[Pair[Integer, Integer]]) extends PacketInput {
  def accountId: Int = _accountId

  def maplePoints: Int = _maplePoints

  def mesos: Int = _mesos

  def cashItems: java.util.List[Item] = _cashItems

  def items: java.util.List[Pair[Integer, Integer]] = _items

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_OPERATION
}