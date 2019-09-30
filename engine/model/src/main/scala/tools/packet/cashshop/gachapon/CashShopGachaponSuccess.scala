package tools.packet.cashshop.gachapon

import client.inventory.Item
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CashShopGachaponSuccess(private var _accountId: Int, private var _sn: Long, private var _remainingBoxes: Int,
                              private var _item: Item, private var _itemId: Int, private var _selectedItemCount: Int,
                              private var _jackpot: Boolean) extends PacketInput {
  def accountId: Int = _accountId

  def sn: Long = _sn

  def remainingBoxes: Int = _remainingBoxes

  def item: Item = _item

  def itemId: Int = _itemId

  def selectedItemCount: Int = _selectedItemCount

  def jackpot: Boolean = _jackpot

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_CASH_ITEM_GACHAPON_RESULT
}