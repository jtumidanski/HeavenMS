package tools.packet.cashshop.operation

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowBoughtCashPackageSuccess(private var _cashPacket: java.util.List[Item], private var _accountId: Int) extends PacketInput {
  def cashPacket: java.util.List[Item] = _cashPacket

  def accountId: Int = _accountId

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}